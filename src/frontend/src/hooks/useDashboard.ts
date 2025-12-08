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
}

export const useDashboard = () => {
  const [stats, setStats] = useState<DashboardStats>({
    totalEmployees: 0,
    totalEmployers: 0,
    monthlyPayroll: 0,
    pendingTrainings: 0,
  });
  
  const [recentActivity, setRecentActivity] = useState<Activity[]>([]);
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
            // Sort by timestamp (most recent first) - descending order
            // This ensures the most relevant/recent activities appear at the top
            activityData.sort((a, b) => {
              const dateA = new Date(a.timestamp).getTime();
              const dateB = new Date(b.timestamp).getTime();
              return dateA - dateB; // Descending order (newest first)
            });
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

            // Use the exact same logic as Training.tsx getPendingCount()
            const now = new Date();
            const pendingTrainings = businessTrainings.filter((t: Training) => {
              if (t.completed || t.expired) return false;
              // If no expiry date, it's pending
              if (!t.expiryDate) return true;
              // If expiry date is more than 30 days away, it's pending
              const expiryDate = new Date(t.expiryDate);
              const daysUntilExpiry = Math.ceil(
                (expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
              );
              return daysUntilExpiry > 30;
            }).length;

            setStats({
              totalEmployees: employeesResponse.data.length,
              totalEmployers: employersResponse.data.length,
              monthlyPayroll: payrollSummaryResponse?.data?.totalNetPay || 0,
              pendingTrainings: pendingTrainings,
            });
          } catch (error) {
            console.error('Failed to fetch dashboard stats:', error);
            // Fallback to default values on error
            setStats({
              totalEmployees: 0,
              totalEmployers: 0,
              monthlyPayroll: 0,
              pendingTrainings: 0,
            });
          }
        } else {
          // No businessId available, set empty activity and default stats
          setRecentActivity([]);
          setStats({
            totalEmployees: 0,
            totalEmployers: 0,
            monthlyPayroll: 0,
            pendingTrainings: 0,
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

  return { stats, recentActivity, loading };
};

