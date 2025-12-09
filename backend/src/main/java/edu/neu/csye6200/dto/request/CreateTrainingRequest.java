package edu.neu.csye6200.dto.request;

import java.time.LocalDate;

public record CreateTrainingRequest(
        String trainingName,
        String description,
        LocalDate completionDate,
        LocalDate expiryDate,
        boolean required
) {}