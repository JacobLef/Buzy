import { useState, useEffect, useMemo } from 'react';
import { Search, Filter, ArrowLeft } from 'lucide-react';
import { MindMapNode } from '../../components/company/MindMapNode';
import { EmployeeModal } from '../../components/company/EmployeeModal';
import { Button } from '../../components/ui/Button';
import { buildOrgTree, flattenTree, getPathToNode, findNodeById } from '../../utils/orgTreeBuilder';
import { getEmployeesByBusiness, getEmployee } from '../../api/employees';
import { getEmployersByBusiness } from '../../api/employers';
import type { EmployeeNode, DepartmentSummary } from '../../types/company';
import type { Employee } from '../../types/employee';
import type { Employer } from '../../types/employer';

/**
 * Extract department information from tree
 */
function extractDepartments(
  tree: EmployeeNode | null,
  employers: Employer[]
): DepartmentSummary[] {
  if (!tree) return [];

  const deptMap = new Map<string, { headId: number; headName: string; members: Set<number> }>();

  const collectMembers = (node: EmployeeNode) => {
    const employer = employers.find(e => e.id === node.id);
    
    if (employer && employer.department) {
      const deptName = employer.department;
      
      if (!deptMap.has(deptName)) {
        deptMap.set(deptName, {
          headId: node.id,
          headName: node.name,
          members: new Set(),
        });
      }
      
      deptMap.get(deptName)!.members.add(node.id);
    }
    
    if (node.children) {
      node.children.forEach(child => {
        const childEmployer = employers.find(e => e.id === child.id);
        const childDept = childEmployer?.department || node.department;
        
        if (deptMap.has(childDept)) {
          deptMap.get(childDept)!.members.add(child.id);
        }
        
        collectMembers(child);
      });
    }
  };

  if (tree.children) {
    tree.children.forEach(child => collectMembers(child));
  }

  return Array.from(deptMap.entries()).map(([name, data]) => ({
    name,
    headId: data.headId,
    headName: data.headName,
    employeeCount: data.members.size,
  }));
}

export default function EmployeeDirectory() {
  const [selectedEmp, setSelectedEmp] = useState<EmployeeNode | null>(null);
  const [loading, setLoading] = useState(true);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [employers, setEmployers] = useState<Employer[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [expandedNodes, setExpandedNodes] = useState<Set<number>>(new Set());
  const [highlightedNode, setHighlightedNode] = useState<number | null>(null);
  const [currentRoot, setCurrentRoot] = useState<EmployeeNode | null>(null);
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
          if (user.role === 'EMPLOYEE' && user.businessPersonId) {
            // For employees, get their own data first to find companyId
            try {
              const employeeResponse = await getEmployee(user.businessPersonId);
              currentBusinessId = employeeResponse.data.companyId;
            } catch (error) {
              console.error('Failed to get employee data:', error);
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
    return buildOrgTree(employees, employers);
  }, [employees, employers]);

  // Set initial root
  useEffect(() => {
    if (fullTree && !currentRoot) {
      setCurrentRoot(fullTree);
    }
  }, [fullTree, currentRoot]);

  // Extract departments from tree structure
  const departments = useMemo(() => {
    return extractDepartments(fullTree, employers);
  }, [fullTree, employers]);

  // Flatten tree for manager lookup in modal
  const allPeople = useMemo(() => {
    return flattenTree(fullTree);
  }, [fullTree]);

  // Handle search
  useEffect(() => {
    if (!searchQuery.trim() || !fullTree) {
      setHighlightedNode(null);
      setExpandedNodes(new Set());
      setCurrentRoot(fullTree);
      return;
    }

    const queryLower = searchQuery.toLowerCase();
    const allNodes = flattenTree(fullTree);

    const match = allNodes.find(
      (node) =>
        node.name.toLowerCase().includes(queryLower) ||
        node.position.toLowerCase().includes(queryLower) ||
        node.department.toLowerCase().includes(queryLower) ||
        node.email.toLowerCase().includes(queryLower)
    );

    if (match) {
      setHighlightedNode(match.id);
      setCurrentRoot(fullTree); // Show full tree context
      const path = getPathToNode(match.id, fullTree);
      if (path) {
        setExpandedNodes(new Set(path));
      }
    } else {
      setHighlightedNode(null);
    }
  }, [searchQuery, fullTree]);

  // Handle department filter
  const openDepartment = (headId: number) => {
    if (!fullTree) return;
    const rootNode = findNodeById(headId, fullTree);
    if (rootNode) {
      setCurrentRoot(rootNode);
      setSearchQuery('');
      setHighlightedNode(null);
      setExpandedNodes(new Set());
      setShowFilterMenu(false);
    }
  };

  const resetView = () => {
    setCurrentRoot(fullTree);
    setSearchQuery('');
    setHighlightedNode(null);
    setExpandedNodes(new Set());
    setShowFilterMenu(false);
  };

  const onSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Search is handled by useEffect
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

  const toggleNode = (id: number) => {
    const next = new Set(expandedNodes);
    if (next.has(id)) next.delete(id);
    else next.add(id);
    setExpandedNodes(next);
  };

  const isNodeExpanded = (id: number) => expandedNodes.has(id);

  // Render Mind Map - Let MindMapNode handle recursion internally
  const renderMindMap = (): React.ReactNode => {
    if (!currentRoot) return null;

    const expanded = isNodeExpanded(currentRoot.id);
    const highlighted = currentRoot.id === highlightedNode;

    return (
      <MindMapNode 
        node={currentRoot}
        isExpanded={expanded}
        isHighlighted={highlighted}
        onToggle={toggleNode}
        onSelect={setSelectedEmp}
        depth={0}
        getExpandedState={isNodeExpanded}
        getHighlightedState={(id) => id === highlightedNode}
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
      {/* 1. Header (Fixed Height) - Same as Employer View */}
      <div className="flex-none flex flex-col md:flex-row justify-between gap-4 bg-white p-4 rounded-xl shadow-md border border-slate-50 z-20 mb-6">
        <div className="flex items-center gap-4 flex-1">
          {currentRoot && currentRoot.id !== fullTree?.id && (
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
                    onClick={() => openDepartment(dept.headId)}
                    className="w-full text-left px-4 py-2 hover:bg-blue-50 transition-colors"
                  >
                    <p className="text-sm font-medium text-gray-900">{dept.name}</p>
                    <p className="text-xs text-gray-500">{dept.employeeCount} members</p>
                  </button>
                ))}
                <button
                  onClick={resetView}
                  className="w-full text-left px-4 py-2 hover:bg-gray-50 transition-colors border-t border-gray-100 mt-1"
                >
                  <p className="text-sm font-medium text-gray-700">Show All</p>
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* 2. Content Area (Fills remaining space) */}
      <div className="flex-1 min-h-0 relative">
        {/* Mind Map View */}
        <div className="bg-slate-50/50 rounded-2xl border border-slate-200 h-full relative flex flex-col">
          {/* Context Header */}
          <div className="absolute top-4 left-4 z-20 flex items-center gap-2">
            <span className="text-xs font-bold text-blue-600 bg-blue-50 px-3 py-1 rounded-full border border-blue-100">
              {currentRoot && currentRoot.id !== fullTree?.id 
                ? `Department: ${currentRoot.department}` 
                : 'Company Directory'}
            </span>
            {currentRoot && currentRoot.id !== fullTree?.id && (
              <span className="text-xs text-gray-400">
                {currentRoot.name}'s Hierarchy
              </span>
            )}
          </div>

          {/* SCROLLABLE CANVAS */}
          <div className="flex-1 overflow-auto">
            <div className="min-h-full w-full flex pl-20 pr-10">
              {renderMindMap()}
            </div>
          </div>
        </div>
      </div>

      {/* 3. Employee Modal */}
      <EmployeeModal
        isOpen={!!selectedEmp}
        onClose={() => setSelectedEmp(null)}
        employee={selectedEmp}
        mode="EMPLOYEE"
        allPeople={allPeople}
      />
    </div>
  );
}

