import React from 'react';
import type { EmployeeNode } from '../../types/company';
import { Users, ChevronUp, MoreHorizontal } from 'lucide-react';

interface OrgNodeProps {
  node: EmployeeNode;
  isExpanded: boolean;
  isHighlighted: boolean;
  onToggle: (id: number) => void;
  onClick: (node: EmployeeNode) => void;
  depth: number;
}

export const OrgNode: React.FC<OrgNodeProps> = ({
  node,
  isExpanded,
  isHighlighted,
  onToggle,
  onClick,
  depth,
}) => {
  const hasChildren = node.children && node.children.length > 0;
  const isDeptHead = depth === 0; // The root of the current view

  return (
    <div className="flex flex-col items-center relative z-10">
      {depth > 0 && <div className="w-px h-8 bg-gray-300" />}

      <div
        id={`node-${node.id}`}
        className={`relative group transition-all duration-500 ${isHighlighted ? 'scale-105 z-20' : ''}`}
      >
        <div
          onClick={() => onClick(node)}
          className={`
            w-64 p-4 rounded-xl border flex items-center gap-4 cursor-pointer bg-white transition-all
            ${isHighlighted
              ? 'border-blue-500 shadow-[0_0_20px_rgba(59,130,246,0.2)] ring-2 ring-blue-100'
              : 'border-slate-200 shadow-md hover:shadow-lg hover:border-blue-200'
            }
            ${isDeptHead ? 'border-t-4 border-t-slate-900' : ''}
          `}
        >
          {/* Avatar/Initials */}
          <div
            className={`w-12 h-12 rounded-full flex items-center justify-center font-bold text-lg border-2 border-white shadow-sm ${
              isHighlighted ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-500'
            }`}
          >
            {node.name.charAt(0).toUpperCase()}
          </div>

          <div className="flex-1 min-w-0">
            <p className="font-bold text-slate-900 truncate">{node.position}</p>
            <p className="text-sm text-gray-500 truncate">{node.name}</p>
          </div>

          {/* Context Menu Trigger (Placeholder) */}
          <button
            className="p-1.5 text-gray-300 hover:text-slate-900 hover:bg-gray-50 rounded-lg"
            onClick={(e) => {
              e.stopPropagation();
              // TODO: Open context menu
            }}
          >
            <MoreHorizontal size={16} />
          </button>
        </div>

        {/* Scalability Feature: The "Stack" Indicator */}
        {hasChildren && !isExpanded && (
          <button
            onClick={(e) => {
              e.stopPropagation();
              onToggle(node.id);
            }}
            className="absolute -bottom-3 left-1/2 -translate-x-1/2 bg-white border border-slate-200 shadow-sm px-3 py-1 rounded-full text-[10px] font-bold text-gray-500 flex items-center gap-1 hover:text-blue-600 hover:border-blue-200 transition-colors"
          >
            <Users size={10} /> +{node.children?.length}
          </button>
        )}

        {/* Standard Toggle Button */}
        {hasChildren && isExpanded && (
          <button
            onClick={(e) => {
              e.stopPropagation();
              onToggle(node.id);
            }}
            className="absolute -bottom-3 left-1/2 -translate-x-1/2 w-6 h-6 bg-slate-900 text-white rounded-full flex items-center justify-center border-2 border-white shadow-md z-10 hover:scale-110 transition-transform"
          >
            <ChevronUp size={12} />
          </button>
        )}
      </div>
    </div>
  );
};

