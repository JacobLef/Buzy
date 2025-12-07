import { useState, useEffect, useMemo } from 'react';
import type { Paycheck } from '../types/payroll';
import { getPayrollHistory } from '../api/payroll';

export const useEmployeePayroll = (employeeId: number | null) => {
  const [history, setHistory] = useState<Paycheck[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchHistory = async () => {
      if (!employeeId) {
        setLoading(false);
        return;
      }

      setLoading(true);
      try {
        const response = await getPayrollHistory(employeeId);
        // Sort: Newest first
        const sorted = response.data.sort((a: Paycheck, b: Paycheck) => 
          new Date(b.payDate).getTime() - new Date(a.payDate).getTime()
        );
        setHistory(sorted);
      } catch (error) {
        console.error("Failed to load payroll", error);
        setHistory([]);
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, [employeeId]);

  // Frontend YTD Calculation (MVP Shortcut)
  const stats = useMemo(() => {
    const currentYear = new Date().getFullYear();
    const thisYearChecks = history.filter(p => new Date(p.payDate).getFullYear() === currentYear);
    
    return {
      totalGross: thisYearChecks.reduce((sum, p) => sum + p.grossPay, 0),
      totalNet: thisYearChecks.reduce((sum, p) => sum + p.netPay, 0),
      totalTax: thisYearChecks.reduce((sum, p) => sum + p.taxDeduction, 0),
      totalInsurance: thisYearChecks.reduce((sum, p) => sum + p.insuranceDeduction, 0),
      paycheckCount: thisYearChecks.length
    };
  }, [history]);

  const latestPaycheck = history.length > 0 ? history[0] : null;

  return { history, latestPaycheck, stats, loading };
};

