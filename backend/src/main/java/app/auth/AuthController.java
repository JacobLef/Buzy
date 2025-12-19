package app.auth;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import app.auth.dto.AuthDTO;
import app.auth.dto.AuthRequest;
import app.employee.dto.CreateEmployeeRequest;
import app.employer.dto.CreateEmployerRequest;

/** Authentication controller. Handles login and token refresh. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private SignupService signupService;

  public AuthController(AuthService authService, SignupService signupService) {
    this.authService = authService;
    this.signupService = signupService;
  }

  /** Login endpoint. POST /api/auth/login */
  @PostMapping("/login")
  public ResponseEntity<AuthDTO> login(@RequestBody AuthRequest request) {
    AuthDTO response = authService.authenticate(request);
    return ResponseEntity.ok(response);
  }


  /** Refresh token endpoint. POST /api/auth/refresh */
  @PostMapping("/refresh")
  public ResponseEntity<AuthDTO> refreshToken(@RequestHeader("Authorization") String token) {
    String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
    AuthDTO response = authService.refreshToken(jwtToken);
    return ResponseEntity.ok(response);
  }

  /** Validate token endpoint. GET /api/auth/validate */
  @GetMapping("/validate")
  public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
    String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
    boolean isValid = authService.validateToken(jwtToken);
    if (isValid) {
      return ResponseEntity.ok(true);
    }
    return ResponseEntity.status(401).body(false);
  }

  /** Health check endpoint. GET /api/auth/health */
  @GetMapping("/health")
  public ResponseEntity<Map<String, String>> health() {
    Map<String, String> response = new HashMap<>();
    response.put("status", "UP");
    response.put("service", "Authentication Service");
    return ResponseEntity.ok(response);
  }

  /** Employee signup endpoint. POST /api/auth/signup/employee */
  @PostMapping("/signup/employee")
  public ResponseEntity<AuthDTO> signupEmployee(@RequestBody CreateEmployeeRequest request) {
    AuthDTO response = signupService.signupEmployee(request);
    return ResponseEntity.status(201).body(response);
  }

  /** Employer signup endpoint. POST /api/auth/signup/employer */
  @PostMapping("/signup/employer")
  public ResponseEntity<AuthDTO> signupEmployer(@RequestBody CreateEmployerRequest request) {
    AuthDTO response = signupService.signupEmployer(request);
    return ResponseEntity.status(201).body(response);
  }
}
