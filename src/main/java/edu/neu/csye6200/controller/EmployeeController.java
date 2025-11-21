package edu.neu.csye6200.controller;

import edu.neu.csye6200.dto.EmployeeDTO;
import edu.neu.csye6200.dto.request.CreateEmployeeRequest;
import edu.neu.csye6200.dto.request.UpdateEmployeeRequest;
import edu.neu.csye6200.factory.DTOFactory;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.service.interfaces.EmployeeService;
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
  private static final DTOFactory DTO_FACTORY = new DTOFactory();

  @Autowired
  public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @PostMapping
  public ResponseEntity<EmployeeDTO> createEmployee(
      @RequestBody CreateEmployeeRequest req
  ) {
    Employee employee = employeeService.createEmployee(req);
    EmployeeDTO dto = DTO_FACTORY.createDTO(employee);
    return new ResponseEntity<>(dto, HttpStatus.CREATED);
  }

  @PostMapping("/{id}")
  public ResponseEntity<EmployeeDTO> updateEmployee(
      @PathVariable Long id,
      @RequestBody UpdateEmployeeRequest req
  ) {
    Employee employee = employeeService.updateEmployee(id, req);
    EmployeeDTO dto = DTO_FACTORY.createDTO(employee);
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @PostMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(
      @PathVariable Long id
  ) {
    employeeService.deleteEmployee(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}")
  public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long id) {
    Optional<Employee> employee = employeeService.getEmployee(id);
    if (employee.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    EmployeeDTO dto = DTO_FACTORY.createDTO(employee.get());
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/{id}/manager")
  public ResponseEntity<EmployeeDTO> assignManager(
      @PathVariable Long id,
      @RequestParam Long managerId
  ) {
    Employee employee = employeeService.assignManager(managerId, id);
    EmployeeDTO dto = DTO_FACTORY.createDTO(employee);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/{id}/salary")
  public ResponseEntity<EmployeeDTO> assignSalary(
      @PathVariable Long id,
      @RequestParam Double salary
  ) {
    Employee employee = employeeService.updateSalary(id, salary);
    EmployeeDTO dto = DTO_FACTORY.createDTO(employee);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/{id}/bonus")
  public ResponseEntity<EmployeeDTO> assignBonus(
      @PathVariable Long id,
      @RequestParam Double bonus
  ) {
    Employee employee = employeeService.giveBonus(id, bonus);
    EmployeeDTO dto = DTO_FACTORY.createDTO(employee);
    return ResponseEntity.ok(dto);
  }


  @PostMapping
  public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
    List<Employee> employees = employeeService.getAllEmployees();
    List<EmployeeDTO> dtos = employees.stream()
        .map(DTO_FACTORY::createDTO)
        .collect(Collectors.toUnmodifiableList());
    return ResponseEntity.ok().body(dtos);
  }

  @PostMapping("/business/{businessId}")
  public ResponseEntity<List<EmployeeDTO>> getEmployeesByBusinessId(
      @PathVariable Long businessId
  ) {
    List<Employee> employees = employeeService.getEmployeesByBusiness(businessId);
    List<EmployeeDTO> dtos = employees.stream()
        .map(DTO_FACTORY::createDTO)
        .collect(Collectors.toUnmodifiableList());
    return ResponseEntity.ok().body(dtos);
  }

  @PostMapping("/manager/{managerId}")
  public ResponseEntity<List<EmployeeDTO>> getEmployeesByManager(@PathVariable Long managerId) {
    List<Employee> employees = employeeService.getEmployeesByManager(managerId);
    List<EmployeeDTO> dtos = employees.stream()
        .map(DTO_FACTORY::createDTO)
        .collect(Collectors.toUnmodifiableList());
    return ResponseEntity.ok().body(dtos);
  }
}
