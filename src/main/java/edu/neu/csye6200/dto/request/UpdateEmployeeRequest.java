package edu.neu.csye6200.dto.request;

/**
 * Request record for updating an existing employee.
 * All fields are optional (can be null).
 */
public record UpdateEmployeeRequest(
    String name,
    String email,
    String password,
    Double salary,
    String position,
    Long managerId,
    String status
) {
}