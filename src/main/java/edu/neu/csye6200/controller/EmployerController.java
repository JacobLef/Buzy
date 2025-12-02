package edu.neu.csye6200.controller;

import edu.neu.csye6200.dto.EmployeeDTO;
import edu.neu.csye6200.dto.EmployerDTO;
import edu.neu.csye6200.dto.request.CreateEmployerRequest;
import edu.neu.csye6200.dto.request.UpdateEmployerRequest;
import edu.neu.csye6200.factory.DTOFactory;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.service.interfaces.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

  private final EmployerService employerService;
  private final DTOFactory dtoFactory;

  @Autowired
  public EmployerController(EmployerService employerService, DTOFactory dtoFactory) {
    this.employerService = employerService;
    this.dtoFactory = dtoFactory;
  }

  @PostMapping
  public ResponseEntity<EmployerDTO> createEmployer(@RequestBody CreateEmployerRequest req) {
    return ResponseEntity.status(201).body(dtoFactory.createDTO(employerService.createEmployer(req)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployerDTO> getEmployer(@PathVariable Long id) {
    Optional<Employer> employer = employerService.getEmployer(id);
    return employer.map(
            value -> ResponseEntity.ok(dtoFactory.createDTO(value)))
        .orElseGet(() -> ResponseEntity.notFound().build()
        );
  }

  @GetMapping
  public ResponseEntity<List<EmployerDTO>> getAllEmployers() {
    List<Employer> employers = employerService.getAllEmployers();
    List<EmployerDTO> dtos = employers.stream()
        .map(dtoFactory::createDTO)
        .collect(Collectors.toUnmodifiableList());
    return ResponseEntity.ok(dtos);
  }

  @PutMapping("/{id}")
  public ResponseEntity<EmployerDTO> updateEmployer(
      @PathVariable Long id,
      @RequestBody UpdateEmployerRequest req
  ) {
    Employer employer = employerService.updateEmployer(id, req);
    EmployerDTO dto = dtoFactory.createDTO(employer);
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployer(@PathVariable Long id) {
    employerService.deleteEmployer(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/business/{businessId}")
  public ResponseEntity<List<EmployerDTO>> getEmployersByBusiness(
      @PathVariable Long businessId
  ) {
    List<Employer> employers = employerService.getEmployersByBusiness(businessId);
    List<EmployerDTO> dtos = employers.stream()
        .map(dtoFactory::createDTO)
        .collect(Collectors.toUnmodifiableList());
    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/department/{department}")
  public ResponseEntity<List<EmployerDTO>> getEmployersByDepartment(
      @PathVariable String department
  ) {
    List<Employer> employers = employerService.getEmployersByDepartment(department);
    List<EmployerDTO> dtos = employers.stream()
        .map(dtoFactory::createDTO)
        .collect(Collectors.toUnmodifiableList());
    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/{id}/direct-reports")
  public ResponseEntity<Set<EmployeeDTO>> getDirectReports(@PathVariable Long id) {
    Set<Employee> drs = employerService.getDirectReports(id);
    Set<EmployeeDTO> dtos = drs.stream()
        .map(dtoFactory::createDTO)
        .collect(Collectors.toUnmodifiableSet());
    return ResponseEntity.ok(dtos);
  }
}