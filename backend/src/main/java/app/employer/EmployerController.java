package app.employer;

import app.employee.dto.EmployeeDTO;
import app.employer.dto.EmployerDTO;
import app.employer.dto.CreateEmployerRequest;
import app.employer.dto.UpdateEmployerRequest;
import app.common.factory.DTOFactory;
import app.employee.Employee;
import app.employer.Employer;
import app.employer.EmployerService;
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

  @PostMapping("/{id}/promote-admin")
  public ResponseEntity<EmployerDTO> promoteToAdmin(@PathVariable Long id) {
    Employer employer = employerService.promoteToAdmin(id);
    EmployerDTO dto = dtoFactory.createDTO(employer);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/{id}/remove-admin")
  public ResponseEntity<EmployerDTO> removeAdmin(@PathVariable Long id) {
    Employer employer = employerService.removeAdmin(id);
    EmployerDTO dto = dtoFactory.createDTO(employer);
    return ResponseEntity.ok(dto);
  }
}