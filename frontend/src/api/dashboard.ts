import api from "./axios";
import type { Activity } from "../types/dashboard";

export const getRecentActivity = (businessId: number) =>
  api.get<Activity[]>(`/api/dashboard/activity?businessId=${businessId}`);

