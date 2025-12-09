export interface Company {
  id: number;
  name: string;
  address: string;
  industry: string;
  foundedDate: string;
  createdAt: string;
  updatedAt: string;
  businessType: string;
  totalEmployees: number;
  totalEmployers: number;
  totalPersons: number;
  employeeIds: number[];
  employerIds: number[];
}

export interface CreateBusinessRequest {
  name: string;
  address: string;
  industry: string;
  foundedDate: string;
}

export interface UpdateBusinessRequest {
  name?: string;
  address?: string;
  industry?: string;
  foundedDate?: string;
}