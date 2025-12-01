export interface Training {
  id: number,
  trainingName: string,
  description: string,
  completionDate: string,
  expiryDate: string,
  required: boolean,
  expired: boolean,
  personId: number,
  personName: string,
  personType: string,
  createdAt: string
};