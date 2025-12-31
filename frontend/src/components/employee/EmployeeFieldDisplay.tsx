import React from 'react';

interface EmployeeFieldDisplayProps {
  icon: React.ReactNode;
  label: string;
  value: string | number | null | undefined;
  className?: string;
}

export const EmployeeFieldDisplay = ({
  icon,
  label,
  value,
  className = ''
}: EmployeeFieldDisplayProps) => {
  const displayValue = value ?? 'N/A';

  return (
    <div className={`flex items-center gap-4 ${className}`}>
      <div className="p-3 bg-gray-100 rounded-lg">
        {icon}
      </div>
      <div>
        <p className="text-sm text-gray-500">{label}</p>
        <p className="text-sm font-medium text-slate-900">{displayValue}</p>
      </div>
    </div>
  );
};

