import { useState } from 'react';
import { useEmployeePayroll } from '../../hooks/useEmployeePayroll';
import { YTDSummary } from '../../components/payroll/YTDSummary';
import { LatestPaycheck } from '../../components/payroll/LatestPaycheck';
import { PaycheckModal } from '../../components/payroll/PaycheckModal';
import { Card, CardHeader } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import type { Paycheck } from '../../types/payroll';
import { Eye } from 'lucide-react';

export default function EmployeePayroll() {
  // Get employee ID from localStorage
  const employeeId = (() => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        // For employees, businessPersonId is the employeeId
        if (user.role === 'EMPLOYEE' && user.businessPersonId) {
          return user.businessPersonId;
        }
      } catch (error) {
        console.error('Failed to parse user data:', error);
      }
    }
    return null;
  })();

  const { history, latestPaycheck, stats, loading } = useEmployeePayroll(employeeId);
  const [selectedCheck, setSelectedCheck] = useState<Paycheck | null>(null);

  if (loading) {
    return (
      <div className="flex h-96 items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto space-y-8 animate-in fade-in duration-500 p-6">
      {/* 1. Page Header */}
      <div>
        <h1 className="text-3xl font-bold text-navy-900">My Payroll</h1>
        <p className="text-gray-500 mt-2">View your paystubs, tax deductions, and yearly earnings.</p>
      </div>

      {/* 2. YTD Summary */}
      <YTDSummary stats={stats} />

      {/* 3. Latest Paycheck Hero */}
      {latestPaycheck && (
        <LatestPaycheck 
          paycheck={latestPaycheck} 
          onClick={() => setSelectedCheck(latestPaycheck)} 
        />
      )}

      {/* 4. History Table */}
      <Card padding="none">
        <div className="p-6 border-b border-gray-100">
          <CardHeader title="Payment History" subtitle="Your past 12 months of earnings" />
        </div>
        
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-gray-50 text-xs uppercase text-gray-500 font-semibold">
              <tr>
                <th className="px-6 py-4">Pay Date</th>
                <th className="px-6 py-4">Gross Pay</th>
                <th className="px-6 py-4">Deductions</th>
                <th className="px-6 py-4">Net Pay</th>
                <th className="px-6 py-4 text-center">Status</th>
                <th className="px-6 py-4"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {history.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-gray-500">
                    No payroll history available.
                  </td>
                </tr>
              ) : (
                history.map((check) => (
                  <tr 
                    key={check.id} 
                    onClick={() => setSelectedCheck(check)}
                    className="hover:bg-blue-50/30 transition-colors cursor-pointer group"
                  >
                    <td className="px-6 py-4 text-sm font-medium text-navy-900">
                      {new Date(check.payDate).toLocaleDateString('en-US', {
                        year: 'numeric',
                        month: 'short',
                        day: 'numeric'
                      })}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      ${check.grossPay.toLocaleString()}
                    </td>
                    <td className="px-6 py-4 text-sm text-red-600">
                      -${check.totalDeductions.toLocaleString()}
                    </td>
                    <td className="px-6 py-4 text-sm font-bold text-green-700">
                      ${check.netPay.toLocaleString()}
                    </td>
                    <td className="px-6 py-4 text-center">
                      <Badge variant={check.status === 'PAID' ? 'success' : 'warning'}>
                        {check.status}
                      </Badge>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <button className="text-gray-400 group-hover:text-blue-600 transition-colors">
                        <Eye size={18} />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {/* 5. Detail Modal */}
      <PaycheckModal 
        isOpen={!!selectedCheck} 
        onClose={() => setSelectedCheck(null)} 
        check={selectedCheck} 
      />
    </div>
  );
}

