import axios from "axios";
import { authStorage } from "../utils/authStorage";

const api = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(
  (config) => {
    const token = authStorage.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const token = authStorage.getToken();
        const response = await axios.post(
          "http://localhost:8080/api/auth/refresh",
          {},
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        const newToken = response.data.token;
        // Update token in storage
        const user = authStorage.getUser();
        if (user) {
          authStorage.saveAuth({
            token: newToken,
            role: user.role,
            email: user.email,
            userId: user.userId,
            businessPersonId: user.businessPersonId,
          });
        }

        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        authStorage.clearAuth();
        window.location.href = "/login";
        return Promise.reject(
          refreshError instanceof Error 
            ? refreshError 
            : new Error("Token refresh failed")
        );
      }
    }

    return Promise.reject(
      error instanceof Error 
        ? error 
        : new Error(error?.message || "Request failed")
    );
  }
);

export default api;