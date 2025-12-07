import { useState, useEffect } from 'react';
import { Card, CardHeader } from '../ui/Card';
import { Button } from '../ui/Button';
import { Calculator, DollarSign, CheckCircle } from 'lucide-react';
import type { Paycheck } from '../../types/payroll';
import type { Employee } from '../../types/employee';

interface PayrollCalculatorProps {
  employees: Employee[];
  onCalculate: (employeeId: number, additionalPay?: number) => Promise<Paycheck | null>;
  onGenerate: (employeeId: number, additionalPay?: number) => Promise<boolean>;
  isLoading: boolean;
  preselectedEmployeeId?: number;
}

export const PayrollCalculator = ({ 
  employees, 
  onCalculate, 
  onGenerate, 
  isLoading,
  preselectedEmployeeId
}: PayrollCalculatorProps) => {
  const [selectedId, setSelectedId] = useState<string>("");
  const [bonus, setBonus] = useState<string>("");
  const [preview, setPreview] = useState<Paycheck | null>(null);
  const [hasAutoCalculated, setHasAutoCalculated] = useState(false);

  // Auto-select and calculate when preselectedEmployeeId is provided
  useEffect(() => {
    if (preselectedEmployeeId && employees.length > 0 && !hasAutoCalculated) {
      const employeeExists = employees.some(emp => emp.id === preselectedEmployeeId);
      if (employeeExists) {
        setSelectedId(String(preselectedEmployeeId));
        setHasAutoCalculated(true);
        // Auto-calculate preview
        const calculatePreview = async () => {
          const result = await onCalculate(preselectedEmployeeId, bonus ? Number(bonus) : undefined);
          setPreview(result);
        };
        calculatePreview();
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [preselectedEmployeeId, employees.length, hasAutoCalculated]);

  const handleCalc = async () => {
    if (!selectedId) return;
    const result = await onCalculate(Number(selectedId), bonus ? Number(bonus) : undefined);
    setPreview(result);
  };

  const handleGenerate = async () => {
    if (!preview || !selectedId) return;
    const success = await onGenerate(Number(selectedId), bonus ? Number(bonus) : undefined);
    if (success) {
      setPreview(null);
      setSelectedId("");
      setBonus("");
    }
  };

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
      {/* Input Section */}
      <Card>
        <CardHeader 
          title="Run Payroll" 
          subtitle="Calculate individual checks"
          icon={<Calculator size={20} />} 
        />
        <div className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-slate-900 mb-1.5">
              Select Employee
            </label>
            <select 
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 bg-white disabled:opacity-50 disabled:cursor-not-allowed"
              value={selectedId}
              onChange={e => setSelectedId(e.target.value)}
              disabled={isLoading}
            >
              <option value="">Choose employee...</option>
              {employees.map((e) => (
                <option key={e.id} value={e.id}>
                  {e.name} - {e.position}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-900 mb-1.5">
              Bonus / Commission (Optional)
            </label>
            <div className="relative">
              <DollarSign className="absolute left-3 top-3 text-gray-400" size={16} />
              <input 
                type="number" 
                className="w-full pl-9 pr-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 disabled:opacity-50"
                placeholder="0.00"
                value={bonus}
                onChange={e => setBonus(e.target.value)}
                disabled={isLoading}
              />
            </div>
          </div>

          <Button 
            onClick={handleCalc} 
            disabled={!selectedId || isLoading} 
            className="w-full" 
            variant="secondary"
            isLoading={isLoading}
          >
            Preview Calculation
          </Button>
        </div>
      </Card>

      {/* Preview Section - Designed to look like a "Statement" */}
      <Card className={`transition-all duration-300 ${preview ? 'opacity-100' : 'opacity-50 grayscale'}`}>
        <div className="h-full flex flex-col justify-between">
          <div>
            <div className="flex justify-between items-start mb-6">
              <div>
                <h3 className="text-sm font-bold text-gray-400 uppercase tracking-wider mb-2">
                  Payslip Preview
                </h3>
                {preview && (
                  <div>
                    <p className="text-sm text-gray-500">Preview for</p>
                    <h3 className="text-lg font-bold text-slate-900">
                      {preview.employeeName}
                    </h3>
                  </div>
                )}
              </div>
              {preview && (
                <span className="px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded-full font-medium">
                  {preview.taxStrategyUsed}
                </span>
              )}
            </div>
            
            {preview ? (
              <div className="space-y-4">
                <div className="flex justify-between py-3 border-b border-gray-100">
                  <span className="text-gray-600">Base Salary</span>
                  <span className="font-semibold text-slate-900">
                    ${preview.grossPay.toLocaleString()}
                  </span>
                </div>
                {preview.bonus && preview.bonus > 0 && (
                  <div className="flex justify-between py-3 border-b border-gray-100">
                    <span className="text-blue-600">Bonus / Additional Pay</span>
                    <span className="font-semibold text-blue-600">
                      +${preview.bonus.toLocaleString()}
                    </span>
                  </div>
                )}
                <div className="flex justify-between py-3 border-b border-gray-200">
                  <span className="text-gray-700 font-medium">Gross Pay</span>
                  <span className="font-semibold text-slate-900">
                    ${(preview.grossPay + (preview.bonus || 0)).toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between py-3 border-b border-gray-100">
                  <span className="text-gray-600">
                    Tax (
                    {(
                      (preview.taxDeduction /
                        (preview.grossPay + (preview.bonus || 0))) *
                      100
                    ).toFixed(1)}
                    %)
                  </span>
                  <span className="font-semibold text-red-600">
                    -${preview.taxDeduction.toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between py-3 border-b border-gray-100">
                  <span className="text-gray-600">Insurance</span>
                  <span className="font-semibold text-red-600">
                    -${preview.insuranceDeduction.toLocaleString()}
                  </span>
                </div>
                <div className="flex justify-between py-3 pt-4 border-t-2 border-gray-200">
                  <span className="font-bold text-slate-900 text-lg">
                    Net Pay
                  </span>
                  <span className="text-xl font-bold text-green-600">
                    ${preview.netPay.toLocaleString()}
                  </span>
                </div>
              </div>
            ) : (
              <div className="text-center py-10 text-gray-400">
                <Calculator size={48} className="mx-auto mb-2 opacity-20" />
                <p className="text-sm">Select an employee to view breakdown</p>
              </div>
            )}
          </div>

          <Button 
            onClick={handleGenerate} 
            disabled={!preview || isLoading} 
            isLoading={isLoading}
            className="w-full mt-6"
            icon={<CheckCircle size={18} />}
          >
            Confirm & Generate Paycheck
          </Button>
        </div>
      </Card>
    </div>
  );
};

