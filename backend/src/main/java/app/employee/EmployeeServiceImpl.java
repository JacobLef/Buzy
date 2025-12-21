package app.employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.business.BusinessPerson;
import app.business.BusinessPersonRepository;
import app.business.BusinessRepository;
import app.business.Company;
import app.employee.dto.CreateEmployeeRequest;
import app.employee.dto.UpdateEmployeeRequest;
import app.user.PersonStatus;
import app.user.User;
import app.user.UserRepository;

/**
 * Transactional employee service implementation for all employee related functionality.
 *
 * @author Jacob Lefkowitz
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final BusinessPersonRepository businessPersonRepository;
  private final BusinessRepository businessRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public EmployeeServiceImpl(EmployeeRepository employeeRepository,
      BusinessPersonRepository businessPersonRepository, BusinessRepository businessRepository,
      UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.employeeRepository = employeeRepository;
    this.businessPersonRepository = businessPersonRepository;
    this.businessRepository = businessRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Employee createEmployee(CreateEmployeeRequest request) {
    Employee employee = new Employee(request.name(), request.email(), request.password(),
        request.salary(), request.position());

    if (request.hireDate() != null) {
      employee.setHireDate(request.hireDate());
    }

    if (request.companyId() != null) {
      Company company = businessRepository.findById(request.companyId()).orElseThrow(
          () -> new RuntimeException("Company not found with id: " + request.companyId()));
      employee.setCompany(company);
    }

    if (request.managerId() != null) {
      BusinessPerson manager = businessPersonRepository.findById(request.managerId()).orElseThrow(
          () -> new RuntimeException("Manager not found with id: " + request.managerId()));
      employee.setManager(manager);
    }

    return employeeRepository.save(employee);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Employee> getEmployee(Long id) {
    return employeeRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

  @Override
  public Employee updateEmployee(Long id, UpdateEmployeeRequest request) {
    Employee employee =
        employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

    // Track if email changed to update User table
    String oldEmail = employee.getEmail();
    boolean emailChanged = false;

    if (request.name() != null) {
      employee.setName(request.name());
    }
    if (request.email() != null && !request.email().equals(oldEmail)) {
      employee.setEmail(request.email());
      emailChanged = true;
    }

    // Handle password update: encrypt and sync with User table
    if (request.password() != null && !request.password().trim().isEmpty()) {
      String encryptedPassword = passwordEncoder.encode(request.password());
      employee.setPassword(encryptedPassword);

      // Update User table password for authentication
      Optional<User> userOpt = userRepository.findByBusinessPersonId(id);
      if (userOpt.isPresent()) {
        User user = userOpt.get();
        user.setPassword(encryptedPassword);
        userRepository.save(user);
      }
    }

    if (request.salary() != null) {
      employee.setSalary(request.salary());
    }
    if (request.position() != null) {
      employee.setPosition(request.position());
    }
    if (request.hireDate() != null) {
      employee.setHireDate(request.hireDate());
    }
    if (request.managerId() != null) {
      BusinessPerson manager = businessPersonRepository.findById(request.managerId()).orElseThrow(
          () -> new RuntimeException("Manager not found with id: " + request.managerId()));
      employee.setManager(manager);
    }
    if (request.status() != null) {
      employee.setStatus(request.status());
    }

    Employee savedEmployee = employeeRepository.save(employee);

    // Update User table email if it changed
    if (emailChanged) {
      Optional<User> userOpt = userRepository.findByBusinessPersonId(id);
      if (userOpt.isPresent()) {
        User user = userOpt.get();
        user.setEmail(request.email());
        userRepository.save(user);
      }
    }

    return savedEmployee;
  }

  @Override
  public void deleteEmployee(Long id) {
    if (!employeeRepository.existsById(id)) {
      throw new EmployeeNotFoundException(id);
    }
    employeeRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employee> getEmployeesByBusiness(Long companyId) {
    return employeeRepository.findByCompanyId(companyId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employee> getEmployeesByManager(Long managerId) {
    return employeeRepository.findByManagerId(managerId);
  }

  @Override
  public Employee assignManager(Long employeeId, Long managerId) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    BusinessPerson manager = businessPersonRepository.findById(managerId)
        .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));

    employee.setManager(manager);
    return employeeRepository.save(employee);
  }

  @Override
  public Employee updateSalary(Long id, Double salary) {
    if (salary < 0) {
      throw new IllegalArgumentException("Salary cannot be negative");
    }

    Employee employee =
        employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

    employee.setSalary(salary);
    return employeeRepository.save(employee);
  }

  @Override
  public Employee giveBonus(Long id, Double bonus) {
    if (bonus < 0) {
      throw new IllegalArgumentException("Bonus amount cannot be negative");
    }

    Employee employee =
        employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

    employee.setSalary(employee.getSalary() + bonus);
    return employeeRepository.save(employee);
  }

  @Override
  public Employee removeManager(Long employeeId) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    employee.setManager(null);
    return employeeRepository.save(employee);
  }

  @Override
  public Employee updatePosition(Long employeeId, String newPosition) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    employee.setPosition(newPosition);
    return employeeRepository.save(employee);
  }

  @Override
  public Employee updateStatus(Long employeeId, String status) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    employee.setStatus(PersonStatus.valueOf(status));
    return employeeRepository.save(employee);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employee> searchByName(String name) {
    return employeeRepository.findByNameContainingIgnoreCase(name);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employee> getEmployeesByPosition(String position) {
    return employeeRepository.findByPosition(position);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employee> getEmployeesHiredAfter(String date) {
    LocalDate localDate = LocalDate.parse(date);
    return employeeRepository.findByHireDateAfter(localDate);
  }
}
