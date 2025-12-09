package app.business.dto;

import java.time.LocalDate;

/**
 * Request record for creating a new business.
 */
public record CreateBusinessRequest(
    String name,
    String address,
    String industry,
    LocalDate foundedDate
) {
}

