import { useState, useEffect, useRef } from "react";
import { useSearchParams } from "react-router-dom";
import { Calculator, DollarSign, Settings, CheckCircle, AlertCircle } from "lucide-react";
import { usePayroll } from "../../hooks/usePayroll";
import { PayrollCalculator } from "../../components/payroll/PayrollCalculator";
import { BonusDistribution } from "../../components/payroll/BonusDistribution";
import { PayrollHistory } from "../../components/payroll/PayrollHistory";
import type { DistributeBonusRequest } from "../../types/payroll";

export default function PayrollManagement() {
  const [searchParams] = useSearchParams();
  const [activeTab, setActiveTab] = useState<"calculate" | "bonus">("calculate");
  const [businessId, setBusinessId] = useState<number | null>(null);
  const [showStrategyDropdown, setShowStrategyDropdown] = useState(false);
  const strategyDropdownRef = useRef<HTMLDivElement>(null);
  
  // Get employeeId from URL query params
  const employeeIdFromUrl = searchParams.get('employeeId');

  const {
    taxStrategy,
    availableStrategies,
    employees,
    history,
    isLoading,
    isLoadingStrategy,
    isLoadingEmployees,
    isLoadingHistory,
    notification,
    loadUserBusiness,
    handleSwitchStrategy,
    calculatePreview,
    generatePaycheck,
    distributeBonus,
    handleDeletePaycheck,
    handleUpdateStatus,
  } = usePayroll(businessId);

  // Load business ID on mount
  useEffect(() => {
    const loadBusiness = async () => {
      const id = await loadUserBusiness();
      setBusinessId(id);
    };
    loadBusiness();
  }, [loadUserBusiness]);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        strategyDropdownRef.current &&
        !strategyDropdownRef.current.contains(event.target as Node)
      ) {
        setShowStrategyDropdown(false);
      }
    };

    if (showStrategyDropdown) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showStrategyDropdown]);

  const handleDistributeBonus = async (request: DistributeBonusRequest) => {
    return await distributeBonus(request);
  };

  return (
    <div className="space-y-8 max-w-7xl mx-auto px-4 py-6">
      {/* Page Header */}
      <div className="flex flex-col md:flex-row justify-between md:items-end gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Payroll Management</h1>
          <p className="text-gray-500 mt-2">
            Manage employee compensation, bonuses, and tax history.
          </p>
        </div>

        {/* Tax Strategy Setting */}
        <div className="relative" ref={strategyDropdownRef}>
          <div
            onClick={() => setShowStrategyDropdown(!showStrategyDropdown)}
            className="flex items-center gap-3 bg-white p-3 rounded-xl border border-gray-200 shadow-sm cursor-pointer hover:border-blue-300 hover:shadow-md transition-all"
          >
            <div className="p-2 bg-blue-50 rounded-lg">
              <Settings size={18} className="text-blue-600" />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-semibold text-gray-500 uppercase">
                Tax Strategy
              </span>
              <span className="text-sm font-medium text-slate-900">
                {taxStrategy || "Loading..."}
              </span>
            </div>
            <div className="ml-auto">
              <svg
                className={`w-4 h-4 text-gray-500 transition-transform ${
                  showStrategyDropdown ? "rotate-180" : ""
                }`}
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M19 9l-7 7-7-7"
                />
              </svg>
            </div>
          </div>

          {/* Dropdown Menu */}
          {showStrategyDropdown && (
            <div className="absolute right-0 mt-2 w-64 bg-white rounded-xl shadow-lg border border-gray-200 z-50">
              <div className="p-2">
                <div className="text-xs font-semibold text-gray-500 uppercase px-2 py-1 mb-1">
                  Select Strategy
                </div>
                {Object.keys(availableStrategies).length === 0 ? (
                  <div className="px-3 py-2 text-sm text-gray-500">
                    Loading strategies...
                  </div>
                ) : (
                  Object.entries(availableStrategies).map(([key, name]) => (
                    <button
                      key={key}
                      onClick={() => {
                        handleSwitchStrategy(key);
                        setShowStrategyDropdown(false);
                      }}
                      disabled={isLoadingStrategy || taxStrategy === name}
                      className={`w-full text-left px-3 py-2 rounded-lg text-sm transition-colors ${
                        taxStrategy === name
                          ? "bg-blue-50 text-blue-700 font-medium"
                          : "text-gray-700 hover:bg-gray-50"
                      } disabled:opacity-50 disabled:cursor-not-allowed`}
                    >
                      <div className="flex items-center justify-between">
                        <span>{name}</span>
                        {taxStrategy === name && (
                          <CheckCircle size={16} className="text-blue-600" />
                        )}
                      </div>
                    </button>
                  ))
                )}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Notification Toast */}
      {notification && (
        <div
          className={`fixed top-4 right-4 z-50 p-4 rounded-xl shadow-lg flex items-center gap-3 ${
            notification.type === "success"
              ? "bg-green-50 text-green-800 border border-green-200"
              : "bg-red-50 text-red-800 border border-red-200"
          }`}
        >
          {notification.type === "success" ? (
            <CheckCircle size={20} />
          ) : (
            <AlertCircle size={20} />
          )}
          <p className="font-medium">{notification.msg}</p>
        </div>
      )}

      {/* Main Action Area */}
      <div 
        className="bg-white rounded-xl border border-gray-200 overflow-hidden"
        style={{ boxShadow: '0 4px 20px -2px rgba(0, 0, 0, 0.05)' }}
      >
        {/* Tabs */}
        <div className="flex border-b border-gray-200">
          <button
            onClick={() => setActiveTab("calculate")}
            className={`flex-1 py-4 text-sm font-medium text-center flex items-center justify-center gap-2 transition-colors ${
              activeTab === "calculate"
                ? "text-blue-600 border-b-2 border-blue-600 bg-blue-50/50"
                : "text-gray-500 hover:text-gray-700 hover:bg-gray-50"
            }`}
          >
            <Calculator size={18} />
            Calculate Individual Payroll
          </button>
          <button
            onClick={() => setActiveTab("bonus")}
            className={`flex-1 py-4 text-sm font-medium text-center flex items-center justify-center gap-2 transition-colors ${
              activeTab === "bonus"
                ? "text-blue-600 border-b-2 border-blue-600 bg-blue-50/50"
                : "text-gray-500 hover:text-gray-700 hover:bg-gray-50"
            }`}
          >
            <DollarSign size={18} />
            Distribute Bulk Bonuses
          </button>
        </div>

        <div className="p-6">
          {/* Dynamic Content Area */}
          <div className="animate-in fade-in slide-in-from-bottom-4 duration-500">
            {activeTab === "calculate" ? (
              <PayrollCalculator
                employees={employees}
                onCalculate={calculatePreview}
                onGenerate={generatePaycheck}
                isLoading={isLoading || isLoadingEmployees}
                preselectedEmployeeId={employeeIdFromUrl ? Number(employeeIdFromUrl) : undefined}
              />
            ) : (
              <BonusDistribution
                employees={employees}
                businessId={businessId}
                onDistribute={handleDistributeBonus}
                isLoading={isLoading || isLoadingEmployees}
              />
            )}
          </div>
        </div>
      </div>

      {/* History Table Section */}
      <PayrollHistory
        history={history}
        isLoading={isLoadingHistory}
        onDelete={handleDeletePaycheck}
        onUpdateStatus={handleUpdateStatus}
      />
    </div>
  );
}
