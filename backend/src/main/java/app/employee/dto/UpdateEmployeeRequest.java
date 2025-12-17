package app.employee.dto;

import app.user.PersonStatus;

import java.time.LocalDate;

public record UpdateEmployeeRequest(String name, String email, String password, Double salary,
    String position, Long managerId, LocalDate hireDate, PersonStatus status) {
}
