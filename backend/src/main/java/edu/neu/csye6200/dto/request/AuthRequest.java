package edu.neu.csye6200.dto.request;

/**
 * Request DTO for authentication (login).
 * Uses email for login.
 */
public record AuthRequest(
        String email,
        String password
) {
}