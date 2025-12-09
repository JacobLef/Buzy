import React from 'react';

interface CardProps {
  children: React.ReactNode;
  className?: string;
  padding?: 'none' | 'sm' | 'md' | 'lg';
}

export const Card = ({ children, className = '', padding = 'md' }: CardProps) => {
  const paddings = {
    none: '',
    sm: 'p-4',
    md: 'p-6',
    lg: 'p-8'
  };

  return (
    <div 
      className={`bg-white rounded-xl border border-slate-100 ${paddings[padding]} ${className}`}
      style={{ boxShadow: '0 4px 20px -2px rgba(0, 0, 0, 0.05)' }}
    >
      {children}
    </div>
  );
};

interface CardHeaderProps {
  title: string;
  subtitle?: string;
  icon?: React.ReactNode;
  action?: React.ReactNode;
}

export const CardHeader = ({ title, subtitle, icon, action }: CardHeaderProps) => (
  <div className="flex justify-between items-start mb-6">
    <div className="flex gap-3 items-center">
      {icon && <div className="p-2 bg-blue-50 text-blue-600 rounded-lg">{icon}</div>}
      <div>
        <h3 className="text-lg font-bold text-slate-900 tracking-tight">{title}</h3>
        {subtitle && <p className="text-sm text-gray-500 mt-1">{subtitle}</p>}
      </div>
    </div>
    {action && <div>{action}</div>}
  </div>
);

