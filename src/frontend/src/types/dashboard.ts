export interface Activity {
  id: number;
  type: 'PAYROLL' | 'EMPLOYEE' | 'TRAINING';
  title: string;
  date: string; // Relative time like "2 hours ago"
  timestamp: string; // ISO datetime
  status: 'completed' | 'warning' | 'alert' | 'info';
}

