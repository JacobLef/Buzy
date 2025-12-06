import { useNavigate } from 'react-router-dom';
import { 
  GraduationCap, 
  DollarSign, 
  Calendar,
  Gift,
  AlertTriangle,
  ArrowRight,
  User,
  FileText
} from 'lucide-react';
import { useEmployeeDashboard } from '../../hooks/useEmployeeDashboard';
import { StatCard } from '../../components/ui/StatCard';
import { Card, CardHeader } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';

export default function EmployeeDashboard() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const { 
    employee, 
    trainings, 
    expiredTrainings, 
    recentPaychecks, 
    stats, 
    loading 
  } = useEmployeeDashboard(user.businessPersonId);

  // Helper for Training Status Badge
  const getTrainingStatus = (expirationDate: string) => {
    const today = new Date();
    const expDate = new Date(expirationDate);
    const daysUntil = Math.ceil((expDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
    
    if (daysUntil < 0) return { variant: 'error' as const, label: 'Expired' };
    if (daysUntil <= 30) return { variant: 'warning' as const, label: `${daysUntil} days left` };
    return { variant: 'success' as const, label: 'Valid' };
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
          <h1 className="text-3xl font-bold text-slate-900 tracking-tight">
            Welcome back, {employee?.name?.split(' ')[0] || 'Employee'}!
          </h1>
          <p className="text-gray-500 mt-2">
            {employee?.position} at {employee?.companyName}
          </p>
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
          title="Upcoming Trainings" 
          value={stats.upcomingTrainings} 
          icon={<GraduationCap size={24} />} 
          color="blue"
        />
        <StatCard 
          title="Expired Trainings" 
          value={stats.expiredTrainings} 
          icon={<AlertTriangle size={24} />} 
          color="orange"
          trend={stats.expiredTrainings > 0 ? { value: stats.expiredTrainings, isPositive: false, label: "Needs attention" } : undefined}
        />
        <StatCard 
          title="Next Pay Date" 
          value={stats.nextPayDate ? new Date(stats.nextPayDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }) : 'N/A'} 
          icon={<Calendar size={24} />} 
          color="green"
        />
        <StatCard 
          title="Last Bonus" 
          value={stats.lastBonus ? `$${stats.lastBonus.toLocaleString()}` : 'N/A'} 
          icon={<Gift size={24} />} 
          color="indigo"
        />
      </div>

      {/* 3. Main Content Split: Quick Actions (Left) & Alerts/Info (Right) */}
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
                onClick={() => navigate('/employee/profile')}
              >
                <span className="flex items-center gap-2">
                  <User size={18} /> View Profile
                </span>
                <ArrowRight size={16} className="opacity-0 group-hover:opacity-100 transition-opacity" />
              </Button>
              
              <Button 
                variant="secondary" 
                className="w-full justify-between group"
                onClick={() => navigate('/employee/training')}
              >
                <span className="flex items-center gap-2">
                  <GraduationCap size={18} /> My Trainings
                </span>
                <ArrowRight size={16} className="text-gray-400 group-hover:text-blue-600 transition-colors" />
              </Button>

              <Button 
                variant="secondary" 
                className="w-full justify-between group"
                onClick={() => navigate('/employee/payroll')}
              >
                <span className="flex items-center gap-2">
                  <FileText size={18} /> View Pay Stubs
                </span>
                <ArrowRight size={16} className="text-gray-400 group-hover:text-blue-600 transition-colors" />
              </Button>
            </div>
            
            {/* Expiring Training Alert */}
            {expiredTrainings.length > 0 && (
              <div className="mt-8 p-4 bg-red-50 rounded-xl border border-red-100">
                <h4 className="text-sm font-bold text-red-800 mb-1">Action Required</h4>
                <p className="text-xs text-red-600">
                  You have {expiredTrainings.length} expired training(s) that need renewal.
                </p>
              </div>
            )}

            {expiredTrainings.length === 0 && (
              <div className="mt-8 p-4 bg-green-50 rounded-xl border border-green-100">
                <h4 className="text-sm font-bold text-green-800 mb-1">All Caught Up!</h4>
                <p className="text-xs text-green-600">
                  Your trainings are up to date.
                </p>
              </div>
            )}
          </Card>
        </div>

        {/* Right Side: Trainings & Recent Pay */}
        <div className="lg:col-span-2 space-y-8">
          
          {/* Training Status */}
          <Card>
            <CardHeader 
              title="My Trainings" 
              subtitle="Certifications and compliance status"
            />
            
            <div className="space-y-1">
              {[...expiredTrainings, ...trainings].slice(0, 5).map((training) => {
                const status = getTrainingStatus(training.expiryDate);
                return (
                  <div 
                    key={training.id} 
                    className="flex items-center gap-4 p-4 hover:bg-gray-50 rounded-lg transition-colors border-b border-gray-100 last:border-0"
                  >
                    <div className={`p-3 rounded-full ${status.variant === 'error' ? 'bg-red-50 text-red-600' : status.variant === 'warning' ? 'bg-orange-50 text-orange-600' : 'bg-green-50 text-green-600'}`}>
                      <GraduationCap size={16} />
                    </div>

                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-slate-900 truncate">
                        {training.trainingName}
                      </p>
                      <p className="text-xs text-gray-500">
                        Expires: {new Date(training.expiryDate).toLocaleDateString()}
                      </p>
                    </div>

                    <Badge variant={status.variant}>{status.label}</Badge>
                  </div>
                );
              })}

              {trainings.length === 0 && expiredTrainings.length === 0 && (
                <div className="text-center py-12 text-gray-400">
                  No trainings assigned.
                </div>
              )}
            </div>
            
            <div className="mt-4 pt-4 border-t border-gray-100 text-center">
              <button 
                onClick={() => navigate('/employee/training')}
                className="text-sm font-medium text-blue-600 hover:text-blue-700 hover:underline"
              >
                View All Trainings
              </button>
            </div>
          </Card>

          {/* Recent Pay Stubs */}
          <Card>
            <CardHeader 
              title="Recent Pay Stubs" 
              subtitle="Your payment history"
            />
            
            <div className="space-y-1">
              {recentPaychecks.slice(0, 3).map((paycheck) => (
                <div 
                  key={paycheck.id} 
                  className="flex items-center gap-4 p-4 hover:bg-gray-50 rounded-lg transition-colors border-b border-gray-100 last:border-0"
                >
                  <div className="p-3 rounded-full bg-green-50 text-green-600">
                    <DollarSign size={16} />
                  </div>

                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-slate-900">
                      {new Date(paycheck.payDate).toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}
                    </p>
                    <p className="text-xs text-gray-500">
                      {paycheck.bonus ? `Includes $${paycheck.bonus.toLocaleString()} bonus` : 'Regular pay'}
                    </p>
                  </div>

                  <div className="text-right">
                    <p className="text-sm font-semibold text-slate-900">
                      ${paycheck.netPay.toLocaleString()}
                    </p>
                    <p className="text-xs text-gray-500">Net Pay</p>
                  </div>
                </div>
              ))}

              {recentPaychecks.length === 0 && (
                <div className="text-center py-12 text-gray-400">
                  No pay stubs available.
                </div>
              )}
            </div>
            
            <div className="mt-4 pt-4 border-t border-gray-100 text-center">
              <button 
                onClick={() => navigate('/employee/payroll')}
                className="text-sm font-medium text-blue-600 hover:text-blue-700 hover:underline"
              >
                View Full Pay History
              </button>
            </div>
          </Card>

        </div>
      </div>
    </div>
  );
}