package edu.neu.csye6200.factory;

import edu.neu.csye6200.dto.EmployeeDTO;
import edu.neu.csye6200.dto.EmployerDTO;
import edu.neu.csye6200.dto.TrainingDTO;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.model.domain.Training;

public class DTOFactory {

  public EmployeeDTO createDTO(Employee employee) {
    EmployeeDTO.Builder builder = EmployeeDTO.builder()
        .withId(employee.getId())
        .withName(employee.getName())
        .withEmail(employee.getEmail())
        .withStatus(employee.getStatus())
        .withSalary(employee.getSalary())
        .withHireDate(employee.getHireDate())
        .withPosition(employee.getPosition())
        .withCreatedAt(employee.getCreatedAt())
        .withUpdatedAt(employee.getUpdatedAt());

    if (employee.getCompany() != null) {
      builder.withCompanyId(employee.getCompany().getId())
          .withCompanyName(employee.getCompany().getName());
    }

    if (employee.getManager() != null) {
      builder.withManagerId(employee.getManager().getId())
          .withManagerName(employee.getManager().getName());
    }

    return builder.build();
  }

  public EmployerDTO createDTO(Employer employer) {
    EmployerDTO.Builder builder = EmployerDTO.builder()
        .withId(employer.getId())
        .withName(employer.getName())
        .withEmail(employer.getEmail())
        .withStatus(employer.getStatus())
        .withSalary(employer.getSalary())
        .withHireDate(employer.getHireDate())
        .withDepartment(employer.getDepartment())
        .withTitle(employer.getTitle())
        .withDirectReportsCount(employer.getDirectReportsCount())
        .withCreatedAt(employer.getCreatedAt())
        .withUpdatedAt(employer.getUpdatedAt());

    if (employer.getCompany() != null) {
      builder.withCompanyId(employer.getCompany().getId())
          .withCompanyName(employer.getCompany().getName());
    }

    return builder.build();
  }

  public TrainingDTO createDTO(Training training) {
    TrainingDTO.Builder builder = TrainingDTO.builder()
        .withId(training.getId())
        .withTrainingName(training.getTrainingName())
        .withDescription(training.getDescription())
        .withCompletionDate(training.getCompletionDate())
        .withExpiryDate(training.getExpiryDate())
        .withRequired(training.isRequired())
        .withExpired(training.isExpired())
        .withCreatedAt(training.getCreatedAt());

    if (training.getPerson() != null) {
      builder.withPersonId(training.getPerson().getId())
          .withPersonName(training.getPerson().getName())
          .withPersonType(training.getPerson().getPersonType());
    }

    return builder.build();
  }
}