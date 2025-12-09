import { useState, useMemo, useCallback } from 'react';
import type { EmployeeNode, DepartmentSummary } from '../types/company';
import { findNodeById, getPathToNode, flattenTree } from '../utils/orgTreeBuilder';

export const useCompanyNavigation = (fullTree: EmployeeNode | null) => {
  const [viewMode, setViewMode] = useState<'MACRO_GRID' | 'MICRO_TREE'>('MACRO_GRID');
  const [currentRoot, setCurrentRoot] = useState<EmployeeNode | null>(fullTree);
  const [searchHighlight, setSearchHighlight] = useState<number | null>(null);
  const [expandedPath, setExpandedPath] = useState<Set<number>>(new Set());

  // 1. Flatten tree to extract Department Summaries for Macro View
  const departments = useMemo(() => {
    if (!fullTree) return [];

    const depts: Record<string, DepartmentSummary> = {};
    const allNodes = flattenTree(fullTree);

    // Group by department and find department heads
    allNodes.forEach((node) => {
      if (!depts[node.department]) {
        // Find the highest ranking person in this department (typically an employer)
        const deptMembers = allNodes.filter(n => n.department === node.department);
        const deptHead = deptMembers.find(n => !n.managerId) || deptMembers[0];

        depts[node.department] = {
          name: node.department,
          headName: deptHead.name,
          headId: deptHead.id,
          employeeCount: 0,
        };
      }
      depts[node.department].employeeCount++;
    });

    return Object.values(depts);
  }, [fullTree]);

  // 2. Drill Down Action
  const openDepartment = useCallback((headId: number) => {
    if (!fullTree) return;

    const rootNode = findNodeById(headId, fullTree);
    if (rootNode) {
      setCurrentRoot(rootNode);
      setViewMode('MICRO_TREE');
      setSearchHighlight(null);
      setExpandedPath(new Set());
    }
  }, [fullTree]);

  // 3. Search & Deep Link Logic
  const handleSearch = useCallback((query: string) => {
    if (!fullTree) return;

    if (!query.trim()) {
      setViewMode('MACRO_GRID');
      setSearchHighlight(null);
      setExpandedPath(new Set());
      return;
    }

    const queryLower = query.toLowerCase();
    const allNodes = flattenTree(fullTree);
    
    // Find matching nodes
    const matches = allNodes.filter(
      (node) =>
        node.name.toLowerCase().includes(queryLower) ||
        node.position.toLowerCase().includes(queryLower) ||
        node.department.toLowerCase().includes(queryLower) ||
        node.email.toLowerCase().includes(queryLower)
    );

    if (matches.length > 0) {
      // Use first match
      const targetNode = matches[0];
      const path = getPathToNode(targetNode.id, fullTree);

      // Switch to Tree Mode using GLOBAL root to show full context
      setCurrentRoot(fullTree);
      setViewMode('MICRO_TREE');

      // Auto-expand the path
      if (path) {
        setExpandedPath(new Set(path));
      }
      setSearchHighlight(targetNode.id);
    }
  }, [fullTree]);

  const resetView = useCallback(() => {
    setViewMode('MACRO_GRID');
    setCurrentRoot(fullTree);
    setSearchHighlight(null);
    setExpandedPath(new Set());
  }, [fullTree]);

  return {
    viewMode,
    departments,
    currentRoot,
    openDepartment,
    handleSearch,
    searchHighlight,
    expandedPath,
    resetView,
  };
};

