import React from 'react';
import { Card } from '../ui/Card';
import { DollarSign, TrendingUp, Shield, PieChart } from 'lucide-react';

interface StatsProps {
  stats: {
    totalGross: number;
    totalNet: number;
    totalTax: number;
    totalInsurance: number;
  };
}

const StatItem = ({ label, value, icon, color }: { 
  label: string; 
  value: number; 
  icon: React.ReactNode; 
  color: string;
}) => (
  <Card padding="sm" className="flex flex-col justify-between h-full">
    <div className="flex justify-between items-start mb-2">
      <span className="text-xs font-bold text-gray-400 uppercase tracking-wider">{label}</span>
      <div className={`p-2 rounded-lg ${color}`}>
        {icon}
      </div>
    </div>
    <p className="text-xl font-bold text-navy-900">${value.toLocaleString()}</p>
  </Card>
);

export const YTDSummary = ({ stats }: StatsProps) => {
  return (
    <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
      <StatItem 
        label="YTD Gross Pay" 
        value={stats.totalGross} 
        icon={<DollarSign size={16} />} 
        color="bg-blue-50 text-blue-600" 
      />
      <StatItem 
        label="YTD Net Earnings" 
        value={stats.totalNet} 
        icon={<TrendingUp size={16} />} 
        color="bg-green-50 text-green-600" 
      />
      <StatItem 
        label="Taxes Paid" 
        value={stats.totalTax} 
        icon={<PieChart size={16} />} 
        color="bg-red-50 text-red-600" 
      />
      <StatItem 
        label="Insurance Paid" 
        value={stats.totalInsurance} 
        icon={<Shield size={16} />} 
        color="bg-orange-50 text-orange-600" 
      />
    </div>
  );
};

