import React from 'react';
import type { EmployeeNode } from '../../types/company';
import { Modal } from '../ui/Modal';
import { Mail, Calendar, DollarSign, Building, User, Crown, Users } from 'lucide-react';

interface EmployeeModalProps {
  isOpen: boolean;
  onClose: () => void;
  employee: EmployeeNode | null;
  mode: 'EMPLOYER' | 'EMPLOYEE';
  allPeople?: EmployeeNode[];
}

export const EmployeeModal: React.FC<EmployeeModalProps> = ({
  isOpen,
  onClose,
  employee,
  mode,
  allPeople = [],
}) => {
  if (!isOpen || !employee) return null;

  // Find direct manager
  const manager = employee.managerId 
    ? allPeople.find(p => p.id === employee.managerId)
    : null;

  // Check if this person is a CEO
  const isCEO = !employee.managerId && (
    employee.position.toLowerCase().includes('ceo') ||
    employee.position.toLowerCase().includes('chief executive') ||
    employee.position.toLowerCase().includes('president')
  );

  // Get direct reports
  const directReports = employee.children || [];

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`${employee.name} - ${employee.position}`}
      maxWidth="lg"
    >
      <div className="space-y-6">

        {/* CEO Badge */}
        {isCEO && (
          <div className="bg-gradient-to-r from-yellow-50 to-amber-50 border border-yellow-200 rounded-lg p-4 flex items-center gap-3">
            <Crown size={20} className="text-yellow-600" />
            <div>
              <p className="text-sm font-semibold text-yellow-900">Chief Executive Officer</p>
              <p className="text-xs text-yellow-700">Top-level executive</p>
            </div>
          </div>
        )}

        {/* Email (Full Width) */}
        <div className="flex items-center gap-3">
          <div className="p-2 bg-gray-100 rounded-lg">
            <Mail size={18} className="text-gray-600" />
          </div>
          <div>
            <p className="text-xs text-gray-500">Email</p>
            <p className="text-sm font-medium text-gray-900">{employee.email}</p>
          </div>
        </div>

        {/* Department & Status (Side by Side) */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Department */}
          <div className="flex items-center gap-3">
            <div className="p-2 bg-gray-100 rounded-lg">
              <Building size={18} className="text-gray-600" />
            </div>
            <div>
              <p className="text-xs text-gray-500">Department</p>
              <p className="text-sm font-medium text-gray-900">{employee.department}</p>
            </div>
          </div>

          {/* Status */}
          {employee.status && (
            <div className="flex items-center gap-3">
              <div className="p-2 bg-gray-100 rounded-lg">
                <User size={18} className="text-gray-600" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Status</p>
                <span
                  className={`inline-block mt-1 px-3 py-1 rounded-full text-xs font-semibold ${
                    employee.status === 'ACTIVE'
                      ? 'bg-green-100 text-green-700'
                      : employee.status === 'INACTIVE'
                      ? 'bg-gray-100 text-gray-700'
                      : 'bg-yellow-100 text-yellow-700'
                  }`}
                >
                  {employee.status}
                </span>
              </div>
            </div>
          )}
        </div>

        {/* Reports To Section */}
        {manager && (
          <div className="bg-blue-50 p-4 rounded-lg border border-blue-200">
            <p className="text-sm font-medium text-blue-700 mb-2">Reports to</p>
            <div className="flex items-center gap-3">
              <div className="p-2 bg-blue-100 rounded-lg">
                <User size={18} className="text-blue-600" />
              </div>
              <div>
                <p className="text-base font-semibold text-gray-900">{manager.name}</p>
                <p className="text-sm text-gray-600">{manager.position}</p>
              </div>
            </div>
          </div>
        )}

        {/* Hire Date & Salary (Only show in EMPLOYER mode) */}
        {mode === 'EMPLOYER' && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {employee.hireDate && (
              <div className="flex items-center gap-3">
                <div className="p-2 bg-gray-100 rounded-lg">
                  <Calendar size={18} className="text-gray-600" />
                </div>
                <div>
                  <p className="text-xs text-gray-500">Hire Date</p>
                  <p className="text-sm font-medium text-gray-900">
                    {new Date(employee.hireDate).toLocaleDateString('en-US', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric'
                    })}
                  </p>
                </div>
              </div>
            )}

            {employee.salary && (
              <div className="flex items-center gap-3">
                <div className="p-2 bg-gray-100 rounded-lg">
                  <DollarSign size={18} className="text-gray-600" />
                </div>
                <div>
                  <p className="text-xs text-gray-500">Annual Salary</p>
                  <p className="text-sm font-medium text-gray-900">
                    ${employee.salary.toLocaleString()}
                  </p>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Direct Reports */}
        {mode === 'EMPLOYER' && directReports.length > 0 && (
          <div className="pt-6 border-t border-gray-200">
            <div className="flex items-center gap-2 mb-4">
              <div className="p-2 bg-gray-100 rounded-lg">
                <Users size={18} className="text-gray-600" />
              </div>
              <h3 className="font-semibold text-slate-900">
                Direct Reports ({directReports.length})
              </h3>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 max-h-60 overflow-y-auto">
              {directReports.map((child) => (
                <div 
                  key={child.id} 
                  className="bg-gray-50 p-3 rounded-lg border border-gray-200 hover:border-blue-300 transition-colors cursor-pointer"
                >
                  <p className="text-sm font-semibold text-gray-900">{child.name}</p>
                  <p className="text-xs text-gray-600 mt-1">{child.position}</p>
                  {child.department && (
                    <span className="inline-block mt-2 px-2 py-0.5 bg-gray-200 text-gray-700 rounded text-xs">
                      {child.department}
                    </span>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </Modal>
  );
};