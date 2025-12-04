import { useState, useCallback, useEffect } from 'react';
import type { Paycheck, PaycheckStatus, DistributeBonusRequest } from '../types/payroll';
import type { Employee } from '../types/employee';
import {
  previewPayroll,
  calculatePayroll,
  distributeBonuses,
  getCurrentTaxStrategy,
  getAvailableTaxStrategies,
  switchTaxStrategy,
  getPayrollHistory,
  deletePaycheck,
  updatePaycheckStatus,
} from '../api/payroll';
import { getEmployeesByBusiness } from '../api/employees';
import { getEmployer } from '../api/employers';

export const usePayroll = (businessId: number | null) => {
  // Tax Strategy State
  const [taxStrategy, setTaxStrategy] = useState<string>("");
  const [availableStrategies, setAvailableStrategies] = useState<Record<string, string>>({});
  const [isLoadingStrategy, setIsLoadingStrategy] = useState(false);

  // Employees State
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [isLoadingEmployees, setIsLoadingEmployees] = useState(false);

  // Payroll History State
  const [history, setHistory] = useState<Paycheck[]>([]);
  const [isLoadingHistory, setIsLoadingHistory] = useState(false);

  // General Loading State
  const [isLoading, setIsLoading] = useState(false);

  // Notification State
  const [notification, setNotification] = useState<{
    type: "success" | "error";
    msg: string;
  } | null>(null);

  // Load Tax Strategy
  const loadTaxStrategy = useCallback(async () => {
    try {
      const [currentResponse, availableResponse] = await Promise.all([
        getCurrentTaxStrategy(),
        getAvailableTaxStrategies(),
      ]);
      
      const currentStrategy = currentResponse.data || "Flat Tax Strategy";
      setTaxStrategy(currentStrategy);
      
      const strategies = availableResponse.data;
      if (strategies && Object.keys(strategies).length > 0) {
        setAvailableStrategies(strategies);
      } else {
        setAvailableStrategies({
          flatTaxStrategy: "Flat Tax Strategy",
          progressiveTaxStrategy: "Progressive Tax Strategy",
        });
      }
    } catch (error) {
      console.error("Failed to load tax strategy:", error);
      setTaxStrategy("Flat Tax Strategy");
      setAvailableStrategies({
        flatTaxStrategy: "Flat Tax Strategy",
        progressiveTaxStrategy: "Progressive Tax Strategy",
      });
    }
  }, []);

  // Switch Tax Strategy
  const handleSwitchStrategy = useCallback(async (strategyKey: string) => {
    setIsLoadingStrategy(true);
    try {
      const response = await switchTaxStrategy(strategyKey);
      setTaxStrategy(response.data.strategy);
      showNotification("success", `Tax strategy switched to ${response.data.strategy}`);
      return true;
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { error?: string } } };
      const errorMsg =
        axiosError.response?.data?.error ||
        "Failed to switch tax strategy. Please try again.";
      showNotification("error", errorMsg);
      return false;
    } finally {
      setIsLoadingStrategy(false);
    }
  }, []);

  // Load Employees
  const loadEmployees = useCallback(async () => {
    if (!businessId) return;

    setIsLoadingEmployees(true);
    try {
      const response = await getEmployeesByBusiness(businessId);
      setEmployees(response.data);
    } catch (error) {
      console.error("Failed to load employees:", error);
      showNotification("error", "Failed to load employees");
    } finally {
      setIsLoadingEmployees(false);
    }
  }, [businessId]);

  // Load Payroll History
  const loadAllPayrollHistory = useCallback(async () => {
    setIsLoadingHistory(true);
    try {
      const allHistory: Paycheck[] = [];
      for (const emp of employees) {
        try {
          const response = await getPayrollHistory(emp.id);
          allHistory.push(...response.data);
        } catch (error) {
          console.error(`Failed to load history for employee ${emp.id}:`, error);
        }
      }
      // Sort by date descending
      allHistory.sort(
        (a, b) =>
          new Date(b.payDate).getTime() - new Date(a.payDate).getTime()
      );
      setHistory(allHistory);
    } catch (error) {
      console.error("Failed to load payroll history:", error);
    } finally {
      setIsLoadingHistory(false);
    }
  }, [employees]);

  // Preview Payroll Calculation
  const calculatePreview = useCallback(async (employeeId: number, additionalPay?: number) => {
    setIsLoading(true);
    try {
      const additionalPayValue =
        additionalPay && Number(additionalPay) > 0
          ? Number(additionalPay)
          : undefined;

      const response = await previewPayroll(employeeId, additionalPayValue);
      return response.data;
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      const errorMsg =
        axiosError.response?.data?.message ||
        "Failed to calculate payroll preview. Please try again.";
      showNotification("error", errorMsg);
      return null;
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Generate Paycheck
  const generatePaycheck = useCallback(async (employeeId: number, additionalPay?: number) => {
    setIsLoading(true);
    try {
      const additionalPayValue =
        additionalPay && Number(additionalPay) > 0
          ? Number(additionalPay)
          : undefined;

      await calculatePayroll(employeeId, additionalPayValue);
      showNotification("success", "Paycheck generated successfully");
      await loadAllPayrollHistory();
      return true;
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      const errorMsg =
        axiosError.response?.data?.message ||
        "Failed to generate paycheck. Please try again.";
      showNotification("error", errorMsg);
      return false;
    } finally {
      setIsLoading(false);
    }
  }, [loadAllPayrollHistory]);

  // Distribute Bonuses
  const distributeBonus = useCallback(async (request: DistributeBonusRequest) => {
    setIsLoading(true);
    try {
      const response = await distributeBonuses(request);
      const result = response.data;

      showNotification(
        "success",
        `Bonuses distributed to ${result.successfulPaychecks} employees`
      );
      await loadAllPayrollHistory();
      return result;
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      const errorMsg =
        axiosError.response?.data?.message ||
        "Failed to distribute bonuses. Please try again.";
      showNotification("error", errorMsg);
      return null;
    } finally {
      setIsLoading(false);
    }
  }, [loadAllPayrollHistory]);

  // Delete Paycheck
  const handleDeletePaycheck = useCallback(async (paycheckId: number) => {
    setIsLoading(true);
    try {
      await deletePaycheck(paycheckId);
      showNotification("success", "Paycheck deleted successfully");
      await loadAllPayrollHistory();
      return true;
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      const errorMsg =
        axiosError.response?.data?.message ||
        "Failed to delete paycheck. Please try again.";
      showNotification("error", errorMsg);
      return false;
    } finally {
      setIsLoading(false);
    }
  }, [loadAllPayrollHistory]);

  // Update Paycheck Status
  const handleUpdateStatus = useCallback(async (paycheckId: number, newStatus: PaycheckStatus) => {
    setIsLoading(true);
    try {
      await updatePaycheckStatus(paycheckId, newStatus);
      showNotification("success", `Paycheck status updated to ${newStatus}`);
      await loadAllPayrollHistory();
      return true;
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      const errorMsg =
        axiosError.response?.data?.message ||
        "Failed to update paycheck status. Please try again.";
      showNotification("error", errorMsg);
      return false;
    } finally {
      setIsLoading(false);
    }
  }, [loadAllPayrollHistory]);

  // Notification Helper
  const showNotification = useCallback((type: "success" | "error", msg: string) => {
    setNotification({ type, msg });
    setTimeout(() => setNotification(null), 3000);
  }, []);

  // Load Business ID from User
  const loadUserBusiness = useCallback(async (): Promise<number | null> => {
    try {
      const userStr = localStorage.getItem("user");
      if (!userStr) return null;

      const user = JSON.parse(userStr);
      if (user.role === "EMPLOYER" && user.businessPersonId) {
        const response = await getEmployer(user.businessPersonId);
        const employer = response.data;
        return employer.companyId;
      }
      return null;
    } catch (error) {
      console.error("Failed to load user business:", error);
      showNotification("error", "Failed to load business information");
      return null;
    }
  }, [showNotification]);

  // Initialize
  useEffect(() => {
    loadTaxStrategy();
  }, [loadTaxStrategy]);

  useEffect(() => {
    if (businessId) {
      loadEmployees();
    }
  }, [businessId, loadEmployees]);

  useEffect(() => {
    if (employees.length > 0) {
      loadAllPayrollHistory();
    }
  }, [employees, loadAllPayrollHistory]);

  return {
    // State
    taxStrategy,
    availableStrategies,
    employees,
    history,
    isLoading,
    isLoadingStrategy,
    isLoadingEmployees,
    isLoadingHistory,
    notification,

    // Actions
    loadUserBusiness,
    handleSwitchStrategy,
    calculatePreview,
    generatePaycheck,
    distributeBonus,
    handleDeletePaycheck,
    handleUpdateStatus,
    loadAllPayrollHistory,
    showNotification,
  };
};

