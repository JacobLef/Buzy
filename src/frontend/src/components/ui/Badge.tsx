import React from 'react';

type BadgeVariant = 'success' | 'warning' | 'error' | 'neutral' | 'blue';

interface BadgeProps {
  children: React.ReactNode;
  variant?: BadgeVariant;
  className?: string;
}

export const Badge = ({ children, variant = 'neutral', className = '' }: BadgeProps) => {
  const styles = {
    success: 'bg-green-50 text-green-700 border-green-100',
    warning: 'bg-yellow-50 text-yellow-700 border-yellow-100',
    error: 'bg-red-50 text-red-700 border-red-100',
    neutral: 'bg-gray-100 text-gray-700 border-gray-200',
    blue: 'bg-blue-50 text-blue-700 border-blue-100',
  };

  return (
    <span className={`px-2.5 py-1 rounded-full text-xs font-semibold border ${styles[variant]} inline-flex items-center gap-1.5 ${className}`}>
      {children}
    </span>
  );
};

