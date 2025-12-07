import { createBrowserRouter, Navigate, Outlet } from "react-router-dom";

import Landing from "../pages/Landing";
import Login from "../pages/Login";
import SignupEmployee from "../pages/SignupEmployee";
import SignupEmployer from "../pages/SignupEmployer";
import CreateCompany from "../pages/CreateCompany";

import EmployeeDashboard from "../pages/employee/Dashboard";
import EmployeeProfile from "../pages/employee/Profile";
import EmployeeTraining from "../pages/employee/Training";
import EmployeePayroll from "../pages/employee/Payroll";
import EmployeeCompany from "../pages/employee/CompanyDirectory";

import EmployerDashboard from "../pages/employer/Dashboard";
import EmployeeManagement from "../pages/employer/Employees";
import EmployerManagement from "../pages/employer/Employers";
import TrainingManagement from "../pages/employer/Training";
import PayrollManagement from "../pages/employer/Payroll";
import CompanySettings from "../pages/employer/Company";

import EmployeeLayout from "../components/layout/EmployeeLayout";
import EmployerLayout from "../components/layout/EmployerLayout";
import EmployerProfile from "../pages/employer/Profile";

const ProtectedRoute = ({ allowedRoles }: { allowedRoles: string[] }) => {
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

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Landing />,
  },
  {
    path: "/login",
    element: <Login />,
  },
  {
    path: "/signup/employee",
    element: <SignupEmployee />,
  },
  {
    path: "/signup/employer",
    element: <SignupEmployer />,
  },
  {
    path: "/create-company",
    element: <CreateCompany />
  },
  {
    element: <ProtectedRoute allowedRoles={["EMPLOYEE"]} />,
    children: [
      {
        element: <EmployeeLayout />,
        children: [
          {
            path: "/employee",
            element: <EmployeeDashboard />,
          },
          {
            path: "/employee/profile",
            element: <EmployeeProfile />,
          },
          {
            path: "/employee/training",
            element: <EmployeeTraining />,
          },
          {
            path: "/employee/payroll",
            element: <EmployeePayroll />,
          },
          {
            path: "/employee/company",
            element: <EmployeeCompany />,
          },
        ],
      },
    ],
  },

  {
    element: <ProtectedRoute allowedRoles={["EMPLOYER"]} />,
    children: [
      {
        element: <EmployerLayout />,
        children: [
          {
            path: "/employer",
            element: <EmployerDashboard />,
          },
          {
            path: "/employer/employees",
            element: <EmployeeManagement />,
          },
          {
            path: "/employer/employers",
            element: <EmployerManagement />,
          },
          {
            path: "/employer/training",
            element: <TrainingManagement />,
          },
          {
            path: "/employer/profile",
            element: <EmployerProfile />,
          },
          {
            path: "/employer/payroll",
            element: <PayrollManagement />,
          },
          {
            path: "/employer/company",
            element: <CompanySettings />,
          },
        ],
      },
    ],
  },

  {
    path: "*",
    element: <Navigate to="/" replace />,
  },
]);