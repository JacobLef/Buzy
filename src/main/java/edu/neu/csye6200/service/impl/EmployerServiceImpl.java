package edu.neu.csye6200.service.impl;

import edu.neu.csye6200.dto.request.CreateEmployerRequest;
import edu.neu.csye6200.dto.request.UpdateEmployerRequest;
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
import edu.neu.csye6200.service.interfaces.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EmployerServiceImpl implements EmployerService {

  @Autowired
  private EmployerRepository employerRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private BusinessRepository businessRepository;

  @Override
  @Transactional
  public Employer createEmployer(CreateEmployerRequest req) {
    Employer employer = BusinessPersonFactory.createEmployer(
        req.name(),
        req.email(),
        req.password(),
        req.department(),
        req.title()
    );

    Company business = businessRepository.findById(req.businessId())
        .orElseThrow(() -> new BusinessNotFoundException(req.businessId()));
    employer.setCompany(business);

    return employerRepository.save(employer);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Employer> getEmployer(Long id) {
    return employerRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employer> getAllEmployers() {
    return employerRepository.findAll();
  }

  @Override
  @Transactional
  public Employer updateEmployer(Long id, UpdateEmployerRequest req) {
    Employer employer = employerRepository.findById(id)
        .orElseThrow(() -> new EmployerNotFoundException(id));

    if (req.name() != null) {
      employer.setName(req.name());
    }
    if (req.email() != null) {
      employer.setEmail(req.email());
    }
    if (req.password() != null) {
      employer.setPassword(req.password());
    }
    if (req.department() != null) {
      employer.setDepartment(req.department());
    }
    if (req.title() != null) {
      employer.setTitle(req.title());
    }
    if (req.status() != null) {
      employer.setStatus(PersonStatus.valueOf(req.status()));
    }

    return employerRepository.save(employer);
  }

  @Override
  @Transactional
  public void deleteEmployer(Long id) {
    Employer employer = employerRepository.findById(id)
        .orElseThrow(() -> new EmployerNotFoundException(id));

    for (Employee employee : employer.getManagedEmployees()) {
      employee.setManager(null);
      employeeRepository.save(employee);
    }

    employerRepository.delete(employer);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employer> getEmployersByBusiness(Long businessId) {
    return employerRepository.findByBusinessId(businessId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employer> getEmployersByDepartment(String department) {
    return employerRepository.findByDepartment(department);
  }

  @Override
  @Transactional
  public Employer addManagedEmployee(Long employerId, Long employeeId) {
    Employer employer = employerRepository.findById(employerId)
        .orElseThrow(() -> new EmployerNotFoundException(employerId));

    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    if (!employer.getCompany().equals(employee.getCompany())) {
      throw new IllegalArgumentException("Employer and employee must be in the same business");
    }

    if (employee.getManager() != null) {
      employee.getManager().removeManagedEmployee(employee);
    }

    employer.addManagedEmployee(employee);
    employee.setManager(employer);

    employeeRepository.save(employee);
    return employerRepository.save(employer);
  }

  @Override
  @Transactional
  public Employer removeManagedEmployee(Long employerId, Long employeeId) {
    Employer employer = employerRepository.findById(employerId)
        .orElseThrow(() -> new EmployerNotFoundException(employerId));

    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    employer.removeManagedEmployee(employee);
    employee.setManager(null);

    employeeRepository.save(employee);
    return employerRepository.save(employer);
  }

  @Override
  @Transactional(readOnly = true)
  public Set<Employee> getDirectReports(Long employerId) {
    Employer employer = employerRepository.findById(employerId)
        .orElseThrow(() -> new EmployerNotFoundException(employerId));

    return employer.getManagedEmployees();
  }

  @Override
  @Transactional(readOnly = true)
  public int getDirectReportsCount(Long employerId) {
    Employer employer = employerRepository.findById(employerId)
        .orElseThrow(() -> new EmployerNotFoundException(employerId));

    return employer.getDirectReportsCount();
  }

  @Override
  @Transactional
  public Employer updateDepartment(Long employerId, String newDepartment) {
    Employer employer = employerRepository.findById(employerId)
        .orElseThrow(() -> new EmployerNotFoundException(employerId));

    employer.setDepartment(newDepartment);
    return employerRepository.save(employer);
  }

  @Override
  @Transactional
  public Employer updateTitle(Long employerId, String newTitle) {
    Employer employer = employerRepository.findById(employerId)
        .orElseThrow(() -> new EmployerNotFoundException(employerId));

    employer.setTitle(newTitle);
    return employerRepository.save(employer);
  }

  @Override
  @Transactional
  public Employer updateStatus(Long employerId, String status) {
    Employer employer = employerRepository.findById(employerId)
        .orElseThrow(() -> new EmployerNotFoundException(employerId));

    employer.setStatus(PersonStatus.valueOf(status));
    return employerRepository.save(employer);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employer> searchByName(String name) {
    return employerRepository.findByNameContainingIgnoreCase(name);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employer> getEmployersByTitle(String title) {
    return employerRepository.findByTitle(title);
  }
}