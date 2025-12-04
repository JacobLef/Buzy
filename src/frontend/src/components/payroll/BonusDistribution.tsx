import React, { useState, useMemo } from 'react';
import { Card, CardHeader } from '../ui/Card';
import { Button } from '../ui/Button';
import { DollarSign, Filter } from 'lucide-react';
import type { Employee } from '../../types/employee';
import type { DistributeBonusRequest } from '../../types/payroll';

interface BonusDistributionProps {
  employees: Employee[];
  businessId: number | null;
  onDistribute: (request: DistributeBonusRequest) => Promise<any>;
  isLoading: boolean;
}

export const BonusDistribution = ({ 
  employees, 
  businessId, 
  onDistribute, 
  isLoading 
}: BonusDistributionProps) => {
  const [bonusAmount, setBonusAmount] = useState<string>("1000");
  const [deptFilter, setDeptFilter] = useState<string>("All");
  const [selectedBonusEmps, setSelectedBonusEmps] = useState<number[]>([]);

  // Get unique departments from employees
  const departments = useMemo(() => {
    return Array.from(
      new Set(employees.map((e) => e.position).filter(Boolean))
    );
  }, [employees]);

  // Filter employees by department
  const filteredEmployees = useMemo(() => {
    return employees.filter(
      (e) => deptFilter === "All" || e.position === deptFilter
    );
  }, [employees, deptFilter]);

  const toggleEmployeeSelection = (id: number) => {
    if (selectedBonusEmps.includes(id)) {
      setSelectedBonusEmps(selectedBonusEmps.filter((e) => e !== id));
    } else {
      setSelectedBonusEmps([...selectedBonusEmps, id]);
    }
  };

  const handleSelectAll = () => {
    setSelectedBonusEmps(filteredEmployees.map((e) => e.id));
  };

  const handleDistribute = async () => {
    if (!businessId) {
      return;
    }

    const amount = Number(bonusAmount);
    if (!amount || selectedBonusEmps.length === 0) {
      return;
    }

    const request: DistributeBonusRequest = {
      businessId: businessId,
      bonusAmount: amount,
      employeeIds: selectedBonusEmps,
      department: deptFilter !== "All" ? deptFilter : undefined,
    };

    const result = await onDistribute(request);
    if (result) {
      setSelectedBonusEmps([]);
      setBonusAmount("1000");
    }
  };

  const estimatedTotal = selectedBonusEmps.length * Number(bonusAmount);

  return (
    <Card>
      <CardHeader 
        title="Distribute Bulk Bonuses" 
        subtitle="Bulk distribute bonuses to departments or selected employees"
        icon={<DollarSign size={20} />} 
      />
      
      <div className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-slate-900 mb-1.5">
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
                className="block w-full rounded-lg border-gray-300 border p-2.5 pr-2 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 disabled:opacity-50"
                style={{ paddingLeft: "20px" }}
              />
            </div>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-slate-900 mb-1.5">
              Filter Department
            </label>
            <div className="relative">
              <Filter className="absolute left-3 top-2.5 text-gray-400" size={16} />
              <select
                value={deptFilter}
                onChange={(e) => {
                  setDeptFilter(e.target.value);
                  setSelectedBonusEmps([]);
                }}
                disabled={isLoading}
                className="block w-full rounded-lg border-gray-300 border p-2.5 pr-2 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 disabled:opacity-50"
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
            <div className="bg-blue-50 text-blue-800 px-4 py-2.5 rounded-lg w-full text-center text-sm font-medium border border-blue-100">
              Est. Total Cost: ${estimatedTotal.toLocaleString()}
            </div>
          </div>
        </div>

        <div className="border rounded-lg border-gray-200 overflow-hidden">
          <div className="bg-gray-50 px-4 py-3 border-b border-gray-200 flex justify-between items-center">
            <span className="text-sm font-medium text-slate-900">
              Select Employees ({selectedBonusEmps.length} selected)
            </span>
            <button
              onClick={handleSelectAll}
              disabled={isLoading || filteredEmployees.length === 0}
              className="text-xs text-blue-600 hover:text-blue-800 font-medium disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Select All Visible
            </button>
          </div>
          <div className="max-h-60 overflow-y-auto">
            {filteredEmployees.length === 0 ? (
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
                      <p className="text-sm font-medium text-slate-900">
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
          <Button
            onClick={handleDistribute}
            disabled={selectedBonusEmps.length === 0 || isLoading || !businessId}
            isLoading={isLoading}
            icon={<DollarSign size={18} />}
          >
            Distribute Bonuses
          </Button>
        </div>
      </div>
    </Card>
  );
};

