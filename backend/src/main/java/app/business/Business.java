package app.business;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import app.employee.Employee;
import app.employer.Employer;

/**
 * Business interface defining core operations for business entities
 *
 * @author Qing Mi
 */
public interface Business {
  Long getId();

  void setId(Long id);

  String getName();

  void setName(String name);

  String getAddress();

  void setAddress(String address);

  String getIndustry();

  void setIndustry(String industry);

  LocalDate getFoundedDate();

  void setFoundedDate(LocalDate foundedDate);

  LocalDateTime getCreatedAt();

  void setCreatedAt(LocalDateTime createdAt);

  LocalDateTime getUpdatedAt();

  void setUpdatedAt(LocalDateTime updatedAt);

  /**
   * Add a business person (Employee or Employer) to this business
   *
   * @param employee The business person to add
   */
  void addEmployee(BusinessPerson employee);

  /**
   * Remove a business person (Employee or Employer) from this business
   *
   * @param employee The business person to remove
   */
  void removeEmployee(BusinessPerson employee);

  /**
   * Get all business persons (both Employees and Employers) in this business
   *
   * @return Combined list of all business persons
   */
  List<BusinessPerson> getEmployees();

  /**
   * Get only Employee objects (not Employers) in this business
   *
   * @return List of employees only
   */
  List<Employee> getEmployeesOnly();

  /**
   * Get only Employer objects (not Employees) in this business
   *
   * @return List of employers only
   */
  List<Employer> getEmployersOnly();

  /**
   * Get total count of all business persons (employees + employers)
   *
   * @return Total count
   */
  int getTotalPersonsCount();
}
