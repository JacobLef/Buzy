package edu.neu.csye6200.dto.request;

/**
 * Request record for updating an existing employer.
 * All fields are optional (can be null).
 */
public record UpdateEmployerRequest(
    String name,
    String email,
    String password,
    String department,
    String title,
    String status
) {
}