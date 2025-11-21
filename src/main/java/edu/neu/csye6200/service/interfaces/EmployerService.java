package edu.neu.csye6200.service.interfaces;

import edu.neu.csye6200.dto.request.CreateEmployerRequest;
import edu.neu.csye6200.dto.request.UpdateEmployerRequest;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.model.domain.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for employer management operations.
 *
 * @author jacoblefkowitz
 */
@Service
public interface EmployerService {
  /**
   * Creates a new employer.
   * @param req the Data for the new employer.
   * @return Created Employer.
   */
  Employer createEmployer(CreateEmployerRequest req);

  /**
   * Fetches the employer mapped to the provided ID.
   * @param id the id of the employer to be fetched.
   * @return {@code Optional.empty()} if there is no employer by the id provided or the employer
   * object that relates to the given id.
   */
  Optional<Employer> getEmployer(Long id);

  /**
   * Fetches all employers.
   * @return a list of the employers to be fetched.
   */
  List<Employer> getAllEmployers();

  /**
   * Updates an existing employer with the given data.
   * @param id the id of the employer to be updated.
   * @param req Updated employer data.
   * @return the new Updated employer.
   */
  Employer updateEmployer(Long id, UpdateEmployerRequest req);

  /**
   * Delete an employer.
   * @param id the id of the Employer to be deleted.
   */
  void deleteEmployer(Long id);

  /**
   * Get all employers for a specific business.
   * @param businessId the id of the business whose employers are to be fetched.
   * @return the list of employers working at that business.
   */
  List<Employer> getEmployersByBusiness(Long businessId);

  /**
   * Get all employers in a specific department.
   * @param department the department whose employers are to be fetched.
   * @return a list of the employers found.
   */
  List<Employer> getEmployersByDepartment(String department);

  /**
   * Add a managed employee to an employer.
   * @param employerId Employer ID
   * @param employeeId Employee ID
   * @return Updated employer
   */
  Employer addManagedEmployee(Long employerId, Long employeeId);

  /**
   * Remove a managed employee from an employer.
   * @param employerId Employer ID
   * @param employeeId Employee ID
   * @return Updated employer
   */
  Employer removeManagedEmployee(Long employerId, Long employeeId);

  /**
   * Get all direct reports for an employer.
   * @param employerId Employer ID
   * @return Set of employees managed by this employer
   */
  Set<Employee> getDirectReports(Long employerId);

  /**
   * Get count of direct reports for an employer.
   * @param employerId Employer ID
   * @return Number of employees managed by this employer
   */
  int getDirectReportsCount(Long employerId);

  /**
   * Update employer department.
   * @param employerId Employer ID
   * @param newDepartment New department
   * @return Updated employer
   */
  Employer updateDepartment(Long employerId, String newDepartment);

  /**
   * Update employer title.
   * @param employerId Employer ID
   * @param newTitle New title
   * @return Updated employer
   */
  Employer updateTitle(Long employerId, String newTitle);

  /**
   * Update employer status (Active, Inactive, On Leave, etc.).
   * @param employerId Employer ID
   * @param status New status
   * @return Updated employer
   */
  Employer updateStatus(Long employerId, String status);

  /**
   * Search employers by name.
   * @param name Name to search for
   * @return List of matching employers
   */
  List<Employer> searchByName(String name);

  /**
   * Get employers by title.
   * @param title Title to search for
   * @return List of employers with that title
   */
  List<Employer> getEmployersByTitle(String title);
}