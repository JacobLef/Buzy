import { PersonStatus } from "./person_status";

export interface Employer {
  id: number;
  name: string;
  email: string;
  status: PersonStatus;
  salary: number;
  hireDate: string;
  companyId: number;
  companyName: string;
  department: string;
  title: string;
  directReportsCount: number;
  isAdmin?: boolean;
  isOwner?: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateEmployerRequest {
  name: string;
  email: string;
  password: string;
  salary: number;
  department: string;
  title: string;
  companyId: number;
  hireDate: string;
}

export interface UpdateEmployerRequest {
  name: string;
  email: string;
  password?: string;
  salary: number;
  department: string;
  title: string;
  hireDate: string;
  status: PersonStatus;
}
