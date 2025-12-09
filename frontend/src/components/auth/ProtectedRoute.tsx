import { Navigate, Outlet } from "react-router-dom";

export const ProtectedRoute = ({ allowedRoles }: { allowedRoles: string[] }) => {
  const token = localStorage.getItem("token");
  const user = localStorage.getItem("user");

  if (!token || !user) {
    return <Navigate to="/login" replace />;
  }

  const { role } = JSON.parse(user);

  if (!allowedRoles.includes(role)) {
    return <Navigate to={role === "EMPLOYER" ? "/employer" : "/employee"} replace />;
  }

  return <Outlet />;
};

