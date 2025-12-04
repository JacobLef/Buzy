import { useState, useEffect } from 'react';
// import { getAllEmployees, getAllEmployers, getPayrollHistory } from '../api/...';

interface DashboardStats {
  totalEmployees: number;
  totalEmployers: number;
  monthlyPayroll: number;
  pendingTrainings: number;
}

interface RecentActivity {
  id: number;
  type: string;
  title: string;
  date: string;
  status: 'completed' | 'info' | 'warning' | 'alert';
}

export const useDashboard = () => {
  const [stats, setStats] = useState<DashboardStats>({
    totalEmployees: 0,
    totalEmployers: 0,
    monthlyPayroll: 0,
    pendingTrainings: 0,
  });
  
  const [recentActivity, setRecentActivity] = useState<RecentActivity[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      try {
        // --- MOCK DATA START ---
        // Replace with: const [emps, employers, history] = await Promise.all([...])
        
        // Mocking API delay
        await new Promise(resolve => setTimeout(resolve, 800));

        setStats({
          totalEmployees: 24,
          totalEmployers: 3,
          monthlyPayroll: 124500.00,
          pendingTrainings: 5, // Placeholder for Ren's data
        });

        setRecentActivity([
          { id: 1, type: 'PAYROLL', title: 'October Payroll Generated', date: '2 hours ago', status: 'completed' },
          { id: 2, type: 'EMPLOYEE', title: 'New Hire: Sarah J.', date: '1 day ago', status: 'info' },
          { id: 3, type: 'TRAINING', title: 'Safety Compliance Due', date: '2 days remaining', status: 'warning' },
          { id: 4, type: 'PAYROLL', title: 'Bonus Distribution', date: '3 days ago', status: 'completed' },
          { id: 5, type: 'EMPLOYEE', title: 'Employee Termination', date: '1 week ago', status: 'alert' },
        ]);
        // --- MOCK DATA END ---

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

