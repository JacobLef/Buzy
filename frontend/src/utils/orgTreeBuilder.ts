import type { EmployeeNode } from '../types/company';
import type { Employee } from '../types/employee';
import type { Employer } from '../types/employer';

/**
 * Convert flat list of employees and employers into a hierarchical tree structure
 * 
 * Key Principles:
 * 1. Respect ALL managerId relationships (employers can manage employers)
 * 2. Each department head (employer with no manager in their dept) is a subtree root
 * 3. Create virtual root if no CEO exists
 * 4. General department for unmanaged employees
 */
export function buildOrgTree(
  employees: Employee[],
  employers: Employer[]
): EmployeeNode | null {
  if (employees.length === 0 && employers.length === 0) {
    return null;
  }

  // Step 1: Create unified person list
  const allPeople: Array<EmployeeNode & { isEmployer: boolean; rawDepartment?: string }> = [
    ...employers.map(emp => ({
      id: emp.id,
      name: emp.name,
      position: emp.title,
      department: emp.department,
      rawDepartment: emp.department, // Store original for dept head detection
      email: emp.email,
      managerId: null, // Employers don't have managers in current model, but can be extended
      salary: emp.salary,
      isEmployer: true,
      status: emp.status,
      hireDate: emp.hireDate,
      children: [],
    })),
    ...employees.map(emp => ({
      id: emp.id,
      name: emp.name,
      position: emp.position,
      department: getDepartmentFromEmployee(emp, employers),
      email: emp.email,
      managerId: emp.managerId || null,
      salary: emp.salary,
      isEmployer: false,
      status: emp.status,
      hireDate: emp.hireDate,
      children: [],
    })),
  ];

  // Step 2: Create lookup map
  const nodeMap = new Map<number, EmployeeNode>();
  allPeople.forEach(person => {
    nodeMap.set(person.id, {
      id: person.id,
      name: person.name,
      position: person.position,
      department: person.department,
      email: person.email,
      managerId: person.managerId,
      salary: person.salary,
      status: person.status,
      hireDate: person.hireDate,
      children: [],
    });
  });

  // Step 3: Build parent-child relationships (respecting ALL managerId)
  nodeMap.forEach((node) => {
    if (node.managerId && nodeMap.has(node.managerId)) {
      const manager = nodeMap.get(node.managerId)!;
      if (!manager.children) {
        manager.children = [];
      }
      // Avoid duplicates
      if (!manager.children.some(child => child.id === node.id)) {
        manager.children.push(node);
      }
    }
  });

  // Step 4: Find TRUE root nodes (no manager OR manager not in dataset)
  const trueRoots: EmployeeNode[] = [];
  nodeMap.forEach((node) => {
    if (!node.managerId || !nodeMap.has(node.managerId)) {
      trueRoots.push(node);
    }
  });

  // Step 5: Handle root selection
  let finalRoot: EmployeeNode;

  if (trueRoots.length === 0) {
    console.error('No root nodes found - circular reference issue');
    return null;
  }

  if (trueRoots.length === 1) {
    // Single root - use it
    finalRoot = trueRoots[0];
  } else {
    // Multiple roots - check for CEO
    const ceo = trueRoots.find(node => {
      const person = allPeople.find(p => p.id === node.id);
      if (!person?.isEmployer) return false;
      const title = node.position.toLowerCase();
      return title.includes('ceo') || 
             title.includes('chief executive') ||
             title.includes('president');
    });

    if (ceo) {
      // CEO exists - make other roots report to CEO
      finalRoot = ceo;
      trueRoots.forEach(node => {
        if (node.id !== ceo.id) {
          if (!ceo.children) ceo.children = [];
          if (!ceo.children.some(child => child.id === node.id)) {
            ceo.children.push(node);
            node.managerId = ceo.id; // Update managerId for consistency
          }
        }
      });
    } else {
      // No CEO - create virtual root
      finalRoot = {
        id: -1, // Virtual node ID
        name: 'Organization',
        position: 'Root',
        department: 'Executive',
        email: '',
        managerId: null,
        salary: 0,
        status: 'ACTIVE',
        hireDate: new Date().toISOString().split('T')[0],
        children: trueRoots,
      };
    }
  }

  // Step 6: Handle unmanaged employees (create General department dummy)
  const unmanagedEmployees = employees.filter(emp => 
    !emp.managerId || !nodeMap.has(emp.managerId)
  );

  if (unmanagedEmployees.length > 0) {
    // Create virtual "General Manager" node
    const generalManager: EmployeeNode = {
      id: -2, // Virtual node ID
      name: 'General Department',
      position: 'Department Head',
      department: 'General',
      email: '',
      managerId: finalRoot.id,
      salary: 0,
      status: 'ACTIVE',
      hireDate: new Date().toISOString().split('T')[0],
      children: unmanagedEmployees.map(emp => nodeMap.get(emp.id)!).filter(Boolean),
    };

    // Add to final root
    if (!finalRoot.children) finalRoot.children = [];
    finalRoot.children.push(generalManager);
  }

  // Step 7: Sort children (employers first, then by name)
  const sortChildren = (node: EmployeeNode) => {
    if (node.children && node.children.length > 0) {
      node.children.sort((a, b) => {
        const aIsEmployer = allPeople.find(p => p.id === a.id)?.isEmployer || false;
        const bIsEmployer = allPeople.find(p => p.id === b.id)?.isEmployer || false;
        
        // Employers first
        if (aIsEmployer !== bIsEmployer) {
          return aIsEmployer ? -1 : 1;
        }
        
        // Then by name
        return a.name.localeCompare(b.name);
      });
      
      node.children.forEach(sortChildren);
    }
  };

  sortChildren(finalRoot);

  return finalRoot;
}

/**
 * Get department for an employee based on their manager's department
 */
function getDepartmentFromEmployee(employee: Employee, employers: Employer[]): string {
  if (employee.managerId) {
    const manager = employers.find(emp => emp.id === employee.managerId);
    if (manager) {
      return manager.department;
    }
  }
  // Default department if no manager found
  return 'General';
}

/**
 * Flatten tree to get all nodes
 */
export function flattenTree(node: EmployeeNode | null): EmployeeNode[] {
  if (!node) return [];
  
  const result: EmployeeNode[] = [node];
  if (node.children && node.children.length > 0) {
    node.children.forEach(child => {
      result.push(...flattenTree(child));
    });
  }
  return result;
}

/**
 * Find a node in the tree by ID
 */
export function findNodeById(
  id: number,
  node: EmployeeNode | null
): EmployeeNode | null {
  if (!node) return null;
  if (node.id === id) return node;
  
  if (node.children) {
    for (const child of node.children) {
      const found = findNodeById(id, child);
      if (found) return found;
    }
  }
  
  return null;
}

/**
 * Get path from root to target node
 */
export function getPathToNode(
  targetId: number,
  node: EmployeeNode | null,
  path: number[] = []
): number[] | null {
  if (!node) return null;
  
  const currentPath = [...path, node.id];
  
  if (node.id === targetId) {
    return currentPath;
  }
  
  if (node.children) {
    for (const child of node.children) {
      const result = getPathToNode(targetId, child, currentPath);
      if (result) return result;
    }
  }
  
  return null;
}

/**
 * Debug function: Print tree structure to console
 * Helps diagnose hierarchy issues
 */
export function debugTreeStructure(
  node: EmployeeNode | null,
  indent: string = '',
  isLast: boolean = true
): void {
  if (!node) {
    console.log('Tree is null or empty');
    return;
  }

  const marker = isLast ? '└── ' : '├── ';
  console.log(
    `${indent}${marker}${node.name} (ID: ${node.id}, Position: ${node.position}, ManagerID: ${node.managerId || 'None'})`
  );

  if (node.children && node.children.length > 0) {
    const childIndent = indent + (isLast ? '    ' : '│   ');
    node.children.forEach((child, index) => {
      debugTreeStructure(child, childIndent, index === node.children!.length - 1);
    });
  }
}

/**
 * Debug function: Validate tree structure
 * Checks for common issues like circular references, orphaned nodes, etc.
 */
export function validateTreeStructure(
  root: EmployeeNode | null,
  employees: Employee[],
  employers: Employer[]
): { isValid: boolean; issues: string[] } {
  const issues: string[] = [];
  const visitedIds = new Set<number>();

  if (!root) {
    issues.push('Tree root is null');
    return { isValid: false, issues };
  }

  // Check for circular references and orphaned nodes
  const validateNode = (node: EmployeeNode, path: number[] = []): void => {
    if (visitedIds.has(node.id)) {
      issues.push(`Circular reference detected: Node ${node.id} (${node.name}) appears multiple times`);
      return;
    }

    if (path.includes(node.id)) {
      issues.push(`Circular reference in path: ${path.join(' -> ')} -> ${node.id}`);
      return;
    }

    visitedIds.add(node.id);
    const newPath = [...path, node.id];

    // Verify manager relationship
    if (node.managerId) {
      const managerExists = 
        employees.some(e => e.id === node.managerId) ||
        employers.some(e => e.id === node.managerId);
      
      if (!managerExists) {
        issues.push(`Node ${node.id} (${node.name}) has managerId ${node.managerId} but manager not found in data`);
      }
    }

    if (node.children) {
      node.children.forEach(child => {
        validateNode(child, newPath);
      });
    }
  };

  validateNode(root);

  // Check for orphaned nodes (nodes not in the tree)
  const allNodeIds = new Set(flattenTree(root).map(n => n.id));
  const allPeopleIds = new Set([
    ...employees.map(e => e.id),
    ...employers.map(e => e.id),
  ]);

  allPeopleIds.forEach(id => {
    if (!allNodeIds.has(id)) {
      const person = employees.find(e => e.id === id) || employers.find(e => e.id === id);
      issues.push(`Orphaned node: ${person?.name} (ID: ${id}) is not in the tree structure`);
    }
  });

  return {
    isValid: issues.length === 0,
    issues,
  };
}

