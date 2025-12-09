package edu.neu.csye6200.dto.request;

import edu.neu.csye6200.model.domain.PersonStatus;

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