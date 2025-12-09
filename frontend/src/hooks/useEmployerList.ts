import { useState, useEffect, useMemo, useCallback } from 'react';
import { getEmployersByBusiness, getAllEmployers } from '../api/employers';
import { getEmployer } from '../api/employers';
import type { Employer } from '../types/employer';
import { PersonStatus } from '../types/person_status';

type SortField = 'name' | 'salary' | 'hireDate' | 'title' | 'department' | 'directReportsCount';
type SortDirection = 'asc' | 'desc';

interface Filters {
  search: string;
  status: PersonStatus | 'ALL';
  department: string;
  sortBy: SortField;
  sortDirection: SortDirection;
}

export const useEmployerList = () => {
  const [employers, setEmployers] = useState<Employer[]>([]);
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

  // Fetch employers
  const fetchEmployers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const businessId = await loadBusinessId();
      const response = businessId 
        ? await getEmployersByBusiness(businessId)
        : await getAllEmployers();
      setEmployers(response.data);
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      setError(error.response?.data?.message || 'Failed to load employers');
      console.error('Error fetching employers:', err);
    } finally {
      setLoading(false);
    }
  }, [loadBusinessId]);

  useEffect(() => {
    fetchEmployers();
  }, [fetchEmployers]);

  // Filter and sort employers
  const filteredAndSortedEmployers = useMemo(() => {
    let result = [...employers];

    // Apply search filter
    if (filters.search) {
      const searchLower = filters.search.toLowerCase();
      result = result.filter(
        emp =>
          emp.name.toLowerCase().includes(searchLower) ||
          emp.email.toLowerCase().includes(searchLower) ||
          emp.title.toLowerCase().includes(searchLower) ||
          emp.department.toLowerCase().includes(searchLower)
      );
    }

    // Apply status filter
    if (filters.status !== 'ALL') {
      result = result.filter(emp => emp.status === filters.status);
    }

    // Apply department filter
    if (filters.department) {
      result = result.filter(emp => emp.department === filters.department);
    }

    // Apply sorting
    result.sort((a, b) => {
      let aValue: string | number;
      let bValue: string | number;

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
        case 'title':
          aValue = a.title.toLowerCase();
          bValue = b.title.toLowerCase();
          break;
        case 'department':
          aValue = a.department.toLowerCase();
          bValue = b.department.toLowerCase();
          break;
        case 'directReportsCount':
          aValue = a.directReportsCount;
          bValue = b.directReportsCount;
          break;
        default:
          return 0;
      }

      if (aValue < bValue) return filters.sortDirection === 'asc' ? -1 : 1;
      if (aValue > bValue) return filters.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });

    return result;
  }, [employers, filters]);

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
    if (selectedIds.size === filteredAndSortedEmployers.length) {
      setSelectedIds(new Set());
    } else {
      setSelectedIds(new Set(filteredAndSortedEmployers.map(emp => emp.id)));
    }
  }, [selectedIds.size, filteredAndSortedEmployers]);

  const clearSelection = useCallback(() => {
    setSelectedIds(new Set());
  }, []);

  // Filter handlers
  const updateFilter = useCallback((key: keyof Filters, value: string | PersonStatus | SortField | SortDirection) => {
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

  // Get unique departments for filter
  const departments = useMemo(() => {
    const deptSet = new Set(employers.map(emp => emp.department));
    return Array.from(deptSet).sort();
  }, [employers]);

  return {
    // Data
    employers: filteredAndSortedEmployers,
    allEmployers: employers,
    loading,
    error,
    selectedIds: Array.from(selectedIds),
    filters,
    departments,

    // Actions
    refetch: fetchEmployers,
    toggleSelect,
    toggleSelectAll,
    clearSelection,
    updateFilter,
    clearFilters,
  };
};

