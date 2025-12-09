package edu.neu.csye6200.dto.request;

import java.time.LocalDate;


public record UpdateTrainingRequest(
        String trainingName,
        String description,
        LocalDate completionDate,
        LocalDate expiryDate,
        Boolean required
) {}