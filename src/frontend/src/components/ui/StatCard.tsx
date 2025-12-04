import { Card } from './Card';
import { ArrowUpRight, ArrowDownRight } from 'lucide-react';

interface StatCardProps {
  title: string;
  value: string | number;
  icon: React.ReactNode;
  trend?: {
    value: number;
    isPositive: boolean;
    label: string; // e.g., "vs last month"
    showPercent?: boolean; // Optional: show % sign (default: false)
  };
  color?: 'blue' | 'indigo' | 'green' | 'orange';
}

export const StatCard = ({ title, value, icon, trend, color = 'blue' }: StatCardProps) => {
  const colorStyles = {
    blue: 'bg-blue-50 text-blue-600',
    indigo: 'bg-indigo-50 text-indigo-600',
    green: 'bg-green-50 text-green-600',
    orange: 'bg-orange-50 text-orange-600',
  };

  return (
    <Card className="hover:translate-y-[-2px] transition-transform duration-300" padding="lg">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm font-medium text-gray-500 mb-1">{title}</p>
          <h3 className="text-2xl font-bold text-slate-900 tracking-tight">{value}</h3>
        </div>
        <div className={`p-3 rounded-xl ${colorStyles[color]}`}>
          {icon}
        </div>
      </div>
      
      {trend && (
        <div className="mt-4 flex items-center gap-2 text-sm">
          <span className={`flex items-center font-semibold ${trend.isPositive ? 'text-green-600' : 'text-red-600'}`}>
            {trend.isPositive ? <ArrowUpRight size={16} /> : <ArrowDownRight size={16} />}
            {Math.abs(trend.value)}{trend.showPercent ? '%' : ''}
          </span>
          <span className="text-gray-400">{trend.label}</span>
        </div>
      )}
    </Card>
  );
};

