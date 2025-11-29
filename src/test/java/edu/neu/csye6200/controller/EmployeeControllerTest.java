package edu.neu.csye6200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.csye6200.TestConfig;
import edu.neu.csye6200.dto.request.CreateEmployeeRequest;
import edu.neu.csye6200.dto.request.UpdateEmployeeRequest;
import edu.neu.csye6200.exception.EmployeeNotFoundException;
import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.service.interfaces.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = TestConfig.class)
class EmployeeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private EmployeeService employeeService;

  private Employee testEmployee;
  private Company testCompany;

  @BeforeEach
  public void setUp() {
    testCompany = new Company("TechStart", "123 Main St");
    testCompany.setId(1L);

    Employer testManager = new Employer(
        "Jane Manager",
        "jane@test.com",
        "pass",
        150000.0,
        "Engineering",
        "CTO"
    );
    testManager.setId(2L);
    testManager.setCompany(testCompany);

    testEmployee = new Employee("John Doe", "john@test.com", "pass", 75000.0, "Engineer");
    testEmployee.setId(1L);
    testEmployee.setCompany(testCompany);
    testEmployee.setManager(testManager);
    testEmployee.setHireDate(LocalDate.of(2020, 1, 15));
  }

  @Test
  public void createEmployee_Success() throws Exception {
    CreateEmployeeRequest request = new CreateEmployeeRequest(
        "John Doe",
        "john@test.com",
        "password123",
        75000.0,
        "Engineer",
        1L,
        null,
        null
    );

    when(employeeService.createEmployee(any(CreateEmployeeRequest.class)))
        .thenReturn(testEmployee);

    mockMvc.perform(post("/api/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@test.com"))
        .andExpect(jsonPath("$.salary").value(75000.0))
        .andExpect(jsonPath("$.position").value("Engineer"));

    verify(employeeService, times(1)).createEmployee(any(CreateEmployeeRequest.class));
  }

  @Test
  public void getEmployee_Success() throws Exception {
    when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));

    mockMvc.perform(get("/api/employees/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@test.com"));

    verify(employeeService, times(1)).getEmployee(1L);
  }

  @Test
  public void getEmployee_NotFound() throws Exception {
    when(employeeService.getEmployee(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/employees/999"))
        .andExpect(status().isNotFound());

    verify(employeeService, times(1)).getEmployee(999L);
  }

  @Test
  public void getAllEmployees_Success() throws Exception {
    Employee employee2 = new Employee(
        "Jane Smith",
        "jane@test.com",
        "pass",
        85000.0,
        "Senior Engineer"
    );
    employee2.setId(2L);
    employee2.setCompany(testCompany);

    List<Employee> employees = Arrays.asList(testEmployee, employee2);
    when(employeeService.getAllEmployees()).thenReturn(employees);

    mockMvc.perform(get("/api/employees"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("John Doe"))
        .andExpect(jsonPath("$[1].name").value("Jane Smith"));

    verify(employeeService, times(1)).getAllEmployees();
  }

  @Test
  public void updateEmployee_Success() throws Exception {
    UpdateEmployeeRequest request = new UpdateEmployeeRequest(
        "John Updated",
        null,
        null,
        80000.0,
        "Senior Engineer",
        null,
        null,
        null
    );

    Employee updatedEmployee = new Employee(
        "John Updated",
        "john@test.com",
        "pass",
        80000.0,
        "Senior Engineer"
    );
    updatedEmployee.setId(1L);
    updatedEmployee.setCompany(testCompany);

    when(employeeService.updateEmployee(eq(1L), any(UpdateEmployeeRequest.class)))
        .thenReturn(updatedEmployee);

    mockMvc.perform(put("/api/employees/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("John Updated"))
        .andExpect(jsonPath("$.salary").value(80000.0))
        .andExpect(jsonPath("$.position").value("Senior Engineer"));

    verify(employeeService, times(1)).updateEmployee(eq(1L), any(UpdateEmployeeRequest.class));
  }

  @Test
  public void updateEmployee_NotFound() throws Exception {
    UpdateEmployeeRequest request = new UpdateEmployeeRequest(
        "John Updated",
        null,
        null,
        80000.0,
        null,
        null,
        null,
        null
    );

    when(employeeService.updateEmployee(eq(999L), any(UpdateEmployeeRequest.class)))
        .thenThrow(new EmployeeNotFoundException(999L));

    mockMvc.perform(put("/api/employees/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Employee not found with id: 999"));

    verify(employeeService, times(1)).updateEmployee(eq(999L), any(UpdateEmployeeRequest.class));
  }

  @Test
  public void deleteEmployee_Success() throws Exception {
    doNothing().when(employeeService).deleteEmployee(1L);

    mockMvc.perform(delete("/api/employees/1"))
        .andExpect(status().isNoContent());

    verify(employeeService, times(1)).deleteEmployee(1L);
  }

  @Test
  public void deleteEmployee_NotFound() throws Exception {
    doThrow(new EmployeeNotFoundException(999L))
        .when(employeeService).deleteEmployee(999L);

    mockMvc.perform(delete("/api/employees/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Employee not found with id: 999"));

    verify(employeeService, times(1)).deleteEmployee(999L);
  }

  @Test
  public void getEmployeesByBusiness_Success() throws Exception {
    List<Employee> employees = Arrays.asList(testEmployee);
    when(employeeService.getEmployeesByBusiness(1L)).thenReturn(employees);

    mockMvc.perform(get("/api/employees/business/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].companyId").value(1));

    verify(employeeService, times(1)).getEmployeesByBusiness(1L);
  }

  @Test
  public void getEmployeesByManager_Success() throws Exception {
    List<Employee> employees = Arrays.asList(testEmployee);
    when(employeeService.getEmployeesByManager(2L)).thenReturn(employees);

    mockMvc.perform(get("/api/employees/manager/2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].managerId").value(2));

    verify(employeeService, times(1)).getEmployeesByManager(2L);
  }

  @Test
  public void assignManager_Success() throws Exception {
    when(employeeService.assignManager(1L, 2L)).thenReturn(testEmployee);

    mockMvc.perform(put("/api/employees/1/manager")
            .param("managerId", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.managerId").value(2));

    verify(employeeService, times(1)).assignManager(1L, 2L);
  }
}