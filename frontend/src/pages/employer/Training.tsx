import { useState, useEffect } from "react";
import {
  GraduationCap,
  Plus,
  AlertCircle,
  CheckCircle,
  Clock,
  Edit,
  Trash2,

  Search,
} from "lucide-react";
import { Card, CardHeader } from "../../components/ui/Card";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { Modal } from "../../components/ui/Modal";
import { getAllTrainings, addTraining, updateTraining, deleteTraining } from "../../api/training";
import { getAllEmployees } from "../../api/employees";
import { getEmployersByBusiness, getEmployer } from "../../api/employers";
import type { Training } from "../../types/training";
import type { Employee } from "../../types/employee";
import type { Employer } from "../../types/employer";
import type { CreateTrainingRequest, UpdateTrainingRequest } from "../../types/training";

type FilterType = "all" | "completed" | "expiring" | "expired" | "pending" | "department";

export default function TrainingManagement() {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [allTrainings, setAllTrainings] = useState<Training[]>([]);
  const [filteredTrainings, setFilteredTrainings] = useState<Training[]>([]);
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingTraining, setEditingTraining] = useState<Training | null>(null);
  const [activeFilter, setActiveFilter] = useState<FilterType>("all");
  const [searchTerm, setSearchTerm] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Load employees and their trainings
  useEffect(() => {
    const loadData = async () => {
        try {
        setIsLoading(true);

        // Get current user's company ID
        const userStr = localStorage.getItem("user");
        let companyId: number | null = null;
        if (userStr) {
          try {
            const user = JSON.parse(userStr);
            if (user.role === 'EMPLOYER' && user.businessPersonId) {
              // Get employer to find companyId
              const employerResponse = await getEmployer(user.businessPersonId);
              companyId = employerResponse.data.companyId;
            }
          } catch (err) {
            console.error("Failed to get company ID:", err);
          }
        }

        const [trainingsResponse, employeesResponse, employersResponse] = await Promise.all([
          getAllTrainings(),
          getAllEmployees(),
          companyId ? getEmployersByBusiness(companyId) : Promise.resolve({ data: [] })
        ]);

        // Filter trainings for this company only
        const allTrainingsData = trainingsResponse.data;
        const employeesData = employeesResponse.data;
        const employersData = employersResponse.data;
        
        // Get all person IDs for this company
        const companyEmployeeIds = new Set(employeesData.filter((e: Employee) => e.companyId === companyId).map((e: Employee) => e.id));
        const companyEmployerIds = new Set(employersData.map((e: Employer) => e.id));
        
        // Filter trainings by company
        const companyTrainings = allTrainingsData.filter((t: Training) => {
          return t.personId && (companyEmployeeIds.has(Number(t.personId)) || companyEmployerIds.has(Number(t.personId)));
        });

        setAllTrainings(companyTrainings);
        setEmployees(employeesData.filter((e: Employee) => e.companyId === companyId));
        } catch (err) {
            console.error("Failed to load data:", err);
            setError("Failed to load training data. Please try again.");
        } finally {
            setIsLoading(false);
        }
    };

    loadData();
    }, []);

  // Filter trainings
  useEffect(() => {
    const now = new Date();
    let filtered = [...allTrainings];

    // Apply search filter
    if (searchTerm) {
      filtered = filtered.filter(
        (t) =>
          t.trainingName.toLowerCase().includes(searchTerm.toLowerCase()) ||
          t.personName.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Apply status filter
    switch (activeFilter) {
      case "completed":
        // Completed trainings (has completion_date)
        filtered = filtered.filter((t) => t.completed);
        break;
      case "expired":
        // Expired trainings (only for non-completed trainings)
        filtered = filtered.filter((t) => !t.completed && t.expired);
        break;
      case "expiring":
        // Pending trainings that are expiring soon (not completed, not expired)
        filtered = filtered.filter((t) => {
          if (t.completed || t.expired || !t.expiryDate) return false;
          const expiryDate = new Date(t.expiryDate);
          const daysUntilExpiry = Math.ceil(
            (expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
          );
          return daysUntilExpiry > 0 && daysUntilExpiry <= 30;
        });
        break;
      case "pending":
        // Pending trainings: not completed, not expired, and not expiring soon
        filtered = filtered.filter((t) => {
          if (t.completed || t.expired) return false;
          // If no expiry date, it's pending
          if (!t.expiryDate) return true;
          // If expiry date is more than 30 days away, it's pending
          const expiryDate = new Date(t.expiryDate);
          const daysUntilExpiry = Math.ceil(
            (expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
          );
          return daysUntilExpiry > 30;
        });
        break;
      default:
        // "all" - show all trainings
        break;
    }

    setFilteredTrainings(filtered);
  }, [allTrainings, activeFilter, searchTerm]);

  // Handle add training
  const handleAddTraining = async (data: CreateTrainingRequest, personId: number) => {
    try {
      setIsSubmitting(true);
      const response = await addTraining(personId, data);
      setAllTrainings([...allTrainings, response.data]);
      setShowAddModal(false);
      setError(null);
    } catch (err) {
      console.error("Failed to add training:", err);
      setError("Failed to add training. Please try again.");
      throw err;
    } finally {
      setIsSubmitting(false);
    }
  };

  // Handle update training
  const handleUpdateTraining = async (
    trainingId: number,
    data: UpdateTrainingRequest
  ) => {
    try {
      setIsSubmitting(true);
      const response = await updateTraining(trainingId, data);
      setAllTrainings(
        allTrainings.map((t) => (t.id === trainingId ? response.data : t))
      );
      setEditingTraining(null);
      setError(null);
    } catch (err) {
      console.error("Failed to update training:", err);
      setError("Failed to update training. Please try again.");
      throw err;
    } finally {
      setIsSubmitting(false);
    }
  };

  // Handle delete training
  const handleDeleteTraining = async (trainingId: number) => {
    if (!confirm("Are you sure you want to delete this training record?")) {
      return;
    }

    try {
      setIsSubmitting(true);
      await deleteTraining(trainingId);
      setAllTrainings(allTrainings.filter((t) => t.id !== trainingId));
      setError(null);
    } catch (err) {
      console.error("Failed to delete training:", err);
      setError("Failed to delete training. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  // Get training status
  const getTrainingStatus = (training: Training) => {
    // Priority 1: If completed (has completion_date), show completed status
    if (training.completed) {
      return { variant: "success" as const, label: "Completed", icon: CheckCircle };
    }
    
    // Priority 2: If expired, show expired status
    if (training.expired) {
      return { variant: "error" as const, label: "Expired", icon: AlertCircle };
    }
    
    // Priority 3: Check expiry date for pending trainings
    if (!training.expiryDate) {
      // No expiry date -> Pending
      return { variant: "neutral" as const, label: "Pending", icon: Clock };
    }
    
    const expiryDate = new Date(training.expiryDate);
    const now = new Date();
    const daysUntilExpiry = Math.ceil(
      (expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
    );

    if (daysUntilExpiry < 0) {
      // Already expired (shouldn't happen if expired check passed, but just in case)
      return { variant: "error" as const, label: "Expired", icon: AlertCircle };
    } else if (daysUntilExpiry <= 30) {
      // Expiring soon (within 30 days)
      return {
        variant: "warning" as const,
        label: `Expires in ${daysUntilExpiry} days`,
        icon: Clock,
      };
    } else {
      // Not expiring soon -> Pending
      return { variant: "neutral" as const, label: "Pending", icon: Clock };
    }
  };

  const formatDate = (dateString: string | null) => {
    if (!dateString) return "N/A";
    const [year, month, day] = dateString.split("-");
    const date = new Date(parseInt(year), parseInt(month) - 1, parseInt(day));
    return date.toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  };

  // Get expiring trainings count (only pending trainings, not completed)
  const getExpiringCount = () => {
    const now = new Date();
    return allTrainings.filter((t) => {
      if (t.completed || !t.expiryDate || t.expired) return false;
      const expiryDate = new Date(t.expiryDate);
      const daysUntilExpiry = Math.ceil(
        (expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
      );
      return daysUntilExpiry > 0 && daysUntilExpiry <= 30;
    }).length;
  };

  // Get pending trainings count (not completed, not expired, not expiring soon)
  const getPendingCount = () => {
    const now = new Date();
    return allTrainings.filter((t) => {
      if (t.completed || t.expired) return false;
      // If no expiry date, it's pending
      if (!t.expiryDate) return true;
      // If expiry date is more than 30 days away, it's pending
      const expiryDate = new Date(t.expiryDate);
      const daysUntilExpiry = Math.ceil(
        (expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
      );
      return daysUntilExpiry > 30;
    }).length;
  };

  if (isLoading) {
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
      {/* Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-end gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 tracking-tight">
            Training Management
          </h1>
          <p className="text-gray-500 mt-2">
            Manage training records and certifications for all employees
          </p>
        </div>
        <Button
          variant="primary"
          icon={<Plus size={18} />}
          onClick={() => setShowAddModal(true)}
        >
          Assign Training
        </Button>
      </div>

      {/* Error Message */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500">Total Trainings</p>
              <p className="text-2xl font-bold text-slate-900 mt-1">
                {allTrainings.length}
              </p>
            </div>
            <div className="p-3 bg-blue-50 rounded-lg">
              <GraduationCap size={24} className="text-blue-600" />
            </div>
          </div>
        </Card>
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500">Expiring Within 30 Days</p>
              <p className="text-2xl font-bold text-slate-900 mt-1">
                {getExpiringCount()}
              </p>
            </div>
            <div className="p-3 bg-yellow-50 rounded-lg">
              <Clock size={24} className="text-yellow-600" />
            </div>
          </div>
        </Card>
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500">Expired</p>
              <p className="text-2xl font-bold text-slate-900 mt-1">
                {allTrainings.filter((t) => t.expired).length}
              </p>
            </div>
            <div className="p-3 bg-red-50 rounded-lg">
              <AlertCircle size={24} className="text-red-600" />
            </div>
          </div>
        </Card>
      </div>

      {/* Expiring Trainings Alert */}
      {getExpiringCount() > 0 && activeFilter !== "expiring" && (
        <Card className="bg-yellow-50 border-yellow-200">
          <div className="flex items-start gap-4">
            <AlertCircle size={24} className="text-yellow-600 mt-1" />
            <div className="flex-1">
              <h3 className="font-semibold text-yellow-900 mb-1">
                ⚠️ {getExpiringCount()} Training{getExpiringCount() !== 1 ? "s" : ""}{" "}
                Expiring Within 30 Days
              </h3>
              <p className="text-sm text-yellow-700 mb-3">
                Some employees have trainings that will expire soon and may need renewal.
              </p>
              <Button
                variant="secondary"
                onClick={() => setActiveFilter("expiring")}
                className="bg-white"
              >
                View Expiring Trainings
              </Button>
            </div>
          </div>
        </Card>
      )}

      
      

      {/* Training List */}
      <Card padding="none">
        <div className="px-6 py-4 border-b border-gray-100">
          <CardHeader
            title="All Trainings"
            subtitle={`Showing ${filteredTrainings.length} training${filteredTrainings.length !== 1 ? "s" : ""}`}
            icon={<GraduationCap size={20} />}
          />
      </div>

      {/* Filter and Search */}
        <div className="px-6 py-4 flex flex-col md:flex-row gap-4 items-start md:items-center justify-between">
          <div className="flex flex-wrap gap-2">
            <button
              onClick={() => setActiveFilter("all")}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                activeFilter === "all"
                  ? "bg-blue-50 text-blue-700"
                  : "text-gray-600 hover:bg-gray-50"
              }`}
            >
              All ({allTrainings.length})
            </button>
            <button
              onClick={() => setActiveFilter("pending")}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                activeFilter === "pending"
                  ? "bg-blue-50 text-blue-700"
                  : "text-gray-600 hover:bg-gray-50"
              }`}
            >
              Pending ({getPendingCount()})
            </button>
            <button
              onClick={() => setActiveFilter("expiring")}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                activeFilter === "expiring"
                  ? "bg-blue-50 text-blue-700"
                  : "text-gray-600 hover:bg-gray-50"
              }`}
            >
              Expiring Soon ({getExpiringCount()})
            </button>
            <button
              onClick={() => setActiveFilter("expired")}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                activeFilter === "expired"
                  ? "bg-blue-50 text-blue-700"
                  : "text-gray-600 hover:bg-gray-50"
              }`}
            >
              Expired ({allTrainings.filter((t) => !t.completed && t.expired).length})
            </button>
            <button
              onClick={() => setActiveFilter("completed")}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                activeFilter === "completed"
                  ? "bg-blue-50 text-blue-700"
                  : "text-gray-600 hover:bg-gray-50"
              }`}
            >
              Completed ({allTrainings.filter((t) => t.completed).length})
            </button>
            
          </div>

          <div className="relative w-full md:w-auto">
            <Search
              size={20}
              className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
            />
            <input
              type="text"
              placeholder="Search trainings..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full md:w-64 pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
        </div>

        {filteredTrainings.length === 0 ? (
          <div className="text-center py-12 text-gray-400">
            <GraduationCap size={48} className="mx-auto mb-4 opacity-50" />
            <p className="text-lg font-medium">No trainings found</p>
            <p className="text-sm mt-1">
              {searchTerm || activeFilter !== "all"
                ? "Try adjusting your search or filter."
                : "Start by assigning a training to an employee."}
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                    Training Name
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                    Employee
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                    Completion Date
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                    Expiry Date
                  </th>
                  <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                    Status
                  </th>
                  <th className="px-6 py-4 text-right text-xs font-medium text-gray-500 uppercase">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {filteredTrainings.map((training) => {
                  const status = getTrainingStatus(training);
                  const StatusIcon = status.icon;

                  return (
                    <tr key={training.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4">
                        <div>
                          <p className="text-sm font-medium text-slate-900">
                            {training.trainingName}
                          </p>
                          {training.required && (
                            <Badge variant="blue" className="mt-1">
                              Required
                            </Badge>
                          )}
                        </div>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">
                        {training.personName}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">
                        {formatDate(training.completionDate)}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">
                        {formatDate(training.expiryDate)}
                      </td>
                      <td className="px-6 py-4">
                        <Badge variant={status.variant}>
                          <StatusIcon size={12} />
                          {status.label}
                        </Badge>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex gap-2 justify-end">
                          <Button
                            variant="ghost"
                            icon={<Edit size={18} />}
                            onClick={() => setEditingTraining(training)}
                          >
                            Edit
                          </Button>
                          <Button
                            variant="danger"
                            icon={<Trash2 size={18} />}
                            onClick={() => handleDeleteTraining(training.id)}
                            disabled={isSubmitting}
                          >
                            Delete
                          </Button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      {/* Add Training Modal */}
      {showAddModal && (
        <AddTrainingModal
          employees={employees}
          onClose={() => setShowAddModal(false)}
          onSubmit={handleAddTraining}
          isSubmitting={isSubmitting}
        />
      )}

      {/* Edit Training Modal */}
      {editingTraining && (
        <EditTrainingModal
          training={editingTraining}
          onClose={() => setEditingTraining(null)}
          onSubmit={handleUpdateTraining}
          isSubmitting={isSubmitting}
        />
      )}
    </div>
  );
}

// Add Training Modal Component
interface AddTrainingModalProps {
  employees: Employee[];
  onClose: () => void;
  onSubmit: (data: CreateTrainingRequest, personId: number) => Promise<void>;
  isSubmitting: boolean;
}

function AddTrainingModal({
  employees,
  onClose,
  onSubmit,
  isSubmitting,
}: AddTrainingModalProps) {
  const [isCompleted, setIsCompleted] = useState(false);
  const [formData, setFormData] = useState<CreateTrainingRequest & { personId: number }>({
    personId: employees[0]?.id || 0,
    trainingName: "",
    description: "",
    completionDate: "", // Default empty
    expiryDate: "",
    required: false,
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.personId || !formData.trainingName) {
      alert("Please fill in required fields");
      return;
    }

    try {
      await onSubmit(
        {
          trainingName: formData.trainingName,
          description: formData.description || undefined,
          completionDate: isCompleted && formData.completionDate ? formData.completionDate : undefined,
          expiryDate: formData.expiryDate || undefined,
          required: formData.required,
        },
        formData.personId
      );
    } catch {
      // Error is handled in parent
    }
  };

  return (
    <Modal isOpen={true} onClose={onClose} title="Assign Training" maxWidth="2xl">
      <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-slate-900 mb-1.5">
              Employee *
            </label>
            <select
              value={formData.personId}
              onChange={(e) =>
                setFormData({ ...formData, personId: Number(e.target.value) })
              }
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            >
              <option value="">Select Employee</option>
              {employees.map((emp) => (
                <option key={emp.id} value={emp.id}>
                  {emp.name} ({emp.email})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-900 mb-1.5">
              Training Name *
            </label>
            <input
              type="text"
              value={formData.trainingName}
              onChange={(e) =>
                setFormData({ ...formData, trainingName: e.target.value })
              }
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-900 mb-1.5">
              Description
            </label>
            <textarea
              value={formData.description}
              onChange={(e) =>
                setFormData({ ...formData, description: e.target.value })
              }
              rows={3}
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          
          <div>
            <label className="block text-sm font-medium text-slate-900 mb-1.5">
              Expiry Date
            </label>
            <input
              type="date"
              value={formData.expiryDate}
              onChange={(e) =>
                setFormData({ ...formData, expiryDate: e.target.value })
              }
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="required"
              checked={formData.required}
              onChange={(e) =>
                setFormData({ ...formData, required: e.target.checked })
              }
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <label htmlFor="required" className="text-sm font-medium text-slate-900">
              Required Training
            </label>
          </div>

          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="completed"
              checked={isCompleted}
              onChange={(e) => {
                setIsCompleted(e.target.checked);
                if (!e.target.checked) {
                  setFormData({ ...formData, completionDate: "" });
                }
              }}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <label htmlFor="completed" className="text-sm font-medium text-slate-900">
              Training Completed
            </label>
          </div>

          {isCompleted && (
            <div>
              <label className="block text-sm font-medium text-slate-900 mb-1.5">
                Completion Date *
              </label>
              <input
                type="date"
                value={formData.completionDate}
                onChange={(e) =>
                  setFormData({ ...formData, completionDate: e.target.value })
                }
                className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                required={isCompleted}
              />
            </div>
          )}


        <div className="pt-4 border-t border-gray-100 flex justify-end gap-4">
          <Button variant="secondary" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button type="submit" isLoading={isSubmitting}>
            Assign Training
          </Button>
        </div>
      </form>
    </Modal>
  );
}

// Edit Training Modal Component
interface EditTrainingModalProps {
  training: Training;
  onClose: () => void;
  onSubmit: (trainingId: number, data: UpdateTrainingRequest) => Promise<void>;
  isSubmitting: boolean;
}

function EditTrainingModal({
  training,
  onClose,
  onSubmit,
  isSubmitting,
}: EditTrainingModalProps) {
  const [isCompleted, setIsCompleted] = useState(!!training.completionDate);
  const [formData, setFormData] = useState<UpdateTrainingRequest>({
    trainingName: training.trainingName,
    description: training.description || "",
    completionDate: training.completionDate || "",
    expiryDate: training.expiryDate || "",
    required: training.required,
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await onSubmit(training.id, {
        ...formData,
        completionDate: isCompleted && formData.completionDate ? formData.completionDate : undefined,
      });
    } catch {
      // Error is handled in parent
    }
  };

  return (
    <Modal isOpen={true} onClose={onClose} title="Edit Training" maxWidth="2xl">
      <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-slate-900 mb-1.5">
              Training Name *
            </label>
            <input
              type="text"
              value={formData.trainingName}
              onChange={(e) =>
                setFormData({ ...formData, trainingName: e.target.value })
              }
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-900 mb-1.5">
              Description
            </label>
            <textarea
              value={formData.description}
              onChange={(e) =>
                setFormData({ ...formData, description: e.target.value })
              }
              rows={3}
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="completed-edit"
              checked={isCompleted}
              onChange={(e) => {
                setIsCompleted(e.target.checked);
                if (!e.target.checked) {
                  setFormData({ ...formData, completionDate: "" });
                }
              }}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <label htmlFor="completed-edit" className="text-sm font-medium text-slate-900">
              Training Completed
            </label>
          </div>

          {isCompleted && (
            <div>
              <label className="block text-sm font-medium text-slate-900 mb-1.5">
                Completion Date *
              </label>
              <input
                type="date"
                value={formData.completionDate}
                onChange={(e) =>
                  setFormData({ ...formData, completionDate: e.target.value })
                }
                className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                required={isCompleted}
              />
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-slate-900 mb-1.5">
              Expiry Date
            </label>
            <input
              type="date"
              value={formData.expiryDate}
              onChange={(e) =>
                setFormData({ ...formData, expiryDate: e.target.value })
              }
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="required-edit"
              checked={formData.required}
              onChange={(e) =>
                setFormData({ ...formData, required: e.target.checked })
              }
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <label htmlFor="required-edit" className="text-sm font-medium text-slate-900">
              Required Training
            </label>
          </div>

        <div className="pt-4 border-t border-gray-100 flex justify-end gap-4">
          <Button variant="secondary" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button type="submit" isLoading={isSubmitting}>
            Save Changes
          </Button>
        </div>
      </form>
    </Modal>
  );
}