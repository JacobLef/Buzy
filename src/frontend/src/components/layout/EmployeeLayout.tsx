import { Outlet, Link, useNavigate, useLocation } from "react-router-dom";
import {
  LayoutDashboard,
  User,
  FileText,
  DollarSign,
  LogOut,
} from "lucide-react";

export default function EmployeeLayout() {
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  };

  // Get user email from localStorage
  const getUserEmail = () => {
    const userStr = localStorage.getItem("user");
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        return user.email || "employee@company.com";
      } catch {
        return "employee@company.com";
      }
    }
    return "employee@company.com";
  };

  const navItems = [
    { name: "Dashboard", icon: LayoutDashboard, path: "/employee" },
    { name: "Profile", icon: User, path: "/employee/profile" },
    { name: "Training", icon: FileText, path: "/employee/training" },
    { name: "Payroll", icon: DollarSign, path: "/employee/payroll" },
  ];

  const isActive = (path: string) => {
    if (path === "/employee") {
      return location.pathname === "/employee";
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
                Employee Portal
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
            <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">
              Signed in as
            </p>
            <p className="text-sm text-gray-700 font-medium truncate">
              {getUserEmail()}
            </p>
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
