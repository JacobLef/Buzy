import { useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../api/auth";
import { authStorage } from "../utils/authStorage";
import type { AuthRequest } from "../types/auth";

interface UseAuthReturn {
  isLoading: boolean;
  error: string | null;
  handleLogin: (credentials: AuthRequest) => Promise<void>;
  clearError: () => void;
}

/**
 * Custom hook for handling authentication logic
 */
export const useAuth = (): UseAuthReturn => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleLogin = useCallback(
    async (credentials: AuthRequest) => {
      setError(null);
      setIsLoading(true);

      try {
        const response = await login(credentials);
        const authData = response.data;

        // Save authentication data
        authStorage.saveAuth(authData);

        // Redirect based on role
        const redirectPath =
          authData.role === "EMPLOYER" ? "/employer" : "/employee";
        navigate(redirectPath);
      } catch (err) {
        const axiosError = err as { response?: { status?: number } };
        const errorMessage =
          axiosError.response?.status === 401
            ? "Invalid email or password"
            : "Login failed. Please try again.";
        setError(errorMessage);
        throw err; // Re-throw to allow component to handle if needed
      } finally {
        setIsLoading(false);
      }
    },
    [navigate]
  );

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return {
    isLoading,
    error,
    handleLogin,
    clearError,
  };
};

