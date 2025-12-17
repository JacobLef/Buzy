package app.employer.dto;

import java.time.LocalDate;

import app.user.PersonStatus;

public record UpdateEmployerRequest(
    String name,
    String email,
    String password,
    Double salary,
    String department,
    String title,
    LocalDate hireDate,
    PersonStatus status) {}
