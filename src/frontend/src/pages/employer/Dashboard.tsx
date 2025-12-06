import { useNavigate } from 'react-router-dom';
import { 
  Users, 
  Briefcase, 
  DollarSign, 
  GraduationCap, 
  Calculator, 
  Gift, 
  List, 
  Clock,
  ArrowRight
} from 'lucide-react';
import { useDashboard } from '../../hooks/useDashboard';
import { StatCard } from '../../components/ui/StatCard';
import { Card, CardHeader } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';

export default function EmployerDashboard() {
  const navigate = useNavigate();
  const { stats, recentActivity, loading } = useDashboard();

  // Helper for Recent Activity Icons
  const getActivityIcon = (type: string) => {
    switch (type) {
      case 'PAYROLL': return <DollarSign size={16} />;
      case 'TRAINING': return <GraduationCap size={16} />;
      case 'EMPLOYEE': return <Users size={16} />;
      default: return <Clock size={16} />;
    }
  };

  // Helper for Activity Colors
  const getActivityColor = (type: string) => {
    switch (type) {
      case 'PAYROLL': return 'bg-green-50 text-green-600';
      case 'TRAINING': return 'bg-orange-50 text-orange-600';
      case 'EMPLOYEE': return 'bg-blue-50 text-blue-600';
      default: return 'bg-gray-50 text-gray-600';
    }
  };

  if (loading) {
    return (
      <div className="space-y-8 max-w-7xl mx-auto px-4 py-6">
        <div className="flex h-96 items-center justify-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8 max-w-7xl mx-auto px-4 py-6 animate-in fade-in duration-500">
      
      {/* 1. Header Section */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-end gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 tracking-tight">Dashboard Overview</h1>
          <p className="text-gray-500 mt-2">Welcome back! Here's what's happening in your business today.</p>
        </div>
        <div className="text-right hidden md:block">
          <p className="text-sm font-medium text-slate-900">
            {new Date().toLocaleDateString('en-US', { 
              weekday: 'long', 
              year: 'numeric', 
              month: 'long', 
              day: 'numeric' 
            })}
          </p>
        </div>
      </div>

      {/* 2. Summary Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard 
          title="Total Employees" 
          value={stats.totalEmployees} 
          icon={<Users size={24} />} 
          color="blue"
        />
        <StatCard 
          title="Total Employers" 
          value={stats.totalEmployers} 
          icon={<Briefcase size={24} />} 
          color="indigo"
        />
        <StatCard 
          title="Monthly Payroll" 
          value={`$${stats.monthlyPayroll.toLocaleString()}`} 
          icon={<DollarSign size={24} />} 
          color="green"
        />
        <StatCard 
          title="Pending Trainings" 
          value={stats.pendingTrainings} 
          icon={<GraduationCap size={24} />} 
          color="orange"
        />
      </div>

      {/* 3. Main Content Split: Quick Actions (Left) & Recent Activity (Right) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Quick Actions Panel */}
        <div className="lg:col-span-1 space-y-6">
          <Card className="h-full">
            <CardHeader 
              title="Quick Actions" 
              subtitle="Common tasks" 
            />
            <div className="space-y-4">
              <Button 
                variant="primary" 
                className="w-full justify-between group"
                onClick={() => navigate('/employer/payroll')}
              >
                <span className="flex items-center gap-2">
                  <Calculator size={18} /> Calculate Payroll
                </span>
                <ArrowRight size={16} className="opacity-0 group-hover:opacity-100 transition-opacity" />
              </Button>
              
              <Button 
                variant="secondary" 
                className="w-full justify-between group"
                onClick={() => navigate('/employer/payroll?tab=bonus')}
              >
                <span className="flex items-center gap-2">
                  <Gift size={18} /> Distribute Bonus
                </span>
                <ArrowRight size={16} className="text-gray-400 group-hover:text-blue-600 transition-colors" />
              </Button>

              <Button 
                variant="secondary" 
                className="w-full justify-between group"
                onClick={() => navigate('/employer/employees')}
              >
                <span className="flex items-center gap-2">
                  <List size={18} /> Manage Employees
                </span>
                <ArrowRight size={16} className="text-gray-400 group-hover:text-blue-600 transition-colors" />
              </Button>
            </div>
            
            {/* Contextual Tip */}
            <div className="mt-8 p-4 bg-blue-50 rounded-xl border border-blue-100">
              <h4 className="text-sm font-bold text-blue-800 mb-1">Payroll Tip</h4>
              <p className="text-xs text-blue-600">
                Remember to review tax strategy settings before the end of the fiscal quarter.
              </p>
            </div>
          </Card>
        </div>

        {/* Recent Activity Feed */}
        <div className="lg:col-span-2">
          <Card className="h-full">
            <CardHeader 
              title="Recent Activity" 
              subtitle="Latest system events and deadlines"
            />
            
            <div className="space-y-1">
              {recentActivity.map((activity) => (
                <div 
                  key={activity.id} 
                  className="flex items-center gap-4 p-4 hover:bg-gray-50 rounded-lg transition-colors border-b border-gray-100 last:border-0"
                >
                  {/* Icon Bubble */}
                  <div className={`p-3 rounded-full ${getActivityColor(activity.type)}`}>
                    {getActivityIcon(activity.type)}
                  </div>

                  {/* Text Content */}
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-slate-900 truncate">
                      {activity.title}
                    </p>
                    <p className="text-xs text-gray-500">
                      {activity.type} • {activity.date}
                    </p>
                  </div>

                  {/* Status Indicator */}
                  <div>
                    {activity.status === 'completed' && <Badge variant="success">Done</Badge>}
                    {activity.status === 'warning' && <Badge variant="warning">Due Soon</Badge>}
                    {activity.status === 'alert' && <Badge variant="error">Action Req</Badge>}
                    {activity.status === 'info' && <Badge variant="blue">Info</Badge>}
                  </div>
                </div>
              ))}

              {recentActivity.length === 0 && (
                <div className="text-center py-12 text-gray-400">
                  No recent activity found.
                </div>
              )}
            </div>
            
            <div className="mt-4 pt-4 border-t border-gray-100 text-center">
              <button className="text-sm font-medium text-blue-600 hover:text-blue-700 hover:underline">
                View Full Activity Log
              </button>
            </div>
          </Card>
        </div>

      </div>
    </div>
  );
}
