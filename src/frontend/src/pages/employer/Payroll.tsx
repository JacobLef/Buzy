import { useState, useEffect, useCallback, useRef } from "react";
import {
  Calculator,
  DollarSign,
  FileText,
  Download,
  Settings,
  CheckCircle,
  AlertCircle,
  Search,
  Filter,
  Trash2,
  X,
} from "lucide-react";
import type { Paycheck, DistributeBonusRequest } from "../../types/payroll";
import type { Employee } from "../../types/employee";
import {
  previewPayroll,
  calculatePayroll,
  distributeBonuses,
  getCurrentTaxStrategy,
  getAvailableTaxStrategies,
  switchTaxStrategy,
  getPayrollHistory,
  deletePaycheck,
  updatePaycheckStatus,
} from "../../api/payroll";
import type { PaycheckStatus } from "../../types/payroll";
import { getEmployeesByBusiness } from "../../api/employees";
import { getEmployer } from "../../api/employers";

export default function PayrollManagement() {
  // --- STATE ---
  const [activeTab, setActiveTab] = useState<"calculate" | "bonus">("calculate");
  const [taxStrategy, setTaxStrategy] = useState<string>("");
  const [availableStrategies, setAvailableStrategies] = useState<Record<string, string>>({});
  const [showStrategyDropdown, setShowStrategyDropdown] = useState(false);
  const [notification, setNotification] = useState<{
    type: "success" | "error";
    msg: string;
  } | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  // Employees and Business
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [businessId, setBusinessId] = useState<number | null>(null);
  const [isLoadingEmployees, setIsLoadingEmployees] = useState(false);

  // Section A State (Calculate)
  const [selectedEmpId, setSelectedEmpId] = useState<string>("");
  const [additionalPay, setAdditionalPay] = useState<string>("");
  const [calculatedPreview, setCalculatedPreview] = useState<Paycheck | null>(
    null
  );

  // Section B State (Bonus)
  const [bonusAmount, setBonusAmount] = useState<string>("1000");
  const [deptFilter, setDeptFilter] = useState<string>("All");
  const [selectedBonusEmps, setSelectedBonusEmps] = useState<number[]>([]);

  // History State
  const [history, setHistory] = useState<Paycheck[]>([]);
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [isLoadingHistory, setIsLoadingHistory] = useState(false);

  // Ref for dropdown click outside detection
  const strategyDropdownRef = useRef<HTMLDivElement>(null);

  // --- INITIALIZATION ---
  const showNotification = useCallback((type: "success" | "error", msg: string) => {
    setNotification({ type, msg });
    setTimeout(() => setNotification(null), 3000);
  }, []);

  const loadTaxStrategy = useCallback(async () => {
    try {
      const [currentResponse, availableResponse] = await Promise.all([
        getCurrentTaxStrategy(),
        getAvailableTaxStrategies(),
      ]);
      
      const currentStrategy = currentResponse.data || "Flat Tax Strategy";
      setTaxStrategy(currentStrategy);
      
      // Set available strategies or use defaults if API fails
      const strategies = availableResponse.data;
      console.log("Available strategies from API:", strategies);
      
      if (strategies && Object.keys(strategies).length > 0) {
        setAvailableStrategies(strategies);
      } else {
        // Fallback to default strategies if API returns empty
        console.warn("API returned empty strategies, using defaults");
        setAvailableStrategies({
          flatTaxStrategy: "Flat Tax Strategy",
          progressiveTaxStrategy: "Progressive Tax Strategy",
        });
      }
    } catch (error) {
      console.error("Failed to load tax strategy:", error);
      setTaxStrategy("Flat Tax Strategy");
      // Set default strategies on error
      setAvailableStrategies({
        flatTaxStrategy: "Flat Tax Strategy",
        progressiveTaxStrategy: "Progressive Tax Strategy",
      });
    }
  }, []);

  const handleSwitchStrategy = async (strategyKey: string) => {
    setIsLoading(true);
    try {
      const response = await switchTaxStrategy(strategyKey);
      setTaxStrategy(response.data.strategy);
      setShowStrategyDropdown(false);
      showNotification("success", `Tax strategy switched to ${response.data.strategy}`);
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { error?: string } } };
      const errorMsg =
        axiosError.response?.data?.error ||
        "Failed to switch tax strategy. Please try again.";
      showNotification("error", errorMsg);
    } finally {
      setIsLoading(false);
    }
  };

  const loadUserBusiness = useCallback(async () => {
    try {
      const userStr = localStorage.getItem("user");
      if (!userStr) return;

      const user = JSON.parse(userStr);
      if (user.role === "EMPLOYER" && user.businessPersonId) {
        const response = await getEmployer(user.businessPersonId);
        const employer = response.data;
        setBusinessId(employer.companyId);
      }
    } catch (error) {
      console.error("Failed to load user business:", error);
      // Can't use showNotification here due to circular dependency, use setNotification directly
      setNotification({ type: "error", msg: "Failed to load business information" });
      setTimeout(() => setNotification(null), 3000);
    }
  }, []);

  const loadEmployees = useCallback(async () => {
    if (!businessId) return;

    setIsLoadingEmployees(true);
    try {
      const response = await getEmployeesByBusiness(businessId);
      setEmployees(response.data);
    } catch (error) {
      console.error("Failed to load employees:", error);
      // Can't use showNotification here due to circular dependency, use setNotification directly
      setNotification({ type: "error", msg: "Failed to load employees" });
      setTimeout(() => setNotification(null), 3000);
    } finally {
      setIsLoadingEmployees(false);
    }
  }, [businessId]);

  const loadAllPayrollHistory = useCallback(async () => {
    setIsLoadingHistory(true);
    try {
      const allHistory: Paycheck[] = [];
      for (const emp of employees) {
        try {
          const response = await getPayrollHistory(emp.id);
          allHistory.push(...response.data);
        } catch (error) {
          console.error(`Failed to load history for employee ${emp.id}:`, error);
        }
      }
      // Sort by date descending
      allHistory.sort(
        (a, b) =>
          new Date(b.payDate).getTime() - new Date(a.payDate).getTime()
      );
      setHistory(allHistory);
    } catch (error) {
      console.error("Failed to load payroll history:", error);
    } finally {
      setIsLoadingHistory(false);
    }
  }, [employees]);

  useEffect(() => {
    loadTaxStrategy();
    loadUserBusiness();
  }, [loadTaxStrategy, loadUserBusiness]);

  useEffect(() => {
    if (businessId) {
      loadEmployees();
    }
  }, [businessId, loadEmployees]);

  useEffect(() => {
    if (employees.length > 0) {
      loadAllPayrollHistory();
    }
  }, [employees, loadAllPayrollHistory]);

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

  // --- HANDLERS ---
  const handleCalculatePreview = async () => {
    if (!selectedEmpId) return;

    setIsLoading(true);
    try {
      const additionalPayValue =
        additionalPay && Number(additionalPay) > 0
          ? Number(additionalPay)
          : undefined;

      // Use preview endpoint - does not save to database
      const response = await previewPayroll(
        Number(selectedEmpId),
        additionalPayValue
      );
      setCalculatedPreview(response.data);
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      const errorMsg =
        axiosError.response?.data?.message ||
        "Failed to calculate payroll preview. Please try again.";
      showNotification("error", errorMsg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleGeneratePaycheck = async () => {
    if (!calculatedPreview || !selectedEmpId) return;

    setIsLoading(true);
    try {
      const additionalPayValue =
        additionalPay && Number(additionalPay) > 0
          ? Number(additionalPay)
          : undefined;

      // Now actually create and save the paycheck
      await calculatePayroll(Number(selectedEmpId), additionalPayValue);
      
      showNotification("success", "Paycheck generated successfully");
      setCalculatedPreview(null);
      setSelectedEmpId("");
      setAdditionalPay("");
      await loadAllPayrollHistory();
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      const errorMsg =
        axiosError.response?.data?.message ||
        "Failed to generate paycheck. Please try again.";
      showNotification("error", errorMsg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDistributeBonus = async () => {
    if (!businessId) {
      showNotification("error", "Business information not available");
      return;
    }

    const amount = Number(bonusAmount);
    if (!amount || selectedBonusEmps.length === 0) {
      showNotification(
        "error",
        "Please select employees and enter an amount"
      );
      return;
    }

    setIsLoading(true);
    try {
      const request: DistributeBonusRequest = {
        businessId: businessId,
        bonusAmount: amount,
        employeeIds: selectedBonusEmps,
        department: deptFilter !== "All" ? deptFilter : undefined,
      };

      const response = await distributeBonuses(request);
      const result = response.data;

      showNotification(
        "success",
        `Bonuses distributed to ${result.successfulPaychecks} employees`
      );

      setSelectedBonusEmps([]);
      setBonusAmount("1000");
      await loadAllPayrollHistory();
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      const errorMsg =
        axiosError.response?.data?.message ||
        "Failed to distribute bonuses. Please try again.";
      showNotification("error", errorMsg);
    } finally {
      setIsLoading(false);
    }
  };


  // Helper for Section B filtering
  const filteredEmployees = employees.filter(
    (e) => deptFilter === "All" || e.position === deptFilter
  );

  // Get unique departments from employees
  const departments = Array.from(
    new Set(employees.map((e) => e.position).filter(Boolean))
  );

  const toggleEmployeeSelection = (id: number) => {
    if (selectedBonusEmps.includes(id)) {
      setSelectedBonusEmps(selectedBonusEmps.filter((e) => e !== id));
    } else {
      setSelectedBonusEmps([...selectedBonusEmps, id]);
    }
  };

  // Filter history by search query
  const filteredHistory = history.filter((item) =>
    item.employeeName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleDeletePaycheck = async (paycheckId: number) => {
    if (!window.confirm("Are you sure you want to delete this paycheck? This action cannot be undone.")) {
      return;
    }

    setIsLoading(true);
    try {
      await deletePaycheck(paycheckId);
      showNotification("success", "Paycheck deleted successfully");
      await loadAllPayrollHistory();
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      const errorMsg =
        axiosError.response?.data?.message ||
        "Failed to delete paycheck. Please try again.";
      showNotification("error", errorMsg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdateStatus = async (paycheckId: number, newStatus: PaycheckStatus) => {
    setIsLoading(true);
    try {
      await updatePaycheckStatus(paycheckId, newStatus);
      showNotification("success", `Paycheck status updated to ${newStatus}`);
      await loadAllPayrollHistory();
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      const errorMsg =
        axiosError.response?.data?.message ||
        "Failed to update paycheck status. Please try again.";
      showNotification("error", errorMsg);
    } finally {
      setIsLoading(false);
    }
  };

  const getStatusBadgeColor = (status: PaycheckStatus) => {
    switch (status) {
      case "DRAFT":
        return "bg-gray-100 text-gray-800";
      case "PENDING":
        return "bg-yellow-100 text-yellow-800";
      case "PAID":
        return "bg-green-100 text-green-800";
      case "VOIDED":
        return "bg-red-100 text-red-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  return (
    <div className="space-y-6">
      {/* --- PAGE HEADER & SETTINGS --- */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Payroll Operations</h1>
          <p className="text-sm text-gray-500">
            Manage salaries, bonuses, and tax configurations.
          </p>
        </div>

        {/* Tax Strategy Setting */}
        <div className="relative" ref={strategyDropdownRef}>
          <div
            onClick={() => setShowStrategyDropdown(!showStrategyDropdown)}
            className="flex items-center gap-3 bg-white p-2 rounded-lg border border-gray-200 shadow-sm cursor-pointer hover:border-blue-300 hover:shadow-md transition-all"
          >
            <div className="p-2 bg-blue-50 rounded-md">
              <Settings size={18} className="text-blue-600" />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-semibold text-gray-500 uppercase">
                Tax Strategy
              </span>
              <span className="text-sm font-medium text-gray-900">
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
            <div className="absolute right-0 mt-2 w-64 bg-white rounded-lg shadow-lg border border-gray-200 z-50">
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
                      onClick={() => handleSwitchStrategy(key)}
                      disabled={isLoading || taxStrategy === name}
                      className={`w-full text-left px-3 py-2 rounded-md text-sm transition-colors ${
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

      {/* --- NOTIFICATION TOAST --- */}
      {notification && (
        <div
          className={`fixed top-4 right-4 z-50 p-4 rounded-lg shadow-lg flex items-center gap-3 animate-in slide-in-from-right ${
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

      {/* --- MAIN ACTION AREA --- */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
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
          {/* SECTION A: CALCULATE PAYROLL */}
          {activeTab === "calculate" && (
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              <div className="space-y-4">
                <h3 className="text-lg font-semibold text-gray-800">
                  1. Paycheck Details
                </h3>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Select Employee
                  </label>
                  <select
                    value={selectedEmpId}
                    onChange={(e) => setSelectedEmpId(e.target.value)}
                    disabled={isLoadingEmployees || isLoading}
                    className="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2 border disabled:opacity-50"
                  >
                    <option value="">-- Choose Employee --</option>
                    {employees.map((emp) => (
                      <option key={emp.id} value={emp.id}>
                        {emp.name} ({emp.position})
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Additional Pay / Bonus (Optional)
                  </label>
                  <div className="relative rounded-md shadow-sm">
                    <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 z-10">
                      <span className="text-gray-500 sm:text-sm">$</span>
                    </div>
                    <input
                      type="number"
                      className="block w-full rounded-md border-gray-300 pr-2 focus:border-blue-500 focus:ring-blue-500 sm:text-sm py-2 border"
                      style={{ paddingLeft: "20px" }}
                      placeholder="0.00"
                      value={additionalPay}
                      onChange={(e) => setAdditionalPay(e.target.value)}
                      disabled={isLoading}
                    />
                  </div>
                </div>
                <button
                  onClick={handleCalculatePreview}
                  disabled={!selectedEmpId || isLoading}
                  className="w-full bg-gray-900 text-white py-2 px-4 rounded-md hover:bg-gray-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {isLoading ? "Calculating..." : "Preview Calculation"}
                </button>
              </div>
              {/* Preview Card */}
              <div className="bg-gray-50 rounded-lg border border-gray-200 p-6 flex flex-col justify-between">
                {calculatedPreview ? (
                  <>
                    <div>
                      <div className="flex justify-between items-start mb-4">
                        <div>
                          <p className="text-sm text-gray-500">Preview for</p>
                          <h3 className="text-lg font-bold text-gray-900">
                            {calculatedPreview.employeeName}
                          </h3>
                        </div>
                        <span className="px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded-full font-medium">
                          {calculatedPreview.taxStrategyUsed}
                        </span>
                      </div>

                      <div className="space-y-2 text-sm">
                        <div className="flex justify-between text-gray-600">
                          <span>Base Salary</span>
                          <span>
                            ${calculatedPreview.grossPay.toLocaleString()}
                          </span>
                        </div>
                        {calculatedPreview.bonus && calculatedPreview.bonus > 0 && (
                          <div className="flex justify-between text-blue-600">
                            <span>Bonus / Additional Pay</span>
                            <span>
                              +${calculatedPreview.bonus.toLocaleString()}
                            </span>
                          </div>
                        )}
                        <div className="flex justify-between text-gray-700 font-medium pt-2 border-t border-gray-200">
                          <span>Gross Pay</span>
                          <span>
                            ${(calculatedPreview.grossPay + (calculatedPreview.bonus || 0)).toLocaleString()}
                          </span>
                        </div>
                        <div className="flex justify-between text-red-600">
                          <span>
                            Tax (
                            {(
                              (calculatedPreview.taxDeduction /
                                (calculatedPreview.grossPay + (calculatedPreview.bonus || 0))) *
                              100
                            ).toFixed(1)}
                            %)
                          </span>
                          <span>
                            -${calculatedPreview.taxDeduction.toLocaleString()}
                          </span>
                        </div>
                        <div className="flex justify-between text-red-600">
                          <span>Insurance</span>
                          <span>
                            -$
                            {calculatedPreview.insuranceDeduction.toLocaleString()}
                          </span>
                        </div>
                        <div className="pt-3 border-t border-gray-200 flex justify-between items-center mt-2">
                          <span className="font-bold text-gray-900">
                            Net Pay
                          </span>
                          <span className="font-bold text-green-600 text-lg">
                            ${calculatedPreview.netPay.toLocaleString()}
                          </span>
                        </div>
                      </div>
                    </div>

                    <button
                      onClick={handleGeneratePaycheck}
                      disabled={isLoading}
                      className="w-full mt-6 bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 flex items-center justify-center gap-2 disabled:opacity-50"
                    >
                      <CheckCircle size={18} />
                      Confirm & Generate Paycheck
                    </button>
                  </>
                ) : (
                  <div className="h-full flex flex-col items-center justify-center text-gray-400">
                    <Calculator size={48} className="mb-2 opacity-20" />
                    <p className="text-sm">
                      Select an employee and click Calculate to view breakdown.
                    </p>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* SECTION B: DISTRIBUTE BONUSES */}
          {activeTab === "bonus" && (
            <div className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Bonus Amount (Per Person)
                  </label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                      <span className="text-gray-500 sm:text-sm">$</span>
                    </div>
                    <input
                      type="number"
                      value={bonusAmount}
                      onChange={(e) => setBonusAmount(e.target.value)}
                      disabled={isLoading}
                      className="block w-full rounded-md border-gray-300 border p-2 pr-2 focus:ring-blue-500 focus:border-blue-500 disabled:opacity-50"
                      style={{ paddingLeft: "20px" }}
                    />
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Filter Department
                  </label>
                  <div className="relative">
                    <Filter className="absolute left-3 top-2.5 text-gray-400" size={16} />
                    <select
                      value={deptFilter}
                      onChange={(e) => setDeptFilter(e.target.value)}
                      disabled={isLoading}
                      className="block w-full rounded-md border-gray-300 border p-2 pr-2 focus:ring-blue-500 focus:border-blue-500 disabled:opacity-50"
                      style={{ paddingLeft: "36px" }}
                    >
                      <option value="All">All Departments</option>
                      {departments.map((dept) => (
                        <option key={dept} value={dept}>
                          {dept}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
                <div className="flex items-end">
                  <div className="bg-blue-50 text-blue-800 px-4 py-2 rounded-md w-full text-center text-sm font-medium">
                    Est. Total Cost: $
                    {(selectedBonusEmps.length * Number(bonusAmount)).toLocaleString()}
                  </div>
                </div>
              </div>
              <div className="border rounded-lg border-gray-200 overflow-hidden">
                <div className="bg-gray-50 px-4 py-2 border-b border-gray-200 flex justify-between items-center">
                  <span className="text-sm font-medium text-gray-700">
                    Select Employees ({selectedBonusEmps.length} selected)
                  </span>
                  <button
                    onClick={() =>
                      setSelectedBonusEmps(filteredEmployees.map((e) => e.id))
                    }
                    disabled={isLoading}
                    className="text-xs text-blue-600 hover:text-blue-800 font-medium disabled:opacity-50"
                  >
                    Select All Visible
                  </button>
                </div>
                <div className="max-h-60 overflow-y-auto">
                  {isLoadingEmployees ? (
                    <div className="p-4 text-center text-sm text-gray-500">
                      Loading employees...
                    </div>
                  ) : filteredEmployees.length === 0 ? (
                    <div className="p-4 text-center text-sm text-gray-500">
                      No employees found
                    </div>
                  ) : (
                    filteredEmployees.map((emp) => (
                      <div
                        key={emp.id}
                        onClick={() => toggleEmployeeSelection(emp.id)}
                        className={`flex items-center justify-between p-3 border-b border-gray-100 last:border-0 cursor-pointer hover:bg-gray-50 transition-colors ${
                          selectedBonusEmps.includes(emp.id)
                            ? "bg-blue-50/30"
                            : ""
                        }`}
                      >
                        <div className="flex items-center gap-3">
                          <input
                            type="checkbox"
                            checked={selectedBonusEmps.includes(emp.id)}
                            readOnly
                            className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                          />
                          <div>
                            <p className="text-sm font-medium text-gray-900">
                              {emp.name}
                            </p>
                            <p className="text-xs text-gray-500">
                              {emp.position}
                            </p>
                          </div>
                        </div>
                        <span className="text-xs text-gray-400">ID: {emp.id}</span>
                      </div>
                    ))
                  )}
                </div>
              </div>
              <div className="flex justify-end">
                <button
                  onClick={handleDistributeBonus}
                  disabled={selectedBonusEmps.length === 0 || isLoading}
                  className="bg-green-600 text-white px-6 py-2 rounded-md hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                >
                  <DollarSign size={18} />
                  Distribute Bonuses
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* --- SECTION C: HISTORY TABLE --- */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200">
        <div className="px-6 py-4 border-b border-gray-200 flex flex-col sm:flex-row justify-between items-center gap-4">
          <h2 className="text-lg font-bold text-gray-900 flex items-center gap-2">
            <FileText size={20} className="text-gray-500" />
            Payroll History
          </h2>
          <div className="flex gap-2 w-full sm:w-auto">
            <div className="relative flex-1 sm:w-64">
              <Search className="absolute left-3 top-2.5 text-gray-400" size={16} />
              <input
                type="text"
                placeholder="Search employee..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pr-4 py-2 border border-gray-300 rounded-md text-sm focus:ring-blue-500 focus:border-blue-500"
                style={{ paddingLeft: "36px" }}
              />
            </div>
            <button className="flex items-center gap-2 px-3 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 text-sm font-medium">
              <Download size={16} />
              Export
            </button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Date
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Employee
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Gross Pay
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Deductions
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Net Pay
                </th>
                <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {isLoadingHistory ? (
                <tr>
                  <td colSpan={7} className="px-6 py-10 text-center text-sm text-gray-500">
                    Loading history...
                  </td>
                </tr>
              ) : filteredHistory.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-6 py-10 text-center text-sm text-gray-500">
                    No payroll history found.
                  </td>
                </tr>
              ) : (
                filteredHistory.map((item) => (
                  <tr key={item.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {item.payDate}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">
                        {item.employeeName}
                      </div>
                      <div className="text-xs text-gray-500">ID: {item.employeeId}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-right text-gray-900 font-mono">
                      ${item.grossPay.toLocaleString()}
                      {item.bonus && item.bonus > 0 && (
                        <div className="text-xs text-blue-600">+${item.bonus.toLocaleString()} bonus</div>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-right text-red-600 font-mono">
                      -${item.totalDeductions.toLocaleString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-right text-green-600 font-bold font-mono">
                      ${item.netPay.toLocaleString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-center">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusBadgeColor(item.status || "DRAFT")}`}>
                        {item.status || "DRAFT"}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-center">
                      <div className="flex items-center justify-center gap-1">
                        {(() => {
                          const status = item.status || "DRAFT";
                          
                          switch (status) {
                            case "DRAFT":
                              return (
                                <>
                                  <button
                                    onClick={() => handleUpdateStatus(item.id, "PAID")}
                                    disabled={isLoading}
                                    className="p-2 text-green-600 hover:bg-green-50 rounded-md border border-green-200 hover:border-green-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                    title="Mark as Paid"
                                  >
                                    <CheckCircle size={18} />
                                  </button>
                                  <button
                                    onClick={() => handleDeletePaycheck(item.id)}
                                    disabled={isLoading}
                                    className="p-2 text-red-600 hover:bg-red-50 rounded-md border border-red-200 hover:border-red-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                    title="Delete Paycheck"
                                  >
                                    <Trash2 size={18} />
                                  </button>
                                </>
                              );
                            case "PENDING":
                              return (
                                <button
                                  onClick={() => handleUpdateStatus(item.id, "PAID")}
                                  disabled={isLoading}
                                  className="p-2 text-green-600 hover:bg-green-50 rounded-md border border-green-200 hover:border-green-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                  title="Mark as Paid"
                                >
                                  <CheckCircle size={18} />
                                </button>
                              );
                            case "PAID":
                              return (
                                <button
                                  onClick={() => handleUpdateStatus(item.id, "VOIDED")}
                                  disabled={isLoading}
                                  className="p-2 text-red-600 hover:bg-red-50 rounded-md border border-red-200 hover:border-red-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                  title="Void Paycheck"
                                >
                                  <X size={18} />
                                </button>
                              );
                            case "VOIDED":
                              return (
                                <span className="text-xs text-gray-400 italic px-2">
                                  Locked
                                </span>
                              );
                            default:
                              // Fallback for unknown status - treat as DRAFT
                              return (
                                <>
                                  <button
                                    onClick={() => handleUpdateStatus(item.id, "PAID")}
                                    disabled={isLoading}
                                    className="p-2 text-green-600 hover:bg-green-50 rounded-md border border-green-200 hover:border-green-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                    title="Mark as Paid"
                                  >
                                    <CheckCircle size={18} />
                                  </button>
                                  <button
                                    onClick={() => handleDeletePaycheck(item.id)}
                                    disabled={isLoading}
                                    className="p-2 text-red-600 hover:bg-red-50 rounded-md border border-red-200 hover:border-red-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                    title="Delete Paycheck"
                                  >
                                    <Trash2 size={18} />
                                  </button>
                                </>
                              );
                          }
                        })()}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
