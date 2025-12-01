import api from "./axios";
import type { 
  Employee, 
  CreateEmployeeRequest, 
  UpdateEmployeeRequest 
} from "../types/employee";

export const createEmployee = (data: CreateEmployeeRequest) => 
  api.post<Employee>("/api/employees", data);

export const getEmployee = (id: number) => 
  api.get<Employee>(`/api/employees/${id}`);

export const getAllEmployees = () => 
  api.get<Employee[]>("/api/employees");

export const updateEmployee = (id: number, data: UpdateEmployeeRequest) => 
  api.put<Employee>(`/api/employees/${id}`, data);

export const deleteEmployee = (id: number) => 
  api.delete(`/api/employees/${id}`);

export const getEmployeesByBusiness = (businessId: number) => 
  api.get<Employee[]>(`/api/employees/business/${businessId}`);

export const getEmployeesByManager = (managerId: number) => 
  api.get<Employee[]>(`/api/employees/manager/${managerId}`);

export const assignManager = (id: number, managerId: number) => 
  api.put<Employee>(`/api/employees/${id}/manager`, null, {
    params: { managerId }
  });