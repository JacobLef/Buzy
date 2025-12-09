package app.controller;

import app.dto.EmployeeDTO;
import app.dto.request.CreateEmployeeRequest;
import app.dto.request.UpdateEmployeeRequest;
import app.factory.DTOFactory;
import app.model.domain.Employee;
import app.service.interfaces.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST API based controller for Employee object based requests.
 *
 * @author jacoblefkowitz
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
  private final EmployeeService employeeService;
  private final DTOFactory dtoFactory;

  @Autowired
  public EmployeeController(EmployeeService employeeService, DTOFactory dtoFactory) {
    this.employeeService = employeeService;
    this.dtoFactory = dtoFactory;
  }

  @PostMapping
  public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody CreateEmployeeRequest req) {
    Employee employee = employeeService.createEmployee(req);
    EmployeeDTO dto = dtoFactory.createDTO(employee);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long id) {
    Optional<Employee> employee = employeeService.getEmployee(id);
    if (employee.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    EmployeeDTO dto = dtoFactory.createDTO(employee.get());
    return ResponseEntity.ok(dto);
  }

  @GetMapping
  public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
    List<Employee> employees = employeeService.getAllEmployees();
    List<EmployeeDTO> dtos = employees.stream()
        .map(dtoFactory::createDTO)
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  @PutMapping("/{id}")
  public ResponseEntity<EmployeeDTO> updateEmployee(
      @PathVariable Long id,
      @RequestBody UpdateEmployeeRequest req) {
    Employee employee = employeeService.updateEmployee(id, req);
    EmployeeDTO dto = dtoFactory.createDTO(employee);
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
    employeeService.deleteEmployee(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/business/{businessId}")
  public ResponseEntity<List<EmployeeDTO>> getEmployeesByBusinessId(
      @PathVariable Long businessId) {
    List<Employee> employees = employeeService.getEmployeesByBusiness(businessId);
    List<EmployeeDTO> dtos = employees.stream()
        .map(dtoFactory::createDTO)
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/manager/{managerId}")
  public ResponseEntity<List<EmployeeDTO>> getEmployeesByManager(
      @PathVariable Long managerId) {
    List<Employee> employees = employeeService.getEmployeesByManager(managerId);
    List<EmployeeDTO> dtos = employees.stream()
        .map(dtoFactory::createDTO)
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  @PutMapping("/{id}/manager")
  public ResponseEntity<EmployeeDTO> assignManager(
      @PathVariable Long id,
      @RequestParam Long managerId) {
    Employee employee = employeeService.assignManager(id, managerId);
    EmployeeDTO dto = dtoFactory.createDTO(employee);
    return ResponseEntity.ok(dto);
  }
}
