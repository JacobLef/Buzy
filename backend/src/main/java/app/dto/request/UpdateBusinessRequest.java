package app.dto.request;

import java.time.LocalDate;

/**
 * Request record for updating an existing business.
 * All fields are optional (can be null).
 */
public record UpdateBusinessRequest(
    String name,
    String address,
    String industry,
    LocalDate foundedDate
) {
}

