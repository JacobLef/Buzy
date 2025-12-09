package app.dto.request;

import app.model.domain.PersonStatus;

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