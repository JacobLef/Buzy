import api from "./axios";
import type {
  Paycheck,
  PaycheckStatus,
  BonusDistributionResponse,
  PayrollSummary,
  DistributeBonusRequest,
  UpdatePaycheckRequest
} from "../types/payroll";
export const previewPayroll = (employeeId: number, additionalPay?: number) => {
  const params = additionalPay ? { additionalPay } : {};
  return api.get<Paycheck>(`/api/payroll/preview/${employeeId}`, { params });
};

export const calculatePayroll = (employeeId: number, additionalPay?: number) => {
  const params = additionalPay ? { additionalPay } : {};
  return api.post<Paycheck>(`/api/payroll/calculate/${employeeId}`, null, { params });
};

export const distributeBonuses = (data: DistributeBonusRequest) =>
  api.post<BonusDistributionResponse>("/api/payroll/bonuses", data);

export const getCurrentTaxStrategy = () =>
  api.get<string>("/api/payroll/tax-strategy");

export const getAvailableTaxStrategies = () =>
  api.get<Record<string, string>>("/api/payroll/tax-strategies");

export const switchTaxStrategy = (strategy: string) =>
  api.put<{ message: string; strategy: string }>("/api/payroll/tax-strategy", null, {
    params: { strategy }
  });

export const getPayrollHistory = (employeeId: number) =>
  api.get<Paycheck[]>(`/api/payroll/history/${employeeId}`);

export const getPayrollHistoryByDateRange = (
  employeeId: number,
  startDate: string,
  endDate: string
) =>
  api.get<Paycheck[]>(`/api/payroll/history/${employeeId}/range`, {
    params: { startDate, endDate }
  });

export const getPayrollSummary = (
  businessId: number,
  startDate: string,
  endDate: string
) =>
  api.get<PayrollSummary>(`/api/payroll/summary/${businessId}`, {
    params: { startDate, endDate }
  });

export const updatePaycheck = (paycheckId: number, data: UpdatePaycheckRequest) =>
  api.put<Paycheck>(`/api/payroll/paycheck/${paycheckId}`, null, {
    params: data
  });

export const deletePaycheck = (paycheckId: number) =>
  api.delete(`/api/payroll/paycheck/${paycheckId}`);

export const updatePaycheckStatus = (paycheckId: number, status: PaycheckStatus) =>
  api.put<Paycheck>(`/api/payroll/paycheck/${paycheckId}/status`, null, {
    params: { status }
  });