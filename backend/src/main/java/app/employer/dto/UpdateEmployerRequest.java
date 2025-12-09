package app.employer.dto;

import app.user.PersonStatus;

import java.time.LocalDate;

public record UpdateEmployerRequest(
    String name,
    String email,
    String password,
    Double salary,
    String department,
    String title,
    LocalDate hireDate,
    PersonStatus status
) {}