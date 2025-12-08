package edu.neu.csye6200.service;

import edu.neu.csye6200.dto.request.CreateEmployerRequest;
import edu.neu.csye6200.dto.request.UpdateEmployerRequest;
import edu.neu.csye6200.exception.EmployerNotFoundException;
import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.model.domain.PersonStatus;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.repository.EmployerRepository;
import edu.neu.csye6200.service.impl.EmployerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {

  @Mock
  private EmployerRepository employerRepository;

  @Mock
  private BusinessRepository businessRepository;

  @InjectMocks
  private EmployerServiceImpl employerService;

  private Employer testEmployer;
  private Company testCompany;
  private Employee testEmployee;

  @BeforeEach
  void setUp() {
    testCompany = new Company("Test Company", "123 Test St");
    testCompany.setId(1L);

    testEmployer = new Employer(
        "Jane Manager",
        "jane@test.com",
        "password",
        80000.0,
        "Engineering",
        "Manager"
    );
    testEmployer.setId(1L);
    testEmployer.setCompany(testCompany);

    testEmployee = new Employee(
        "John Doe",
        "john@test.com",
        "password",
        50000.0,
        "Developer"
    );
    testEmployee.setId(1L);
  }

  @Nested
  @DisplayName("Create Employer Tests")
  class CreateEmployerTests {

    @Test
    @DisplayName("Should create employer with all fields")
    void createEmployer_WithAllFields_Success() {
      CreateEmployerRequest request = new CreateEmployerRequest(
          "Jane Manager",
          "jane@test.com",
          "password",
          80000.0,
          "Engineering",
          "Manager",
          1L,
          LocalDate.now()
      );

      when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
      when(employerRepository.save(any(Employer.class))).thenAnswer(invocation -> {
        Employer emp = invocation.getArgument(0);
        emp.setId(1L);
        return emp;
      });

      Employer result = employerService.createEmployer(request);

      assertNotNull(result);
      assertEquals("Jane Manager", result.getName());
      assertEquals("jane@test.com", result.getEmail());
      assertEquals(80000.0, result.getSalary());
      assertEquals("Engineering", result.getDepartment());
      assertEquals("Manager", result.getTitle());
      assertEquals(testCompany, result.getCompany());

      verify(employerRepository).save(any(Employer.class));
      verify(businessRepository).findById(1L);
    }

    @Test
    @DisplayName("Should create employer without company")
    void createEmployer_WithoutCompany_Success() {
      CreateEmployerRequest request = new CreateEmployerRequest(
          "Jane Manager",
          "jane@test.com",
          "password",
          80000.0,
          "Engineering",
          "Manager",
          null,
          LocalDate.now()
      );

      when(employerRepository.save(any(Employer.class))).thenAnswer(invocation -> {
        Employer emp = invocation.getArgument(0);
        emp.setId(1L);
        return emp;
      });

      Employer result = employerService.createEmployer(request);

      assertNotNull(result);
      assertNull(result.getCompany());

      verify(employerRepository).save(any(Employer.class));
      verify(businessRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should create employer without hire date")
    void createEmployer_WithoutHireDate_Success() {
      CreateEmployerRequest request = new CreateEmployerRequest(
          "Jane Manager",
          "jane@test.com",
          "password",
          80000.0,
          "Engineering",
          "Manager",
          null,
          null
      );

      when(employerRepository.save(any(Employer.class))).thenAnswer(invocation -> {
        Employer emp = invocation.getArgument(0);
        emp.setId(1L);
        return emp;
      });

      Employer result = employerService.createEmployer(request);

      assertNotNull(result);
      verify(employerRepository).save(any(Employer.class));
    }

    @Test
    @DisplayName("Should throw exception when company not found")
    void createEmployer_CompanyNotFound_ThrowsException() {
      CreateEmployerRequest request = new CreateEmployerRequest(
          "Jane Manager",
          "jane@test.com",
          "password",
          80000.0,
          "Engineering",
          "Manager",
          999L,
          LocalDate.now()
      );

      when(businessRepository.findById(999L)).thenReturn(Optional.empty());

      RuntimeException exception = assertThrows(RuntimeException.class,
          () -> employerService.createEmployer(request));

      assertTrue(exception.getMessage().contains("Company not found"));
      verify(employerRepository, never()).save(any(Employer.class));
    }
  }

  @Nested
  @DisplayName("Get Employer Tests")
  class GetEmployerTests {

    @Test
    @DisplayName("Should return employer when found")
    void getEmployer_Found_ReturnsEmployer() {
      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));

      Optional<Employer> result = employerService.getEmployer(1L);

      assertTrue(result.isPresent());
      assertEquals(testEmployer.getId(), result.get().getId());
      assertEquals(testEmployer.getName(), result.get().getName());
      assertEquals(testEmployer.getDepartment(), result.get().getDepartment());
    }

    @Test
    @DisplayName("Should return empty when employer not found")
    void getEmployer_NotFound_ReturnsEmpty() {
      when(employerRepository.findById(999L)).thenReturn(Optional.empty());

      Optional<Employer> result = employerService.getEmployer(999L);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return all employers")
    void getAllEmployers_ReturnsAll() {
      Employer employer2 = new Employer(
          "Bob Director",
          "bob@test.com",
          "password",
          100000.0,
          "Sales",
          "Director"
      );
      List<Employer> employers = Arrays.asList(testEmployer, employer2);

      when(employerRepository.findAll()).thenReturn(employers);

      List<Employer> result = employerService.getAllEmployers();

      assertEquals(2, result.size());
      verify(employerRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no employers")
    void getAllEmployers_Empty_ReturnsEmptyList() {
      when(employerRepository.findAll()).thenReturn(Collections.emptyList());

      List<Employer> result = employerService.getAllEmployers();

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("Update Employer Tests")
  class UpdateEmployerTests {

    @Test
    @DisplayName("Should update all employer fields")
    void updateEmployer_AllFields_Success() {
      UpdateEmployerRequest request = new UpdateEmployerRequest(
          "Jane Updated",
          "jane.updated@test.com",
          "newpassword",
          90000.0,
          "Product",
          "Senior Manager",
          LocalDate.of(2024, 1, 1),
          PersonStatus.Active
      );

      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));
      when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

      Employer result = employerService.updateEmployer(1L, request);

      assertEquals("Jane Updated", result.getName());
      assertEquals("jane.updated@test.com", result.getEmail());
      assertEquals(90000.0, result.getSalary());
      assertEquals("Product", result.getDepartment());
      assertEquals("Senior Manager", result.getTitle());

      verify(employerRepository).save(testEmployer);
    }

    @Test
    @DisplayName("Should update only provided fields")
    void updateEmployer_PartialUpdate_Success() {
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

      String originalEmail = testEmployer.getEmail();
      Double originalSalary = testEmployer.getSalary();
      String originalDepartment = testEmployer.getDepartment();

      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));
      when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

      Employer result = employerService.updateEmployer(1L, request);

      assertEquals("Jane Updated", result.getName());
      assertEquals(originalEmail, result.getEmail());
      assertEquals(originalSalary, result.getSalary());
      assertEquals(originalDepartment, result.getDepartment());
    }

    @Test
    @DisplayName("Should update status")
    void updateEmployer_UpdateStatus_Success() {
      UpdateEmployerRequest request = new UpdateEmployerRequest(
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          PersonStatus.Inactive
      );

      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));
      when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

      Employer result = employerService.updateEmployer(1L, request);

      assertEquals(PersonStatus.Inactive, result.getStatus());
      verify(employerRepository).save(testEmployer);
    }

    @Test
    @DisplayName("Should throw exception when employer not found")
    void updateEmployer_NotFound_ThrowsException() {
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

      when(employerRepository.findById(999L)).thenReturn(Optional.empty());

      assertThrows(EmployerNotFoundException.class,
          () -> employerService.updateEmployer(999L, request));

      verify(employerRepository, never()).save(any(Employer.class));
    }
  }

  @Nested
  @DisplayName("Delete Employer Tests")
  class DeleteEmployerTests {

    @Test
    @DisplayName("Should delete employer when exists")
    void deleteEmployer_Exists_Success() {
      when(employerRepository.existsById(1L)).thenReturn(true);
      doNothing().when(employerRepository).deleteById(1L);

      assertDoesNotThrow(() -> employerService.deleteEmployer(1L));

      verify(employerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when employer not found")
    void deleteEmployer_NotFound_ThrowsException() {
      when(employerRepository.existsById(999L)).thenReturn(false);

      assertThrows(EmployerNotFoundException.class,
          () -> employerService.deleteEmployer(999L));

      verify(employerRepository, never()).deleteById(anyLong());
    }
  }

  @Nested
  @DisplayName("Get Employers By Business Tests")
  class GetEmployersByBusinessTests {

    @Test
    @DisplayName("Should return employers for business")
    void getEmployersByBusiness_ReturnsEmployers() {
      List<Employer> employers = Arrays.asList(testEmployer);

      when(employerRepository.findByCompanyId(1L)).thenReturn(employers);

      List<Employer> result = employerService.getEmployersByBusiness(1L);

      assertEquals(1, result.size());
      assertEquals(testEmployer, result.get(0));
    }

    @Test
    @DisplayName("Should return empty list when no employers for business")
    void getEmployersByBusiness_NoEmployers_ReturnsEmpty() {
      when(employerRepository.findByCompanyId(1L)).thenReturn(Collections.emptyList());

      List<Employer> result = employerService.getEmployersByBusiness(1L);

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("Get Employers By Department Tests")
  class GetEmployersByDepartmentTests {

    @Test
    @DisplayName("Should return employers for department")
    void getEmployersByDepartment_ReturnsEmployers() {
      List<Employer> employers = Arrays.asList(testEmployer);

      when(employerRepository.findByDepartment("Engineering")).thenReturn(employers);

      List<Employer> result = employerService.getEmployersByDepartment("Engineering");

      assertEquals(1, result.size());
      assertEquals("Engineering", result.get(0).getDepartment());
    }

    @Test
    @DisplayName("Should return empty list when no employers in department")
    void getEmployersByDepartment_NoEmployers_ReturnsEmpty() {
      when(employerRepository.findByDepartment("Marketing")).thenReturn(Collections.emptyList());

      List<Employer> result = employerService.getEmployersByDepartment("Marketing");

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("Get Direct Reports Tests")
  class GetDirectReportsTests {

    @Test
    @DisplayName("Should return direct reports")
    void getDirectReports_HasReports_ReturnsEmployees() {
      Set<Employee> managedEmployees = new HashSet<>();
      managedEmployees.add(testEmployee);
      testEmployer.setManagedEmployees(managedEmployees);

      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));

      Set<Employee> result = employerService.getDirectReports(1L);

      assertEquals(1, result.size());
      assertTrue(result.contains(testEmployee));
    }

    @Test
    @DisplayName("Should return empty set when no direct reports")
    void getDirectReports_NoReports_ReturnsEmpty() {
      testEmployer.setManagedEmployees(new HashSet<>());

      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));

      Set<Employee> result = employerService.getDirectReports(1L);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when employer not found")
    void getDirectReports_EmployerNotFound_ThrowsException() {
      when(employerRepository.findById(999L)).thenReturn(Optional.empty());

      assertThrows(EmployerNotFoundException.class,
          () -> employerService.getDirectReports(999L));
    }
  }

  @Nested
  @DisplayName("Update Salary Tests")
  class UpdateSalaryTests {

    @Test
    @DisplayName("Should update salary successfully")
    void updateSalary_Success() {
      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));
      when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

      Employer result = employerService.updateSalary(1L, 100000.0);

      assertEquals(100000.0, result.getSalary());
      verify(employerRepository).save(testEmployer);
    }

    @Test
    @DisplayName("Should throw exception for negative salary")
    void updateSalary_NegativeSalary_ThrowsException() {
      assertThrows(IllegalArgumentException.class,
          () -> employerService.updateSalary(1L, -1000.0));

      verify(employerRepository, never()).save(any(Employer.class));
    }

    @Test
    @DisplayName("Should throw exception when employer not found")
    void updateSalary_EmployerNotFound_ThrowsException() {
      when(employerRepository.findById(999L)).thenReturn(Optional.empty());

      assertThrows(EmployerNotFoundException.class,
          () -> employerService.updateSalary(999L, 100000.0));
    }

    @Test
    @DisplayName("Should allow zero salary")
    void updateSalary_ZeroSalary_Success() {
      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));
      when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

      Employer result = employerService.updateSalary(1L, 0.0);

      assertEquals(0.0, result.getSalary());
      verify(employerRepository).save(testEmployer);
    }
  }

  @Nested
  @DisplayName("Give Bonus Tests")
  class GiveBonusTests {

    @Test
    @DisplayName("Should add bonus to salary")
    void giveBonus_Success() {
      Double originalSalary = testEmployer.getSalary();

      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));
      when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

      Employer result = employerService.giveBonus(1L, 10000.0);

      assertEquals(originalSalary + 10000.0, result.getSalary());
      verify(employerRepository).save(testEmployer);
    }

    @Test
    @DisplayName("Should throw exception for negative bonus")
    void giveBonus_NegativeBonus_ThrowsException() {
      assertThrows(IllegalArgumentException.class,
          () -> employerService.giveBonus(1L, -500.0));

      verify(employerRepository, never()).save(any(Employer.class));
    }

    @Test
    @DisplayName("Should throw exception when employer not found")
    void giveBonus_EmployerNotFound_ThrowsException() {
      when(employerRepository.findById(999L)).thenReturn(Optional.empty());

      assertThrows(EmployerNotFoundException.class,
          () -> employerService.giveBonus(999L, 10000.0));
    }

    @Test
    @DisplayName("Should allow zero bonus")
    void giveBonus_ZeroBonus_Success() {
      Double originalSalary = testEmployer.getSalary();

      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));
      when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

      Employer result = employerService.giveBonus(1L, 0.0);

      assertEquals(originalSalary, result.getSalary());
      verify(employerRepository).save(testEmployer);
    }
  }

  @Nested
  @DisplayName("Edge Cases Tests")
  class EdgeCasesTests {

    @Test
    @DisplayName("Should handle employer with multiple direct reports")
    void getDirectReports_MultipleReports_ReturnsAll() {
      Employee employee2 = new Employee("Jane Doe", "janedoe@test.com", "password", 55000.0, "Designer");
      employee2.setId(2L);

      Employee employee3 = new Employee("Bob Smith", "bob@test.com", "password", 60000.0, "Analyst");
      employee3.setId(3L);

      Set<Employee> managedEmployees = new HashSet<>();
      managedEmployees.add(testEmployee);
      managedEmployees.add(employee2);
      managedEmployees.add(employee3);
      testEmployer.setManagedEmployees(managedEmployees);

      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));

      Set<Employee> result = employerService.getDirectReports(1L);

      assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Should handle updating employer with all null fields")
    void updateEmployer_AllNullFields_NoChanges() {
      UpdateEmployerRequest request = new UpdateEmployerRequest(
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null
      );

      String originalName = testEmployer.getName();
      String originalEmail = testEmployer.getEmail();
      Double originalSalary = testEmployer.getSalary();

      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));
      when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

      Employer result = employerService.updateEmployer(1L, request);

      assertEquals(originalName, result.getName());
      assertEquals(originalEmail, result.getEmail());
      assertEquals(originalSalary, result.getSalary());
    }

    @Test
    @DisplayName("Should handle large bonus amount")
    void giveBonus_LargeAmount_Success() {
      Double originalSalary = testEmployer.getSalary();
      Double largeBonus = 1000000.0;

      when(employerRepository.findById(1L)).thenReturn(Optional.of(testEmployer));
      when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

      Employer result = employerService.giveBonus(1L, largeBonus);

      assertEquals(originalSalary + largeBonus, result.getSalary());
    }
  }
}