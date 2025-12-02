package edu.neu.csye6200.controller;

import edu.neu.csye6200.dto.AuthDTO;
import edu.neu.csye6200.dto.request.AuthRequest;
import edu.neu.csye6200.service.interfaces.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller.
 * Handles login and token refresh.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Login endpoint.
   * POST /api/auth/login
   */
  @PostMapping("/login")
  public ResponseEntity<AuthDTO> login(@RequestBody AuthRequest request) {
    try {
      AuthDTO response = authService.authenticate(request);
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      return ResponseEntity.status(401).build();
    }
  }

  /**
   * Refresh token endpoint.
   * POST /api/auth/refresh
   */
  @PostMapping("/refresh")
  public ResponseEntity<AuthDTO> refreshToken(@RequestHeader("Authorization") String token) {
    try {
      String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
      AuthDTO response = authService.refreshToken(jwtToken);
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      return ResponseEntity.status(401).build();
    }
  }

  /**
   * Validate token endpoint.
   * GET /api/auth/validate
   */
  @GetMapping("/validate")
  public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
    String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
    boolean isValid = authService.validateToken(jwtToken);
    if (isValid) {
      return ResponseEntity.ok(true);
    }
    return ResponseEntity.status(401).body(false);
  }

  /**
   * Health check endpoint.
   * GET /api/auth/health
   */
  @GetMapping("/health")
  public ResponseEntity<Map<String, String>> health() {
    Map<String, String> response = new HashMap<>();
    response.put("status", "UP");
    response.put("service", "Authentication Service");
    return ResponseEntity.ok(response);
  }
}