import React from 'react';
import { Card } from '../ui/Card';
import { Users, ChevronRight, PieChart } from 'lucide-react';
import type { DepartmentSummary } from '../../types/company';

interface DepartmentCardProps {
  dept: DepartmentSummary;
  onClick: () => void;
}

export const DepartmentCard: React.FC<DepartmentCardProps> = ({ dept, onClick }) => (
  <Card
    className="group cursor-pointer hover:border-blue-200 transition-all duration-300 hover:shadow-lg"
    padding="md"
  >
    <div onClick={onClick} className="h-full flex flex-col justify-between">
      <div>
        <div className="flex justify-between items-start mb-4">
          <div className="p-3 bg-blue-50 text-blue-600 rounded-xl group-hover:bg-blue-600 group-hover:text-white transition-colors">
            <PieChart size={20} />
          </div>
          <span className="text-xs font-bold text-gray-400 uppercase tracking-wider bg-gray-50 px-2 py-1 rounded-full">
            Active
          </span>
        </div>

        <h3 className="text-xl font-bold text-slate-900 mb-1">{dept.name}</h3>
        <p className="text-sm text-gray-500">Lead: {dept.headName}</p>
      </div>

      <div className="mt-6 pt-6 border-t border-gray-100 flex items-center justify-between">
        <div className="flex items-center gap-2 text-gray-600">
          <Users size={16} />
          <span className="font-semibold">{dept.employeeCount}</span>
          <span className="text-xs text-gray-400">Members</span>
        </div>

        <div className="flex items-center gap-1 text-blue-600 text-sm font-medium group-hover:translate-x-1 transition-transform">
          View Org <ChevronRight size={16} />
        </div>
      </div>
    </div>
  </Card>
);

