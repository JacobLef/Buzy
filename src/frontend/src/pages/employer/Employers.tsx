import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Briefcase,
  Users,
  Search,
  X,
  Edit3,
  Network,
  CheckSquare,
  Square,
} from 'lucide-react';
import { useEmployerList } from '../../hooks/useEmployerList';
import { Modal } from '../../components/ui/Modal';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';
import { Card } from '../../components/ui/Card';
import { EmployerForm } from '../../components/employer/EmployerForm';
import { updateEmployer, getEmployer } from '../../api/employers';
import type { Employer, UpdateEmployerRequest, CreateEmployerRequest } from '../../types/employer';
import { PersonStatus } from '../../types/person_status';
import { Shield, Crown, UserPlus, UserMinus } from 'lucide-react';
import { promoteToAdmin, removeAdmin } from '../../api/employers';

export default function EmployerManagement() {
  const navigate = useNavigate();
  const {
    employers,
    loading,
    error,
    selectedIds,
    filters,
    departments,
    refetch,
    toggleSelect,
    toggleSelectAll,
    updateFilter,
    clearFilters,
  } = useEmployerList();

  const [editingEmployer, setEditingEmployer] = useState<Employer | null>(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [companyId, setCompanyId] = useState<number | null>(null);
  const [currentUserEmployer, setCurrentUserEmployer] = useState<Employer | null>(null);
  
  // Calculate permissions
  const isOwner = currentUserEmployer?.isOwner ?? false;
  const isAdmin = currentUserEmployer?.isAdmin ?? false;
  const canEditFullProfile = isAdmin || isOwner;

  // Load company ID and current user employer info
  useEffect(() => {
    const loadCompanyId = async () => {
      try {
        const userStr = localStorage.getItem('user');
        if (userStr) {
          const user = JSON.parse(userStr);
          if (user.role === 'EMPLOYER' && user.businessPersonId) {
            const response = await getEmployer(user.businessPersonId);
            setCompanyId(response.data.companyId);
            setCurrentUserEmployer(response.data);
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

  const handleEdit = (employer: Employer) => {
    setEditingEmployer(employer);
    setIsEditModalOpen(true);
  };

  const handleViewDepartmentStructure = (employer: Employer) => {
    // Navigate to company page and highlight this employer's department
    navigate(`/employer/company?highlightEmployer=${employer.id}`);
  };

  const handleSaveEdit = async (data: UpdateEmployerRequest) => {
    if (!editingEmployer) return;
    setSaving(true);
    try {
      await updateEmployer(editingEmployer.id, data);
      await refetch();
      setIsEditModalOpen(false);
      setEditingEmployer(null);
    } catch (error: unknown) {
      const err = error as { response?: { data?: { message?: string } } };
      console.error('Failed to update employer:', error);
      alert(err.response?.data?.message || 'Failed to update employer');
    } finally {
      setSaving(false);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
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
          <h1 className="text-3xl font-bold text-slate-900 tracking-tight">Employer Management</h1>
          <p className="text-gray-500 mt-2">
            Manage leadership team, department heads, and admin roles
            {isOwner && <span className="ml-2 text-yellow-600">(Owner)</span>}
            {isAdmin && !isOwner && <span className="ml-2 text-blue-600">(Admin)</span>}
          </p>
        </div>
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
                placeholder="Search by name, email, title, or department..."
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

          {/* Department Filter */}
          <div className="w-full md:w-48">
            <select
              value={filters.department}
              onChange={(e) => updateFilter('department', e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            >
              <option value="">All Departments</option>
              {departments.map((dept) => (
                <option key={dept} value={dept}>
                  {dept}
                </option>
              ))}
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
              <option value="title-asc">Title (A-Z)</option>
              <option value="title-desc">Title (Z-A)</option>
              <option value="department-asc">Department (A-Z)</option>
              <option value="department-desc">Department (Z-A)</option>
              <option value="directReportsCount-desc">Reports (High-Low)</option>
              <option value="directReportsCount-asc">Reports (Low-High)</option>
              <option value="salary-desc">Salary (High-Low)</option>
              <option value="salary-asc">Salary (Low-High)</option>
            </select>
          </div>

          {/* Clear Filters */}
          {(filters.search || filters.status !== 'ALL' || filters.department) && (
            <Button variant="ghost" onClick={clearFilters}>
              <X size={18} className="mr-2" />
              Clear
            </Button>
          )}
        </div>
      </Card>

      {/* Employers Table */}
      <Card padding="none">
        {employers.length === 0 ? (
          <div className="p-12 text-center">
            <Briefcase size={48} className="mx-auto text-gray-300 mb-4" />
            <p className="text-gray-500 text-lg">No employers found</p>
            <p className="text-gray-400 text-sm mt-2">
              {filters.search || filters.status !== 'ALL' || filters.department
                ? 'Try adjusting your filters'
                : 'No employers in the system'}
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
                      {selectedIds.length === employers.length ? (
                        <CheckSquare size={20} className="text-blue-600" />
                      ) : (
                        <Square size={20} className="text-gray-400" />
                      )}
                    </button>
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Employer
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Title
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Email
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Department
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Direct Reports
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Salary
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Status
                  </th>
                  {isOwner && (
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                      Admin Role
                    </th>
                  )}
                  <th className="px-6 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {employers.map((employer) => (
                  <tr
                    key={employer.id}
                    className="hover:bg-gray-50 transition-colors"
                  >
                    <td className="px-6 py-4">
                      <button onClick={() => toggleSelect(employer.id)}>
                        {selectedIds.includes(employer.id) ? (
                          <CheckSquare size={20} className="text-blue-600" />
                        ) : (
                          <Square size={20} className="text-gray-400" />
                        )}
                      </button>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-purple-100 rounded-full flex items-center justify-center">
                          <Briefcase size={20} className="text-purple-600" />
                        </div>
                        <div className="flex items-center gap-2">
                          <div className="font-medium text-slate-900">{employer.name}</div>
                          {employer.isOwner && (
                            <span className="flex items-center gap-1 px-2 py-0.5 bg-yellow-100 text-yellow-800 text-xs font-semibold rounded-full">
                              <Crown size={12} />
                              OWNER
                            </span>
                          )}
                          {employer.isAdmin && !employer.isOwner && (
                            <span className="px-2 py-0.5 bg-blue-100 text-blue-800 text-xs font-semibold rounded-full">
                              <Shield size={12} className="inline mr-1" />
                              ADMIN
                            </span>
                          )}
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-900">{employer.title}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">{employer.email}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">
                      <Badge variant="blue">{employer.department}</Badge>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-900">
                      <div className="flex items-center gap-2">
                        <Users size={16} className="text-gray-400" />
                        <span className="font-medium">{employer.directReportsCount}</span>
                        <span className="text-gray-500 text-xs">reports</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">
                      {formatCurrency(employer.salary)}
                    </td>
                    <td className="px-6 py-4">{getStatusBadge(employer.status)}</td>
                    {isOwner && (
                      <td className="px-6 py-4">
                        {employer.isOwner ? (
                          <span className="text-xs text-gray-500 italic">Cannot change</span>
                        ) : employer.isAdmin ? (
                          <Button
                            variant="secondary"
                            onClick={async () => {
                              if (confirm(`Remove admin rights from ${employer.name}?`)) {
                                try {
                                  await removeAdmin(employer.id);
                                  await refetch();
                                  // Refresh current user if editing self
                                  const user = localStorage.getItem('user');
                                  if (user) {
                                    const userObj = JSON.parse(user);
                                    if (userObj.businessPersonId === employer.id) {
                                      const response = await getEmployer(userObj.businessPersonId);
                                      setCurrentUserEmployer(response.data);
                                    }
                                  }
                                } catch (error) {
                                  console.error('Failed to remove admin:', error);
                                  alert('Failed to remove admin rights');
                                }
                              }
                            }}
                            className="text-red-600 hover:text-red-700 text-xs"
                          >
                            <UserMinus size={14} className="mr-1" />
                            Remove Admin
                          </Button>
                        ) : (
                          <Button
                            variant="primary"
                            onClick={async () => {
                              if (confirm(`Grant admin rights to ${employer.name}?`)) {
                                try {
                                  await promoteToAdmin(employer.id);
                                  await refetch();
                                } catch (error) {
                                  console.error('Failed to promote admin:', error);
                                  alert('Failed to promote to admin');
                                }
                              }
                            }}
                            className="text-xs"
                          >
                            <UserPlus size={14} className="mr-1" />
                            Promote to Admin
                          </Button>
                        )}
                      </td>
                    )}
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        {/* Only show edit button if user has permission to edit this employer */}
                        {canEditFullProfile && !(isAdmin && !isOwner && employer.isOwner) && (
                          <button
                            type="button"
                            onClick={(e) => {
                              e.stopPropagation();
                              handleEdit(employer);
                            }}
                            className="p-2 hover:bg-blue-50 rounded-lg transition-colors text-blue-600 hover:text-blue-700"
                            title="Edit"
                          >
                            <Edit3 size={18} />
                          </button>
                        )}
                        <button
                          type="button"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleViewDepartmentStructure(employer);
                          }}
                          className="p-2 hover:bg-purple-50 rounded-lg transition-colors text-purple-600 hover:text-purple-700"
                          title="View Department Structure"
                        >
                          <Network size={18} />
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

      {/* Edit Modal */}
      {isEditModalOpen && editingEmployer && (
        <Modal
          isOpen={isEditModalOpen}
          onClose={() => {
            setIsEditModalOpen(false);
            setEditingEmployer(null);
          }}
          title="Edit Employer"
          maxWidth="2xl"
        >
          <EmployerForm
            employer={editingEmployer}
            mode="edit"
            companyId={companyId || undefined}
            canEditFullProfile={
              canEditFullProfile && 
              editingEmployer.id !== currentUserEmployer?.id &&
              !(isAdmin && !isOwner && editingEmployer.isOwner)
            }
            onSubmit={handleSaveEdit as (data: UpdateEmployerRequest | CreateEmployerRequest) => Promise<void>}
            onCancel={() => {
              setIsEditModalOpen(false);
              setEditingEmployer(null);
            }}
            saving={saving}
          />
        </Modal>
      )}
    </div>
  );
}
