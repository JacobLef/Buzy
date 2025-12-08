import { useState, useEffect } from 'react';
import { getRecentActivity } from '../api/dashboard';
import { getEmployer } from '../api/employers';
import { getEmployeesByBusiness } from '../api/employees';
import { getEmployersByBusiness } from '../api/employers';
import { getPayrollSummary } from '../api/payroll';
import { getAllTrainings } from '../api/training';
import type { Activity } from '../types/dashboard';
import type { Training } from '../types/training';

interface DashboardStats {
  totalEmployees: number;
  totalEmployers: number;
  monthlyPayroll: number;
  pendingTrainings: number;
  expiringTrainings: number; // For trend display
}

export const useDashboard = () => {
  const [stats, setStats] = useState<DashboardStats>({
    totalEmployees: 0,
    totalEmployers: 0,
    monthlyPayroll: 0,
    pendingTrainings: 0,
    expiringTrainings: 0,
  });
  
  const [recentActivity, setRecentActivity] = useState<Activity[]>([]);
  const [pendingTrainings, setPendingTrainings] = useState<Training[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      try {
        // Get businessId from user
        const userStr = localStorage.getItem('user');
        let businessId: number | null = null;
        
        if (userStr) {
          const user = JSON.parse(userStr);
          if (user.role === 'EMPLOYER' && user.businessPersonId) {
            try {
              const employerResponse = await getEmployer(user.businessPersonId);
              businessId = employerResponse.data.companyId;
            } catch (error) {
              console.error('Failed to get employer:', error);
            }
          }
        }
        
        // If no businessId, use default or skip activity fetch
        let activityData: Activity[] = [];
        if (businessId) {
          try {
            const activityResponse = await getRecentActivity(businessId);
            activityData = activityResponse.data;
            // Filter out TRAINING activities (they will be shown in separate card)
            activityData = activityData.filter((activity) => activity.type !== 'TRAINING');
            // Sort by timestamp: past (ago) -> today -> future (due)
            // This ensures chronological order: oldest past events first, then today, then future events
            const now = new Date().getTime();
            activityData.sort((a, b) => {
              const dateA = new Date(a.timestamp).getTime();
              const dateB = new Date(b.timestamp).getTime();
              const isPastA = dateA < now;
              const isPastB = dateB < now;
              const isFutureA = dateA > now;
              const isFutureB = dateB > now;
              
              // Past events come first (oldest first)
              if (isPastA && isPastB) {
                return dateA - dateB; // Ascending: older past events first
              }
              if (isPastA && !isPastB) return -1; // Past before present/future
              if (!isPastA && isPastB) return 1; // Present/future after past
              
              // Future events come last (soonest first)
              if (isFutureA && isFutureB) {
                return dateA - dateB; // Ascending: soonest future events first
              }
              if (isFutureA && !isFutureB) return 1; // Future after present
              if (!isFutureA && isFutureB) return -1; // Present before future
              
              // Both are today/present - sort by time (earlier first)
              return dateA - dateB;
            });
            console.log('Recent Activity Data:', activityData); // Debug log
            setRecentActivity(activityData);
          } catch (error) {
            console.error('Failed to fetch activity:', error);
            // Fallback to empty array on error
            setRecentActivity([]);
          }

          // Fetch dashboard stats
          try {
            // Get current month start and end dates
            const currentDate = new Date();
            const year = currentDate.getFullYear();
            const month = currentDate.getMonth();
            const startDate = new Date(year, month, 1).toISOString().split('T')[0];
            const endDate = new Date(year, month + 1, 0).toISOString().split('T')[0];

            // Fetch all stats in parallel
            const [employeesResponse, employersResponse, payrollSummaryResponse, trainingsResponse] = await Promise.all([
              getEmployeesByBusiness(businessId),
              getEmployersByBusiness(businessId),
              getPayrollSummary(businessId, startDate, endDate).catch(() => null), // Allow to fail gracefully
              getAllTrainings().catch(() => ({ data: [] })), // Allow to fail gracefully
            ]);

            // Calculate pending trainings: not completed, not expired, not expiring soon
            // Filter trainings for this business only - match personId with employee/employer IDs
            const businessEmployeeIds = new Set(employeesResponse.data.map((e: { id: number }) => e.id));
            const businessEmployerIds = new Set(employersResponse.data.map((e: { id: number }) => e.id));
            const businessTrainings = trainingsResponse.data.filter((t: Training) => {
              // Match personId (number) with employee/employer IDs
              return t.personId && (businessEmployeeIds.has(Number(t.personId)) || businessEmployerIds.has(Number(t.personId)));
            });

            // Calculate pending and expiring trainings
            const now = new Date();
            
            // Filter for expiring soon trainings (expiring within 30 days)
            const expiringSoonTrainingsList = businessTrainings.filter((t: Training) => {
              if (t.completed || t.expired) return false;
              // Must have expiry date
              if (!t.expiryDate) return false;
              // Expiring within 30 days
              const expiryDate = new Date(t.expiryDate);
              const daysUntilExpiry = Math.ceil(
                (expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
              );
              return daysUntilExpiry > 0 && daysUntilExpiry <= 30;
            });

            // Filter for pending trainings (not completed, not expired, not expiring soon)
            const pendingTrainingsList = businessTrainings.filter((t: Training) => {
              if (t.completed || t.expired) return false;
              // If no expiry date, it's pending
              if (!t.expiryDate) return true;
              // If expiry date is more than 30 days away, it's pending
              const expiryDate = new Date(t.expiryDate);
              const daysUntilExpiry = Math.ceil(
                (expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
              );
              return daysUntilExpiry > 30;
            });

            // Sort expiring soon trainings by expiry date (soonest first)
            const sortedExpiringSoonTrainings = expiringSoonTrainingsList.sort((a, b) => {
              const dateA = a.expiryDate ? new Date(a.expiryDate).getTime() : Infinity;
              const dateB = b.expiryDate ? new Date(b.expiryDate).getTime() : Infinity;
              return dateA - dateB;
            });

            setPendingTrainings(sortedExpiringSoonTrainings);

            // Total pending + expiring count
            const totalPendingAndExpiring = pendingTrainingsList.length + expiringSoonTrainingsList.length;
            const expiringCount = expiringSoonTrainingsList.length;

            setStats({
              totalEmployees: employeesResponse.data.length,
              totalEmployers: employersResponse.data.length,
              monthlyPayroll: payrollSummaryResponse?.data?.totalNetPay || 0,
              pendingTrainings: totalPendingAndExpiring,
              expiringTrainings: expiringCount,
            });
          } catch (error) {
            console.error('Failed to fetch dashboard stats:', error);
            // Fallback to default values on error
            setStats({
              totalEmployees: 0,
              totalEmployers: 0,
              monthlyPayroll: 0,
              pendingTrainings: 0,
              expiringTrainings: 0,
            });
          }
        } else {
          // No businessId available, set empty activity and default stats
          setRecentActivity([]);
          setPendingTrainings([]);
          setStats({
            totalEmployees: 0,
            totalEmployers: 0,
            monthlyPayroll: 0,
            pendingTrainings: 0,
            expiringTrainings: 0,
          });
        }

      } catch (error) {
        console.error("Failed to load dashboard", error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  return { stats, recentActivity, pendingTrainings, loading };
};

