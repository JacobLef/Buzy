export interface Training {
  id: number;
  trainingName: string;
  description: string;
  completionDate: string | null;
  expiryDate: string | null;
  required: boolean;
  completed: boolean;
  expired: boolean;
  personId: number;
  personName: string;
  personType: string;
  createdAt: string;
}

export interface CreateTrainingRequest {
  trainingName: string;
  description?: string;
  completionDate?: string;
  expiryDate?: string;
  required: boolean;
}

export interface UpdateTrainingRequest {
  trainingName?: string;
  description?: string;
  completionDate?: string;
  expiryDate?: string;
  required?: boolean;
}