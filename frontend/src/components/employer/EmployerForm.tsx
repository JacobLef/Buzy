import { useState, useEffect } from 'react';
import { Button } from '../ui/Button';
import type { Employer, UpdateEmployerRequest, CreateEmployerRequest } from '../../types/employer';
import { PersonStatus } from '../../types/person_status';

interface EmployerFormProps {
  employer?: Employer | null;
  onSubmit: (data: UpdateEmployerRequest | CreateEmployerRequest) => Promise<void>;
  onCancel: () => void;
  saving?: boolean;
  mode?: 'edit' | 'create';
  companyId?: number;
  canEditFullProfile?: boolean; // If false, only name, email, password can be edited
}

export const EmployerForm = ({
  employer,
  onSubmit,
  onCancel,
  saving = false,
  mode = 'edit',
  companyId,
  canEditFullProfile = true
}: EmployerFormProps) => {
  const [formData, setFormData] = useState<UpdateEmployerRequest | CreateEmployerRequest>(
    mode === 'create' && companyId
      ? {
          name: '',
          email: '',
          password: '',
          salary: 0,
          department: '',
          title: '',
          companyId,
          hireDate: new Date().toISOString().split('T')[0],
        }
      : {
          name: '',
          email: '',
          password: '',
          salary: 0,
          department: '',
          title: '',
          hireDate: new Date().toISOString().split('T')[0],
          status: PersonStatus.ACTIVE,
        }
  );

  useEffect(() => {
    if (mode === 'create' && companyId) {
      setFormData({
        name: '',
        email: '',
        password: '',
        salary: 0,
        department: '',
        title: '',
        companyId,
        hireDate: new Date().toISOString().split('T')[0],
      });
    } else if (employer && mode === 'edit') {
      setFormData({
        name: employer.name,
        email: employer.email,
        password: '',
        salary: employer.salary,
        department: employer.department,
        title: employer.title,
        hireDate: employer.hireDate,
        status: employer.status,
      });
    }
  }, [employer?.id, mode, companyId]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'salary' ? parseFloat(value) || 0 : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    // Build base data
    const baseData: any = {
      ...formData,
    };
    
    // For edit mode, only include password if it's provided and not empty
    if (mode === 'edit') {
      if (!formData.password || formData.password.trim() === '') {
        // Remove password field if empty in edit mode
        delete baseData.password;
      }
    }
    
    await onSubmit(baseData as UpdateEmployerRequest | CreateEmployerRequest);
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

        {canEditFullProfile && (
          <>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Title *
              </label>
              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleInputChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Department *
              </label>
              <input
                type="text"
                name="department"
                value={formData.department}
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

            {mode === 'edit' && (
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
          </>
        )}
        
        {!canEditFullProfile && mode === 'edit' && (
          <div className="md:col-span-2 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
            <p className="text-sm text-yellow-800">
              Limited edit mode: You can only edit name, email, and password. 
              Contact an admin to modify other fields.
            </p>
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
      </div>

      <div className="flex justify-end gap-3 pt-4 border-t border-gray-100">
        <Button type="button" variant="secondary" onClick={onCancel} disabled={saving}>
          Cancel
        </Button>
        <Button type="submit" variant="primary" isLoading={saving}>
          {saving ? 'Saving...' : mode === 'create' ? 'Create Employer' : 'Save Changes'}
        </Button>
      </div>
    </form>
  );
};

