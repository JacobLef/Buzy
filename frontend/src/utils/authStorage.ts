import type { AuthResponse, UserRole } from "../types/auth";

export interface StoredUser {
  role: UserRole;
  email: string;
  userId: number;
  businessPersonId: number | null;
}

const TOKEN_KEY = "token";
const USER_KEY = "user";

/**
 * Authentication storage utilities for managing token and user data in localStorage
 */
export const authStorage = {
  /**
   * Save authentication data to localStorage
   */
  saveAuth: (authData: AuthResponse): void => {
    localStorage.setItem(TOKEN_KEY, authData.token);
    localStorage.setItem(
      USER_KEY,
      JSON.stringify({
        role: authData.role,
        email: authData.email,
        userId: authData.userId,
        businessPersonId: authData.businessPersonId,
      })
    );
  },

  /**
   * Get stored token
   */
  getToken: (): string | null => {
    return localStorage.getItem(TOKEN_KEY);
  },

  /**
   * Get stored user data
   */
  getUser: (): StoredUser | null => {
    const userStr = localStorage.getItem(USER_KEY);
    if (!userStr) return null;
    try {
      return JSON.parse(userStr) as StoredUser;
    } catch {
      return null;
    }
  },

  /**
   * Clear all authentication data
   */
  clearAuth: (): void => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },

  /**
   * Check if user is authenticated
   */
  isAuthenticated: (): boolean => {
    return !!authStorage.getToken() && !!authStorage.getUser();
  },
};

