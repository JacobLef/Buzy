package app.payroll;

import app.payroll.dto.DistributeBonusRequest;
import app.payroll.dto.PaycheckDTO;
import app.payroll.dto.PayrollSummaryDTO;
import app.common.factory.DTOFactory;
import app.business.BusinessValidationException;
import app.common.exception.ResourceNotFoundException;
import app.business.Company;
import app.employee.Employee;
import app.employer.Employer;
import app.business.BusinessRepository;
import app.employee.EmployeeService;
import app.payroll.strategy.TaxCalculationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of PayrollService handling all payroll calculations Uses
 * Strategy pattern for tax calculations and supports multiple deduction types
 *
 * @author Qing Mi
 */
@Service
public class PayrollServiceImpl implements PayrollService {

	private static final Logger logger = LoggerFactory.getLogger(PayrollServiceImpl.class);

	private static final double MINIMUM_SALARY_THRESHOLD = 0.0;

	private final EmployeeService employeeService;
	private final PaycheckRepository paycheckRepository;
	private final BusinessRepository businessRepository;
	private final DTOFactory dtoFactory;
	private TaxCalculationStrategy taxStrategy;

	private final double insuranceRate;

	/**
	 * Constructor injection - Spring provides all dependencies Insurance rate is
	 * configurable via application.properties with default value 0.05 (5%)
	 */
	@Autowired
	public PayrollServiceImpl(EmployeeService employeeService, PaycheckRepository paycheckRepository,
			BusinessRepository businessRepository, DTOFactory dtoFactory,
			@Qualifier("flatTaxStrategy") TaxCalculationStrategy taxStrategy,
			@Value("${payroll.default.insurance.rate:0.05}") double insuranceRate) {
		this.employeeService = employeeService;
		this.paycheckRepository = paycheckRepository;
		this.businessRepository = businessRepository;
		this.dtoFactory = dtoFactory;
		this.taxStrategy = taxStrategy;
		this.insuranceRate = insuranceRate;
	}

	/**
	 * Initialize service after dependency injection Logs the tax strategy and
	 * insurance rate being used
	 */
	@PostConstruct
	public void init() {
		if (taxStrategy == null) {
			logger.error("CRITICAL: Tax strategy is null after initialization! This should not happen.");
			throw new IllegalStateException("Tax strategy bean was not properly injected. Check TaxStrategyConfig.");
		}
		logger.info("PayrollService initialized with tax strategy: {} and insurance rate: {}%",
				taxStrategy.getStrategyName(), insuranceRate * 100);
	}

	@Override
	@Transactional
	public PaycheckDTO calculatePayroll(Long employeeId) {
		logger.debug("Calculating regular payroll for employee ID: {}", employeeId);

		// Fetch employee via EmployeeService
		Employee employee = employeeService.getEmployee(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

		// Calculate regular payroll (base salary only)
		try {
			Paycheck paycheck = calculateRegularPayroll(employee);

			// Save to database
			Paycheck savedPaycheck = paycheckRepository.save(paycheck);

			logger.info("Regular payroll calculated successfully for employee {}: Net Pay = ${}", employee.getName(),
					savedPaycheck.getNetPay());

			return convertToDTO(savedPaycheck, employee);

		} catch (Exception e) {
			logger.error("Failed to calculate payroll for employee ID: {}", employeeId, e);
			throw new PayrollCalculationException("Failed to calculate payroll: " + e.getMessage(), employeeId, e);
		}
	}

	@Override
	@Transactional
	public PaycheckDTO calculatePayrollWithAdditionalPay(Long employeeId, Double additionalPay) {
		// Validate input
		if (employeeId == null) {
			throw new IllegalArgumentException("Employee ID cannot be null");
		}

		if (additionalPay == null || additionalPay == 0.0) {
			// Delegate to regular payroll calculation for consistency
			return calculatePayroll(employeeId);
		}

		if (additionalPay < 0) {
			throw new IllegalArgumentException("Additional pay cannot be negative");
		}

		logger.debug("Calculating payroll for employee ID: {} with additional pay: {}", employeeId, additionalPay);

		// Fetch employee via EmployeeService
		Employee employee = employeeService.getEmployee(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

		// Calculate payroll with bonus (additionalPay > 0)
		try {
			Paycheck paycheck = calculatePayrollWithBonus(employee, additionalPay);

			// Save to database
			Paycheck savedPaycheck = paycheckRepository.save(paycheck);

			logger.info("Payroll calculated successfully for employee {}: Net Pay = ${}", employee.getName(),
					savedPaycheck.getNetPay());

			return convertToDTO(savedPaycheck, employee);

		} catch (Exception e) {
			logger.error("Failed to calculate payroll for employee ID: {}", employeeId, e);
			throw new PayrollCalculationException("Failed to calculate payroll: " + e.getMessage(), employeeId, e);
		}
	}

	@Override
	@Transactional
	public List<PaycheckDTO> distributeBonuses(DistributeBonusRequest request) {
		logger.info("Starting bonus distribution for business ID: {} with amount: ${}", request.businessId(),
				request.bonusAmount());

		// Validate business exists
		Company business = businessRepository.findById(request.businessId())
				.orElseThrow(() -> new ResourceNotFoundException("Business", "id", request.businessId()));

		// Fetch employees based on filters
		List<Employee> employees = fetchEmployeesForBonus(request, business);

		// Validate we have employees to process
		if (employees.isEmpty()) {
			throw new BusinessValidationException("No employees found matching the criteria for bonus distribution");
		}

		// Calculate payroll for all employees with bonus
		List<PaycheckDTO> paycheckDTOs = new ArrayList<>();
		List<String> errors = new ArrayList<>();

		for (Employee employee : employees) {
			try {
				// Calculate payroll with bonus (bonus stored separately from base salary)
				Paycheck paycheck = calculatePayrollWithBonus(employee, request.bonusAmount());
				Paycheck savedPaycheck = paycheckRepository.save(paycheck);

				PaycheckDTO dto = convertToDTO(savedPaycheck, employee);
				paycheckDTOs.add(dto);

				logger.debug("Bonus paycheck created for employee {}: Base=${}, Bonus=${}, Net=${}", employee.getName(),
						savedPaycheck.getGrossPay(), savedPaycheck.getBonus(), savedPaycheck.getNetPay());

			} catch (Exception e) {
				String errorMsg = String.format("Failed to process bonus for employee %s (ID: %d): %s",
						employee.getName(), employee.getId(), e.getMessage());
				errors.add(errorMsg);
				logger.error(errorMsg, e);
				// Continue processing other employees
			}
		}

		// If all failed, throw exception to rollback transaction
		if (paycheckDTOs.isEmpty() && !errors.isEmpty()) {
			throw new PayrollCalculationException(
					"Failed to distribute bonuses to any employees. Errors: " + String.join("; ", errors),
					request.businessId());
		}

		logger.info("Bonus distribution completed: {} successful, {} failed", paycheckDTOs.size(), errors.size());

		return paycheckDTOs;
	}

	@Override
	public PaycheckDTO previewPayroll(Long employeeId, Double additionalPay) {
		// Validate input
		if (employeeId == null) {
			throw new IllegalArgumentException("Employee ID cannot be null");
		}

		if (additionalPay != null && additionalPay < 0) {
			throw new IllegalArgumentException("Additional pay cannot be negative");
		}

		logger.debug("Previewing payroll for employee ID: {} with additional pay: {}", employeeId, additionalPay);

		// Fetch employee via EmployeeService
		Employee employee = employeeService.getEmployee(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

		// Calculate payroll without saving
		try {
			Paycheck paycheck;
			if (additionalPay == null || additionalPay == 0.0) {
				paycheck = calculateRegularPayroll(employee);
			} else {
				paycheck = calculatePayrollWithBonus(employee, additionalPay);
			}

			// Convert to DTO without saving to database
			logger.debug("Payroll preview calculated for employee {}: Net Pay = ${}", employee.getName(),
					paycheck.getNetPay());

			return convertToDTO(paycheck, employee);

		} catch (Exception e) {
			logger.error("Failed to preview payroll for employee ID: {}", employeeId, e);
			throw new PayrollCalculationException("Failed to preview payroll: " + e.getMessage(), employeeId, e);
		}
	}

	@Override
	public void setTaxStrategy(TaxCalculationStrategy strategy) {
		if (strategy == null) {
			throw new IllegalArgumentException("Tax strategy cannot be null");
		}

		String oldStrategy = (this.taxStrategy != null) ? this.taxStrategy.getStrategyName() : "None";
		this.taxStrategy = strategy;

		logger.info("Tax strategy changed from '{}' to '{}'", oldStrategy, strategy.getStrategyName());
	}

	@Override
	public String getCurrentTaxStrategyName() {
		if (taxStrategy == null) {
			logger.warn("Tax strategy is null, returning default strategy name");
			return "Flat Tax Strategy";
		}
		return taxStrategy.getStrategyName();
	}

	// ==================== Private Helper Methods ====================

	/**
	 * Calculate regular payroll (base salary only, no bonus)
	 *
	 * @param employee
	 *            Employee to calculate payroll for
	 * @return Paycheck object with base salary calculations
	 */
	private Paycheck calculateRegularPayroll(Employee employee) {
		// Validate employee has valid salary
		if (employee.getSalary() == null || employee.getSalary() < MINIMUM_SALARY_THRESHOLD) {
			throw new PayrollCalculationException("Employee has invalid salary: " + employee.getSalary(),
					employee.getId());
		}

		double baseSalary = employee.getSalary();
		if (taxStrategy == null) {
			logger.error("Tax strategy is null in calculateRegularPayroll");
			throw new IllegalStateException("Tax strategy is not initialized. Cannot calculate payroll.");
		}
		double taxDeduction = taxStrategy.calculateTax(baseSalary);
		double insuranceDeduction = calculateInsuranceDeduction(baseSalary);

		// Create paycheck for regular salary (no bonus)
		Paycheck paycheck = new Paycheck(employee, baseSalary, taxDeduction, insuranceDeduction, LocalDate.now());
		paycheck.setBonus(null); // Explicitly set no bonus

		logger.debug("Regular payroll calculated - Base Salary: ${}, Tax: ${}, Insurance: ${}, Net: ${}", baseSalary,
				taxDeduction, insuranceDeduction, paycheck.getNetPay());

		return paycheck;
	}

	/**
	 * Calculate payroll with bonus payment In real-life: bonus is separate from
	 * base salary, taxed on total (base + bonus)
	 *
	 * @param employee
	 *            Employee to calculate payroll for
	 * @param bonusAmount
	 *            Bonus amount to add (must be > 0)
	 * @return Paycheck object with base salary and bonus calculations
	 * @throws IllegalArgumentException
	 *             if bonusAmount is null or <= 0
	 */
	private Paycheck calculatePayrollWithBonus(Employee employee, Double bonusAmount) {
		// Validate bonus amount
		if (bonusAmount == null || bonusAmount <= 0) {
			throw new IllegalArgumentException("Bonus amount must be greater than 0");
		}

		// Validate employee has valid salary
		if (employee.getSalary() == null || employee.getSalary() < MINIMUM_SALARY_THRESHOLD) {
			throw new PayrollCalculationException("Employee has invalid salary: " + employee.getSalary(),
					employee.getId());
		}

		double baseSalary = employee.getSalary();
		double totalGrossPay = baseSalary + bonusAmount;

		// Tax and insurance calculated on total (base salary + bonus)
		if (taxStrategy == null) {
			logger.error("Tax strategy is null in calculatePayrollWithBonus");
			throw new IllegalStateException("Tax strategy is not initialized. Cannot calculate payroll.");
		}
		double taxDeduction = taxStrategy.calculateTax(totalGrossPay);
		double insuranceDeduction = calculateInsuranceDeduction(totalGrossPay);

		// Create paycheck: grossPay = base salary, bonus stored separately
		Paycheck paycheck = new Paycheck(employee, baseSalary, // Base salary stored as grossPay
				taxDeduction, insuranceDeduction, LocalDate.now());
		paycheck.setBonus(bonusAmount); // Bonus stored separately

		logger.debug(
				"Payroll with bonus calculated - Base: ${}, Bonus: ${}, Total Gross: ${}, Tax: ${}, Insurance: ${}, Net: ${}",
				baseSalary, bonusAmount, totalGrossPay, taxDeduction, insuranceDeduction, paycheck.getNetPay());

		return paycheck;
	}

	/**
	 * Calculate insurance deduction based on gross pay Uses configurable insurance
	 * rate from application.properties Can be extended to use Strategy pattern if
	 * needed
	 *
	 * @param grossPay
	 *            Gross payment amount
	 * @return Insurance deduction amount
	 */
	private double calculateInsuranceDeduction(double grossPay) {
		return grossPay * insuranceRate;
	}

	/**
	 * Fetch employees for bonus distribution based on request filters
	 *
	 * @param request
	 *            Bonus request with filters
	 * @param business
	 *            Company entity
	 * @return List of employees matching criteria
	 */
	private List<Employee> fetchEmployeesForBonus(DistributeBonusRequest request, Company business) {
		List<Employee> allEmployees;

		// If specific employee IDs provided, fetch only those
		if (request.employeeIds() != null && !request.employeeIds().isEmpty()) {
			// Fetch employees via EmployeeService
			allEmployees = request.employeeIds().stream().map(employeeService::getEmployee)
					.filter(java.util.Optional::isPresent).map(java.util.Optional::get).collect(Collectors.toList());

			// Validate all requested employees exist
			if (allEmployees.size() != request.employeeIds().size()) {
				logger.warn("Some employee IDs not found. Requested: {}, Found: {}", request.employeeIds().size(),
						allEmployees.size());
			}
		} else {
			// Fetch all employees for the business via EmployeeService
			allEmployees = employeeService.getEmployeesByBusiness(business.getId());
		}

		// Apply department filter if specified
		if (request.department() != null && !request.department().isBlank()) {
			allEmployees = allEmployees.stream().filter(emp -> {
				if (emp.getManager() == null)
					return false;
				// Manager can be Employee or Employer, only Employer has department
				if (emp.getManager() instanceof Employer) {
					return request.department().equalsIgnoreCase(((Employer) emp.getManager()).getDepartment());
				}
				return false;
			}).collect(Collectors.toList());
		}

		logger.debug("Fetched {} employees for bonus distribution", allEmployees.size());
		return allEmployees;
	}

	@Override
	public List<PaycheckDTO> getPayrollHistory(Long employeeId) {
		logger.debug("Fetching payroll history for employee ID: {}", employeeId);

		// Validate employee exists
		Employee employee = employeeService.getEmployee(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

		List<Paycheck> paychecks = paycheckRepository.findByEmployeeId(employeeId);

		return paychecks.stream().map(p -> convertToDTO(p, p.getEmployee() != null ? p.getEmployee() : employee))
				.collect(Collectors.toList());
	}

	@Override
	public List<PaycheckDTO> getPayrollHistory(Long employeeId, LocalDate startDate, LocalDate endDate) {
		logger.debug("Fetching payroll history for employee ID: {} from {} to {}", employeeId, startDate, endDate);

		// Validate employee exists
		Employee employee = employeeService.getEmployee(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Start date cannot be after end date");
		}

		List<Paycheck> paychecks = paycheckRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);

		return paychecks.stream().map(p -> convertToDTO(p, p.getEmployee() != null ? p.getEmployee() : employee))
				.collect(Collectors.toList());
	}

	@Override
	public PayrollSummaryDTO getPayrollSummary(Long businessId, LocalDate startDate, LocalDate endDate) {
		logger.info("Generating payroll summary for business ID: {} from {} to {}", businessId, startDate, endDate);

		// Validate business exists
		Company business = businessRepository.findById(businessId)
				.orElseThrow(() -> new ResourceNotFoundException("Business", "id", businessId));

		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Start date cannot be after end date");
		}

		// Fetch all paychecks for the business in date range
		List<Paycheck> paychecks = paycheckRepository.findByBusinessIdAndDateRange(businessId, startDate, endDate);

		if (paychecks.isEmpty()) {
			return new PayrollSummaryDTO(businessId, business.getName(), startDate, endDate, 0, 0, 0.0, 0.0, 0.0, 0.0,
					0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		}

		// Calculate aggregates
		int totalPaychecks = paychecks.size();
		long totalEmployees = paychecks.stream().map(p -> p.getEmployeeId()).distinct().count();

		double totalGrossPay = paychecks.stream().mapToDouble(Paycheck::getGrossPay).sum();

		double totalBonus = paychecks.stream().mapToDouble(p -> p.getBonus() != null ? p.getBonus() : 0.0).sum();

		double totalTaxDeductions = paychecks.stream().mapToDouble(Paycheck::getTaxDeduction).sum();

		double totalInsuranceDeductions = paychecks.stream().mapToDouble(Paycheck::getInsuranceDeduction).sum();

		double totalDeductions = totalTaxDeductions + totalInsuranceDeductions;

		double totalNetPay = paychecks.stream().mapToDouble(Paycheck::getNetPay).sum();

		return new PayrollSummaryDTO(businessId, business.getName(), startDate, endDate, totalPaychecks,
				(int) totalEmployees, totalGrossPay, totalBonus, totalTaxDeductions, totalInsuranceDeductions,
				totalDeductions, totalNetPay, 0.0, // Calculated in constructor
				0.0, // Calculated in constructor
				0.0, // Calculated in constructor
				0.0 // Calculated in constructor
		);
	}

	@Override
	@Transactional
	public PaycheckDTO updatePaycheck(Long paycheckId, Double grossPay, Double bonus, Double taxDeduction,
			Double insuranceDeduction) {
		logger.info("Updating paycheck ID: {}", paycheckId);

		Paycheck paycheck = paycheckRepository.findById(paycheckId)
				.orElseThrow(() -> new ResourceNotFoundException("Paycheck", "id", paycheckId));

		// Only allow updates if status is DRAFT
		if (paycheck.getStatus() != PaycheckStatus.DRAFT) {
			throw new PayrollCalculationException("Cannot update paycheck with status: " + paycheck.getStatus()
					+ ". Only DRAFT paychecks can be updated.", paycheckId, null);
		}

		Employee employee = paycheck.getEmployee();
		if (employee == null) {
			employee = employeeService.getEmployee(paycheck.getEmployeeId())
					.orElseThrow(() -> new ResourceNotFoundException("Employee", "id", paycheck.getEmployeeId()));
			paycheck.setEmployee(employee);
		}

		// Update fields if provided
		boolean recalculateTax = false;
		boolean recalculateInsurance = false;

		if (grossPay != null && grossPay != paycheck.getGrossPay()) {
			paycheck.setGrossPay(grossPay);
			recalculateTax = true;
			recalculateInsurance = true;
		}

		if (bonus != null) {
			paycheck.setBonus(bonus);
		}

		if (taxDeduction != null && !recalculateTax) {
			paycheck.setTaxDeduction(taxDeduction);
		} else if (recalculateTax) {
			// Recalculate tax based on total (grossPay + bonus)
			if (taxStrategy == null) {
				logger.error("Tax strategy is null in updatePaycheck");
				throw new IllegalStateException("Tax strategy is not initialized. Cannot update paycheck.");
			}
			double totalGross = paycheck.getGrossPay() + (paycheck.getBonus() != null ? paycheck.getBonus() : 0.0);
			paycheck.setTaxDeduction(taxStrategy.calculateTax(totalGross));
		}

		if (insuranceDeduction != null && !recalculateInsurance) {
			paycheck.setInsuranceDeduction(insuranceDeduction);
		} else if (recalculateInsurance) {
			// Recalculate insurance based on total (grossPay + bonus)
			double totalGross = paycheck.getGrossPay() + (paycheck.getBonus() != null ? paycheck.getBonus() : 0.0);
			paycheck.setInsuranceDeduction(calculateInsuranceDeduction(totalGross));
		}

		// Net pay will be recalculated automatically via setter
		Paycheck updatedPaycheck = paycheckRepository.save(paycheck);

		logger.info("Paycheck ID: {} updated successfully. New net pay: ${}", paycheckId, updatedPaycheck.getNetPay());

		return convertToDTO(updatedPaycheck, employee);
	}

	@Override
	@Transactional
	public void deletePaycheck(Long paycheckId) {
		logger.info("Deleting paycheck ID: {}", paycheckId);

		Paycheck paycheck = paycheckRepository.findById(paycheckId)
				.orElseThrow(() -> new ResourceNotFoundException("Paycheck", "id", paycheckId));

		// Only allow deletion if status is DRAFT
		if (paycheck.getStatus() != PaycheckStatus.DRAFT) {
			throw new PayrollCalculationException("Cannot delete paycheck with status: " + paycheck.getStatus()
					+ ". Only DRAFT paychecks can be deleted.", paycheckId, null);
		}

		paycheckRepository.delete(paycheck);
		logger.info("Paycheck ID: {} deleted successfully", paycheckId);
	}

	@Override
	@Transactional
	public PaycheckDTO updatePaycheckStatus(Long paycheckId, PaycheckStatus newStatus) {
		logger.info("Updating paycheck ID: {} status to: {}", paycheckId, newStatus);

		Paycheck paycheck = paycheckRepository.findById(paycheckId)
				.orElseThrow(() -> new ResourceNotFoundException("Paycheck", "id", paycheckId));

		PaycheckStatus currentStatus = paycheck.getStatus();

		// Validate status transitions
		if (currentStatus == PaycheckStatus.PAID && newStatus != PaycheckStatus.VOIDED) {
			throw new PayrollCalculationException(
					"Cannot change status from PAID to " + newStatus + ". Only VOIDED is allowed.", paycheckId, null);
		}

		if (currentStatus == PaycheckStatus.VOIDED) {
			throw new PayrollCalculationException("Cannot change status of VOIDED paycheck.", paycheckId, null);
		}

		paycheck.setStatus(newStatus);
		Paycheck updatedPaycheck = paycheckRepository.save(paycheck);

		Employee employee = updatedPaycheck.getEmployee();
		if (employee == null) {
			employee = employeeService.getEmployee(updatedPaycheck.getEmployeeId()).orElseThrow(
					() -> new ResourceNotFoundException("Employee", "id", updatedPaycheck.getEmployeeId()));
		}

		logger.info("Paycheck ID: {} status updated from {} to {}", paycheckId, currentStatus, newStatus);

		return convertToDTO(updatedPaycheck, employee);
	}

	/**
	 * Convert Paycheck entity to PaycheckDTO using DTOFactory
	 *
	 * @param paycheck
	 *            Paycheck entity from database
	 * @param employee
	 *            Employee entity for employee name
	 * @return PaycheckDTO for API response
	 */
	private PaycheckDTO convertToDTO(Paycheck paycheck, Employee employee) {
		String strategyName = (taxStrategy != null) ? taxStrategy.getStrategyName() : "Flat Tax Strategy";
		return dtoFactory.createDTO(paycheck, employee, strategyName);
	}
}
