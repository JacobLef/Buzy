package app.service.interfaces;

import app.dto.TrainingDTO;
import app.dto.request.CreateTrainingRequest;
import app.dto.request.UpdateTrainingRequest;

import java.util.List;

public interface TrainingService {

    /**
     * Add a new training record for a person (Employee or Employer).
     */
    TrainingDTO addTraining(Long personId, CreateTrainingRequest request);
    /**
     * Get all trainings for a person.
     */
    List<TrainingDTO> getTrainingsByPerson(Long personId);

    /**
     * Get expired trainings for a person.
     */
    List<TrainingDTO> getExpiredTrainings(Long personId);

    /**
     * Get a training by its ID.
     */
    TrainingDTO getTrainingById(Long trainingId);

    /**
     * Update an existing training.
     */
    TrainingDTO updateTraining(Long trainingId, UpdateTrainingRequest request);
    /**
     * Delete a training by its ID.
     */
    void deleteTraining(Long trainingId);

    /**
     * Get all trainings (for admin/employer view).
     * Returns all training records regardless of person.
     */
    List<TrainingDTO> getAllTrainings();
}