import { useState, useEffect } from "react";
import { getEmployee } from "../api/employees";
import { getTrainingsByPerson, getExpiredTrainings } from "../api/training";
import { getPayrollHistory } from "../api/payroll";
import type { Employee } from "../types/employee";
import type { Training } from "../types/training";
import type { Paycheck } from "../types/payroll";

interface EmployeeDashboardStats {
  upcomingTrainings: number;
  expiredTrainings: number;
  nextPayDate: string | null;
  lastBonus: number | null;
}

const DAYS_BETWEEN_PAYCHEKS = 14;

const calculateNextPayDate = (paychecks: Paycheck[]): string | null => {
  if (paychecks.length === 0) return null;

  const lastPayDate = new Date(paychecks[0].payDate);
  const nextPayDate = new Date(lastPayDate);
  nextPayDate.setDate(nextPayDate.getDate() + DAYS_BETWEEN_PAYCHEKS);

  return nextPayDate.toISOString().split("T")[0];
};

const findLastBonus = (paychecks: Paycheck[]): number | null => {
  const withBonus = paychecks.find((p) => p.bonus !== null && p.bonus > 0);
  return withBonus?.bonus ?? null;
};

export const useEmployeeDashboard = (employeeId: number) => {
  const [employee, setEmployee] = useState<Employee | null>(null);
  const [trainings, setTrainings] = useState<Training[]>([]);
  const [expiredTrainings, setExpiredTrainings] = useState<Training[]>([]);
  const [recentPaychecks, setRecentPaychecks] = useState<Paycheck[]>([]);
  const [stats, setStats] = useState<EmployeeDashboardStats>({
    upcomingTrainings: 0,
    expiredTrainings: 0,
    nextPayDate: null,
    lastBonus: null,
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      try {
        const [empRes, trainingRes, expiredRes, paycheckRes] =
          await Promise.all([
            getEmployee(employeeId),
            getTrainingsByPerson(employeeId),
            getExpiredTrainings(employeeId),
            getPayrollHistory(employeeId),
          ]);

        setEmployee(empRes.data);
        setTrainings(trainingRes.data);
        setExpiredTrainings(expiredRes.data);
        setRecentPaychecks(paycheckRes.data);

        const now = new Date();
        const pendingTrainings = trainingRes.data.filter((t: Training) => {
          if (t.completed || t.expired) return false;
          if (!t.expiryDate) return true;
          const expiryDate = new Date(t.expiryDate);
          const daysUntilExpiry = Math.ceil(
            (expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24),
          );
          return daysUntilExpiry > 30;
        }).length;

        const expiredTrainingsCount = trainingRes.data.filter((t: Training) => {
          return !t.completed && t.expired;
        }).length;

        setStats({
          upcomingTrainings: pendingTrainings,
          expiredTrainings: expiredTrainingsCount,
          nextPayDate: calculateNextPayDate(paycheckRes.data),
          lastBonus: findLastBonus(paycheckRes.data),
        });
      } catch (error) {
        console.error("Failed to load employee dashboard", error);
        setError("Failed to laod dashboard data");
      } finally {
        setLoading(false);
      }
    };

    if (employeeId) {
      fetchDashboardData();
    }
  }, [employeeId]);

  return {
    employee,
    trainings,
    expiredTrainings,
    recentPaychecks,
    stats,
    loading,
    error,
  };
};

