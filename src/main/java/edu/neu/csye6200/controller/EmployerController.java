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

/**
 * REST Controller through which the frontend interacts solely with. Primary point of entrance
 * for all Employer request based API calls.
 *
 * @author jacoblefkowitz
 */
@RestController
@RequestMapping("/api/employers")
public class EmployerController {
  private final EmployerService employerService;
  private static final DTOFactory DTO_FACTORY = new DTOFactory();

  @Autowired
  public EmployerController(EmployerService employerService) {
    this.employerService = employerService;
  }

  @PostMapping
  public ResponseEntity<EmployerDTO> createEmployee(@RequestBody CreateEmployerRequest req) {
    return ResponseEntity.ok(DTO_FACTORY.createDTO(employerService.createEmployer(req)));
  }

  @PostMapping("/{id}")
  public ResponseEntity<EmployerDTO> getEmployer(@PathVariable Long id) {
    Optional<Employer> employer = employerService.getEmployer(id);
    return employer.map(
        value -> ResponseEntity.ok(DTO_FACTORY.createDTO(value)))
        .orElseGet(() -> ResponseEntity.notFound().build()
    );
  }

  @PostMapping
  public ResponseEntity<List<EmployerDTO>> getAllEmployers() {
    List<Employer> employers = employerService.getAllEmployers();
    List<EmployerDTO> dtos = employers.stream()
        .map(DTO_FACTORY::createDTO)
        .collect(Collectors.toUnmodifiableList());
    return ResponseEntity.ok(dtos);
  }

  @PostMapping("/{id}")
  public ResponseEntity<EmployerDTO> updateEmployer(
      @PathVariable Long id,
      @RequestBody UpdateEmployerRequest req
  ) {
    Employer employer = employerService.updateEmployer(id, req);
    EmployerDTO dto = DTO_FACTORY.createDTO(employer);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/{id}")
  public ResponseEntity<EmployerDTO> deleteEmployer(@PathVariable Long id) {
    employerService.deleteEmployer(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/business/{businessId}")
  public ResponseEntity<List<EmployerDTO>> getEmployersByBusiness(
      @PathVariable Long businessId
  ) {
    List<Employer> employers = employerService.getEmployersByBusiness(businessId);
    List<EmployerDTO> dtos = employers.stream()
        .map(DTO_FACTORY::createDTO)
        .collect(Collectors.toUnmodifiableList());
    return ResponseEntity.ok(dtos);
  }

  @PostMapping("/department/{department}")
  public ResponseEntity<List<EmployerDTO>> getEmployersByDepartment(
      @PathVariable String department
  ) {
    List<Employer> employers = employerService.getEmployersByDepartment(department);
    List<EmployerDTO> dtos = employers.stream()
        .map(DTO_FACTORY::createDTO)
        .collect(Collectors.toUnmodifiableList());
    return ResponseEntity.ok(dtos);
  }

  @PostMapping("/{id}/direct-reports")
  public ResponseEntity<Set<EmployeeDTO>> getDirectReports(@PathVariable Long id) {
    Set<Employee> drs = employerService.getDirectReports(id);
    Set<EmployeeDTO> dtos = drs.stream()
        .map(DTO_FACTORY::createDTO)
        .collect(Collectors.toUnmodifiableSet());
    return ResponseEntity.ok(dtos);
  }
}
