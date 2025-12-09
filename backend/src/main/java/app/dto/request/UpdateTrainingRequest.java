package app.dto.request;

import java.time.LocalDate;


public record UpdateTrainingRequest(
        String trainingName,
        String description,
        LocalDate completionDate,
        LocalDate expiryDate,
        Boolean required
) {}