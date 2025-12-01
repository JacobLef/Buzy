import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
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
        const token = localStorage.getItem("token");
        const response = await axios.post(
          "http://localhost:8080/api/auth/refresh",
          {},
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        const newToken = response.data.token;
        localStorage.setItem("token", newToken);

        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
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