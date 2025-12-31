import { useState, useEffect } from 'react';
import { Button } from '../ui/Button';
import { ManagerSearch } from './ManagerSearch';
import type { Employee, UpdateEmployeeRequest, CreateEmployeeRequest } from '../../types/employee';
import { PersonStatus } from '../../types/person_status';

interface EmployeeFormProps {
  employee?: Employee | null;
  onSubmit: (data: UpdateEmployeeRequest | CreateEmployeeRequest) => Promise<void>;
  onCancel: () => void;
  saving?: boolean;
  mode?: 'edit' | 'create';
  companyId?: number;
}

export const EmployeeForm = ({
  employee,
  onSubmit,
  onCancel,
  saving = false,
  mode = 'edit',
  companyId
}: EmployeeFormProps) => {
  const [formData, setFormData] = useState<UpdateEmployeeRequest | CreateEmployeeRequest>(
    mode === 'create' && companyId
      ? {
          name: '',
          email: '',
          password: '',
          salary: 0,
          position: '',
          companyId,
          managerId: 0,
          hireDate: new Date().toISOString().split('T')[0],
        }
      : {
          name: '',
          email: '',
          password: '',
          salary: 0,
          position: '',
          managerId: 0,
          hireDate: new Date().toISOString().split('T')[0],
          status: PersonStatus.ACTIVE,
        }
  );
  const [managerId, setManagerId] = useState<number | null>(null);

  useEffect(() => {
    if (mode === 'create' && companyId) {
      setFormData({
        name: '',
        email: '',
        password: '',
        salary: 0,
        position: '',
        companyId,
        managerId: 0,
        hireDate: new Date().toISOString().split('T')[0],
      });
      setManagerId(null);
    } else if (employee && mode === 'edit') {
      setFormData({
        name: employee.name,
        email: employee.email,
        password: '',
        salary: employee.salary,
        position: employee.position,
        managerId: employee.managerId || 0,
        hireDate: employee.hireDate,
        status: employee.status,
      });
      setManagerId(employee.managerId);
    }
  }, [employee?.id, mode, companyId]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'salary' ? parseFloat(value) || 0 : value,
    }));
  };

  const handleManagerChange = (newManagerId: number | null) => {
    setManagerId(newManagerId);
    setFormData(prev => ({
      ...prev,
      managerId: newManagerId || 0,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const baseData: any = {
      ...formData,
      managerId: managerId || 0,
    };

    if (mode === 'edit') {
      if (!formData.password || formData.password.trim() === '') {
        delete baseData.password;
      }
    }

    await onSubmit(baseData as UpdateEmployeeRequest | CreateEmployeeRequest);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Full Name *
          </label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Email *
          </label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleInputChange}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Password {mode === 'edit' && '(leave blank to keep current)'}
            {mode === 'create' && '*'}
          </label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleInputChange}
            required={mode === 'create'}
            placeholder={mode === 'edit' ? 'Leave blank to keep current' : ''}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Position *
          </label>
          <input
            type="text"
            name="position"
            value={formData.position}
            onChange={handleInputChange}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Salary *
          </label>
          <input
            type="number"
            name="salary"
            value={formData.salary}
            onChange={handleInputChange}
            required
            min="0"
            step="0.01"
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
          />
        </div>

        {mode === 'edit' && 'status' in formData && (
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Status *
            </label>
            <select
              name="status"
              value={formData.status}
              onChange={handleInputChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
            >
              <option value={PersonStatus.ACTIVE}>Active</option>
              <option value={PersonStatus.INACTIVE}>Inactive</option>
              <option value={PersonStatus.ON_LEAVE}>On Leave</option>
            </select>
          </div>
        )}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Hire Date *
          </label>
          <input
            type="date"
            name="hireDate"
            value={formData.hireDate}
            onChange={handleInputChange}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
          />
        </div>

        {companyId && (
          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Manager (optional)
            </label>
            <ManagerSearch
              value={managerId}
              onChange={handleManagerChange}
              companyId={companyId}
              excludeId={employee?.id}
            />
            <p className="mt-1 text-xs text-gray-500">
              Search for a manager by name. Managers can be employees or employers.
            </p>
          </div>
        )}
      </div>

      <div className="flex justify-end gap-3 pt-4 border-t border-gray-100">
        <Button type="button" variant="secondary" onClick={onCancel} disabled={saving}>
          Cancel
        </Button>
        <Button type="submit" variant="primary" isLoading={saving}>
          {saving ? 'Saving...' : mode === 'create' ? 'Create Employee' : 'Save Changes'}
        </Button>
      </div>
    </form>
  );
};
