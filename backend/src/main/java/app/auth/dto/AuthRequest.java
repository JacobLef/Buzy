package app.auth.dto;

/**
 * Request DTO for authentication (login). Uses email for login.
 */
public record AuthRequest(String email, String password) {
}
