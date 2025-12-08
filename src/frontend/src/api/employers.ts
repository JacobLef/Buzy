import api from "./axios";
import type { 
  Employer, 
  CreateEmployerRequest, 
  UpdateEmployerRequest 
} from "../types/employer";
import type { Employee } from "../types/employee";

export const createEmployer = (data: CreateEmployerRequest) => 
  api.post<Employer>("/api/employers", data);

export const getEmployer = (id: number) => 
  api.get<Employer>(`/api/employers/${id}`);

export const getAllEmployers = () => 
  api.get<Employer[]>("/api/employers");

export const updateEmployer = (id: number, data: UpdateEmployerRequest) => 
  api.put<Employer>(`/api/employers/${id}`, data);

export const deleteEmployer = (id: number) => 
  api.delete(`/api/employers/${id}`);

export const getEmployersByBusiness = (businessId: number) => 
  api.get<Employer[]>(`/api/employers/business/${businessId}`);

export const getEmployersByDepartment = (department: string) => 
  api.get<Employer[]>(`/api/employers/department/${department}`);

export const getDirectReports = (id: number) => 
  api.get<Employee[]>(`/api/employers/${id}/direct-reports`);

export const promoteToAdmin = (id: number) => 
  api.post<Employer>(`/api/employers/${id}/promote-admin`);

export const removeAdmin = (id: number) => 
  api.post<Employer>(`/api/employers/${id}/remove-admin`);