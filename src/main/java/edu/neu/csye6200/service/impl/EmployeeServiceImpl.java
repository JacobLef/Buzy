package edu.neu.csye6200.service.impl;

import edu.neu.csye6200.dto.request.CreateEmployeeRequest;
import edu.neu.csye6200.dto.request.UpdateEmployeeRequest;
import edu.neu.csye6200.exception.BusinessNotFoundException;
import edu.neu.csye6200.exception.EmployeeNotFoundException;
import edu.neu.csye6200.exception.EmployerNotFoundException;
import edu.neu.csye6200.factory.BusinessPersonFactory;
import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.model.domain.PersonStatus;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.repository.EmployeeRepository;
import edu.neu.csye6200.repository.EmployerRepository;
import edu.neu.csye6200.service.interfaces.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private EmployerRepository employerRepository;

  @Autowired
  private BusinessRepository businessRepository;

  @Override
  @Transactional
  public Employee createEmployee(CreateEmployeeRequest req) {
    Employee employee = BusinessPersonFactory.createEmployee(
        req.name(),
        req.email(),
        req.password(),
        req.salary(),
        req.position()
    );

    if (req.hireDate() != null) {
      employee.setHireDate(req.hireDate());
    }

    Company business = businessRepository.findById(req.businessId())
        .orElseThrow(() -> new BusinessNotFoundException(req.businessId()));
    employee.setCompany(business);

    if (req.managerId() != null) {
      Employer manager = employerRepository.findById(req.managerId())
          .orElseThrow(() -> new EmployerNotFoundException(req.managerId()));
      employee.setManager(manager);
      manager.addManagedEmployee(employee);
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
  @Transactional
  public Employee updateEmployee(Long id, UpdateEmployeeRequest req) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new EmployeeNotFoundException(id));

    if (req.name() != null) {
      employee.setName(req.name());
    }
    if (req.email() != null) {
      employee.setEmail(req.email());
    }
    if (req.password() != null) {
      employee.setPassword(req.password());
    }
    if (req.salary() != null) {
      employee.setSalary(req.salary());
    }
    if (req.position() != null) {
      employee.setPosition(req.position());
    }
    if (req.status() != null) {
      employee.setStatus(PersonStatus.valueOf(req.status()));
    }
    if (req.managerId() != null) {
      Employer manager = employerRepository.findById(req.managerId())
          .orElseThrow(() -> new EmployerNotFoundException(req.managerId()));

      if (employee.getManager() != null) {
        employee.getManager().removeManagedEmployee(employee);
      }

      employee.setManager(manager);
      manager.addManagedEmployee(employee);
    }

    return employeeRepository.save(employee);
  }

  @Override
  @Transactional
  public void deleteEmployee(Long id) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new EmployeeNotFoundException(id));

    if (employee.getManager() != null) {
      employee.getManager().removeManagedEmployee(employee);
    }

    employeeRepository.delete(employee);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employee> getEmployeesByBusiness(Long businessId) {
    return employeeRepository.findByBusinessId(businessId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employee> getEmployeesByManager(Long managerId) {
    return employeeRepository.findByManagerId(managerId);
  }

  @Override
  @Transactional
  public Employee assignManager(Long employeeId, Long managerId) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    Employer manager = employerRepository.findById(managerId)
        .orElseThrow(() -> new EmployerNotFoundException(managerId));

    if (!employee.getCompany().equals(manager.getCompany())) {
      throw new IllegalArgumentException("Employee and manager must be in the same business");
    }

    if (employee.getManager() != null) {
      employee.getManager().removeManagedEmployee(employee);
    }

    employee.setManager(manager);
    manager.addManagedEmployee(employee);

    employeeRepository.save(employee);
    employerRepository.save(manager);

    return employee;
  }

  @Override
  @Transactional
  public Employee removeManager(Long employeeId) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    if (employee.getManager() != null) {
      Employer oldManager = employee.getManager();
      oldManager.removeManagedEmployee(employee);
      employee.setManager(null);
      employerRepository.save(oldManager);
    }

    return employeeRepository.save(employee);
  }

  @Override
  @Transactional
  public Employee updateSalary(Long employeeId, Double newSalary) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    if (newSalary < 0) {
      throw new IllegalArgumentException("Salary cannot be negative");
    }

    employee.setSalary(newSalary);
    return employeeRepository.save(employee);
  }

  @Override
  @Transactional
  public Employee giveBonus(Long employeeId, Double bonusAmount) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    if (bonusAmount < 0) {
      throw new IllegalArgumentException("Bonus amount cannot be negative");
    }

    employee.setSalary(employee.getSalary() + bonusAmount);
    return employeeRepository.save(employee);
  }

  @Override
  @Transactional
  public Employee updatePosition(Long employeeId, String newPosition) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    employee.setPosition(newPosition);
    return employeeRepository.save(employee);
  }

  @Override
  @Transactional
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