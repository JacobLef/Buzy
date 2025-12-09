import api from "./axios";
import type { 
  Company, 
  CreateBusinessRequest, 
  UpdateBusinessRequest 
} from "../types/business";

export const createBusiness = (data: CreateBusinessRequest) => 
  api.post<Company>("/api/businesses", data);

export const getBusiness = (id: number) => 
  api.get<Company>(`/api/businesses/${id}`);

export const getAllBusinesses = () => 
  api.get<Company[]>("/api/businesses");

export const updateBusiness = (id: number, data: UpdateBusinessRequest) => 
  api.put<Company>(`/api/businesses/${id}`, data);

export const deleteBusiness = (id: number) => 
  api.delete(`/api/businesses/${id}`);