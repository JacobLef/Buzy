package app.common.factory;

import app.business.dto.CompanyDTO;
import app.employee.dto.EmployeeDTO;
import app.employer.dto.EmployerDTO;
import app.training.dto.TrainingDTO;
import app.payroll.dto.PaycheckDTO;
import app.business.Company;
import app.employee.Employee;
import app.employer.Employer;
import app.training.Training;
import app.payroll.Paycheck;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Factory class for Data Type Object creation.
 */
@Component
public class DTOFactory {

  public EmployeeDTO createDTO(Employee employee) {
    EmployeeDTO.Builder builder = EmployeeDTO.builder().withId(employee.getId())
        .withName(employee.getName()).withEmail(employee.getEmail())
        .withStatus(employee.getStatus()).withSalary(employee.getSalary())
        .withHireDate(employee.getHireDate()).withPosition(employee.getPosition())
        .withCreatedAt(employee.getCreatedAt()).withUpdatedAt(employee.getUpdatedAt());

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
    EmployerDTO.Builder builder = EmployerDTO.builder().withId(employer.getId())
        .withName(employer.getName()).withEmail(employer.getEmail())
        .withStatus(employer.getStatus()).withSalary(employer.getSalary())
        .withHireDate(employer.getHireDate()).withDepartment(employer.getDepartment())
        .withTitle(employer.getTitle()).withDirectReportsCount(employer.getDirectReportsCount())
        .withIsAdmin(employer.getIsAdmin()).withIsOwner(employer.getIsOwner())
        .withCreatedAt(employer.getCreatedAt()).withUpdatedAt(employer.getUpdatedAt());

    if (employer.getCompany() != null) {
      builder.withCompanyId(employer.getCompany().getId())
          .withCompanyName(employer.getCompany().getName());
    }

    return builder.build();
  }

  public TrainingDTO createDTO(Training training) {
    TrainingDTO.Builder builder = TrainingDTO.builder().withId(training.getId())
        .withTrainingName(training.getTrainingName()).withDescription(training.getDescription())
        .withCompletionDate(training.getCompletionDate()).withExpiryDate(training.getExpiryDate())
        .withRequired(training.isRequired()).withCompleted(training.isCompleted())
        .withExpired(training.isExpired()).withCreatedAt(training.getCreatedAt());

    if (training.getPerson() != null) {
      builder.withPersonId(training.getPerson().getId())
          .withPersonName(training.getPerson().getName())
          .withPersonType(training.getPerson().getPersonType());
    }

    return builder.build();
  }

  public CompanyDTO createDTO(Company company) {
    List<Long> employeeIds = company.getEmployeesOnly().stream().map(Employee::getId).toList();

    List<Long> employerIds = company.getEmployersOnly().stream().map(Employer::getId).toList();

    return CompanyDTO.builder().withId(company.getId()).withName(company.getName())
        .withAddress(company.getAddress()).withIndustry(company.getIndustry())
        .withFoundedDate(company.getFoundedDate()).createdAt(company.getCreatedAt())
        .updatedAt(company.getUpdatedAt()).withTotalEmployees(company.getEmployeesOnly().size())
        .withTotalEmployers(company.getEmployersOnly().size())
        .withTotalPersons(company.getTotalPersonsCount()).withEmployeeIds(employeeIds)
        .withEmployerIds(employerIds).build();
  }

  public PaycheckDTO createDTO(Paycheck paycheck, Employee employee, String taxStrategyName) {
    double totalDeductions = paycheck.getTaxDeduction() + paycheck.getInsuranceDeduction();

    return new PaycheckDTO(paycheck.getId(), paycheck.getEmployeeId(), employee.getName(),
        paycheck.getGrossPay(), // Base
        // salary
        paycheck.getBonus(), // Bonus amount (null if regular payroll)
        paycheck.getTaxDeduction(), paycheck.getInsuranceDeduction(), totalDeductions,
        paycheck.getNetPay(), paycheck.getPayDate(), taxStrategyName, paycheck.getStatus() // Status:
                                                                                           // DRAFT,
                                                                                           // PENDING,
                                                                                           // PAID,
                                                                                           // VOIDED
    );
  }
}
