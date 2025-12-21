package app.employer;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import app.employee.Employee;
import app.employer.dto.CreateEmployerRequest;
import app.employer.dto.UpdateEmployerRequest;

/**
 * Service interface for employers such that this is the outward facing API for all employer related
 * functionalities.
 *
 * @author Jacob Lefkowitz
 */
public interface EmployerService {

  Employer createEmployer(CreateEmployerRequest request);

  Optional<Employer> getEmployer(Long id);

  List<Employer> getAllEmployers();

  Employer updateEmployer(Long id, UpdateEmployerRequest request);

  void deleteEmployer(Long id);

  List<Employer> getEmployersByBusiness(Long companyId);

  List<Employer> getEmployersByDepartment(String department);

  Set<Employee> getDirectReports(Long id);

  Employer updateSalary(Long id, Double salary);

  Employer giveBonus(Long id, Double bonus);

  Employer promoteToAdmin(Long id);

  Employer removeAdmin(Long id);
}
