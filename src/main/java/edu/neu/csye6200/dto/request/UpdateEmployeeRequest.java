package edu.neu.csye6200.dto.request;

import edu.neu.csye6200.model.domain.PersonStatus;

import java.time.LocalDate;

public record UpdateEmployeeRequest(
    String name,
    String email,
    String password,
    Double salary,
    String position,
    Long managerId,
    LocalDate hireDate,
    PersonStatus status
) {}