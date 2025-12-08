import { Navigate, Outlet } from "react-router-dom";
import { useState, useEffect } from "react";
import { authStorage } from "../../utils/authStorage";
import { getEmployer } from "../../api/employers";

export const AdminOnlyRoute = () => {
  const [isAuthorized, setIsAuthorized] = useState<boolean | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAdminAccess = async () => {
      try {
        const user = authStorage.getUser();
        if (!user || user.role !== "EMPLOYER" || !user.businessPersonId) {
          setIsAuthorized(false);
          return;
        }

        const response = await getEmployer(user.businessPersonId);
        const employer = response.data;
        const hasAccess = employer.isAdmin === true || employer.isOwner === true;
        setIsAuthorized(hasAccess);
      } catch (error) {
        console.error("Failed to check admin access:", error);
        setIsAuthorized(false);
      } finally {
        setLoading(false);
      }
    };

    checkAdminAccess();
  }, []);

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!isAuthorized) {
    return <Navigate to="/employer" replace />;
  }

  return <Outlet />;
};

