package app.employee.dto;

import java.time.LocalDate;

import app.user.PersonStatus;

public record UpdateEmployeeRequest(
    String name,
    String email,
    String password,
    Double salary,
    String position,
    Long managerId,
    LocalDate hireDate,
    PersonStatus status) {}
