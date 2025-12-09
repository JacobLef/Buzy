import api from "./axios";
import type { Training } from "../types/training";
import type { CreateTrainingRequest, UpdateTrainingRequest } from "../types/training";

export const addTraining = (personId: number, data: CreateTrainingRequest) =>
  api.post<Training>(`/api/training/person/${personId}`, data);

export const getTrainingsByPerson = (personId: number) => 
  api.get<Training[]>(`/api/training/person/${personId}`);

export const getExpiredTrainings = (personId: number) => 
  api.get<Training[]>(`/api/training/person/${personId}/expired`);

export const getTrainingById = (trainingId: number) => 
  api.get<Training>(`/api/training/${trainingId}`);

export const getAllTrainings = () =>
  api.get<Training[]>(`/api/training`);

export const updateTraining = (trainingId: number, data: UpdateTrainingRequest) =>
  api.put<Training>(`/api/training/${trainingId}`, data);

export const deleteTraining = (trainingId: number) => 
  api.delete(`/api/training/${trainingId}`);