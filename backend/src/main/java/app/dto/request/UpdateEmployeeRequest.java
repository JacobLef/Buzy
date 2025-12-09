package app.dto.request;

import app.model.domain.PersonStatus;

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