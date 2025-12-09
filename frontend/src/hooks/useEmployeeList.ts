import { useState, useEffect, useMemo, useCallback } from 'react';
import { getEmployeesByBusiness, getAllEmployees } from '../api/employees';
import { getEmployer, getEmployersByBusiness } from '../api/employers';
import type { Employee } from '../types/employee';
import type { Employer } from '../types/employer';
import { PersonStatus } from '../types/person_status';

// Unified type for display purposes
export interface CompanyMember {
  id: number;
  name: string;
  email: string;
  status: PersonStatus;
  salary: number;
  hireDate: string;
  companyId: number;
  companyName: string;
  position: string;
  managerId: number | null;
  managerName: string | null;
  department?: string;
  type: 'EMPLOYEE' | 'EMPLOYER';
  createdAt: string;
  updatedAt: string;
}

type SortField = 'name' | 'salary' | 'hireDate' | 'position';
type SortDirection = 'asc' | 'desc';

interface Filters {
  search: string;
  status: PersonStatus | 'ALL';
  department: string;
  sortBy: SortField;
  sortDirection: SortDirection;
}

export const useEmployeeList = () => {
  const [members, setMembers] = useState<CompanyMember[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());
  const [filters, setFilters] = useState<Filters>({
    search: '',
    status: 'ALL',
    department: '',
    sortBy: 'name',
    sortDirection: 'asc',
  });

  // Load business ID from user
  const loadBusinessId = useCallback(async (): Promise<number | null> => {
    try {
      const userStr = localStorage.getItem('user');
      if (!userStr) return null;

      const user = JSON.parse(userStr);
      if (user.role === 'EMPLOYER' && user.businessPersonId) {
        const response = await getEmployer(user.businessPersonId);
        return response.data.companyId;
      }
      return null;
    } catch (error) {
      console.error('Failed to load business ID:', error);
      return null;
    }
  }, []);

  // Fetch employees only (not employers)
  const fetchEmployees = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const businessId = await loadBusinessId();
      const response = businessId 
        ? await getEmployeesByBusiness(businessId)
        : await getAllEmployees();

      // Convert employees to CompanyMember (only employees, not employers)
      const employeeMembers: CompanyMember[] = response.data.map((emp: Employee) => ({
        id: emp.id,
        name: emp.name,
        email: emp.email,
        status: emp.status,
        salary: emp.salary,
        hireDate: emp.hireDate,
        companyId: emp.companyId,
        companyName: emp.companyName,
        position: emp.position,
        managerId: emp.managerId,
        managerName: emp.managerName,
        type: 'EMPLOYEE' as const,
        createdAt: emp.createdAt,
        updatedAt: emp.updatedAt,
      }));

      setMembers(employeeMembers);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load employees');
      console.error('Error fetching employees:', err);
    } finally {
      setLoading(false);
    }
  }, [loadBusinessId]);

  useEffect(() => {
    fetchEmployees();
  }, [fetchEmployees]);

  // Filter and sort members
  const filteredAndSortedEmployees = useMemo(() => {
    let result = [...members];

    // Apply search filter
    if (filters.search) {
      const searchLower = filters.search.toLowerCase();
      result = result.filter(
        emp =>
          emp.name.toLowerCase().includes(searchLower) ||
          emp.email.toLowerCase().includes(searchLower) ||
          emp.position.toLowerCase().includes(searchLower)
      );
    }

    // Apply status filter
    if (filters.status !== 'ALL') {
      result = result.filter(emp => emp.status === filters.status);
    }

    // Apply department filter
    if (filters.department) {
      result = result.filter(member => 
        member.department === filters.department
      );
    }

    // Apply sorting
    result.sort((a, b) => {
      let aValue: any;
      let bValue: any;

      switch (filters.sortBy) {
        case 'name':
          aValue = a.name.toLowerCase();
          bValue = b.name.toLowerCase();
          break;
        case 'salary':
          aValue = a.salary;
          bValue = b.salary;
          break;
        case 'hireDate':
          aValue = new Date(a.hireDate).getTime();
          bValue = new Date(b.hireDate).getTime();
          break;
        case 'position':
          aValue = a.position.toLowerCase();
          bValue = b.position.toLowerCase();
          break;
        default:
          return 0;
      }

      if (aValue < bValue) return filters.sortDirection === 'asc' ? -1 : 1;
      if (aValue > bValue) return filters.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });

    return result;
  }, [members, filters]);

  // Selection handlers
  const toggleSelect = useCallback((id: number) => {
    setSelectedIds(prev => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  }, []);

  const toggleSelectAll = useCallback(() => {
    if (selectedIds.size === filteredAndSortedEmployees.length) {
      setSelectedIds(new Set());
    } else {
      setSelectedIds(new Set(filteredAndSortedEmployees.map(emp => emp.id)));
    }
  }, [selectedIds.size, filteredAndSortedEmployees]);

  const clearSelection = useCallback(() => {
    setSelectedIds(new Set());
  }, []);

  // Filter handlers
  const updateFilter = useCallback((key: keyof Filters, value: any) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  }, []);

  const clearFilters = useCallback(() => {
    setFilters({
      search: '',
      status: 'ALL',
      department: '',
      sortBy: 'name',
      sortDirection: 'asc',
    });
  }, []);

  return {
    // Data
    employees: filteredAndSortedEmployees,
    allEmployees: members,
    loading,
    error,
    selectedIds: Array.from(selectedIds),
    filters,

    // Actions
    refetch: fetchEmployees,
    toggleSelect,
    toggleSelectAll,
    clearSelection,
    updateFilter,
    clearFilters,
  };
};

