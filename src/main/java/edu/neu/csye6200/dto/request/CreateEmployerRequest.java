package edu.neu.csye6200.dto.request;

import java.time.LocalDate;

public record CreateEmployerRequest(
    String name,
    String email,
    String password,
    Double salary,
    String department,
    String title,
    Long companyId,
    LocalDate hireDate
) {}