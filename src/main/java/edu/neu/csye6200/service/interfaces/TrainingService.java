package edu.neu.csye6200.service.interfaces;

import edu.neu.csye6200.dto.TrainingDTO;

import java.util.List;

public interface TrainingService {

    /**
     * Add a new training record for a person (Employee or Employer).
     */
    TrainingDTO addTraining(Long personId, TrainingDTO dto);

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
    TrainingDTO updateTraining(Long trainingId, TrainingDTO dto);

    /**
     * Delete a training by its ID.
     */
    void deleteTraining(Long trainingId);
}