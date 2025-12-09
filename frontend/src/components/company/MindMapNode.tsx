import { ChevronRight, ChevronDown, MoreHorizontal } from 'lucide-react';
import type { EmployeeNode } from '../../types/company';

interface MindMapNodeProps {
  node: EmployeeNode;
  isExpanded: boolean;
  isHighlighted: boolean;
  onToggle: (id: number) => void;
  onSelect: (node: EmployeeNode) => void;
  depth?: number;
  getExpandedState?: (id: number) => boolean;
  getHighlightedState?: (id: number) => boolean;
}

export const MindMapNode = ({ 
  node, 
  isExpanded, 
  isHighlighted, 
  onToggle, 
  onSelect, 
  depth = 0,
  getExpandedState,
  getHighlightedState
}: MindMapNodeProps) => {
  const hasChildren = node.children && node.children.length > 0;

  return (
    <div className="flex items-center">
      {/* 1. THE NODE CARD */}
      <div className="relative z-10 flex items-center my-2"> {/* Added my-2 for vertical spacing between leaves */}
        <div 
          onClick={() => onSelect(node)}
          className={`
            group flex items-center gap-3 p-3 rounded-xl border cursor-pointer min-w-[240px] bg-white relative transition-all z-10
            ${isHighlighted 
              ? 'border-blue-500 shadow-[0_0_15px_rgba(59,130,246,0.3)] ring-1 ring-blue-100 scale-105' 
              : 'border-slate-200 shadow-neo hover:shadow-neo-lg hover:border-blue-200'
            }
          `}
        >
          {/* Avatar */}
          <div className={`w-10 h-10 rounded-full flex items-center justify-center font-bold text-sm border-2 border-white shadow-sm ${isHighlighted ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-500'}`}>
            {node.name.charAt(0)}
          </div>

          <div className="flex-1 min-w-0">
            <p className="font-bold text-sm text-navy-900 truncate">{node.position}</p>
            <p className="text-xs text-gray-500 truncate">{node.name}</p>
          </div>

          {/* Toggle Button */}
          {hasChildren && (
            <button 
              onClick={(e) => { e.stopPropagation(); onToggle(node.id); }}
              className={`
                w-8 h-8 rounded-full flex items-center justify-center transition-colors
                ${isExpanded ? 'bg-navy-50 text-navy-600' : 'bg-gray-100 text-gray-400 hover:bg-blue-50 hover:text-blue-600'}
              `}
            >
              {isExpanded ? <ChevronDown size={18} /> : <ChevronRight size={18} />}
            </button>
          )}

          {/* Context Menu Placeholder */}
          {!hasChildren && (
            <button 
              onClick={(e) => { e.stopPropagation(); }}
              className="p-1.5 text-gray-300 hover:text-navy-900 hover:bg-gray-50 rounded-lg"
            >
              <MoreHorizontal size={14} />
            </button>
          )}
        </div>

        {/* Stack Indicator for Collapsed State */}
        {hasChildren && !isExpanded && (
          <div className="absolute left-full ml-[-8px] z-0">
            <span className="bg-slate-100 text-slate-500 text-[10px] font-bold px-2 py-0.5 rounded-r-lg border border-l-0 border-slate-200 pl-3">
              {node.children?.length}
            </span>
          </div>
        )}
      </div>

      {/* 2. CHILDREN CONTAINER */}
      {hasChildren && isExpanded && (
        <div className="flex flex-col justify-center relative ml-12"> 
          {/* The vertical spine logic handles visual connection */}
          
          {node.children!.map((child, index) => {
             const isFirst = index === 0;
             const isLast = index === node.children!.length - 1;
             const isSingle = node.children!.length === 1;

             return (
              <div key={child.id} className="relative flex items-center">
                {/* CONNECTOR LINES */}
                <div className="absolute -left-12 top-0 bottom-0 w-12 flex items-center pointer-events-none">
                   
                   {/* Horizontal Line to Child */}
                   <div className="absolute right-0 top-1/2 w-8 h-px bg-slate-300" />
                   
                   {/* Vertical Line Spine Logic */}
                   {!isSingle && (
                     <div 
                       className={`absolute right-8 w-px bg-slate-300
                         ${isFirst ? 'top-1/2 h-1/2' : ''} 
                         ${isLast ? 'top-0 h-1/2' : ''}
                         ${!isFirst && !isLast ? 'top-0 h-full' : ''}
                       `}
                     />
                   )}
                   
                   {/* Straight horizontal connector for single child (no fork) */}
                   {isSingle && (
                      <div className="absolute right-0 top-1/2 w-12 h-px bg-slate-300" />
                   )}
                </div>

                <MindMapNode 
                  node={child} 
                  isExpanded={getExpandedState ? getExpandedState(child.id) : false}
                  isHighlighted={getHighlightedState ? getHighlightedState(child.id) : false}
                  onToggle={onToggle}
                  onSelect={onSelect}
                  depth={depth + 1}
                  getExpandedState={getExpandedState}
                  getHighlightedState={getHighlightedState}
                />
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};