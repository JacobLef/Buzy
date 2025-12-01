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
  name: string,
  email: string,
  salary: number,
  position: string,
  comnpanyId: number,
  managerId: number,
  hireDate: string
};

export interface UpdateEmployeeRequest {
  name: string,
  email: string,
  salary: number,
  position: string,
  managerId: number,
  hireDate: string,
  status: PersonStatus
};