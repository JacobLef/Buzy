package app.auth;

import app.auth.dto.AuthDTO;
import app.auth.dto.AuthRequest;
import app.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.employee.dto.CreateEmployeeRequest;
import app.employer.dto.CreateEmployerRequest;
import app.auth.SignupService;

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

  @Autowired
  private SignupService signupService;

  /**
   * Employee signup endpoint.
   * POST /api/auth/signup/employee
   */
  @PostMapping("/signup/employee")
  public ResponseEntity<AuthDTO> signupEmployee(@RequestBody CreateEmployeeRequest request) {
    try {
      AuthDTO response = signupService.signupEmployee(request);
      return ResponseEntity.status(201).body(response);
    } catch (RuntimeException e) {
      throw e;
    }
  }

  /**
   * Employer signup endpoint.
   * POST /api/auth/signup/employer
   */
  @PostMapping("/signup/employer")
  public ResponseEntity<AuthDTO> signupEmployer(@RequestBody CreateEmployerRequest request) {
    try {
      AuthDTO response = signupService.signupEmployer(request);
      return ResponseEntity.status(201).body(response);
    } catch (RuntimeException e) {
      throw e;
    }
  }
}