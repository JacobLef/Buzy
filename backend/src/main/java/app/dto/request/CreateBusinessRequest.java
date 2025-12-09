package app.dto.request;

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

