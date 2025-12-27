import { Modal } from '../ui/Modal';
import type { Paycheck } from '../../types/payroll';
import { Download, Printer } from 'lucide-react';
import { Button } from '../ui/Button';

export const PaycheckModal = ({
  isOpen,
  onClose,
  check
}: {
  isOpen: boolean;
  onClose: () => void;
  check: Paycheck | null;
}) => {
  if (!check) return null;

  const handlePrint = () => {
    window.print();
  };

  const handleDownload = () => {
    // TODO: Implement PDF download when backend endpoint is available
    console.log('Download PDF for paycheck:', check.id);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Pay Stub: ${check.payDate}`} maxWidth="lg">
      <div className="space-y-6">
        {/* Header Info */}
        <div className="flex justify-between items-center pb-6 border-b border-gray-100">
          <div>
            <p className="text-sm text-gray-500">Employee ID: {check.employeeId}</p>
            <p className="font-bold text-navy-900">{check.employeeName}</p>
          </div>
          <div className="text-right">
            <p className="text-sm text-gray-500">Pay Date</p>
            <p className="font-bold text-navy-900">
              {new Date(check.payDate).toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
              })}
            </p>
          </div>
        </div>

        {/* Earnings Section */}
        <div>
          <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">Earnings</h4>
          <div className="flex justify-between py-2 border-b border-gray-50">
            <span className="text-gray-700">Base Salary & Wages</span>
            <span className="font-medium">${check.grossPay.toLocaleString()}</span>
          </div>
          {check.bonus && check.bonus > 0 && (
            <div className="flex justify-between py-2 border-b border-gray-50 text-blue-600">
              <span>Bonus / Commission</span>
              <span className="font-medium">+${check.bonus.toLocaleString()}</span>
            </div>
          )}
          <div className="flex justify-between py-2 font-bold text-navy-900 mt-2">
            <span>Total Gross Pay</span>
            <span>${check.grossPay.toLocaleString()}</span>
          </div>
        </div>

        {/* Deductions Section */}
        <div>
          <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">Deductions</h4>
          <div className="flex justify-between py-2 border-b border-gray-50 text-red-600">
            <span>Tax Withholding</span>
            <span>-${check.taxDeduction.toLocaleString()}</span>
          </div>
          <div className="flex justify-between py-2 border-b border-gray-50 text-red-600">
            <span>Health Insurance</span>
            <span>-${check.insuranceDeduction.toLocaleString()}</span>
          </div>
          <div className="flex justify-between py-2 font-bold text-red-700 mt-2">
            <span>Total Deductions</span>
            <span>-${check.totalDeductions.toLocaleString()}</span>
          </div>
        </div>

        {/* Net Pay Highlight */}
        <div className="bg-green-50 rounded-xl p-4 flex justify-between items-center border border-green-100">
          <span className="text-green-800 font-bold uppercase tracking-wide">Net Pay Distribution</span>
          <span className="text-2xl font-bold text-green-700">${check.netPay.toLocaleString()}</span>
        </div>

        {/* Footer Actions */}
        <div className="flex gap-3 pt-4">
          <Button variant="secondary" className="flex-1" icon={<Printer size={16} />} onClick={handlePrint}>
            Print
          </Button>
          <Button variant="primary" className="flex-1" icon={<Download size={16} />} onClick={handleDownload}>
            Download PDF
          </Button>
        </div>
      </div>
    </Modal>
  );
};

