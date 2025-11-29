package edu.neu.csye6200.controller;

import edu.neu.csye6200.dto.AuthDTO;
import edu.neu.csye6200.dto.request.AuthRequest;
import edu.neu.csye6200.service.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * - Handle login requests (using email)
 * - Generate JWT tokens
 * - Refresh tokens
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login endpoint.
     * POST /api/auth/login
     *
     * @param request AuthRequest with email and password
     * @return AuthResponse with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(@RequestBody AuthRequest request) {
        AuthDTO response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh token endpoint.
     * POST /api/auth/refresh
     *
     * @param token Current JWT token
     * @return AuthResponse with new JWT token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthDTO> refreshToken(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " prefix if present
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        AuthDTO response = authService.refreshToken(jwtToken);
        return ResponseEntity.ok(response);
    }
}