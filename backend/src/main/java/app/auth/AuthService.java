package app.auth;

import app.auth.dto.AuthDTO;
import app.auth.dto.AuthRequest;

/** - Authenticate user credentials using email - Generate JWT tokens - Validate tokens */
public interface AuthService {

  /**
   * Authenticate user and generate JWT token.
   *
   * @param request Authentication request with email and password
   * @return AuthResponse containing JWT token and user info
   * @throws RuntimeException if authentication fails
   */
  AuthDTO authenticate(AuthRequest request);

  /**
   * Refresh JWT token.
   *
   * @param token Current JWT token
   * @return New AuthResponse with refreshed token
   * @throws RuntimeException if token is invalid
   */
  AuthDTO refreshToken(String token);

  /**
   * Validate JWT token.
   *
   * @param token JWT token to validate
   * @return true if token is valid, false otherwise
   */
  boolean validateToken(String token);
}
