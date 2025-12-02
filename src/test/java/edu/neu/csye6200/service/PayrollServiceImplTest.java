package edu.neu.csye6200.service;

import edu.neu.csye6200.dto.request.DistributeBonusRequest;
import edu.neu.csye6200.dto.response.PaycheckDTO;
import edu.neu.csye6200.dto.response.PayrollSummaryDTO;
import edu.neu.csye6200.exception.BusinessValidationException;
import edu.neu.csye6200.exception.PayrollCalculationException;
import edu.neu.csye6200.exception.ResourceNotFoundException;
import edu.neu.csye6200.factory.DTOFactory;
import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.model.payroll.Paycheck;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.repository.PaycheckRepository;
import edu.neu.csye6200.service.impl.PayrollServiceImpl;
import edu.neu.csye6200.service.interfaces.EmployeeService;
import edu.neu.csye6200.strategy.tax.FlatTaxStrategy;
import edu.neu.csye6200.strategy.tax.ProgressiveTaxStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PayrollServiceImpl
 * 
 * @author Qing Mi
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PayrollService Tests")
class PayrollServiceImplTest {
    
    @Mock
    private EmployeeService employeeService;
    
    @Mock
    private PaycheckRepository paycheckRepository;
    
    @Mock
    private BusinessRepository businessRepository;
    
    private DTOFactory dtoFactory;
    private PayrollServiceImpl payrollService;
    
    private Employee testEmployee;
    private Company testCompany;
    private FlatTaxStrategy flatTaxStrategy;
    
    @BeforeEach
    void setUp() {
        // Create test employee
        testEmployee = new Employee("John Doe", "john@example.com", "password123", 50000.0, "Developer");
        testEmployee.setId(1L);
        
        // Create test company
        testCompany = new Company("Test Corp", "123 Main St");
        testCompany.setId(1L);
        testCompany.setIndustry("Technology");
        testEmployee.setCompany(testCompany);
        
        // Create flat tax strategy (15% tax rate)
        flatTaxStrategy = new FlatTaxStrategy(0.15);
        
        // Create DTOFactory instance (stateless factory, no need to mock)
        dtoFactory = new DTOFactory();
        
        // Create PayrollServiceImpl with constructor injection (mocking dependencies)
        // Use default insurance rate of 0.05 (5%) for tests
        double defaultInsuranceRate = 0.05;
        payrollService = new PayrollServiceImpl(
            employeeService,
            paycheckRepository,
            businessRepository,
            dtoFactory,
            flatTaxStrategy,
            defaultInsuranceRate
        );
    }
    
    // ==================== calculatePayroll Tests ====================
    
    @Test
    @DisplayName("Should calculate payroll successfully for employee (as per UML sequence)")
    void testCalculatePayroll_Success() {
        // Given - Following UML sequence diagram flow
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        Paycheck savedPaycheck = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, LocalDate.now());
        savedPaycheck.setId(100L);
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(savedPaycheck);
        
        // When - Step 1: Controller calls service
        PaycheckDTO result = payrollService.calculatePayroll(1L);
        
        // Then - Verify UML sequence diagram steps
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(1L, result.employeeId());
        assertEquals("John Doe", result.employeeName());
        assertEquals(50000.0, result.grossPay(), 0.01); // Base salary
        assertNull(result.bonus()); // No bonus for regular payroll
        assertEquals(7500.0, result.taxDeduction(), 0.01); // 15% of 50000
        assertEquals(2500.0, result.insuranceDeduction(), 0.01); // 5% of 50000
        assertEquals(10000.0, result.totalDeductions(), 0.01); // 7500 + 2500
        assertEquals(40000.0, result.netPay(), 0.01); // 50000 - 7500 - 2500
        assertNotNull(result.payDate());
        assertEquals("Flat Tax Strategy", result.taxStrategyUsed());
        
        // Verify Step 2: EmployeeService.getEmployee was called
        verify(employeeService, times(1)).getEmployee(1L);
        
        // Verify Step 3: PaycheckRepository.save was called
        ArgumentCaptor<Paycheck> paycheckCaptor = ArgumentCaptor.forClass(Paycheck.class);
        verify(paycheckRepository, times(1)).save(paycheckCaptor.capture());
        
        Paycheck capturedPaycheck = paycheckCaptor.getValue();
        assertEquals(1L, capturedPaycheck.getEmployeeId());
        assertEquals(50000.0, capturedPaycheck.getGrossPay(), 0.01);
        assertEquals(7500.0, capturedPaycheck.getTaxDeduction(), 0.01); // 15% of 50000
        assertEquals(2500.0, capturedPaycheck.getInsuranceDeduction(), 0.01); // 5% of 50000
        assertEquals(40000.0, capturedPaycheck.getNetPay(), 0.01); // 50000 - 7500 - 2500
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee not found")
    void testCalculatePayroll_EmployeeNotFound() {
        // Given
        when(employeeService.getEmployee(999L)).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> payrollService.calculatePayroll(999L));
        
        assertTrue(exception.getMessage().contains("Employee"));
        assertTrue(exception.getMessage().contains("999"));
        verify(employeeService, times(1)).getEmployee(999L);
        verify(paycheckRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw PayrollCalculationException when employee has invalid salary")
    void testCalculatePayroll_InvalidSalary() {
        // Given
        testEmployee.setSalary(null);
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        // When & Then
        PayrollCalculationException exception = assertThrows(PayrollCalculationException.class,
            () -> payrollService.calculatePayroll(1L));
        
        assertTrue(exception.getMessage().contains("invalid salary"));
        verify(employeeService, times(1)).getEmployee(1L);
        verify(paycheckRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw PayrollCalculationException when employee has negative salary")
    void testCalculatePayroll_NegativeSalary() {
        // Given
        testEmployee.setSalary(-1000.0);
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        // When & Then
        PayrollCalculationException exception = assertThrows(PayrollCalculationException.class,
            () -> payrollService.calculatePayroll(1L));
        
        assertTrue(exception.getMessage().contains("invalid salary"));
        verify(employeeService, times(1)).getEmployee(1L);
        verify(paycheckRepository, never()).save(any());
    }
    
    // ==================== calculatePayrollWithAdditionalPay Tests ====================
    
    @Test
    @DisplayName("Should calculate payroll with additional pay successfully")
    void testCalculatePayrollWithAdditionalPay_Success() {
        // Given
        Double additionalPay = 5000.0;
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        // Mock will be created with base salary and bonus separately
        Paycheck savedPaycheck = new Paycheck(testEmployee, 50000.0, 8250.0, 2750.0, LocalDate.now());
        savedPaycheck.setId(100L);
        savedPaycheck.setBonus(5000.0); // Bonus stored separately
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(savedPaycheck);
        
        // When
        PaycheckDTO result = payrollService.calculatePayrollWithAdditionalPay(1L, additionalPay);
        
        // Then
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(1L, result.employeeId());
        assertEquals("John Doe", result.employeeName());
        assertEquals(50000.0, result.grossPay(), 0.01); // Base salary
        assertEquals(5000.0, result.bonus(), 0.01); // Bonus amount
        assertEquals(8250.0, result.taxDeduction(), 0.01); // 15% of (50000 + 5000)
        assertEquals(2750.0, result.insuranceDeduction(), 0.01); // 5% of (50000 + 5000)
        assertEquals(11000.0, result.totalDeductions(), 0.01); // 8250 + 2750
        assertEquals(44000.0, result.netPay(), 0.01); // (50000 + 5000) - 8250 - 2750
        assertNotNull(result.payDate());
        assertEquals("Flat Tax Strategy", result.taxStrategyUsed());
        
        verify(employeeService, times(1)).getEmployee(1L);
        
        ArgumentCaptor<Paycheck> paycheckCaptor = ArgumentCaptor.forClass(Paycheck.class);
        verify(paycheckRepository, times(1)).save(paycheckCaptor.capture());
        
        Paycheck capturedPaycheck = paycheckCaptor.getValue();
        assertEquals(50000.0, capturedPaycheck.getGrossPay(), 0.01); // Base salary
        assertEquals(5000.0, capturedPaycheck.getBonus(), 0.01); // Bonus stored separately
        assertEquals(8250.0, capturedPaycheck.getTaxDeduction(), 0.01); // Tax on total
        assertEquals(2750.0, capturedPaycheck.getInsuranceDeduction(), 0.01); // Insurance on total
        assertEquals(44000.0, capturedPaycheck.getNetPay(), 0.01); // Net pay
    }
    
    @Test
    @DisplayName("Should handle null additional pay as zero")
    void testCalculatePayrollWithAdditionalPay_NullAdditionalPay() {
        // Given
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        Paycheck savedPaycheck = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, LocalDate.now());
        savedPaycheck.setId(100L);
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(savedPaycheck);
        
        // When
        PaycheckDTO result = payrollService.calculatePayrollWithAdditionalPay(1L, null);
        
        // Then
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(1L, result.employeeId());
        assertEquals("John Doe", result.employeeName());
        assertEquals(50000.0, result.grossPay(), 0.01); // Base salary
        assertNull(result.bonus()); // No bonus when additionalPay is null
        assertEquals(7500.0, result.taxDeduction(), 0.01);
        assertEquals(2500.0, result.insuranceDeduction(), 0.01);
        assertEquals(10000.0, result.totalDeductions(), 0.01);
        assertEquals(40000.0, result.netPay(), 0.01);
        assertNotNull(result.payDate());
        assertEquals("Flat Tax Strategy", result.taxStrategyUsed());
        
        ArgumentCaptor<Paycheck> paycheckCaptor = ArgumentCaptor.forClass(Paycheck.class);
        verify(paycheckRepository).save(paycheckCaptor.capture());
        assertEquals(50000.0, paycheckCaptor.getValue().getGrossPay(), 0.01);
        assertNull(paycheckCaptor.getValue().getBonus()); // No bonus
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when employeeId is null")
    void testCalculatePayrollWithAdditionalPay_NullEmployeeId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> payrollService.calculatePayrollWithAdditionalPay(null, 1000.0));
        
        assertEquals("Employee ID cannot be null", exception.getMessage());
        verify(employeeService, never()).getEmployee(any());
        verify(paycheckRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when additional pay is negative")
    void testCalculatePayrollWithAdditionalPay_NegativeAdditionalPay() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> payrollService.calculatePayrollWithAdditionalPay(1L, -1000.0));
        
        assertEquals("Additional pay cannot be negative", exception.getMessage());
        verify(employeeService, never()).getEmployee(any());
        verify(paycheckRepository, never()).save(any());
    }
    
    // ==================== distributeBonuses Tests ====================
    
    @Test
    @DisplayName("Should distribute bonuses to all employees in business successfully")
    void testDistributeBonuses_AllEmployees_Success() {
        // Given
        Employee employee2 = new Employee("Jane Smith", "jane@example.com", "password123", 60000.0, "Manager");
        employee2.setId(2L);
        employee2.setCompany(testCompany);
        
        List<Employee> employees = Arrays.asList(testEmployee, employee2);
        
        DistributeBonusRequest request = new DistributeBonusRequest(
            1L, 1000.0, null, null, "Q4 Bonus"
        );
        
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(employeeService.getEmployeesByBusiness(1L)).thenReturn(employees);
        
        // Mock paychecks: base salary stored in grossPay, bonus stored separately
        Paycheck paycheck1 = new Paycheck(testEmployee, 50000.0, 7650.0, 2550.0, LocalDate.now());
        paycheck1.setId(100L);
        paycheck1.setBonus(1000.0); // Bonus stored separately
        Paycheck paycheck2 = new Paycheck(employee2, 60000.0, 9150.0, 3050.0, LocalDate.now());
        paycheck2.setId(101L);
        paycheck2.setBonus(1000.0); // Bonus stored separately
        
        when(paycheckRepository.save(any(Paycheck.class)))
            .thenReturn(paycheck1)
            .thenReturn(paycheck2);
        
        // When
        List<PaycheckDTO> result = payrollService.distributeBonuses(request);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        PaycheckDTO dto1 = result.get(0);
        assertEquals(100L, dto1.id());
        assertEquals(1L, dto1.employeeId());
        assertEquals("John Doe", dto1.employeeName());
        assertEquals(50000.0, dto1.grossPay(), 0.01); // Base salary
        assertEquals(1000.0, dto1.bonus(), 0.01); // Bonus amount
        assertEquals(7650.0, dto1.taxDeduction(), 0.01); // Tax on (50000 + 1000)
        assertEquals(2550.0, dto1.insuranceDeduction(), 0.01); // Insurance on (50000 + 1000)
        assertEquals(10200.0, dto1.totalDeductions(), 0.01);
        assertEquals(40800.0, dto1.netPay(), 0.01); // (50000 + 1000) - 7650 - 2550
        assertNotNull(dto1.payDate());
        assertEquals("Flat Tax Strategy", dto1.taxStrategyUsed());
        
        PaycheckDTO dto2 = result.get(1);
        assertEquals(101L, dto2.id());
        assertEquals(2L, dto2.employeeId());
        assertEquals("Jane Smith", dto2.employeeName());
        assertEquals(60000.0, dto2.grossPay(), 0.01); // Base salary
        assertEquals(1000.0, dto2.bonus(), 0.01); // Bonus amount
        assertEquals(9150.0, dto2.taxDeduction(), 0.01); // Tax on (60000 + 1000)
        assertEquals(3050.0, dto2.insuranceDeduction(), 0.01); // Insurance on (60000 + 1000)
        assertEquals(12200.0, dto2.totalDeductions(), 0.01);
        assertEquals(48800.0, dto2.netPay(), 0.01); // (60000 + 1000) - 9150 - 3050
        assertNotNull(dto2.payDate());
        assertEquals("Flat Tax Strategy", dto2.taxStrategyUsed());
        
        verify(businessRepository, times(1)).findById(1L);
        verify(employeeService, times(1)).getEmployeesByBusiness(1L);
        verify(paycheckRepository, times(2)).save(any(Paycheck.class));
    }
    
    @Test
    @DisplayName("Should distribute bonuses to specific employees by IDs")
    void testDistributeBonuses_SpecificEmployees_Success() {
        // Given
        List<Long> employeeIds = Arrays.asList(1L, 2L);
        Employee employee2 = new Employee("Jane Smith", "jane@example.com", "password123", 60000.0, "Manager");
        employee2.setId(2L);
        
        DistributeBonusRequest request = new DistributeBonusRequest(
            1L, 1000.0, employeeIds, null, "Performance Bonus"
        );
        
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeService.getEmployee(2L)).thenReturn(Optional.of(employee2));
        employee2.setCompany(testCompany);
        
        // Mock paychecks: base salary stored in grossPay, bonus stored separately
        Paycheck paycheck1 = new Paycheck(testEmployee, 50000.0, 7650.0, 2550.0, LocalDate.now());
        paycheck1.setId(100L);
        paycheck1.setBonus(1000.0); // Bonus stored separately
        Paycheck paycheck2 = new Paycheck(employee2, 60000.0, 9150.0, 3050.0, LocalDate.now());
        paycheck2.setId(101L);
        paycheck2.setBonus(1000.0); // Bonus stored separately
        
        when(paycheckRepository.save(any(Paycheck.class)))
            .thenReturn(paycheck1)
            .thenReturn(paycheck2);
        
        // When
        List<PaycheckDTO> result = payrollService.distributeBonuses(request);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        PaycheckDTO dto1 = result.get(0);
        assertEquals(100L, dto1.id());
        assertEquals(1L, dto1.employeeId());
        assertEquals("John Doe", dto1.employeeName());
        assertEquals(50000.0, dto1.grossPay(), 0.01); // Base salary
        assertEquals(1000.0, dto1.bonus(), 0.01); // Bonus amount
        assertEquals("Flat Tax Strategy", dto1.taxStrategyUsed());
        
        PaycheckDTO dto2 = result.get(1);
        assertEquals(101L, dto2.id());
        assertEquals(2L, dto2.employeeId());
        assertEquals("Jane Smith", dto2.employeeName());
        assertEquals(60000.0, dto2.grossPay(), 0.01); // Base salary
        assertEquals(1000.0, dto2.bonus(), 0.01); // Bonus amount
        assertEquals("Flat Tax Strategy", dto2.taxStrategyUsed());
        
        verify(employeeService, times(1)).getEmployee(1L);
        verify(employeeService, times(1)).getEmployee(2L);
        verify(paycheckRepository, times(2)).save(any(Paycheck.class));
    }
    
    @Test
    @DisplayName("Should filter employees by department when distributing bonuses")
    void testDistributeBonuses_ByDepartment_Success() {
        // Given
        Employer manager = new Employer("Manager", "manager@example.com", "password", 80000.0, "Engineering", "Manager");
        manager.setId(10L);
        
        Employee employee2 = new Employee("Jane Smith", "jane@example.com", "password123", 60000.0, "Developer");
        employee2.setId(2L);
        employee2.setManager(manager);
        employee2.setCompany(testCompany);
        
        testEmployee.setManager(manager);
        
        List<Employee> allEmployees = Arrays.asList(testEmployee, employee2);
        
        DistributeBonusRequest request = new DistributeBonusRequest(
            1L, 1000.0, null, "Engineering", "Department Bonus"
        );
        
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(employeeService.getEmployeesByBusiness(1L)).thenReturn(allEmployees);
        
        // Mock paychecks: base salary stored in grossPay, bonus stored separately
        Paycheck paycheck1 = new Paycheck(testEmployee, 50000.0, 7650.0, 2550.0, LocalDate.now());
        paycheck1.setId(100L);
        paycheck1.setBonus(1000.0); // Bonus stored separately
        Paycheck paycheck2 = new Paycheck(employee2, 60000.0, 9150.0, 3050.0, LocalDate.now());
        paycheck2.setId(101L);
        paycheck2.setBonus(1000.0); // Bonus stored separately
        
        when(paycheckRepository.save(any(Paycheck.class)))
            .thenReturn(paycheck1)
            .thenReturn(paycheck2);
        
        // When
        List<PaycheckDTO> result = payrollService.distributeBonuses(request);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        PaycheckDTO dto1 = result.get(0);
        assertEquals(100L, dto1.id());
        assertEquals(1L, dto1.employeeId());
        assertEquals("John Doe", dto1.employeeName());
        assertEquals(50000.0, dto1.grossPay(), 0.01); // Base salary
        assertEquals(1000.0, dto1.bonus(), 0.01); // Bonus amount
        assertEquals("Flat Tax Strategy", dto1.taxStrategyUsed());
        
        PaycheckDTO dto2 = result.get(1);
        assertEquals(101L, dto2.id());
        assertEquals(2L, dto2.employeeId());
        assertEquals("Jane Smith", dto2.employeeName());
        assertEquals(60000.0, dto2.grossPay(), 0.01); // Base salary
        assertEquals(1000.0, dto2.bonus(), 0.01); // Bonus amount
        assertEquals("Flat Tax Strategy", dto2.taxStrategyUsed());
        
        verify(employeeService, times(1)).getEmployeesByBusiness(1L);
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when business not found")
    void testDistributeBonuses_BusinessNotFound() {
        // Given
        DistributeBonusRequest request = new DistributeBonusRequest(
            999L, 1000.0, null, null, "Bonus"
        );
        
        when(businessRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> payrollService.distributeBonuses(request));
        
        assertTrue(exception.getMessage().contains("Business"));
        assertTrue(exception.getMessage().contains("999"));
        verify(businessRepository, times(1)).findById(999L);
        verify(paycheckRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw BusinessValidationException when no employees match criteria")
    void testDistributeBonuses_NoEmployeesFound() {
        // Given
        DistributeBonusRequest request = new DistributeBonusRequest(
            1L, 1000.0, null, null, "Bonus"
        );
        
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(employeeService.getEmployeesByBusiness(1L)).thenReturn(List.of());
        
        // When & Then
        BusinessValidationException exception = assertThrows(BusinessValidationException.class,
            () -> payrollService.distributeBonuses(request));
        
        assertTrue(exception.getMessage().contains("No employees found"));
        verify(businessRepository, times(1)).findById(1L);
        verify(employeeService, times(1)).getEmployeesByBusiness(1L);
        verify(paycheckRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw PayrollCalculationException when all employees fail processing")
    void testDistributeBonuses_AllFail() {
        // Given
        testEmployee.setSalary(null); // Invalid salary will cause failure
        
        DistributeBonusRequest request = new DistributeBonusRequest(
            1L, 1000.0, null, null, "Bonus"
        );
        
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(employeeService.getEmployeesByBusiness(1L)).thenReturn(List.of(testEmployee));
        
        // When & Then
        PayrollCalculationException exception = assertThrows(PayrollCalculationException.class,
            () -> payrollService.distributeBonuses(request));
        
        assertTrue(exception.getMessage().contains("Failed to distribute bonuses"));
        verify(paycheckRepository, never()).save(any());
    }
    
    // ==================== Tax Strategy Tests ====================
    
    @Test
    @DisplayName("Should get current tax strategy name")
    void testGetCurrentTaxStrategyName() {
        // When
        String strategyName = payrollService.getCurrentTaxStrategyName();
        
        // Then
        assertNotNull(strategyName);
        assertEquals("Flat Tax Strategy", strategyName);
    }
    
    @Test
    @DisplayName("Should set tax strategy successfully")
    void testSetTaxStrategy_Success() {
        // Given
        Map<Double, Double> brackets = new HashMap<>();
        brackets.put(10000.0, 0.10);
        brackets.put(50000.0, 0.20);
        ProgressiveTaxStrategy newStrategy = new ProgressiveTaxStrategy(brackets);
        
        // When
        payrollService.setTaxStrategy(newStrategy);
        
        // Then
        String strategyName = payrollService.getCurrentTaxStrategyName();
        assertNotNull(strategyName);
        assertEquals("Progressive Tax Strategy", strategyName);
    }
    
    @Test
    @DisplayName("Should use updated tax strategy in PaycheckDTO when strategy changes")
    void testCalculatePayroll_WithUpdatedTaxStrategy() {
        // Given - Change to Progressive Tax Strategy
        Map<Double, Double> brackets = new HashMap<>();
        brackets.put(10000.0, 0.10);
        brackets.put(50000.0, 0.20);
        ProgressiveTaxStrategy progressiveStrategy = new ProgressiveTaxStrategy(brackets);
        payrollService.setTaxStrategy(progressiveStrategy);
        
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        Paycheck savedPaycheck = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, LocalDate.now());
        savedPaycheck.setId(100L);
        savedPaycheck.setBonus(null); // No bonus for regular payroll
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(savedPaycheck);
        
        // When
        PaycheckDTO result = payrollService.calculatePayroll(1L);
        
        // Then - Verify DTO contains updated tax strategy name
        assertNotNull(result);
        assertEquals("Progressive Tax Strategy", result.taxStrategyUsed());
        assertEquals(100L, result.id());
        assertEquals(1L, result.employeeId());
        assertEquals("John Doe", result.employeeName());
        assertNull(result.bonus()); // No bonus for regular payroll
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when setting null tax strategy")
    void testSetTaxStrategy_NullStrategy() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> payrollService.setTaxStrategy(null));
        
        assertEquals("Tax strategy cannot be null", exception.getMessage());
    }
    
    // ==================== ProgressiveTaxStrategy Tests ====================
    
    @Test
    @DisplayName("Should calculate payroll with ProgressiveTaxStrategy for low income")
    void testCalculatePayroll_ProgressiveTaxStrategy_LowIncome() {
        // Given - Set up progressive tax brackets: 0-10k: 10%, 10k-50k: 20%, above 50k: 30%
        Map<Double, Double> brackets = new HashMap<>();
        brackets.put(10000.0, 0.10);
        brackets.put(50000.0, 0.20);
        ProgressiveTaxStrategy progressiveStrategy = new ProgressiveTaxStrategy(brackets);
        payrollService.setTaxStrategy(progressiveStrategy);
        
        // Employee with $30,000 salary
        Employee lowIncomeEmployee = new Employee("Jane Low", "jane@example.com", "password", 30000.0, "Developer");
        lowIncomeEmployee.setId(2L);
        
        when(employeeService.getEmployee(2L)).thenReturn(Optional.of(lowIncomeEmployee));
        
        // Expected tax: $10,000 * 0.10 + $20,000 * 0.20 = $1,000 + $4,000 = $5,000
        // Expected insurance: $30,000 * 0.05 = $1,500
        // Expected net: $30,000 - $5,000 - $1,500 = $23,500
        Paycheck savedPaycheck = new Paycheck(lowIncomeEmployee, 30000.0, 5000.0, 1500.0, LocalDate.now());
        savedPaycheck.setId(200L);
        savedPaycheck.setBonus(null);
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(savedPaycheck);
        
        // When
        PaycheckDTO result = payrollService.calculatePayroll(2L);
        
        // Then
        assertNotNull(result);
        assertEquals("Progressive Tax Strategy", result.taxStrategyUsed());
        assertEquals(30000.0, result.grossPay(), 0.01);
        assertNull(result.bonus());
        assertEquals(5000.0, result.taxDeduction(), 0.01);
        assertEquals(1500.0, result.insuranceDeduction(), 0.01);
        assertEquals(6500.0, result.totalDeductions(), 0.01);
        assertEquals(23500.0, result.netPay(), 0.01);
        
        // Verify tax calculation was done correctly
        ArgumentCaptor<Paycheck> captor = ArgumentCaptor.forClass(Paycheck.class);
        verify(paycheckRepository).save(captor.capture());
        assertEquals(5000.0, captor.getValue().getTaxDeduction(), 0.01);
    }
    
    @Test
    @DisplayName("Should calculate payroll with ProgressiveTaxStrategy for high income")
    void testCalculatePayroll_ProgressiveTaxStrategy_HighIncome() {
        // Given - Set up progressive tax brackets: 0-10k: 10%, 10k-50k: 20%, above 50k: 30%
        Map<Double, Double> brackets = new HashMap<>();
        brackets.put(10000.0, 0.10);
        brackets.put(50000.0, 0.20);
        ProgressiveTaxStrategy progressiveStrategy = new ProgressiveTaxStrategy(brackets);
        payrollService.setTaxStrategy(progressiveStrategy);
        
        // Employee with $70,000 salary
        Employee highIncomeEmployee = new Employee("John High", "john@example.com", "password", 70000.0, "Manager");
        highIncomeEmployee.setId(3L);
        
        when(employeeService.getEmployee(3L)).thenReturn(Optional.of(highIncomeEmployee));
        
        // Expected tax: $10,000 * 0.10 + $40,000 * 0.20 + $20,000 * 0.20 = $1,000 + $8,000 + $4,000 = $13,000
        // (Above $50k uses highest rate 0.20, not 0.30)
        // Expected insurance: $70,000 * 0.05 = $3,500
        // Expected net: $70,000 - $13,000 - $3,500 = $53,500
        Paycheck savedPaycheck = new Paycheck(highIncomeEmployee, 70000.0, 13000.0, 3500.0, LocalDate.now());
        savedPaycheck.setId(300L);
        savedPaycheck.setBonus(null);
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(savedPaycheck);
        
        // When
        PaycheckDTO result = payrollService.calculatePayroll(3L);
        
        // Then
        assertNotNull(result);
        assertEquals("Progressive Tax Strategy", result.taxStrategyUsed());
        assertEquals(70000.0, result.grossPay(), 0.01);
        assertNull(result.bonus());
        assertEquals(13000.0, result.taxDeduction(), 0.01);
        assertEquals(3500.0, result.insuranceDeduction(), 0.01);
        assertEquals(16500.0, result.totalDeductions(), 0.01);
        assertEquals(53500.0, result.netPay(), 0.01);
        
        // Verify tax calculation was done correctly
        ArgumentCaptor<Paycheck> captor = ArgumentCaptor.forClass(Paycheck.class);
        verify(paycheckRepository).save(captor.capture());
        assertEquals(13000.0, captor.getValue().getTaxDeduction(), 0.01);
    }
    
    @Test
    @DisplayName("Should calculate payroll with bonus using ProgressiveTaxStrategy")
    void testCalculatePayrollWithBonus_ProgressiveTaxStrategy() {
        // Given - Set up progressive tax brackets: 0-10k: 10%, 10k-50k: 20%, above 50k: 30%
        Map<Double, Double> brackets = new HashMap<>();
        brackets.put(10000.0, 0.10);
        brackets.put(50000.0, 0.20);
        ProgressiveTaxStrategy progressiveStrategy = new ProgressiveTaxStrategy(brackets);
        payrollService.setTaxStrategy(progressiveStrategy);
        
        Double bonusAmount = 5000.0;
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        // Total gross: $50,000 + $5,000 = $55,000
        // Expected tax: $10,000 * 0.10 + $40,000 * 0.20 + $5,000 * 0.20 = $1,000 + $8,000 + $1,000 = $10,000
        // (Above $50k uses highest rate 0.20)
        // Expected insurance: $55,000 * 0.05 = $2,750
        // Expected net: $55,000 - $10,000 - $2,750 = $42,250
        Paycheck savedPaycheck = new Paycheck(testEmployee, 50000.0, 10000.0, 2750.0, LocalDate.now());
        savedPaycheck.setId(100L);
        savedPaycheck.setBonus(bonusAmount);
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(savedPaycheck);
        
        // When
        PaycheckDTO result = payrollService.calculatePayrollWithAdditionalPay(1L, bonusAmount);
        
        // Then
        assertNotNull(result);
        assertEquals("Progressive Tax Strategy", result.taxStrategyUsed());
        assertEquals(50000.0, result.grossPay(), 0.01); // Base salary
        assertEquals(5000.0, result.bonus(), 0.01); // Bonus
        assertEquals(10000.0, result.taxDeduction(), 0.01); // Tax on total $55,000
        assertEquals(2750.0, result.insuranceDeduction(), 0.01); // Insurance on total $55,000
        assertEquals(12750.0, result.totalDeductions(), 0.01);
        assertEquals(42250.0, result.netPay(), 0.01);
        
        // Verify bonus was stored separately
        ArgumentCaptor<Paycheck> captor = ArgumentCaptor.forClass(Paycheck.class);
        verify(paycheckRepository).save(captor.capture());
        assertEquals(50000.0, captor.getValue().getGrossPay(), 0.01);
        assertEquals(5000.0, captor.getValue().getBonus(), 0.01);
        assertEquals(10000.0, captor.getValue().getTaxDeduction(), 0.01);
    }
    
    @Test
    @DisplayName("Should distribute bonuses using ProgressiveTaxStrategy")
    void testDistributeBonuses_ProgressiveTaxStrategy() {
        // Given - Set up progressive tax brackets: 0-10k: 10%, 10k-50k: 20%, above 50k: 30%
        Map<Double, Double> brackets = new HashMap<>();
        brackets.put(10000.0, 0.10);
        brackets.put(50000.0, 0.20);
        ProgressiveTaxStrategy progressiveStrategy = new ProgressiveTaxStrategy(brackets);
        payrollService.setTaxStrategy(progressiveStrategy);
        
        Employee employee2 = new Employee("Jane Smith", "jane@example.com", "password123", 60000.0, "Manager");
        employee2.setId(2L);
        employee2.setCompany(testCompany);
        
        List<Employee> employees = Arrays.asList(testEmployee, employee2);
        
        DistributeBonusRequest request = new DistributeBonusRequest(
            1L, 2000.0, null, null, "Q4 Bonus"
        );
        
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(employeeService.getEmployeesByBusiness(1L)).thenReturn(employees);
        
        // Employee 1: $50,000 + $2,000 = $52,000 total
        // Tax: $10,000 * 0.10 + $40,000 * 0.20 + $2,000 * 0.20 = $1,000 + $8,000 + $400 = $9,400
        // (Above $50k uses highest rate 0.20)
        // Insurance: $52,000 * 0.05 = $2,600
        Paycheck paycheck1 = new Paycheck(testEmployee, 50000.0, 9400.0, 2600.0, LocalDate.now());
        paycheck1.setId(100L);
        paycheck1.setBonus(2000.0);
        
        // Employee 2: $60,000 + $2,000 = $62,000 total
        // Tax: $10,000 * 0.10 + $40,000 * 0.20 + $12,000 * 0.20 = $1,000 + $8,000 + $2,400 = $11,400
        // (Above $50k uses highest rate 0.20)
        // Insurance: $62,000 * 0.05 = $3,100
        Paycheck paycheck2 = new Paycheck(employee2, 60000.0, 11400.0, 3100.0, LocalDate.now());
        paycheck2.setId(101L);
        paycheck2.setBonus(2000.0);
        
        when(paycheckRepository.save(any(Paycheck.class)))
            .thenReturn(paycheck1)
            .thenReturn(paycheck2);
        
        // When
        List<PaycheckDTO> result = payrollService.distributeBonuses(request);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        PaycheckDTO dto1 = result.get(0);
        assertEquals("Progressive Tax Strategy", dto1.taxStrategyUsed());
        assertEquals(50000.0, dto1.grossPay(), 0.01);
        assertEquals(2000.0, dto1.bonus(), 0.01);
        assertEquals(9400.0, dto1.taxDeduction(), 0.01);
        assertEquals(2600.0, dto1.insuranceDeduction(), 0.01);
        assertEquals(12000.0, dto1.totalDeductions(), 0.01);
        assertEquals(40000.0, dto1.netPay(), 0.01); // $52,000 - $9,400 - $2,600
        
        PaycheckDTO dto2 = result.get(1);
        assertEquals("Progressive Tax Strategy", dto2.taxStrategyUsed());
        assertEquals(60000.0, dto2.grossPay(), 0.01);
        assertEquals(2000.0, dto2.bonus(), 0.01);
        assertEquals(11400.0, dto2.taxDeduction(), 0.01);
        assertEquals(3100.0, dto2.insuranceDeduction(), 0.01);
        assertEquals(14500.0, dto2.totalDeductions(), 0.01);
        assertEquals(47500.0, dto2.netPay(), 0.01); // $62,000 - $11,400 - $3,100
        
        verify(businessRepository, times(1)).findById(1L);
        verify(employeeService, times(1)).getEmployeesByBusiness(1L);
        verify(paycheckRepository, times(2)).save(any(Paycheck.class));
    }
    
    @Test
    @DisplayName("Should calculate tax correctly at bracket boundaries with ProgressiveTaxStrategy")
    void testCalculatePayroll_ProgressiveTaxStrategy_BracketBoundaries() {
        // Given - Set up progressive tax brackets: 0-10k: 10%, 10k-50k: 20%, above 50k: 30%
        Map<Double, Double> brackets = new HashMap<>();
        brackets.put(10000.0, 0.10);
        brackets.put(50000.0, 0.20);
        ProgressiveTaxStrategy progressiveStrategy = new ProgressiveTaxStrategy(brackets);
        payrollService.setTaxStrategy(progressiveStrategy);
        
        // Test exactly at first bracket boundary: $10,000
        Employee employeeAt10k = new Employee("At10k", "at10k@example.com", "password", 10000.0, "Developer");
        employeeAt10k.setId(4L);
        
        when(employeeService.getEmployee(4L)).thenReturn(Optional.of(employeeAt10k));
        
        // Expected tax: $10,000 * 0.10 = $1,000
        Paycheck savedPaycheck = new Paycheck(employeeAt10k, 10000.0, 1000.0, 500.0, LocalDate.now());
        savedPaycheck.setId(400L);
        savedPaycheck.setBonus(null);
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(savedPaycheck);
        
        // When
        PaycheckDTO result = payrollService.calculatePayroll(4L);
        
        // Then
        assertEquals(1000.0, result.taxDeduction(), 0.01);
        assertEquals(500.0, result.insuranceDeduction(), 0.01);
        assertEquals(8500.0, result.netPay(), 0.01); // $10,000 - $1,000 - $500
        
        // Test exactly at second bracket boundary: $50,000
        Employee employeeAt50k = new Employee("At50k", "at50k@example.com", "password", 50000.0, "Developer");
        employeeAt50k.setId(5L);
        
        when(employeeService.getEmployee(5L)).thenReturn(Optional.of(employeeAt50k));
        
        // Expected tax: $10,000 * 0.10 + $40,000 * 0.20 = $1,000 + $8,000 = $9,000
        Paycheck savedPaycheck2 = new Paycheck(employeeAt50k, 50000.0, 9000.0, 2500.0, LocalDate.now());
        savedPaycheck2.setId(500L);
        savedPaycheck2.setBonus(null);
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(savedPaycheck2);
        
        // When
        PaycheckDTO result2 = payrollService.calculatePayroll(5L);
        
        // Then
        assertEquals(9000.0, result2.taxDeduction(), 0.01);
        assertEquals(2500.0, result2.insuranceDeduction(), 0.01);
        assertEquals(38500.0, result2.netPay(), 0.01); // $50,000 - $9,000 - $2,500
    }
    
    // ==================== Integration Tests Following UML Sequence ====================
    
    @Test
    @DisplayName("Should follow UML sequence diagram: calculatePayroll flow")
    void testCalculatePayroll_UMLSequenceFlow() {
        // Given - Setup as per UML sequence diagram
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        Paycheck savedPaycheck = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, LocalDate.now());
        savedPaycheck.setId(100L);
        savedPaycheck.setBonus(null); // No bonus for regular payroll
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(savedPaycheck);
        
        // When - Step 1: PayrollController calls PayrollService (simulated)
        PaycheckDTO result = payrollService.calculatePayroll(1L);
        
        // Then - Verify UML sequence steps:
        // Step 2: PayrollService -> EmployeeService.getEmployee
        verify(employeeService, times(1)).getEmployee(1L);
        
        // Step 3: PayrollService calculates grossPay = employee.getSalary()
        ArgumentCaptor<Paycheck> captor = ArgumentCaptor.forClass(Paycheck.class);
        verify(paycheckRepository, times(1)).save(captor.capture());
        
        Paycheck captured = captor.getValue();
        assertEquals(testEmployee, captured.getEmployee()); // Verify employee relationship
        assertEquals(50000.0, captured.getGrossPay(), 0.01); // Base salary
        assertNull(captured.getBonus()); // No bonus for regular payroll
        
        // Step 4: Tax strategy was used (via calculateRegularPayroll)
        // Step 5: Insurance deduction calculated
        assertEquals(2500.0, captured.getInsuranceDeduction(), 0.01); // 5% of 50000
        
        // Step 6: Net pay calculated
        assertEquals(40000.0, captured.getNetPay(), 0.01);
        
        // Step 7: PaycheckRepository.save called
        // Step 8: PayrollService returns PaycheckDTO (as per UML sequence diagram)
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(1L, result.employeeId());
        assertEquals("John Doe", result.employeeName());
        assertEquals(50000.0, result.grossPay(), 0.01); // Base salary
        assertNull(result.bonus()); // No bonus for regular payroll
        assertEquals(7500.0, result.taxDeduction(), 0.01);
        assertEquals(2500.0, result.insuranceDeduction(), 0.01);
        assertEquals(10000.0, result.totalDeductions(), 0.01);
        assertEquals(40000.0, result.netPay(), 0.01);
        assertNotNull(result.payDate());
        assertEquals("Flat Tax Strategy", result.taxStrategyUsed());
    }
    
    // ==================== Payroll History Tests ====================
    
    @Test
    @DisplayName("Should get payroll history for employee")
    void testGetPayrollHistory_Success() {
        // Given
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        Paycheck paycheck1 = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, LocalDate.now().minusMonths(2));
        paycheck1.setId(100L);
        Paycheck paycheck2 = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, LocalDate.now().minusMonths(1));
        paycheck2.setId(101L);
        Paycheck paycheck3 = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, LocalDate.now());
        paycheck3.setId(102L);
        
        when(paycheckRepository.findByEmployeeId(1L))
            .thenReturn(Arrays.asList(paycheck1, paycheck2, paycheck3));
        
        // When
        List<PaycheckDTO> result = payrollService.getPayrollHistory(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(100L, result.get(0).id());
        assertEquals(101L, result.get(1).id());
        assertEquals(102L, result.get(2).id());
        
        verify(employeeService, times(1)).getEmployee(1L);
        verify(paycheckRepository, times(1)).findByEmployeeId(1L);
    }
    
    @Test
    @DisplayName("Should get payroll history for employee within date range")
    void testGetPayrollHistory_DateRange_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusMonths(2);
        LocalDate endDate = LocalDate.now();
        
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        Paycheck paycheck1 = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, startDate);
        paycheck1.setId(100L);
        Paycheck paycheck2 = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, endDate);
        paycheck2.setId(101L);
        
        when(paycheckRepository.findByEmployeeIdAndDateRange(1L, startDate, endDate))
            .thenReturn(Arrays.asList(paycheck1, paycheck2));
        
        // When
        List<PaycheckDTO> result = payrollService.getPayrollHistory(1L, startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(employeeService, times(1)).getEmployee(1L);
        verify(paycheckRepository, times(1)).findByEmployeeIdAndDateRange(1L, startDate, endDate);
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when startDate is after endDate")
    void testGetPayrollHistory_InvalidDateRange() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusMonths(1);
        
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> payrollService.getPayrollHistory(1L, startDate, endDate));
        
        assertTrue(exception.getMessage().contains("Start date cannot be after end date"));
    }
    
    // ==================== Payroll Summary/Report Tests ====================
    
    @Test
    @DisplayName("Should generate payroll summary for business")
    void testGetPayrollSummary_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        Employee employee2 = new Employee("Jane Smith", "jane@example.com", "password123", 60000.0, "Manager");
        employee2.setId(2L);
        employee2.setCompany(testCompany);
        
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        
        Paycheck paycheck1 = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, startDate);
        paycheck1.setId(100L);
        Paycheck paycheck2 = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, endDate);
        paycheck2.setId(101L);
        Paycheck paycheck3 = new Paycheck(employee2, 60000.0, 9000.0, 3000.0, endDate);
        paycheck3.setId(102L);
        
        when(paycheckRepository.findByBusinessIdAndDateRange(1L, startDate, endDate))
            .thenReturn(Arrays.asList(paycheck1, paycheck2, paycheck3));
        
        // When
        PayrollSummaryDTO summary = payrollService.getPayrollSummary(1L, startDate, endDate);
        
        // Then
        assertNotNull(summary);
        assertEquals(1L, summary.businessId());
        assertEquals("Test Corp", summary.businessName());
        assertEquals(startDate, summary.startDate());
        assertEquals(endDate, summary.endDate());
        assertEquals(3, summary.totalPaychecks());
        assertEquals(2, summary.totalEmployees());
        assertEquals(160000.0, summary.totalGrossPay(), 0.01); // 50000 + 50000 + 60000
        assertEquals(0.0, summary.totalBonus(), 0.01); // No bonuses in test data
        assertEquals(24000.0, summary.totalTaxDeductions(), 0.01); // 7500 + 7500 + 9000
        assertEquals(8000.0, summary.totalInsuranceDeductions(), 0.01); // 2500 + 2500 + 3000
        assertEquals(128000.0, summary.totalNetPay(), 0.01); // 40000 + 40000 + 48000
        
        // Verify average rates are calculated correctly (denominator = totalGrossPay + totalBonus)
        double totalCompensation = summary.totalGrossPay() + summary.totalBonus();
        double expectedTaxRate = summary.totalTaxDeductions() / totalCompensation; // 24000 / 160000 = 0.15
        double expectedInsuranceRate = summary.totalInsuranceDeductions() / totalCompensation; // 8000 / 160000 = 0.05
        assertEquals(expectedTaxRate, summary.averageTaxRate(), 0.0001);
        assertEquals(expectedInsuranceRate, summary.averageInsuranceRate(), 0.0001);
        
        verify(businessRepository, times(1)).findById(1L);
        verify(paycheckRepository, times(1)).findByBusinessIdAndDateRange(1L, startDate, endDate);
    }
    
    @Test
    @DisplayName("Should calculate average rates correctly with bonuses in PayrollSummaryDTO")
    void testGetPayrollSummary_WithBonuses_CorrectRates() {
        // Given - Paychecks with bonuses
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        Employee employee2 = new Employee("Jane Smith", "jane@example.com", "password123", 60000.0, "Manager");
        employee2.setId(2L);
        employee2.setCompany(testCompany);
        
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        
        // Paycheck 1: $50,000 base + $5,000 bonus = $55,000 total
        Paycheck paycheck1 = new Paycheck(testEmployee, 50000.0, 8250.0, 2750.0, startDate);
        paycheck1.setId(100L);
        paycheck1.setBonus(5000.0);
        
        // Paycheck 2: $60,000 base + $3,000 bonus = $63,000 total
        Paycheck paycheck2 = new Paycheck(employee2, 60000.0, 9450.0, 3150.0, endDate);
        paycheck2.setId(101L);
        paycheck2.setBonus(3000.0);
        
        when(paycheckRepository.findByBusinessIdAndDateRange(1L, startDate, endDate))
            .thenReturn(Arrays.asList(paycheck1, paycheck2));
        
        // When
        PayrollSummaryDTO summary = payrollService.getPayrollSummary(1L, startDate, endDate);
        
        // Then
        assertNotNull(summary);
        assertEquals(110000.0, summary.totalGrossPay(), 0.01); // 50000 + 60000
        assertEquals(8000.0, summary.totalBonus(), 0.01); // 5000 + 3000
        assertEquals(17700.0, summary.totalTaxDeductions(), 0.01); // 8250 + 9450
        assertEquals(5900.0, summary.totalInsuranceDeductions(), 0.01); // 2750 + 3150
        
        // Verify average rates use total compensation (grossPay + bonus) as denominator
        double totalCompensation = summary.totalGrossPay() + summary.totalBonus(); // 110000 + 8000 = 118000
        double expectedTaxRate = summary.totalTaxDeductions() / totalCompensation; // 17700 / 118000 ≈ 0.15
        double expectedInsuranceRate = summary.totalInsuranceDeductions() / totalCompensation; // 5900 / 118000 ≈ 0.05
        assertEquals(expectedTaxRate, summary.averageTaxRate(), 0.0001);
        assertEquals(expectedInsuranceRate, summary.averageInsuranceRate(), 0.0001);
    }
    
    @Test
    @DisplayName("Should return empty summary when no paychecks found")
    void testGetPayrollSummary_NoPaychecks() {
        // Given
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(paycheckRepository.findByBusinessIdAndDateRange(1L, startDate, endDate))
            .thenReturn(List.of());
        
        // When
        PayrollSummaryDTO summary = payrollService.getPayrollSummary(1L, startDate, endDate);
        
        // Then
        assertNotNull(summary);
        assertEquals(0, summary.totalPaychecks());
        assertEquals(0, summary.totalEmployees());
        assertEquals(0.0, summary.totalGrossPay(), 0.01);
    }
    
    // ==================== Update Paycheck Tests ====================
    
    @Test
    @DisplayName("Should update paycheck successfully")
    void testUpdatePaycheck_Success() {
        // Given
        Paycheck existingPaycheck = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, LocalDate.now());
        existingPaycheck.setId(100L);
        
        when(paycheckRepository.findById(100L)).thenReturn(Optional.of(existingPaycheck));
        
        // Update grossPay - should recalculate tax and insurance
        Double newGrossPay = 55000.0;
        Paycheck updatedPaycheck = new Paycheck(testEmployee, newGrossPay, 8250.0, 2750.0, LocalDate.now());
        updatedPaycheck.setId(100L);
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(updatedPaycheck);
        
        // When
        PaycheckDTO result = payrollService.updatePaycheck(100L, newGrossPay, null, null, null);
        
        // Then
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(55000.0, result.grossPay(), 0.01);
        
        ArgumentCaptor<Paycheck> captor = ArgumentCaptor.forClass(Paycheck.class);
        verify(paycheckRepository).save(captor.capture());
        
        Paycheck captured = captor.getValue();
        assertEquals(newGrossPay, captured.getGrossPay(), 0.01);
        // Tax and insurance should be recalculated
        verify(paycheckRepository, times(1)).save(any(Paycheck.class));
    }
    
    @Test
    @DisplayName("Should update paycheck bonus without recalculating tax")
    void testUpdatePaycheck_BonusOnly() {
        // Given
        Paycheck existingPaycheck = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, LocalDate.now());
        existingPaycheck.setId(100L);
        
        when(paycheckRepository.findById(100L)).thenReturn(Optional.of(existingPaycheck));
        
        Double newBonus = 2000.0;
        existingPaycheck.setBonus(newBonus);
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(existingPaycheck);
        
        // When
        PaycheckDTO result = payrollService.updatePaycheck(100L, null, newBonus, null, null);
        
        // Then
        assertNotNull(result);
        assertEquals(2000.0, result.bonus(), 0.01);
        assertEquals(50000.0, result.grossPay(), 0.01); // Unchanged
        
        ArgumentCaptor<Paycheck> captor = ArgumentCaptor.forClass(Paycheck.class);
        verify(paycheckRepository).save(captor.capture());
        assertEquals(newBonus, captor.getValue().getBonus(), 0.01);
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when paycheck not found")
    void testUpdatePaycheck_NotFound() {
        // Given
        when(paycheckRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> payrollService.updatePaycheck(999L, 55000.0, null, null, null));
        
        assertTrue(exception.getMessage().contains("Paycheck"));
        assertTrue(exception.getMessage().contains("999"));
        verify(paycheckRepository, never()).save(any());
    }
    
    // ==================== Insurance Rate Configuration Tests ====================
    
    @Test
    @DisplayName("Should use configured insurance rate correctly")
    void testInsuranceRateConfiguration() {
        // Given - Create service with custom insurance rate (6% instead of default 5%)
        double customInsuranceRate = 0.06;
        PayrollServiceImpl customService = new PayrollServiceImpl(
            employeeService,
            paycheckRepository,
            businessRepository,
            dtoFactory,
            flatTaxStrategy,
            customInsuranceRate
        );
        
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        // Expected insurance: $50,000 * 0.06 = $3,000 (instead of $2,500 with 5%)
        Paycheck savedPaycheck = new Paycheck(testEmployee, 50000.0, 7500.0, 3000.0, LocalDate.now());
        savedPaycheck.setId(100L);
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(savedPaycheck);
        
        // When
        PaycheckDTO result = customService.calculatePayroll(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(3000.0, result.insuranceDeduction(), 0.01); // 6% of 50000
        assertEquals(10500.0, result.totalDeductions(), 0.01); // 7500 + 3000
        assertEquals(39500.0, result.netPay(), 0.01); // 50000 - 7500 - 3000
        
        // Verify the captured paycheck has correct insurance deduction
        ArgumentCaptor<Paycheck> captor = ArgumentCaptor.forClass(Paycheck.class);
        verify(paycheckRepository).save(captor.capture());
        assertEquals(3000.0, captor.getValue().getInsuranceDeduction(), 0.01);
    }
    
    @Test
    @DisplayName("Should use default insurance rate (5%) when not specified")
    void testDefaultInsuranceRate() {
        // Given - Service uses default rate from setUp (0.05 = 5%)
        when(employeeService.getEmployee(1L)).thenReturn(Optional.of(testEmployee));
        
        Paycheck savedPaycheck = new Paycheck(testEmployee, 50000.0, 7500.0, 2500.0, LocalDate.now());
        savedPaycheck.setId(100L);
        when(paycheckRepository.save(any(Paycheck.class))).thenReturn(savedPaycheck);
        
        // When
        PaycheckDTO result = payrollService.calculatePayroll(1L);
        
        // Then - Verify default rate (5%) is used
        assertEquals(2500.0, result.insuranceDeduction(), 0.01); // 5% of 50000
        
        ArgumentCaptor<Paycheck> captor = ArgumentCaptor.forClass(Paycheck.class);
        verify(paycheckRepository).save(captor.capture());
        assertEquals(2500.0, captor.getValue().getInsuranceDeduction(), 0.01);
    }
}

