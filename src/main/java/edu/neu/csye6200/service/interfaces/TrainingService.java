package edu.neu.csye6200.service.interfaces;

import edu.neu.csye6200.dto.TrainingDTO;

import java.util.List;

public interface TrainingService {

    /**
     * Add a new training record for a given employee.
     * Called by TrainingController -> POST /api/training/{employeeId}
     */
    TrainingDTO addTraining(Long employeeId, TrainingDTO dto);

    /**
     * Get all trainings completed by a specific person.
     * Called by TrainingController -> GET /api/training/person/{personId}
     */
    List<TrainingDTO> getTrainingsByPerson(Long personId);

    /**
     * Check expiration status for all trainings of a person.
     * Called by TrainingController -> GET /api/training/check/{personId}
     */
    List<TrainingDTO> checkExpiredTrainings(Long personId);
}
