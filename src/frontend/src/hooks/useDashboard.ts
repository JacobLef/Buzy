import { useState, useEffect } from 'react';
import { getRecentActivity } from '../api/dashboard';
import { getEmployer } from '../api/employers';
import { getEmployeesByBusiness } from '../api/employees';
import { getEmployersByBusiness } from '../api/employers';
import { getPayrollSummary } from '../api/payroll';
import type { Activity } from '../types/dashboard';

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
            setRecentActivity(activityData);
          } catch (error) {
            console.error('Failed to fetch activity:', error);
            // Fallback to empty array on error
            setRecentActivity([]);
          }

          // Fetch dashboard stats
          try {
            // Get current month start and end dates
            const now = new Date();
            const year = now.getFullYear();
            const month = now.getMonth();
            const startDate = new Date(year, month, 1).toISOString().split('T')[0];
            const endDate = new Date(year, month + 1, 0).toISOString().split('T')[0];

            // Fetch all stats in parallel
            const [employeesResponse, employersResponse, payrollSummaryResponse] = await Promise.all([
              getEmployeesByBusiness(businessId),
              getEmployersByBusiness(businessId),
              getPayrollSummary(businessId, startDate, endDate).catch(() => null), // Allow to fail gracefully
            ]);

            // Count pending trainings from recent activity (trainings expiring soon)
            const pendingTrainings = activityData.filter(
              (activity: Activity) => activity.type === 'TRAINING' && activity.status === 'warning'
            ).length;

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

