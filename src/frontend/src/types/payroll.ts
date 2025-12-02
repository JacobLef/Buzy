export type PaycheckStatus = "DRAFT" | "PENDING" | "PAID" | "VOIDED";

export interface Paycheck {
  id: number;
  employeeId: number;
  employeeName: string;
  grossPay: number;
  bonus: number | null;
  taxDeduction: number;
  insuranceDeduction: number;
  totalDeductions: number;
  netPay: number;
  payDate: string;
  taxStrategyUsed: string;
  status: PaycheckStatus;
}

export interface BonusDistributionResponse {
  businessId: number;
  businessName: string;
  totalEmployeesProcessed: number;
  successfulPaychecks: number;
  failedPaychecks: number;
  totalGrossPaid: number;
  totalNetPaid: number;
  totalTaxDeducted: number;
  totalInsuranceDeducted: number;
  paychecks: Paycheck[];
  errors: string[];
}

export interface PayrollSummary {
  businessId: number;
  businessName: string;
  startDate: string;
  endDate: string;
  totalPaychecks: number;
  totalEmployees: number;
  totalGrossPay: number;
  totalBonus: number;
  totalTaxDeductions: number;
  totalInsuranceDeductions: number;
  totalDeductions: number;
  totalNetPay: number;
  averageGrossPay: number;
  averageNetPay: number;
  averageTaxRate: number;
  averageInsuranceRate: number;
}

export interface DistributeBonusRequest {
  businessId: number;
  bonusAmount: number;
  employeeIds?: number[];
  department?: string; 
  description?: string;
}

export interface UpdatePaycheckRequest {
  grossPay?: number;
  bonus?: number;
  taxDeduction?: number;
  insuranceDeduction?: number;
}