import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import type { Paycheck } from '../../types/payroll';
import { Calendar, ArrowRight } from 'lucide-react';

export const LatestPaycheck = ({ paycheck, onClick }: { paycheck: Paycheck; onClick: () => void }) => {
  const taxRate = paycheck.grossPay > 0 
    ? ((paycheck.taxDeduction / paycheck.grossPay) * 100).toFixed(1) 
    : '0.0';

  return (
    <Card className="relative overflow-hidden group">
      {/* Decorative gradient blob */}
      <div className="absolute top-0 right-0 w-64 h-64 bg-gradient-to-br from-green-50 to-blue-50 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2 opacity-70" />

      <div className="relative z-10">
        <div className="flex justify-between items-start mb-6">
          <div>
            <div className="flex items-center gap-2 text-blue-600 mb-1">
              <Calendar size={16} />
              <span className="text-sm font-bold">Latest Payment</span>
            </div>
            <h2 className="text-3xl font-bold text-navy-900">
              {new Date(paycheck.payDate).toLocaleDateString('en-US', { 
                year: 'numeric', 
                month: 'long', 
                day: 'numeric' 
              })}
            </h2>
          </div>
          <div className="text-right">
            <p className="text-sm text-gray-500">Net Pay</p>
            <p className="text-3xl font-bold text-green-600">${paycheck.netPay.toLocaleString()}</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
          <div className="p-4 bg-gray-50 rounded-xl border border-gray-100">
            <p className="text-xs text-gray-500 uppercase font-bold mb-1">Gross Pay</p>
            <p className="text-lg font-semibold text-navy-900">${paycheck.grossPay.toLocaleString()}</p>
          </div>
          <div className="p-4 bg-red-50/50 rounded-xl border border-red-100">
            <p className="text-xs text-red-500 uppercase font-bold mb-1">Tax ({taxRate}%)</p>
            <p className="text-lg font-semibold text-red-700">-${paycheck.taxDeduction.toLocaleString()}</p>
          </div>
          <div className="p-4 bg-orange-50/50 rounded-xl border border-orange-100">
            <p className="text-xs text-orange-500 uppercase font-bold mb-1">Insurance</p>
            <p className="text-lg font-semibold text-orange-700">-${paycheck.insuranceDeduction.toLocaleString()}</p>
          </div>
        </div>

        <Button onClick={onClick} className="w-full sm:w-auto" icon={<ArrowRight size={16} />}>
          View Full Breakdown
        </Button>
      </div>
    </Card>
  );
};

