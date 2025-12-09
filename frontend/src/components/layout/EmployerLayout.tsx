import { Outlet, Link, useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import {
  LayoutDashboard,
  User,
  Users,
  Briefcase,
  FileText,
  DollarSign,
  Building2,
  LogOut,
  Shield,
  Crown,
} from "lucide-react";
import { getEmployer } from "../../api/employers";
import { authStorage } from "../../utils/authStorage";
import type { Employer } from "../../types/employer";

export default function EmployerLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const [currentEmployer, setCurrentEmployer] = useState<Employer | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadCurrentEmployer = async () => {
      try {
        const user = authStorage.getUser();
        if (user?.businessPersonId) {
          const response = await getEmployer(user.businessPersonId);
          setCurrentEmployer(response.data);
        }
      } catch (error) {
        console.error("Failed to load current employer:", error);
      } finally {
        setLoading(false);
      }
    };
    loadCurrentEmployer();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  };

  // Get user info from localStorage
  const getUserInfo = () => {
    const userStr = localStorage.getItem("user");
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        return {
          email: user.email || "employer@company.com",
          name: currentEmployer?.name || user.email?.split("@")[0] || "Employer",
        };
      } catch {
        return {
          email: "employer@company.com",
          name: "Employer",
        };
      }
    }
    return {
      email: "employer@company.com",
      name: "Employer",
    };
  };

  const userInfo = getUserInfo();
  const isOwner = currentEmployer?.isOwner ?? false;
  const isAdmin = currentEmployer?.isAdmin ?? false;

  // Only show Employers nav item if user is admin or owner
  const navItems = [
    { name: "Dashboard", icon: LayoutDashboard, path: "/employer" },
    { name: "Profile", icon: User, path: "/employer/profile" },
    { name: "Employees", icon: Users, path: "/employer/employees" },
    ...(isAdmin || isOwner
      ? [{ name: "Employers", icon: Briefcase, path: "/employer/employers" }]
      : []),
    { name: "Training", icon: FileText, path: "/employer/training" },
    { name: "Payroll", icon: DollarSign, path: "/employer/payroll" },
    { name: "Company", icon: Building2, path: "/employer/company" },
  ];

  const isActive = (path: string) => {
    if (path === "/employer") {
      return location.pathname === "/employer";
    }
    return location.pathname.startsWith(path);
  };

  return (
    <div className="flex h-screen bg-white overflow-hidden">
      {/* SIDEBAR */}
      <aside className="w-44 flex-shrink-0 flex flex-col bg-gray-50 border-r border-gray-200 shadow-[1px_0_2px_0_rgba(0,0,0,0.05)] z-10">
        {/* LOGO / HEADER */}
        <div className="h-16 flex items-center justify-center border-b border-gray-200">
          <span className="font-bold text-xl text-gray-800 tracking-tight">
            Your Company
          </span>
        </div>

        {/* NAVIGATION ITEMS */}
        <nav className="flex-1 overflow-y-auto py-6 px-3 space-y-1">
          {navItems.map((item) => {
            const active = isActive(item.path);
            const Icon = item.icon;

            return (
              <Link
                key={item.name}
                to={item.path}
                className={`
                  w-full flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium transition-all duration-200
                  ${
                    active
                      ? "bg-blue-50 text-blue-700"
                      : "text-gray-600 hover:bg-gray-200 hover:text-gray-900"
                  }
                `}
              >
                <Icon
                  size={20}
                  className={active ? "text-blue-600" : "text-gray-500"}
                />
                {item.name}
              </Link>
            );
          })}
        </nav>

        {/* USER SECTION (FOOTER) */}
        <div className="border-t border-gray-200 p-4 bg-gray-50">
          <div className="mb-3 px-1">
            <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">
              Signed in as
            </p>
            {!loading && (
              <>
                <p className="text-sm text-gray-900 font-semibold truncate mb-1">
                  {userInfo.name}
                </p>
                <p className="text-xs text-gray-600 truncate mb-2">
                  {userInfo.email}
                </p>
                <div className="flex items-center gap-1 flex-wrap">
                  {isOwner && (
                    <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-yellow-100 text-yellow-800 text-xs font-semibold rounded-full">
                      <Crown size={10} />
                      OWNER
                    </span>
                  )}
                  {isAdmin && !isOwner && (
                    <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-blue-100 text-blue-800 text-xs font-semibold rounded-full">
                      <Shield size={10} />
                      ADMIN
                    </span>
                  )}
                  {!isAdmin && !isOwner && (
                    <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-gray-100 text-gray-700 text-xs font-medium rounded-full">
                      EMPLOYER
                    </span>
                  )}
                </div>
              </>
            )}
            {loading && (
              <div className="animate-pulse">
                <div className="h-4 bg-gray-200 rounded mb-2"></div>
                <div className="h-3 bg-gray-200 rounded w-2/3"></div>
              </div>
            )}
          </div>

          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 bg-gray-200 hover:bg-gray-300 text-gray-700 py-2 px-4 rounded-md transition-colors text-sm font-medium"
          >
            <LogOut size={16} />
            Logout
          </button>
        </div>
      </aside>

      {/* MAIN CONTENT AREA */}
      <main className="flex-1 overflow-y-auto bg-white scroll-smooth relative">
          <Outlet />
      </main>
    </div>
  );
}

