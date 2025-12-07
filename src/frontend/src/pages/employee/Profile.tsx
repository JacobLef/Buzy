import { useState, useEffect } from 'react';
import { 
  User, 
  Mail, 
  Briefcase, 
  Building, 
  Calendar, 
  DollarSign,
  Edit3,
  X,
  Save,
  UserCheck
} from 'lucide-react';
import { useProfile } from '../../hooks/useProfile';
import { Card, CardHeader } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';
import type { Employee, UpdateEmployeeRequest } from '../../types/employee';
import { PersonStatus } from '../../types/person_status';

export default function EmployeeProfile() {
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const { 
    profile, 
    loading, 
    editing, 
    saving, 
    error, 
    saveProfile, 
    toggleEditing, 
    cancelEditing 
  } = useProfile(user.businessPersonId, 'EMPLOYEE');

  const employee = profile as Employee | null;

  const [formData, setFormData] = useState<UpdateEmployeeRequest>({
    name: '',
    email: '',
    password: '',
    salary: 0,
    position: '',
    managerId: 0,
    hireDate: '',
    status: PersonStatus.ACTIVE,
  });

  useEffect(() => {
    if (employee && editing) {
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
    }
  }, [employee, editing]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'salary' ? parseFloat(value) || 0 : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await saveProfile(formData);
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'ACTIVE': return <Badge variant="success">Active</Badge>;
      case 'INACTIVE': return <Badge variant="error">Inactive</Badge>;
      case 'ON_LEAVE': return <Badge variant="warning">On Leave</Badge>;
      default: return <Badge variant="blue">{status}</Badge>;
    }
  };

  if (loading) {
    return (
      <div className="space-y-8 max-w-4xl mx-auto px-4 py-6">
        <div className="flex h-96 items-center justify-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      </div>
    );
  }

  if (!employee) {
    return (
      <div className="space-y-8 max-w-4xl mx-auto px-4 py-6">
        <div className="text-center py-12 text-gray-500">
          Profile not found.
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8 max-w-4xl mx-auto px-4 py-6 animate-in fade-in duration-500">
      
      {/* Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 tracking-tight">My Profile</h1>
          <p className="text-gray-500 mt-2">View and manage your personal information</p>
        </div>
        {!editing ? (
          <Button variant="secondary" onClick={toggleEditing}>
            <Edit3 size={18} className="mr-2" />
            Edit Profile
          </Button>
        ) : (
          <div className="flex gap-2">
            <Button variant="secondary" onClick={cancelEditing} disabled={saving}>
              <X size={18} className="mr-2" />
              Cancel
            </Button>
            <Button variant="primary" onClick={handleSubmit} disabled={saving}>
              <Save size={18} className="mr-2" />
              {saving ? 'Saving...' : 'Save Changes'}
            </Button>
          </div>
        )}
      </div>

      {/* Error Message */}
      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
          {error}
        </div>
      )}

      {/* Profile Card */}
      <Card>
        <CardHeader 
          title="Personal Information" 
          subtitle="Your employee details and contact information"
        />

        {!editing ? (
          /* View Mode */
          <div className="space-y-6">
            <div className="flex items-center gap-6 pb-6 border-b border-gray-100">
              <div className="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center">
                <User size={40} className="text-blue-600" />
              </div>
              <div>
                <h2 className="text-2xl font-bold text-slate-900">{employee.name}</h2>
                <p className="text-gray-500">{employee.position}</p>
                <div className="mt-2">
                  {getStatusBadge(employee.status)}
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-gray-100 rounded-lg">
                  <Mail size={20} className="text-gray-600" />
                </div>
                <div>
                  <p className="text-sm text-gray-500">Email</p>
                  <p className="text-sm font-medium text-slate-900">{employee.email}</p>
                </div>
              </div>

              <div className="flex items-center gap-4">
                <div className="p-3 bg-gray-100 rounded-lg">
                  <Building size={20} className="text-gray-600" />
                </div>
                <div>
                  <p className="text-sm text-gray-500">Company</p>
                  <p className="text-sm font-medium text-slate-900">{employee.companyName}</p>
                </div>
              </div>

              <div className="flex items-center gap-4">
                <div className="p-3 bg-gray-100 rounded-lg">
                  <Briefcase size={20} className="text-gray-600" />
                </div>
                <div>
                  <p className="text-sm text-gray-500">Position</p>
                  <p className="text-sm font-medium text-slate-900">{employee.position}</p>
                </div>
              </div>

              <div className="flex items-center gap-4">
                <div className="p-3 bg-gray-100 rounded-lg">
                  <UserCheck size={20} className="text-gray-600" />
                </div>
                <div>
                  <p className="text-sm text-gray-500">Manager</p>
                  <p className="text-sm font-medium text-slate-900">{employee.managerName || 'N/A'}</p>
                </div>
              </div>

              <div className="flex items-center gap-4">
                <div className="p-3 bg-gray-100 rounded-lg">
                  <DollarSign size={20} className="text-gray-600" />
                </div>
                <div>
                  <p className="text-sm text-gray-500">Salary</p>
                  <p className="text-sm font-medium text-slate-900">${employee.salary.toLocaleString()}</p>
                </div>
              </div>

              <div className="flex items-center gap-4">
                <div className="p-3 bg-gray-100 rounded-lg">
                  <Calendar size={20} className="text-gray-600" />
                </div>
                <div>
                  <p className="text-sm text-gray-500">Hire Date</p>
                  <p className="text-sm font-medium text-slate-900">
                    {new Date(employee.hireDate).toLocaleDateString('en-US', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric'
                    })}
                  </p>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Full Name
                </label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Email
                </label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Password
                </label>
                <input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  placeholder="Leave blank to keep current"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Position
                </label>
                <input
                  type="text"
                  name="position"
                  value={formData.position}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Salary
                </label>
                <input
                  type="number"
                  name="salary"
                  value={formData.salary}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Status
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

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Hire Date
                </label>
                <input
                  type="date"
                  name="hireDate"
                  value={formData.hireDate}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                />
              </div>
            </div>
          </form>
        )}
      </Card>
    </div>
  );
}