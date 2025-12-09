import type { Employee } from './employee';
import type { Employer } from './employer';

export interface EmployeeNode {
  id: number;
  name: string;
  position: string;
  department: string; // Key for grouping
  email: string;
  avatar?: string;
  managerId: number | null;
  salary?: number; // Employer only
  children?: EmployeeNode[];
  // Additional fields from backend
  status?: string;
  hireDate?: string;
}

export interface DepartmentSummary {
  name: string;
  headName: string;
  headId: number;
  employeeCount: number;
  budget?: number; // Employer stats
}

// Combined type for employees and employers
export type PersonData = Employee | Employer;

