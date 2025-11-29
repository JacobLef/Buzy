package edu.neu.csye6200.service.impl;

import edu.neu.csye6200.dto.request.CreateEmployerRequest;
import edu.neu.csye6200.dto.request.UpdateEmployerRequest;
import edu.neu.csye6200.exception.EmployerNotFoundException;
import edu.neu.csye6200.model.domain.Business;
import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.repository.EmployerRepository;
import edu.neu.csye6200.service.interfaces.EmployerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class EmployerServiceImpl implements EmployerService {

  private final EmployerRepository employerRepository;
  private final BusinessRepository businessRepository;

  public EmployerServiceImpl(
      EmployerRepository employerRepository,
      BusinessRepository businessRepository
  ) {
    this.employerRepository = employerRepository;
    this.businessRepository = businessRepository;
  }

  @Override
  public Employer createEmployer(CreateEmployerRequest request) {
    Employer employer = new Employer(
        request.name(),
        request.email(),
        request.password(),
        request.salary(),
        request.department(),
        request.title()
    );

    if (request.hireDate() != null) {
      employer.setHireDate(request.hireDate());
    }

    if (request.companyId() != null) {
      Company company = businessRepository.findById(request.companyId())
          .orElseThrow(() -> new RuntimeException("Company not found with id: " + request.companyId()));
      employer.setCompany(company);
    }

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
  public Employer updateEmployer(Long id, UpdateEmployerRequest request) {
    Employer employer = employerRepository.findById(id)
        .orElseThrow(() -> new EmployerNotFoundException(id));

    if (request.name() != null) {
      employer.setName(request.name());
    }
    if (request.email() != null) {
      employer.setEmail(request.email());
    }
    if (request.password() != null) {
      employer.setPassword(request.password());
    }
    if (request.salary() != null) {
      employer.setSalary(request.salary());
    }
    if (request.department() != null) {
      employer.setDepartment(request.department());
    }
    if (request.title() != null) {
      employer.setTitle(request.title());
    }
    if (request.hireDate() != null) {
      employer.setHireDate(request.hireDate());
    }
    if (request.status() != null) {
      employer.setStatus(request.status());
    }

    return employerRepository.save(employer);
  }

  @Override
  public void deleteEmployer(Long id) {
    if (!employerRepository.existsById(id)) {
      throw new EmployerNotFoundException(id);
    }
    employerRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employer> getEmployersByBusiness(Long companyId) {
    return employerRepository.findByCompanyId(companyId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Employer> getEmployersByDepartment(String department) {
    return employerRepository.findByDepartment(department);
  }

  @Override
  @Transactional(readOnly = true)
  public Set<Employee> getDirectReports(Long id) {
    Employer employer = employerRepository.findById(id)
        .orElseThrow(() -> new EmployerNotFoundException(id));

    return employer.getManagedEmployees();
  }

  @Override
  public Employer updateSalary(Long id, Double salary) {
    if (salary < 0) {
      throw new IllegalArgumentException("Salary cannot be negative");
    }

    Employer employer = employerRepository.findById(id)
        .orElseThrow(() -> new EmployerNotFoundException(id));

    employer.setSalary(salary);
    return employerRepository.save(employer);
  }

  @Override
  public Employer giveBonus(Long id, Double bonus) {
    if (bonus < 0) {
      throw new IllegalArgumentException("Bonus amount cannot be negative");
    }

    Employer employer = employerRepository.findById(id)
        .orElseThrow(() -> new EmployerNotFoundException(id));

    employer.setSalary(employer.getSalary() + bonus);
    return employerRepository.save(employer);
  }
}