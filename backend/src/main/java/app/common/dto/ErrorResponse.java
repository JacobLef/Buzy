package app.common.dto;

import java.time.LocalDateTime;

/**
 * Structured record for a response to an error which is to be given to the frontend.
 */
public record ErrorResponse(
    LocalDateTime timeStamp,
    int status,
    String error,
    String message
) { }