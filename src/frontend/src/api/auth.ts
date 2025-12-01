import api from "./axios";
import type { AuthRequest, AuthResponse } from "../types/auth";

export const login = (data: AuthRequest) => 
  api.post<AuthResponse>("/api/auth/login", data);

export const refreshToken = () => 
  api.post<AuthResponse>("/api/auth/refresh");

export const validateToken = () => 
  api.get<boolean>("/api/auth/validate");