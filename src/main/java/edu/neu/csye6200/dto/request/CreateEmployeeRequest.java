package edu.neu.csye6200.dto.request;

import java.time.LocalDate;

/**
 * Request record for creating a new employee.
 */
public record CreateEmployeeRequest(
    String name,
    String email,
    String password,
    Double salary,
    String position,
    Long businessId,
    Long managerId,
    LocalDate hireDate
) {
}