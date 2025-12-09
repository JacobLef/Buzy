import { useState, useEffect, useCallback } from 'react';
import { getEmployee, updateEmployee } from '../api/employees';
import { getEmployer, updateEmployer } from '../api/employers';
import type { Employee, UpdateEmployeeRequest } from '../types/employee';
import type { Employer, UpdateEmployerRequest } from '../types/employer';

type Profile = Employee | Employer;
type UpdateRequest = UpdateEmployeeRequest | UpdateEmployerRequest;

export const useProfile = (id: number, role: 'EMPLOYEE' | 'EMPLOYER') => {
  const [profile, setProfile] = useState<Profile | null>(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchProfileData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      if (role === 'EMPLOYEE') {
        const response = await getEmployee(id);
        setProfile(response.data);
      } else {
        const response = await getEmployer(id);
        setProfile(response.data);
      }
    } catch (err) {
      console.error(`There was an error loading the profile of an ${role.toLowerCase()}`);
      setError('Error loading profile data');
    } finally {
      setLoading(false);
    }
  }, [id, role]);

  const saveProfile = async (data: UpdateRequest) => {
    setSaving(true);
    setError(null);
    try {
      if (role === 'EMPLOYEE') {
        const response = await updateEmployee(id, data as UpdateEmployeeRequest);
        setProfile(response.data);
      } else {
        const response = await updateEmployer(id, data as UpdateEmployerRequest);
        setProfile(response.data);
      }
      setEditing(false);
      return true;
    } catch (err) {
      console.error(`There was an error saving the profile`);
      setError('Error saving profile data');
      return false;
    } finally {
      setSaving(false);
    }
  };

  const toggleEditing = () => {
    setEditing((prev) => !prev);
    setError(null);
  };

  const cancelEditing = () => {
    setEditing(false);
    setError(null);
    fetchProfileData();
  };

  useEffect(() => {
    if (id) {
      fetchProfileData();
    }
  }, [id, fetchProfileData]);

  return {
    profile,
    loading,
    editing,
    saving,
    error,
    saveProfile,
    toggleEditing,
    cancelEditing,
    refetch: fetchProfileData,
  };
};