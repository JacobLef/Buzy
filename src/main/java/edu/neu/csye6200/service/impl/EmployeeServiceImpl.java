package edu.neu.csye6200.service.impl;

import edu.neu.csye6200.dto.request.CreateEmployeeRequest;
import edu.neu.csye6200.dto.request.UpdateEmployeeRequest;
import edu.neu.csye6200.exception.EmployeeNotFoundException;
import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.repository.EmployeeRepository;
import edu.neu.csye6200.repository.EmployerRepository;
import edu.neu.csye6200.service.interfaces.EmployeeService;
import edu.neu.csye6200.model.domain.PersonStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final EmployerRepository employerRepository;
  private final BusinessRepository businessRepository;

  public EmployeeServiceImpl(
      EmployeeRepository employeeRepository,
      EmployerRepository employerRepository,
      BusinessRepository businessRepository
  ) {
    this.employeeRepository = employeeRepository;
    this.employerRepository = employerRepository;
    this.businessRepository = businessRepository;
  }

  @Override
  public Employee createEmployee(CreateEmployeeRequest request) {
    Employee employee = new Employee(
        request.name(),
        request.email(),
        request.password(),
        request.salary(),
        request.position()
    );

    if (request.hireDate() != null) {
      employee.setHireDate(request.hireDate());
    }

    if (request.companyId() != null) {
      Company company = businessRepository.findById(request.companyId())
          .orElseThrow(() -> new RuntimeException("Company not found with id: " + request.companyId()));
      employee.setCompany(company);
    }

    if (request.managerId() != null) {
      Employer manager = employerRepository.findById(request.managerId())
          .orElseThrow(() -> new RuntimeException("Manager not found with id: " + request.managerId()));
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
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new EmployeeNotFoundException(id));

    if (request.name() != null) {
      employee.setName(request.name());
    }
    if (request.email() != null) {
      employee.setEmail(request.email());
    }
    if (request.password() != null) {
      employee.setPassword(request.password());
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
      Employer manager = employerRepository.findById(request.managerId())
          .orElseThrow(() -> new RuntimeException("Manager not found with id: " + request.managerId()));
      employee.setManager(manager);
    }
    if (request.status() != null) {
      employee.setStatus(request.status());
    }

    return employeeRepository.save(employee);
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

    Employer manager = employerRepository.findById(managerId)
        .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));

    employee.setManager(manager);
    return employeeRepository.save(employee);
  }

  @Override
  public Employee updateSalary(Long id, Double salary) {
    if (salary < 0) {
      throw new IllegalArgumentException("Salary cannot be negative");
    }

    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new EmployeeNotFoundException(id));

    employee.setSalary(salary);
    return employeeRepository.save(employee);
  }

  @Override
  public Employee giveBonus(Long id, Double bonus) {
    if (bonus < 0) {
      throw new IllegalArgumentException("Bonus amount cannot be negative");
    }

    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new EmployeeNotFoundException(id));

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