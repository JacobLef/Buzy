package edu.neu.csye6200.service;

import edu.neu.csye6200.service.impl.EmployeeServiceImpl;
import edu.neu.csye6200.dto.request.CreateEmployeeRequest;
import edu.neu.csye6200.dto.request.UpdateEmployeeRequest;
import edu.neu.csye6200.exception.EmployeeNotFoundException;
import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.model.domain.PersonStatus;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.repository.EmployeeRepository;
import edu.neu.csye6200.repository.EmployerRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private EmployerRepository employerRepository;

  @Mock
  private BusinessRepository businessRepository;

  @InjectMocks
  private EmployeeServiceImpl employeeService;

  private Employee testEmployee;
  private Employer testManager;
  private Company testCompany;

  @BeforeEach
  void setUp() {
    testCompany = new Company("Test Company", "123 Test St");
    testCompany.setId(1L);

    testManager = new Employer(
        "Jane Manager",
        "jane@test.com",
        "password",
        80000.0,
        "Engineering",
        "Manager"
    );
    testManager.setId(1L);

    testEmployee = new Employee(
        "John Doe",
        "john@test.com",
        "password",
        50000.0,
        "Developer"
    );
    testEmployee.setId(1L);
    testEmployee.setCompany(testCompany);
  }

  @Nested
  @DisplayName("Create Employee Tests")
  class CreateEmployeeTests {

    @Test
    @DisplayName("Should create employee with all fields")
    void createEmployee_WithAllFields_Success() {
      CreateEmployeeRequest request = new CreateEmployeeRequest(
          "John Doe", "john@test.com", "password", 50000.0,
          "Developer", 1L, 1L, LocalDate.now()
      );

      when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
      when(employerRepository.findById(1L)).thenReturn(Optional.of(testManager));
      when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
        Employee emp = invocation.getArgument(0);
        emp.setId(1L);
        return emp;
      });

      Employee result = employeeService.createEmployee(request);

      assertNotNull(result);
      assertEquals("John Doe", result.getName());
      assertEquals("john@test.com", result.getEmail());
      assertEquals(50000.0, result.getSalary());
      assertEquals("Developer", result.getPosition());
      assertEquals(testCompany, result.getCompany());
      assertEquals(testManager, result.getManager());

      verify(employeeRepository).save(any(Employee.class));
      verify(businessRepository).findById(1L);
      verify(employerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should create employee without company")
    void createEmployee_WithoutCompany_Success() {
      CreateEmployeeRequest request = new CreateEmployeeRequest(
          "John Doe",
          "john@test.com",
          "password",
          50000.0,
          "Developer",
          null,
          null,
          LocalDate.now()
      );

      when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
        Employee emp = invocation.getArgument(0);
        emp.setId(1L);
        return emp;
      });

      Employee result = employeeService.createEmployee(request);

      assertNotNull(result);
      assertNull(result.getCompany());
      assertNull(result.getManager());

      verify(employeeRepository).save(any(Employee.class));
      verify(businessRepository, never()).findById(anyLong());
      verify(employerRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when company not found")
    void createEmployee_CompanyNotFound_ThrowsException() {
      CreateEmployeeRequest request = new CreateEmployeeRequest(
          "John Doe",
          "john@test.com",
          "password",
          50000.0,
          "Developer",
          999L,
          null,
          LocalDate.now()
      );

      when(businessRepository.findById(999L)).thenReturn(Optional.empty());

      RuntimeException exception = assertThrows(RuntimeException.class,
          () -> employeeService.createEmployee(request));

      assertTrue(exception.getMessage().contains("Company not found"));
      verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw exception when manager not found")
    void createEmployee_ManagerNotFound_ThrowsException() {
      CreateEmployeeRequest request = new CreateEmployeeRequest(
          "John Doe",
          "john@test.com",
          "password",
          50000.0,
          "Developer",
          1L,
          999L,
          LocalDate.now()
      );

      when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
      when(employerRepository.findById(999L)).thenReturn(Optional.empty());

      RuntimeException exception = assertThrows(RuntimeException.class,
          () -> employeeService.createEmployee(request));

      assertTrue(exception.getMessage().contains("Manager not found"));
      verify(employeeRepository, never()).save(any(Employee.class));
    }
  }

  @Nested
  @DisplayName("Get Employee Tests")
  class GetEmployeeTests {

    @Test
    @DisplayName("Should return employee when found")
    void getEmployee_Found_ReturnsEmployee() {
      when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

      Optional<Employee> result = employeeService.getEmployee(1L);

      assertTrue(result.isPresent());
      assertEquals(testEmployee.getId(), result.get().getId());
      assertEquals(testEmployee.getName(), result.get().getName());
    }

    @Test
    @DisplayName("Should return empty when employee not found")
    void getEmployee_NotFound_ReturnsEmpty() {
      when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

      Optional<Employee> result = employeeService.getEmployee(999L);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return all employees")
    void getAllEmployees_ReturnsAll() {
      Employee employee2 = new Employee(
          "Jane Doe",
          "jane@test.com",
          "password",
          55000.0,
          "Designer"
      );
      List<Employee> employees = Arrays.asList(testEmployee, employee2);

      when(employeeRepository.findAll()).thenReturn(employees);

      List<Employee> result = employeeService.getAllEmployees();

      assertEquals(2, result.size());
      verify(employeeRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no employees")
    void getAllEmployees_Empty_ReturnsEmptyList() {
      when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

      List<Employee> result = employeeService.getAllEmployees();

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("Update Employee Tests")
  class UpdateEmployeeTests {

    @Test
    @DisplayName("Should update all employee fields")
    void updateEmployee_AllFields_Success() {
      UpdateEmployeeRequest request = new UpdateEmployeeRequest(
          "John Updated",
          "john.updated@test.com",
          "newpassword",
          60000.0,
          "Senior Developer",
          1L,
          LocalDate.of(2024, 1, 1),
          PersonStatus.Active
      );

      when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
      when(employerRepository.findById(1L)).thenReturn(Optional.of(testManager));
      when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

      Employee result = employeeService.updateEmployee(1L, request);

      assertEquals("John Updated", result.getName());
      assertEquals("john.updated@test.com", result.getEmail());
      assertEquals(60000.0, result.getSalary());
      assertEquals("Senior Developer", result.getPosition());
      assertEquals(testManager, result.getManager());

      verify(employeeRepository).save(testEmployee);
    }

    @Test
    @DisplayName("Should update only provided fields")
    void updateEmployee_PartialUpdate_Success() {
      UpdateEmployeeRequest request = new UpdateEmployeeRequest(
          "John Updated",
          null,
          null,
          null,
          null,
          null,
          null,
          null
      );

      String originalEmail = testEmployee.getEmail();
      Double originalSalary = testEmployee.getSalary();

      when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
      when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

      Employee result = employeeService.updateEmployee(1L, request);

      assertEquals("John Updated", result.getName());
      assertEquals(originalEmail, result.getEmail());
      assertEquals(originalSalary, result.getSalary());
    }

    @Test
    @DisplayName("Should throw exception when employee not found")
    void updateEmployee_NotFound_ThrowsException() {
      UpdateEmployeeRequest request = new UpdateEmployeeRequest(
          "John Updated",
          null,
          null,
          null,
          null,
          null,
          null,
          null
      );

      when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

      assertThrows(EmployeeNotFoundException.class,
          () -> employeeService.updateEmployee(999L, request));

      verify(employeeRepository, never()).save(any(Employee.class));
    }
  }

  @Nested
  @DisplayName("Delete Employee Tests")
  class DeleteEmployeeTests {

    @Test
    @DisplayName("Should delete employee when exists")
    void deleteEmployee_Exists_Success() {
      when(employeeRepository.existsById(1L)).thenReturn(true);
      doNothing().when(employeeRepository).deleteById(1L);

      assertDoesNotThrow(() -> employeeService.deleteEmployee(1L));

      verify(employeeRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when employee not found")
    void deleteEmployee_NotFound_ThrowsException() {
      when(employeeRepository.existsById(999L)).thenReturn(false);

      assertThrows(EmployeeNotFoundException.class,
          () -> employeeService.deleteEmployee(999L));

      verify(employeeRepository, never()).deleteById(anyLong());
    }
  }

  @Nested
  @DisplayName("Get Employees By Business Tests")
  class GetEmployeesByBusinessTests {

    @Test
    @DisplayName("Should return employees for business")
    void getEmployeesByBusiness_ReturnsEmployees() {
      List<Employee> employees = Arrays.asList(testEmployee);

      when(employeeRepository.findByCompanyId(1L)).thenReturn(employees);

      List<Employee> result = employeeService.getEmployeesByBusiness(1L);

      assertEquals(1, result.size());
      assertEquals(testEmployee, result.get(0));
    }

    @Test
    @DisplayName("Should return empty list when no employees for business")
    void getEmployeesByBusiness_NoEmployees_ReturnsEmpty() {
      when(employeeRepository.findByCompanyId(1L)).thenReturn(Collections.emptyList());

      List<Employee> result = employeeService.getEmployeesByBusiness(1L);

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("Get Employees By Manager Tests")
  class GetEmployeesByManagerTests {

    @Test
    @DisplayName("Should return employees for manager")
    void getEmployeesByManager_ReturnsEmployees() {
      List<Employee> employees = Arrays.asList(testEmployee);

      when(employeeRepository.findByManagerId(1L)).thenReturn(employees);

      List<Employee> result = employeeService.getEmployeesByManager(1L);

      assertEquals(1, result.size());
    }
  }

  @Nested
  @DisplayName("Assign Manager Tests")
  class AssignManagerTests {

    @Test
    @DisplayName("Should assign manager to employee")
    void assignManager_Success() {
      when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
      when(employerRepository.findById(1L)).thenReturn(Optional.of(testManager));
      when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

      Employee result = employeeService.assignManager(1L, 1L);

      assertEquals(testManager, result.getManager());
      verify(employeeRepository).save(testEmployee);
    }

    @Test
    @DisplayName("Should throw exception when employee not found")
    void assignManager_EmployeeNotFound_ThrowsException() {
      when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

      assertThrows(EmployeeNotFoundException.class,
          () -> employeeService.assignManager(999L, 1L));
    }

    @Test
    @DisplayName("Should throw exception when manager not found")
    void assignManager_ManagerNotFound_ThrowsException() {
      when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
      when(employerRepository.findById(999L)).thenReturn(Optional.empty());

      assertThrows(RuntimeException.class,
          () -> employeeService.assignManager(1L, 999L));
    }
  }

  @Nested
  @DisplayName("Remove Manager Tests")
  class RemoveManagerTests {

    @Test
    @DisplayName("Should remove manager from employee")
    void removeManager_Success() {
      testEmployee.setManager(testManager);

      when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
      when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

      Employee result = employeeService.removeManager(1L);

      assertNull(result.getManager());
      verify(employeeRepository).save(testEmployee);
    }

    @Test
    @DisplayName("Should throw exception when employee not found")
    void removeManager_EmployeeNotFound_ThrowsException() {
      when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

      assertThrows(EmployeeNotFoundException.class,
          () -> employeeService.removeManager(999L));
    }
  }

  @Nested
  @DisplayName("Update Salary Tests")
  class UpdateSalaryTests {

    @Test
    @DisplayName("Should update salary successfully")
    void updateSalary_Success() {
      when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
      when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

      Employee result = employeeService.updateSalary(1L, 75000.0);

      assertEquals(75000.0, result.getSalary());
      verify(employeeRepository).save(testEmployee);
    }

    @Test
    @DisplayName("Should throw exception for negative salary")
    void updateSalary_NegativeSalary_ThrowsException() {
      assertThrows(IllegalArgumentException.class,
          () -> employeeService.updateSalary(1L, -1000.0));

      verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw exception when employee not found")
    void updateSalary_EmployeeNotFound_ThrowsException() {
      when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

      assertThrows(EmployeeNotFoundException.class,
          () -> employeeService.updateSalary(999L, 75000.0));
    }
  }

  @Nested
  @DisplayName("Give Bonus Tests")
  class GiveBonusTests {

    @Test
    @DisplayName("Should add bonus to salary")
    void giveBonus_Success() {
      Double originalSalary = testEmployee.getSalary();

      when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
      when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

      Employee result = employeeService.giveBonus(1L, 5000.0);

      assertEquals(originalSalary + 5000.0, result.getSalary());
      verify(employeeRepository).save(testEmployee);
    }

    @Test
    @DisplayName("Should throw exception for negative bonus")
    void giveBonus_NegativeBonus_ThrowsException() {
      assertThrows(IllegalArgumentException.class,
          () -> employeeService.giveBonus(1L, -500.0));

      verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw exception when employee not found")
    void giveBonus_EmployeeNotFound_ThrowsException() {
      when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

      assertThrows(EmployeeNotFoundException.class,
          () -> employeeService.giveBonus(999L, 5000.0));
    }
  }

  @Nested
  @DisplayName("Update Position Tests")
  class UpdatePositionTests {

    @Test
    @DisplayName("Should update position successfully")
    void updatePosition_Success() {
      when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
      when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

      Employee result = employeeService.updatePosition(1L, "Senior Developer");

      assertEquals("Senior Developer", result.getPosition());
      verify(employeeRepository).save(testEmployee);
    }

    @Test
    @DisplayName("Should throw exception when employee not found")
    void updatePosition_EmployeeNotFound_ThrowsException() {
      when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

      assertThrows(EmployeeNotFoundException.class,
          () -> employeeService.updatePosition(999L, "Senior Developer"));
    }
  }

  @Nested
  @DisplayName("Update Status Tests")
  class UpdateStatusTests {

    @Test
    @DisplayName("Should update status successfully")
    void updateStatus_Success() {
      when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
      when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

      Employee result = employeeService.updateStatus(1L, "Inactive");

      assertEquals(PersonStatus.Inactive, result.getStatus());
      verify(employeeRepository).save(testEmployee);
    }

    @Test
    @DisplayName("Should throw exception for invalid status")
    void updateStatus_InvalidStatus_ThrowsException() {
      when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

      assertThrows(IllegalArgumentException.class,
          () -> employeeService.updateStatus(1L, "INVALID_STATUS"));
    }

    @Test
    @DisplayName("Should throw exception when employee not found")
    void updateStatus_EmployeeNotFound_ThrowsException() {
      when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

      assertThrows(EmployeeNotFoundException.class,
          () -> employeeService.updateStatus(999L, "Inactive"));
    }
  }

  @Nested
  @DisplayName("Search By Name Tests")
  class SearchByNameTests {

    @Test
    @DisplayName("Should return matching employees")
    void searchByName_Found_ReturnsMatches() {
      List<Employee> employees = Arrays.asList(testEmployee);

      when(employeeRepository.findByNameContainingIgnoreCase("John")).thenReturn(employees);

      List<Employee> result = employeeService.searchByName("John");

      assertEquals(1, result.size());
      assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    @DisplayName("Should return empty list when no matches")
    void searchByName_NotFound_ReturnsEmpty() {
      when(employeeRepository.findByNameContainingIgnoreCase("XYZ"))
          .thenReturn(Collections.emptyList());

      List<Employee> result = employeeService.searchByName("XYZ");

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("Get Employees By Position Tests")
  class GetEmployeesByPositionTests {

    @Test
    @DisplayName("Should return employees with matching position")
    void getEmployeesByPosition_Found_ReturnsMatches() {
      List<Employee> employees = Arrays.asList(testEmployee);

      when(employeeRepository.findByPosition("Developer")).thenReturn(employees);

      List<Employee> result = employeeService.getEmployeesByPosition("Developer");

      assertEquals(1, result.size());
      assertEquals("Developer", result.get(0).getPosition());
    }
  }

  @Nested
  @DisplayName("Get Employees Hired After Tests")
  class GetEmployeesHiredAfterTests {

    @Test
    @DisplayName("Should return employees hired after date")
    void getEmployeesHiredAfter_Found_ReturnsMatches() {
      testEmployee.setHireDate(LocalDate.of(2024, 6, 1));
      List<Employee> employees = Arrays.asList(testEmployee);

      when(employeeRepository.findByHireDateAfter(LocalDate.of(2024, 1, 1)))
          .thenReturn(employees);

      List<Employee> result = employeeService.getEmployeesHiredAfter("2024-01-01");

      assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should parse date string correctly")
    void getEmployeesHiredAfter_ParsesDateCorrectly() {
      when(employeeRepository.findByHireDateAfter(LocalDate.of(2024, 1, 15)))
          .thenReturn(Collections.emptyList());

      employeeService.getEmployeesHiredAfter("2024-01-15");

      verify(employeeRepository).findByHireDateAfter(LocalDate.of(2024, 1, 15));
    }
  }
}