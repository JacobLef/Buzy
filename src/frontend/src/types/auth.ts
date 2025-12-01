export type UserRole = "EMPLOYEE" | "EMPLOYER";

export interface AuthResponse {
  token: string;
  role: UserRole;
  email: string;
  userId: number;
  businessPersonId: number | null; 
}

export interface AuthRequest {
  email: string,
  password: string
}