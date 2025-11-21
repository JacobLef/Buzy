package edu.neu.csye6200.dto.request;

/**
 * Request record for creating a new employer.
 */
public record CreateEmployerRequest(
    String name,
    String email,
    String password,
    String department,
    String title,
    Long businessId
) {
}