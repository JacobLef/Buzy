import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  User,
  Search,
  X,
  Plus,
  Edit3,
  Receipt,
  UserX,
  CheckSquare,
  Square,
} from 'lucide-react';
import { useEmployeeList } from '../../hooks/useEmployeeList';
import { EmployeeForm } from '../../components/employee/EmployeeForm';
import { Modal } from '../../components/ui/Modal';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';
import { Card } from '../../components/ui/Card';
import { updateEmployee, createEmployee } from '../../api/employees';
import { getEmployer } from '../../api/employers';
import type { Employee, UpdateEmployeeRequest, CreateEmployeeRequest } from '../../types/employee';
import { PersonStatus } from '../../types/person_status';

export default function EmployeeManagement() {
  const navigate = useNavigate();
  const {
    employees,
    loading,
    error,
    selectedIds,
    filters,
    refetch,
    toggleSelect,
    toggleSelectAll,
    clearSelection,
    updateFilter,
    clearFilters,
  } = useEmployeeList();

  const [editingEmployee, setEditingEmployee] = useState<Employee | null>(null);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [companyId, setCompanyId] = useState<number | null>(null);

  // Load company ID
  useEffect(() => {
    const loadCompanyId = async () => {
      try {
        const userStr = localStorage.getItem('user');
        if (userStr) {
          const user = JSON.parse(userStr);
          if (user.role === 'EMPLOYER' && user.businessPersonId) {
            const response = await getEmployer(user.businessPersonId);
            setCompanyId(response.data.companyId);
          }
        }
      } catch (error) {
        console.error('Failed to load company ID:', error);
      }
    };
    loadCompanyId();
  }, []);

  const getStatusBadge = (status: string) => {
    switch (status) {
      case PersonStatus.ACTIVE:
        return <Badge variant="success">Active</Badge>;
      case PersonStatus.INACTIVE:
        return <Badge variant="error">Inactive</Badge>;
      case PersonStatus.ON_LEAVE:
        return <Badge variant="warning">On Leave</Badge>;
      default:
        return <Badge variant="blue">{status}</Badge>;
    }
  };

  const handleEdit = (employee: Employee) => {
    console.log('Editing employee:', employee); // Debug log
    setEditingEmployee(employee);
    setIsEditModalOpen(true);
  };

  const handleViewPayroll = (employee: Employee) => {
    // Navigate to payroll page with employee ID as query parameter
    navigate(`/employer/payroll?employeeId=${employee.id}`);
  };

  const handleCreate = () => {
    setEditingEmployee(null);
    setIsCreateModalOpen(true);
  };

  const handleSaveEdit = async (data: UpdateEmployeeRequest) => {
    if (!editingEmployee) return;
    setSaving(true);
    try {
      await updateEmployee(editingEmployee.id, data);
      await refetch();
      setIsEditModalOpen(false);
      setEditingEmployee(null);
    } catch (error: unknown) {
      const err = error as { response?: { data?: { message?: string } } };
      console.error('Failed to update employee:', error);
      alert(err.response?.data?.message || 'Failed to update employee');
    } finally {
      setSaving(false);
    }
  };

  const handleSaveCreate = async (data: CreateEmployeeRequest) => {
    setSaving(true);
    try {
      await createEmployee(data);
      await refetch();
      setIsCreateModalOpen(false);
    } catch (error: unknown) {
      const err = error as { response?: { data?: { message?: string } } };
      console.error('Failed to create employee:', error);
      alert(err.response?.data?.message || 'Failed to create employee');
    } finally {
      setSaving(false);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  if (loading) {
    return (
      <div className="space-y-8 max-w-7xl mx-auto px-4 py-6">
        <div className="flex h-96 items-center justify-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto px-4 py-6 animate-in fade-in duration-500">
      {/* Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 tracking-tight">Employee Management</h1>
          <p className="text-gray-500 mt-2">Manage your team members and their information</p>
        </div>
        <Button variant="primary" onClick={handleCreate}>
          <Plus size={18} className="mr-2" />
          Add Employee
        </Button>
      </div>

      {/* Error Message */}
      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
          {error}
        </div>
      )}

      {/* Filters Bar */}
      <Card>
        <div className="flex flex-col md:flex-row gap-4">
          {/* Search */}
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
              <input
                type="text"
                placeholder="Search by name, email, or position..."
                value={filters.search}
                onChange={(e) => updateFilter('search', e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
              />
            </div>
          </div>

          {/* Status Filter */}
          <div className="w-full md:w-48">
            <select
              value={filters.status}
              onChange={(e) => updateFilter('status', e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            >
              <option value="ALL">All Status</option>
              <option value={PersonStatus.ACTIVE}>Active</option>
              <option value={PersonStatus.INACTIVE}>Inactive</option>
              <option value={PersonStatus.ON_LEAVE}>On Leave</option>
            </select>
          </div>

          {/* Sort */}
          <div className="w-full md:w-48">
            <select
              value={`${filters.sortBy}-${filters.sortDirection}`}
              onChange={(e) => {
                const [sortBy, sortDirection] = e.target.value.split('-');
                updateFilter('sortBy', sortBy);
                updateFilter('sortDirection', sortDirection);
              }}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            >
              <option value="name-asc">Name (A-Z)</option>
              <option value="name-desc">Name (Z-A)</option>
              <option value="salary-desc">Salary (High-Low)</option>
              <option value="salary-asc">Salary (Low-High)</option>
              <option value="hireDate-desc">Hire Date (Newest)</option>
              <option value="hireDate-asc">Hire Date (Oldest)</option>
            </select>
          </div>

          {/* Clear Filters */}
          {(filters.search || filters.status !== 'ALL') && (
            <Button variant="ghost" onClick={clearFilters}>
              <X size={18} className="mr-2" />
              Clear
            </Button>
          )}
        </div>
      </Card>

      {/* Bulk Actions Bar */}
      {selectedIds.length > 0 && (
        <Card className="bg-blue-50 border-blue-200">
          <div className="flex items-center justify-between">
            <span className="text-sm font-medium text-blue-900">
              {selectedIds.length} employee{selectedIds.length !== 1 ? 's' : ''} selected
            </span>
            <div className="flex gap-2">
              <Button variant="secondary" onClick={clearSelection} className="text-sm py-1.5 px-3">
                Clear Selection
              </Button>
              <Button variant="danger" className="text-sm py-1.5 px-3">
                <UserX size={16} className="mr-2" />
                Deactivate Selected
              </Button>
            </div>
          </div>
        </Card>
      )}

      {/* Employees Table */}
      <Card padding="none">
        {employees.length === 0 ? (
          <div className="p-12 text-center">
            <User size={48} className="mx-auto text-gray-300 mb-4" />
            <p className="text-gray-500 text-lg">No employees found</p>
            <p className="text-gray-400 text-sm mt-2">
              {filters.search || filters.status !== 'ALL'
                ? 'Try adjusting your filters'
                : 'Get started by adding your first employee'}
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-3 text-left">
                    <button
                      onClick={toggleSelectAll}
                      className="flex items-center"
                    >
                      {selectedIds.length === employees.length ? (
                        <CheckSquare size={20} className="text-blue-600" />
                      ) : (
                        <Square size={20} className="text-gray-400" />
                      )}
                    </button>
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Employee
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Position
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Email
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Salary
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Hire Date
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {employees.map((employee) => (
                  <tr
                    key={employee.id}
                    className="hover:bg-gray-50 transition-colors"
                  >
                    <td className="px-6 py-4">
                      <button onClick={() => toggleSelect(employee.id)}>
                        {selectedIds.includes(employee.id) ? (
                          <CheckSquare size={20} className="text-blue-600" />
                        ) : (
                          <Square size={20} className="text-gray-400" />
                        )}
                      </button>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                          <User size={20} className="text-blue-600" />
                        </div>
                        <div>
                          <div className="font-medium text-slate-900">{employee.name}</div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-900">{employee.position}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">{employee.email}</td>
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">
                      {formatCurrency(employee.salary)}
                    </td>
                    <td className="px-6 py-4">{getStatusBadge(employee.status)}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      {formatDate(employee.hireDate)}
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          type="button"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleEdit(employee);
                          }}
                          className="p-2 hover:bg-blue-50 rounded-lg transition-colors text-blue-600 hover:text-blue-700"
                          title="Edit"
                        >
                          <Edit3 size={18} />
                        </button>
                        <button
                          type="button"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleViewPayroll(employee);
                          }}
                          className="p-2 hover:bg-green-50 rounded-lg transition-colors text-green-600 hover:text-green-700"
                          title="View Payroll"
                        >
                          <Receipt size={18} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      {/* Create Modal */}
      <Modal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        title="Add New Employee"
        maxWidth="2xl"
      >
        <EmployeeForm
          mode="create"
          companyId={companyId || undefined}
          onSubmit={handleSaveCreate as (data: UpdateEmployeeRequest | CreateEmployeeRequest) => Promise<void>}
          onCancel={() => setIsCreateModalOpen(false)}
          saving={saving}
        />
      </Modal>

      {/* Edit Modal */}
      {isEditModalOpen && editingEmployee && (
        <Modal
          isOpen={isEditModalOpen}
          onClose={() => {
            setIsEditModalOpen(false);
            setEditingEmployee(null);
          }}
          title="Edit Employee"
          maxWidth="2xl"
        >
          <EmployeeForm
            employee={editingEmployee}
            mode="edit"
            companyId={companyId || undefined}
            onSubmit={handleSaveEdit as (data: UpdateEmployeeRequest | CreateEmployeeRequest) => Promise<void>}
            onCancel={() => {
              setIsEditModalOpen(false);
              setEditingEmployee(null);
            }}
            saving={saving}
          />
        </Modal>
      )}
    </div>
  );
}
