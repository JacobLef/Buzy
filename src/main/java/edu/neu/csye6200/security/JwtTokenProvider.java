package edu.neu.csye6200.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Token Provider.
 * Handles JWT token generation, validation, and parsing.
 * Used for login authentication - generates tokens after successful login.
 *
 * Responsibilities:
 * - Generate JWT tokens with user email and role
 * - Extract information from tokens (email, role, expiration)
 * - Validate token expiration and signature
 */
@Component
public class JwtTokenProvider {

    /**
     * Secret key for signing JWT tokens.
     * Should be at least 32 characters long.
     * Configured in application.properties as jwt.secret
     */
    @Value("${jwt.secret:your-256-bit-secret-key-must-be-at-least-32-characters-long}")
    private String secret;

    /**
     * Token expiration time in milliseconds.
     * Default: 86400000 (24 hours)
     * Configured in application.properties as jwt.expiration
     */
    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;

    /**
     * Generate JWT token for user after successful login.
     *
     * @param email User email (used as subject)
     * @param role User role (EMPLOYEE or EMPLOYER)
     * @return JWT token string
     */
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, email);
    }

    /**
     * Create JWT token with claims.
     *
     * @param claims Additional claims (e.g., role)
     * @param subject Subject (email in this case)
     * @return JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        // Create secret key from string
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .setClaims(claims)                    // Custom claims (role)
                .setSubject(subject)                  // Email as subject
                .setIssuedAt(now)                     // Token creation time
                .setExpiration(expiryDate)            // Token expiration time
                .signWith(key, SignatureAlgorithm.HS256) // Sign with secret key
                .compact();
    }

    /**
     * Extract email from token (subject contains email).
     *
     * @param token JWT token
     * @return User email
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract role from token.
     *
     * @param token JWT token
     * @return User role (EMPLOYEE or EMPLOYER)
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extract expiration date from token.
     *
     * @param token JWT token
     * @return Expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from token using a function.
     *
     * @param token JWT token
     * @param claimsResolver Function to extract specific claim
     * @return Extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token.
     *
     * @param token JWT token
     * @return All claims
     */
    private Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is expired.
     *
     * @param token JWT token
     * @return true if token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate token (check expiration and signature).
     *
     * @param token JWT token to validate
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token) {
        try {
            // Check if token is expired
            if (isTokenExpired(token)) {
                return false;
            }

            // Try to parse token (validates signature)
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            // Token is invalid (expired, malformed, or signature mismatch)
            return false;
        }
    }
}