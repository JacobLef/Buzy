package edu.neu.csye6200.factory;

import edu.neu.csye6200.dto.EmployeeDTO;
import edu.neu.csye6200.dto.EmployerDTO;
import edu.neu.csye6200.dto.ErrorResponse;
import edu.neu.csye6200.dto.TrainingDTO;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.model.domain.Training;

import java.time.LocalDate;

/**
 * Factory class for Data Type Object creation.
 */
public class DTOFactory {
  public EmployeeDTO createDTO(Employee employee) {
    return EmployeeDTO.builder()
        .createdAt(employee.getCreatedAt())
        .updatedAt(employee.getUpdatedAt())
        .withHireDate(employee.getHireDate())
        .withManagerId(employee.getManager().getId())
        .withManagerName(employee.getManager().getName())
        .withPosition(employee.getPosition())
        .withSalary(employee.getSalary())
        .withBusinessId(employee.getCompany().getId())
        .withBusinessName(employee.getCompany().getName())
        .withEmail(employee.getEmail())
        .withName(employee.getName())
        .withStatus(employee.getStatus())
        .build();
  }

  public EmployerDTO createDTO(Employer employer) {
    return EmployerDTO.builder()
        .createdAt(employer.getCreatedAt())
        .updatedAt(employer.getUpdatedAt())
        .withDepartment(employer.getDepartment())
        .withTitle(employer.getDepartment())
        .withBusinessId(employer.getCompany().getId())
        .withBusinessName(employer.getCompany().getName())
        .withEmail(employer.getEmail())
        .withId(employer.getId())
        .withDirectReportsCount(employer.getDirectReportsCount())
        .withName(employer.getName())
        .withStatus(employer.getStatus())
        .withTitle(employer.getTitle())
        .build();
  }

  public TrainingDTO createDTO(Training training) {
    return TrainingDTO.builder()
        .withCreatedAt(training.getCreatedAt())
        .withDescription(training.getDescription())
        .withExpired(training.isExpired())
        .withCompletionDate(training.getCompletionDate())
        .withId(training.getId())
        .withExpiryDate(training.getExpiryDate())
        .withPersonId(training.getPerson().getId())
        .withRequired(training.isRequired())
        .withPersonName(training.getPerson().getName())
        .withTrainingName(training.getTrainingName())
        .build();
  }
}