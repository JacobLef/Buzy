import { useState, useEffect } from "react";
import { GraduationCap, AlertCircle, CheckCircle, Clock, Eye } from "lucide-react";
import { Card, CardHeader } from "../../components/ui/Card";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { getTrainingsByPerson } from "../../api/training";
import type { Training } from "../../types/training";

type FilterType = "all" | "completed" | "expired" | "expiring" | "pending";

export default function EmployeeTraining() {
  const [trainings, setTrainings] = useState<Training[]>([]);
  const [filteredTrainings, setFilteredTrainings] = useState<Training[]>([]);
  const [activeFilter, setActiveFilter] = useState<FilterType>("all");
  const [selectedTraining, setSelectedTraining] = useState<Training | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Get current user's business person ID from localStorage
  const getPersonId = (): number | null => {
    const userStr = localStorage.getItem("user");
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        return user.businessPersonId || null;
      } catch {
        return null;
      }
    }
    return null;
  };

  // Load trainings on mount
  useEffect(() => {
    const loadTrainings = async () => {
      const personId = getPersonId();
      if (!personId) {
        setError("User information not found. Please login again.");
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        const response = await getTrainingsByPerson(personId);
        setTrainings(response.data);
        setError(null);
      } catch (err) {
        console.error("Failed to load trainings:", err);
        setError("Failed to load trainings. Please try again.");
      } finally {
        setIsLoading(false);
      }
    };

    loadTrainings();
  }, []);

  // Filter trainings based on active filter
  useEffect(() => {
    const now = new Date();
    const filtered = trainings.filter((training) => {
      switch (activeFilter) {
        case "completed":
          // Completed trainings (has completion_date)
          return training.completed;
        case "expired":
          // Expired trainings (only for non-completed trainings)
          return !training.completed && training.expired;
        case "expiring": {
          // Pending trainings that are expiring soon (not completed, not expired)
          if (training.completed || training.expired || !training.expiryDate) return false;
          const expiryDate = new Date(training.expiryDate);
          const daysUntilExpiry = Math.ceil(
            (expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
          );
          return daysUntilExpiry > 0 && daysUntilExpiry <= 30;
        }
        case "pending": {
          // Pending trainings: not completed, not expired, and not expiring soon
          if (training.completed || training.expired) return false;
          // If no expiry date, it's pending
          if (!training.expiryDate) return true;
          // If expiry date is more than 30 days away, it's pending
          const expiryDatePending = new Date(training.expiryDate);
          const daysUntilExpiryPending = Math.ceil(
            (expiryDatePending.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
          );
          return daysUntilExpiryPending > 30;
        }
        default:
          return true;
      }
    });
    setFilteredTrainings(filtered);
  }, [trainings, activeFilter]);

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

  // Format date
  const formatDate = (dateString: string | null) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  };

  // Get expiring trainings count (only pending trainings, not completed)
  const getExpiringCount = () => {
    const now = new Date();
    return trainings.filter((t) => {
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
    return trainings.filter((t) => {
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
      <div>
        <h1 className="text-3xl font-bold text-slate-900 tracking-tight">My Trainings</h1>
        <p className="text-gray-500 mt-2">View and manage your training certifications</p>
      </div>

      {/* Error Message */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}

      {/* Filter Tabs */}
      <Card>
        <div className="flex flex-wrap gap-2 border-b border-gray-100 pb-4">
          <button
            onClick={() => setActiveFilter("all")}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              activeFilter === "all"
                ? "bg-blue-50 text-blue-700"
                : "text-gray-600 hover:bg-gray-50"
            }`}
          >
            All ({trainings.length})
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
            onClick={() => setActiveFilter("completed")}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              activeFilter === "completed"
                ? "bg-blue-50 text-blue-700"
                : "text-gray-600 hover:bg-gray-50"
            }`}
          >
            Completed ({trainings.filter((t) => t.completed).length})
          </button>
          <button
            onClick={() => setActiveFilter("expired")}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              activeFilter === "expired"
                ? "bg-blue-50 text-blue-700"
                : "text-gray-600 hover:bg-gray-50"
            }`}
          >
            Expired ({trainings.filter((t) => !t.completed && t.expired).length})
          </button>
        </div>
      </Card>

      {/* Training List */}
      <Card>
        <CardHeader
          title="Training Records"
          subtitle={`Showing ${filteredTrainings.length} training${filteredTrainings.length !== 1 ? "s" : ""}`}
          icon={<GraduationCap size={20} />}
        />

        {filteredTrainings.length === 0 ? (
          <div className="text-center py-12 text-gray-400">
            <GraduationCap size={48} className="mx-auto mb-4 opacity-50" />
            <p className="text-lg font-medium">No trainings found</p>
            <p className="text-sm mt-1">
              {activeFilter === "all"
                ? "You don't have any training records yet."
                : `No ${activeFilter} trainings found.`}
            </p>
          </div>
        ) : (
          <div className="space-y-4">
            {filteredTrainings.map((training) => {
              const status = getTrainingStatus(training);
              const StatusIcon = status.icon;

              return (
                <div
                  key={training.id}
                  className="p-6 border border-gray-100 rounded-lg hover:border-blue-200 hover:shadow-sm transition-all"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <h3 className="text-lg font-semibold text-slate-900">
                          {training.trainingName}
                        </h3>
                        <Badge variant={status.variant}>
                          <StatusIcon size={12} />
                          {status.label}
                        </Badge>
                        {training.required && (
                          <Badge variant="blue">Required</Badge>
                        )}
                      </div>

                      {training.description && (
                        <p className="text-sm text-gray-600 mb-4">{training.description}</p>
                      )}

                      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
                        <div>
                          <span className="text-gray-500">Completion Date:</span>
                          <p className="font-medium text-slate-900">
                            {formatDate(training.completionDate)}
                          </p>
                        </div>
                        <div>
                          <span className="text-gray-500">Expiry Date:</span>
                          <p className="font-medium text-slate-900">
                            {formatDate(training.expiryDate)}
                          </p>
                        </div>
                        <div>
                          <span className="text-gray-500">Added On:</span>
                          <p className="font-medium text-slate-900">
                            {formatDate(training.createdAt)}
                          </p>
                        </div>
                      </div>
                    </div>

                    <Button
                      variant="ghost"
                      icon={<Eye size={18} />}
                      onClick={() => setSelectedTraining(training)}
                    >
                      View Details
                    </Button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </Card>

      {/* Training Detail Modal */}
      {selectedTraining && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
          onClick={() => setSelectedTraining(null)}
        >
          <div
            className="bg-white rounded-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="p-6 border-b border-gray-100 flex items-center justify-between">
              <h2 className="text-2xl font-bold text-slate-900">Training Details</h2>
              <button
                onClick={() => setSelectedTraining(null)}
                className="text-gray-400 hover:text-gray-600"
              >
                ✕
              </button>
            </div>
            <div className="p-6 space-y-4">
              <div>
                <span className="text-sm text-gray-500">Training Name</span>
                <p className="font-semibold text-slate-900 text-lg">
                  {selectedTraining.trainingName}
                </p>
              </div>
              {selectedTraining.description && (
                <div>
                  <span className="text-sm text-gray-500">Description</span>
                  <p className="text-slate-900">{selectedTraining.description}</p>
                </div>
              )}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <span className="text-sm text-gray-500">Completion Date</span>
                  <p className="font-medium text-slate-900">
                    {formatDate(selectedTraining.completionDate)}
                  </p>
                </div>
                <div>
                  <span className="text-sm text-gray-500">Expiry Date</span>
                  <p className="font-medium text-slate-900">
                    {formatDate(selectedTraining.expiryDate)}
                  </p>
                </div>
                <div>
                  <span className="text-sm text-gray-500">Required</span>
                  <p className="font-medium text-slate-900">
                    {selectedTraining.required ? "Yes" : "No"}
                  </p>
                </div>
                <div>
                  <span className="text-sm text-gray-500">Status</span>
                  <Badge variant={getTrainingStatus(selectedTraining).variant}>
                    {getTrainingStatus(selectedTraining).label}
                  </Badge>
                </div>
              </div>
              <div className="pt-4 border-t border-gray-100 flex justify-end">
                <Button variant="secondary" onClick={() => setSelectedTraining(null)}>
                  Close
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}