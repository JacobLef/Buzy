package edu.neu.csye6200.dto.request;

import java.time.LocalDate;

public record CreateEmployeeRequest(
    String name,
    String email,
    String password,
    Double salary,
    String position,
    Long companyId,
    Long managerId,
    LocalDate hireDate
) {}