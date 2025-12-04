import React, { useState, useMemo } from 'react';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { FileText, Download, Search, CheckCircle, Trash2, X } from 'lucide-react';
import type { Paycheck, PaycheckStatus } from '../../types/payroll';

interface PayrollHistoryProps {
  history: Paycheck[];
  isLoading: boolean;
  onDelete: (paycheckId: number) => Promise<boolean>;
  onUpdateStatus: (paycheckId: number, status: PaycheckStatus) => Promise<boolean>;
}

export const PayrollHistory = ({ 
  history, 
  isLoading, 
  onDelete, 
  onUpdateStatus 
}: PayrollHistoryProps) => {
  const [searchQuery, setSearchQuery] = useState<string>("");

  // Filter history by search query
  const filteredHistory = useMemo(() => {
    return history.filter((item) =>
      item.employeeName.toLowerCase().includes(searchQuery.toLowerCase())
    );
  }, [history, searchQuery]);

  const handleDelete = async (paycheckId: number) => {
    if (!window.confirm("Are you sure you want to delete this paycheck? This action cannot be undone.")) {
      return;
    }
    await onDelete(paycheckId);
  };

  const getStatusBadgeVariant = (status: PaycheckStatus): 'success' | 'warning' | 'error' | 'neutral' => {
    switch (status) {
      case "DRAFT":
        return "neutral";
      case "PENDING":
        return "warning";
      case "PAID":
        return "success";
      case "VOIDED":
        return "error";
      default:
        return "neutral";
    }
  };

  return (
    <Card padding="none">
      <div className="px-6 py-4 border-b border-gray-100 flex flex-col sm:flex-row justify-between items-center gap-4">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-gray-100 rounded-lg text-gray-600">
            <FileText size={18} />
          </div>
          <h3 className="font-bold text-slate-900">Recent Transactions</h3>
        </div>
        <div className="flex gap-2 w-full sm:w-auto">
          <div className="relative flex-1 sm:w-64">
            <Search className="absolute left-3 top-2.5 text-gray-400" size={16} />
            <input
              type="text"
              placeholder="Search employee..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-100 focus:border-blue-500"
              style={{ paddingLeft: "36px" }}
            />
          </div>
          <button className="flex items-center gap-2 px-3 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 text-sm font-medium transition-colors">
            <Download size={16} />
            Export
          </button>
        </div>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left">
          <thead className="bg-gray-50 text-xs uppercase text-gray-500 font-semibold">
            <tr>
              <th className="px-6 py-4">Date</th>
              <th className="px-6 py-4">Employee</th>
              <th className="px-6 py-4 text-right">Gross Pay</th>
              <th className="px-6 py-4 text-right">Deductions</th>
              <th className="px-6 py-4 text-right">Net Pay</th>
              <th className="px-6 py-4 text-center">Status</th>
              <th className="px-6 py-4 text-center">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {isLoading ? (
              <tr>
                <td colSpan={7} className="px-6 py-10 text-center text-sm text-gray-500">
                  Loading history...
                </td>
              </tr>
            ) : filteredHistory.length === 0 ? (
              <tr>
                <td colSpan={7} className="px-6 py-10 text-center text-sm text-gray-500">
                  {searchQuery ? "No matching payroll history found." : "No payroll history found."}
                </td>
              </tr>
            ) : (
              filteredHistory.map((item) => {
                const status = item.status || "DRAFT";
                return (
                  <tr key={item.id} className="hover:bg-blue-50/50 transition-colors group">
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                      {item.payDate}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-slate-900">
                        {item.employeeName}
                      </div>
                      <div className="text-xs text-gray-500">ID: {item.employeeId}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-right font-mono font-medium text-slate-900">
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
                      <Badge variant={getStatusBadgeVariant(status)}>
                        {status}
                      </Badge>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-center">
                      <div className="flex items-center justify-center gap-1">
                        {(() => {
                          switch (status) {
                            case "DRAFT":
                              return (
                                <>
                                  <button
                                    onClick={() => onUpdateStatus(item.id, "PAID")}
                                    disabled={isLoading}
                                    className="p-2 text-green-600 hover:bg-green-50 rounded-lg border border-green-200 hover:border-green-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                    title="Mark as Paid"
                                  >
                                    <CheckCircle size={18} />
                                  </button>
                                  <button
                                    onClick={() => handleDelete(item.id)}
                                    disabled={isLoading}
                                    className="p-2 text-red-600 hover:bg-red-50 rounded-lg border border-red-200 hover:border-red-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                    title="Delete Paycheck"
                                  >
                                    <Trash2 size={18} />
                                  </button>
                                </>
                              );
                            case "PENDING":
                              return (
                                <button
                                  onClick={() => onUpdateStatus(item.id, "PAID")}
                                  disabled={isLoading}
                                  className="p-2 text-green-600 hover:bg-green-50 rounded-lg border border-green-200 hover:border-green-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                  title="Mark as Paid"
                                >
                                  <CheckCircle size={18} />
                                </button>
                              );
                            case "PAID":
                              return (
                                <button
                                  onClick={() => onUpdateStatus(item.id, "VOIDED")}
                                  disabled={isLoading}
                                  className="p-2 text-red-600 hover:bg-red-50 rounded-lg border border-red-200 hover:border-red-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
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
                              return (
                                <>
                                  <button
                                    onClick={() => onUpdateStatus(item.id, "PAID")}
                                    disabled={isLoading}
                                    className="p-2 text-green-600 hover:bg-green-50 rounded-lg border border-green-200 hover:border-green-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                    title="Mark as Paid"
                                  >
                                    <CheckCircle size={18} />
                                  </button>
                                  <button
                                    onClick={() => handleDelete(item.id)}
                                    disabled={isLoading}
                                    className="p-2 text-red-600 hover:bg-red-50 rounded-lg border border-red-200 hover:border-red-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
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
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </Card>
  );
};

