package app.training.dto;

import java.time.LocalDate;

public record CreateTrainingRequest(
        String trainingName,
        String description,
        LocalDate completionDate,
        LocalDate expiryDate,
        boolean required
) {}