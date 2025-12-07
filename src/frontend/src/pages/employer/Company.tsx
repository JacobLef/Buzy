import React, { useState, useEffect, useMemo } from 'react';
import { Search, ArrowLeft, Filter } from 'lucide-react';
import { DepartmentCard } from '../../components/company/DepartmentCard';
import { MindMapNode } from '../../components/company/MindMapNode';
import { EmployeeModal } from '../../components/company/EmployeeModal';
import { Button } from '../../components/ui/Button';
import { useCompanyNavigation } from '../../hooks/useCompanyNavigation';
import { buildOrgTree, flattenTree } from '../../utils/orgTreeBuilder';
import { getEmployeesByBusiness } from '../../api/employees';
import { getEmployersByBusiness } from '../../api/employers';
import { getEmployer } from '../../api/employers';
import type { EmployeeNode, DepartmentSummary } from '../../types/company';
import type { Employee } from '../../types/employee';
import type { Employer } from '../../types/employer';

/**
 * Extract department information from tree
 * Finds employers who are department heads (roots of their department subtrees)
 */
function extractDepartments(
  tree: EmployeeNode | null,
  employers: Employer[]
): DepartmentSummary[] {
  if (!tree) return [];

  const deptMap = new Map<string, { headId: number; headName: string; members: Set<number> }>();

  // Recursive function to collect department members
  const collectMembers = (node: EmployeeNode) => {
    const employer = employers.find(e => e.id === node.id);
    
    // If this is an employer node (potential department head)
    if (employer && employer.department) {
      const deptName = employer.department;
      
      // Initialize department if not exists
      if (!deptMap.has(deptName)) {
        deptMap.set(deptName, {
          headId: node.id,
          headName: node.name,
          members: new Set(),
        });
      }
      
      // Count this employer
      deptMap.get(deptName)!.members.add(node.id);
    }
    
    // Recursively count children
    if (node.children) {
      node.children.forEach(child => {
        // Determine child's department
        const childEmployer = employers.find(e => e.id === child.id);
        const childDept = childEmployer?.department || node.department;
        
        if (deptMap.has(childDept)) {
          deptMap.get(childDept)!.members.add(child.id);
        }
        
        collectMembers(child);
      });
    }
  };

  // Start collecting from root
  if (tree.children) {
    tree.children.forEach(child => collectMembers(child));
  }

  // Convert to array
  return Array.from(deptMap.entries()).map(([name, data]) => ({
    name,
    headId: data.headId,
    headName: data.headName,
    employeeCount: data.members.size,
  }));
}

export default function CompanyView() {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedEmp, setSelectedEmp] = useState<EmployeeNode | null>(null);
  const [loading, setLoading] = useState(true);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [employers, setEmployers] = useState<Employer[]>([]);
  const [showFilterMenu, setShowFilterMenu] = useState(false);

  // Fetch data from backend
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        // Get businessId from user
        const userStr = localStorage.getItem('user');
        let currentBusinessId: number | null = null;

        if (userStr) {
          const user = JSON.parse(userStr);
          if (user.role === 'EMPLOYER' && user.businessPersonId) {
            try {
              const employerResponse = await getEmployer(user.businessPersonId);
              currentBusinessId = employerResponse.data.companyId;
            } catch (error) {
              console.error('Failed to get employer:', error);
            }
          }
        }

        if (currentBusinessId) {
          const [employeesResponse, employersResponse] = await Promise.all([
            getEmployeesByBusiness(currentBusinessId),
            getEmployersByBusiness(currentBusinessId),
          ]);

          setEmployees(employeesResponse.data);
          setEmployers(employersResponse.data);
        }
      } catch (error) {
        console.error('Failed to fetch company data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  // Build tree structure from backend data
  const fullTree = useMemo(() => {
    const tree = buildOrgTree(employees, employers);
    
    return tree;
  }, [employees, employers]);

  const {
    viewMode,
    currentRoot,
    openDepartment,
    handleSearch,
    searchHighlight,
    expandedPath,
    resetView,
  } = useCompanyNavigation(fullTree);

  // Extract departments from tree structure
  const departments = useMemo(() => {
    return extractDepartments(fullTree, employers);
  }, [fullTree, employers]);

  // Flatten tree for manager lookup in modal
  const allPeople = useMemo(() => {
    return flattenTree(fullTree);
  }, [fullTree]);

  // Local state for manual toggle in Tree Mode
  const [localExpanded, setLocalExpanded] = useState<Set<number>>(new Set());

  // Merge auto-expanded paths (from search) with manual toggles
  const isNodeExpanded = (id: number) => localExpanded.has(id) || expandedPath.has(id);

  const toggleNode = (id: number) => {
    const next = new Set(localExpanded);
    if (next.has(id)) next.delete(id);
    else next.add(id);
    setLocalExpanded(next);
  };

  const onSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    handleSearch(searchQuery);
  };

  // Close filter menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as HTMLElement;
      if (showFilterMenu && !target.closest('.filter-menu-container')) {
        setShowFilterMenu(false);
      }
    };

    if (showFilterMenu) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [showFilterMenu]);

  // Render Mind Map - Let MindMapNode handle recursion internally
  const renderMindMap = (): React.ReactNode => {
    if (!currentRoot) return null;

    const expanded = isNodeExpanded(currentRoot.id);
    const highlighted = currentRoot.id === searchHighlight;

    return (
      <MindMapNode 
        node={currentRoot}
        isExpanded={expanded}
        isHighlighted={highlighted}
        onToggle={toggleNode}
        onSelect={setSelectedEmp}
        depth={0}
        getExpandedState={isNodeExpanded}
        getHighlightedState={(id) => id === searchHighlight}
      />
    );
  };

  if (loading) {
    return (
      <div className="flex h-96 items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!fullTree) {
    return (
      <div className="max-w-7xl mx-auto p-6">
        <div className="text-center py-12">
          <p className="text-gray-500">No organization data available.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto p-6 h-[calc(100vh-20px)] flex flex-col"> 
      
      {/* 1. Header (Fixed Height) */}
      <div className="flex-none flex flex-col md:flex-row justify-between gap-4 bg-white p-4 rounded-xl shadow-md border border-slate-50 z-20 mb-6">
        {/* ... Header Content ... */}
        <div className="flex items-center gap-4 flex-1">
          {viewMode === 'MICRO_TREE' && (
            <Button variant="secondary" onClick={resetView}>
              <ArrowLeft size={18} className="mr-2" />
              Back
            </Button>
          )}
          <form onSubmit={onSearchSubmit} className="relative w-full max-w-md">
            <Search className="absolute left-3 top-2.5 text-gray-400" size={18} />
            <input
              type="text"
              placeholder="Search..."
              className="w-full pl-10 pr-4 py-2 bg-gray-50 border-none rounded-lg focus:ring-2 focus:ring-blue-500"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </form>
        </div>
        <div className="flex gap-2">
          {viewMode === 'MICRO_TREE' && (
            <div className="relative filter-menu-container">
              <Button 
                variant="secondary" 
                onClick={() => setShowFilterMenu(!showFilterMenu)}
              >
                <Filter size={18} className="mr-2" />
                Filter Dept
              </Button>
              {showFilterMenu && (
                <div className="absolute right-0 top-full mt-2 w-64 bg-white rounded-xl shadow-lg border border-slate-200 z-30 py-2 max-h-96 overflow-y-auto">
                  <div className="px-4 py-2 border-b border-gray-100">
                    <p className="text-sm font-semibold text-gray-700">Departments</p>
                  </div>
                  {departments.map((dept) => (
                    <button
                      key={dept.name}
                      onClick={() => {
                        openDepartment(dept.headId);
                        setShowFilterMenu(false);
                      }}
                      className="w-full text-left px-4 py-2 hover:bg-blue-50 transition-colors"
                    >
                      <p className="text-sm font-medium text-gray-900">{dept.name}</p>
                      <p className="text-xs text-gray-500">{dept.employeeCount} members</p>
                    </button>
                  ))}
                  <button
                    onClick={() => {
                      resetView();
                      setShowFilterMenu(false);
                    }}
                    className="w-full text-left px-4 py-2 hover:bg-gray-50 transition-colors border-t border-gray-100 mt-1"
                  >
                    <p className="text-sm font-medium text-gray-700">Show All</p>
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* 2. Content Area (Fills remaining space) */}
      <div className="flex-1 min-h-0 relative"> 
        {/* min-h-0 is crucial for nested flex scrolling */}

        {/* MACRO VIEW */}
        {viewMode === 'MACRO_GRID' && (
          <div className="h-full overflow-y-auto pb-10">
            <h2 className="text-lg font-bold text-slate-900 px-1 mb-4">Departments Overview</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {departments.map((dept) => (
                <DepartmentCard
                  key={dept.name}
                  dept={dept}
                  onClick={() => openDepartment(dept.headId)}
                />
              ))}
            </div>
          </div>
        )}

        {/* MICRO VIEW - Fixed Scrolling Container */}
        {viewMode === 'MICRO_TREE' && currentRoot && (
          <div className="bg-slate-50/50 rounded-2xl border border-slate-200 h-full relative flex flex-col">
            
            {/* Badge */}
            <div className="absolute top-4 left-4 z-20">
              <span className="text-xs font-bold text-blue-600 bg-blue-50 px-3 py-1 rounded-full border border-blue-100">
                Department: {currentRoot.department}
              </span>
            </div>

            {/* SCROLLABLE CANVAS */}
            <div className="flex-1 overflow-auto">
               {/* - min-h-full: Ensures the container is at least as tall as the screen
                  - flex: Allows using margin:auto for centering
               */}
               <div className="min-h-full w-full flex pl-20 pr-10">
                  {/* renderMindMap returns the Root Node which has 'my-auto' */}
                  {renderMindMap()}
               </div>
            </div>
          </div>
        )}
      </div>

      {/* 4. Action Modal */}
      <EmployeeModal
        isOpen={!!selectedEmp}
        onClose={() => setSelectedEmp(null)}
        employee={selectedEmp}
        mode="EMPLOYER"
        allPeople={allPeople}
      />
    </div>
  );
}