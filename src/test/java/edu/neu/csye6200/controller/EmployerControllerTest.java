package edu.neu.csye6200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.csye6200.TestConfig;
import edu.neu.csye6200.dto.request.CreateEmployerRequest;
import edu.neu.csye6200.dto.request.UpdateEmployerRequest;
import edu.neu.csye6200.exception.EmployerNotFoundException;
import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.service.interfaces.EmployerService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployerController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = TestConfig.class)
class EmployerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private EmployerService employerService;

  private Employer testEmployer;
  private Company testCompany;

  @BeforeEach
  public void setUp() {
    testCompany = new Company("TechStart", "123 Main St");
    testCompany.setId(1L);

    testEmployer = new Employer(
        "Jane Manager",
        "jane@test.com",
        "password123",
        150000.0,
        "Engineering",
        "CTO"
    );
    testEmployer.setId(1L);
    testEmployer.setCompany(testCompany);
    testEmployer.setHireDate(LocalDate.of(2018, 3, 15));
  }

  @Test
  public void createEmployer_Success() throws Exception {
    CreateEmployerRequest request = new CreateEmployerRequest(
        "Jane Manager",
        "jane@test.com",
        "password123",
        150000.0,
        "Engineering",
        "CTO",
        1L,
        null
    );

    when(employerService.createEmployer(any(CreateEmployerRequest.class)))
        .thenReturn(testEmployer);

    mockMvc.perform(post("/api/employers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Jane Manager"))
        .andExpect(jsonPath("$.email").value("jane@test.com"))
        .andExpect(jsonPath("$.salary").value(150000.0))
        .andExpect(jsonPath("$.department").value("Engineering"))
        .andExpect(jsonPath("$.title").value("CTO"));

    verify(employerService, times(1)).createEmployer(any(CreateEmployerRequest.class));
  }

  @Test
  public void getEmployer_Success() throws Exception {
    when(employerService.getEmployer(1L)).thenReturn(Optional.of(testEmployer));

    mockMvc.perform(get("/api/employers/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Jane Manager"))
        .andExpect(jsonPath("$.email").value("jane@test.com"))
        .andExpect(jsonPath("$.salary").value(150000.0))
        .andExpect(jsonPath("$.department").value("Engineering"))
        .andExpect(jsonPath("$.title").value("CTO"));

    verify(employerService, times(1)).getEmployer(1L);
  }

  @Test
  public void getEmployer_NotFound() throws Exception {
    when(employerService.getEmployer(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/employers/999"))
        .andExpect(status().isNotFound());

    verify(employerService, times(1)).getEmployer(999L);
  }

  @Test
  public void getAllEmployers_Success() throws Exception {
    Employer employer2 = new Employer(
        "Bob Director",
        "bob@test.com",
        "pass",
        140000.0,
        "Marketing",
        "Director"
    );
    employer2.setId(2L);
    employer2.setCompany(testCompany);

    List<Employer> employers = Arrays.asList(testEmployer, employer2);
    when(employerService.getAllEmployers()).thenReturn(employers);

    mockMvc.perform(get("/api/employers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("Jane Manager"))
        .andExpect(jsonPath("$[1].name").value("Bob Director"));

    verify(employerService, times(1)).getAllEmployers();
  }

  @Test
  public void updateEmployer_Success() throws Exception {
    UpdateEmployerRequest request = new UpdateEmployerRequest(
        "Jane Updated",
        null,
        null,
        175000.0,
        "Product",
        "VP of Engineering",
        null,
        null
    );

    Employer updatedEmployer = new Employer(
        "Jane Updated",
        "jane@test.com",
        "password123",
        175000.0,
        "Product",
        "VP of Engineering"
    );
    updatedEmployer.setId(1L);
    updatedEmployer.setCompany(testCompany);

    when(employerService.updateEmployer(eq(1L), any(UpdateEmployerRequest.class)))
        .thenReturn(updatedEmployer);

    mockMvc.perform(put("/api/employers/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Jane Updated"))
        .andExpect(jsonPath("$.salary").value(175000.0))
        .andExpect(jsonPath("$.department").value("Product"))
        .andExpect(jsonPath("$.title").value("VP of Engineering"));

    verify(employerService, times(1)).updateEmployer(eq(1L), any(UpdateEmployerRequest.class));
  }

  @Test
  public void updateEmployer_NotFound() throws Exception {
    UpdateEmployerRequest request = new UpdateEmployerRequest(
        "Jane Updated",
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    when(employerService.updateEmployer(eq(999L), any(UpdateEmployerRequest.class)))
        .thenThrow(new EmployerNotFoundException(999L));

    mockMvc.perform(put("/api/employers/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Employer not found with id: 999"));

    verify(employerService, times(1)).updateEmployer(eq(999L), any(UpdateEmployerRequest.class));
  }

  @Test
  public void deleteEmployer_Success() throws Exception {
    doNothing().when(employerService).deleteEmployer(1L);

    mockMvc.perform(delete("/api/employers/1"))
        .andExpect(status().isNoContent());

    verify(employerService, times(1)).deleteEmployer(1L);
  }

  @Test
  public void deleteEmployer_NotFound() throws Exception {
    doThrow(new EmployerNotFoundException(999L))
        .when(employerService).deleteEmployer(999L);

    mockMvc.perform(delete("/api/employers/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Employer not found with id: 999"));

    verify(employerService, times(1)).deleteEmployer(999L);
  }

  @Test
  public void getEmployersByBusiness_Success() throws Exception {
    List<Employer> employers = Arrays.asList(testEmployer);
    when(employerService.getEmployersByBusiness(1L)).thenReturn(employers);

    mockMvc.perform(get("/api/employers/business/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].companyId").value(1));

    verify(employerService, times(1)).getEmployersByBusiness(1L);
  }

  @Test
  public void getEmployersByDepartment_Success() throws Exception {
    List<Employer> employers = Arrays.asList(testEmployer);
    when(employerService.getEmployersByDepartment("Engineering")).thenReturn(employers);

    mockMvc.perform(get("/api/employers/department/Engineering"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].department").value("Engineering"));

    verify(employerService, times(1)).getEmployersByDepartment("Engineering");
  }

  @Test
  public void getDirectReports_Success() throws Exception {
    Employee employee1 = new Employee(
        "John Doe",
        "john@test.com",
        "pass",
        75000.0,
        "Engineer"
    );
    employee1.setId(1L);
    employee1.setCompany(testCompany);
    employee1.setManager(testEmployer);

    Employee employee2 = new Employee(
        "Jane Smith",
        "janesmith@test.com",
        "pass",
        80000.0,
        "Senior Engineer"
    );
    employee2.setId(2L);
    employee2.setCompany(testCompany);
    employee2.setManager(testEmployer);

    Set<Employee> directReports = new HashSet<>(Arrays.asList(employee1, employee2));
    when(employerService.getDirectReports(1L)).thenReturn(directReports);

    mockMvc.perform(get("/api/employers/1/direct-reports"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));

    verify(employerService, times(1)).getDirectReports(1L);
  }

  @Test
  public void getDirectReports_EmptySet() throws Exception {
    when(employerService.getDirectReports(1L)).thenReturn(new HashSet<>());

    mockMvc.perform(get("/api/employers/1/direct-reports"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));

    verify(employerService, times(1)).getDirectReports(1L);
  }

  @Test
  public void getDirectReports_EmployerNotFound() throws Exception {
    when(employerService.getDirectReports(999L))
        .thenThrow(new EmployerNotFoundException(999L));

    mockMvc.perform(get("/api/employers/999/direct-reports"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Employer not found with id: 999"));

    verify(employerService, times(1)).getDirectReports(999L);
  }
}