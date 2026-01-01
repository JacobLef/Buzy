import { PersonStatus } from "./person_status";

export interface Employee {
  id: number;
  name: string;
  email: string;
  status: PersonStatus;
  salary: number;
  hireDate: string;
  companyId: number;
  companyName: string;
  position: string;
  managerId: number | null;
  managerName: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateEmployeeRequest {
  name: string;
  email: string;
  password: string;
  salary: number;
  position: string;
  companyId: number;
  managerId: number;
  hireDate: string;
}

export interface UpdateEmployeeRequest {
  name: string;
  email: string;
  password?: string;
  salary: number;
  position: string;
  managerId: number;
  hireDate: string;
  status: PersonStatus;
}
