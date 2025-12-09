import api from "./axios";
import type { AuthRequest, AuthResponse } from "../types/auth";
import type { CreateEmployeeRequest } from "../types/employee";
import type { CreateEmployerRequest } from "../types/employer";

export const login = (data: AuthRequest) =>
  api.post<AuthResponse>("/api/auth/login", data);

export const refreshToken = () =>
  api.post<AuthResponse>("/api/auth/refresh");

export const validateToken = () =>
  api.get<boolean>("/api/auth/validate");

export const signupEmployee = (data: CreateEmployeeRequest) =>
  api.post<AuthResponse>("/api/auth/signup/employee", data);

export const signupEmployer = (data: CreateEmployerRequest) =>
  api.post<AuthResponse>("/api/auth/signup/employer", data);