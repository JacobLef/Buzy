package app.payroll;

import app.payroll.dto.DistributeBonusRequest;
import app.payroll.dto.PaycheckDTO;
import app.payroll.dto.PayrollSummaryDTO;
import app.payroll.PaycheckStatus;
import app.payroll.strategy.TaxCalculationStrategy;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for payroll operations
 * Handles payroll calculations, bonus distribution, and tax strategy management
 * 
 * @author Qing Mi
 */
public interface PayrollService {
    
    /**
     * Calculate and save payroll for a single employee
     * 
     * @param employeeId ID of the employee
     * @return PaycheckDTO with calculated gross, deductions, and net pay
     * @throws ResourceNotFoundException if employee not found
     * @throws PayrollCalculationException if calculation fails
     */
    PaycheckDTO calculatePayroll(Long employeeId);
    
    /**
     * Calculate payroll with additional pay (bonus, overtime, etc.)
     * 
     * @param employeeId ID of the employee
     * @param additionalPay Additional amount to add to base salary
     * @return PaycheckDTO with calculated gross, deductions, and net pay
     * @throws ResourceNotFoundException if employee not found
     * @throws PayrollCalculationException if calculation fails
     */
    PaycheckDTO calculatePayrollWithAdditionalPay(Long employeeId, Double additionalPay);
    
    /**
     * Preview payroll calculation without saving to database
     * Used for displaying preview before user confirms
     * 
     * @param employeeId ID of the employee
     * @param additionalPay Optional additional pay (bonus, overtime, etc.)
     * @return PaycheckDTO with calculated gross, deductions, and net pay (not saved)
     * @throws ResourceNotFoundException if employee not found
     * @throws PayrollCalculationException if calculation fails
     */
    PaycheckDTO previewPayroll(Long employeeId, Double additionalPay);
    
    /**
     * Distribute bonuses to employees in a business
     * All operations are performed in a single transaction
     * 
     * @param request Bonus distribution request with businessId, amount, and filters
     * @return List of PaycheckDTOs for all processed employees
     * @throws ResourceNotFoundException if business not found
     * @throws BusinessValidationException if business has no employees
     */
    List<PaycheckDTO> distributeBonuses(DistributeBonusRequest request);
    
    /**
     * Change the tax calculation strategy at runtime
     * 
     * @param strategy New tax calculation strategy to use
     * @throws IllegalArgumentException if strategy is null
     */
    void setTaxStrategy(TaxCalculationStrategy strategy);
    
    /**
     * Get the name of the currently active tax strategy
     * 
     * @return Name/description of current tax strategy
     */
    String getCurrentTaxStrategyName();
    
    /**
     * Get payroll history for a specific employee
     * 
     * @param employeeId ID of the employee
     * @return List of PaycheckDTOs for the employee
     * @throws ResourceNotFoundException if employee not found
     */
    List<PaycheckDTO> getPayrollHistory(Long employeeId);
    
    /**
     * Get payroll history for an employee within a date range
     * 
     * @param employeeId ID of the employee
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of PaycheckDTOs for the employee in the date range
     * @throws ResourceNotFoundException if employee not found
     */
    List<PaycheckDTO> getPayrollHistory(Long employeeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get payroll summary statistics for a business
     * 
     * @param businessId ID of the business
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return PayrollSummaryDTO with aggregated statistics
     * @throws ResourceNotFoundException if business not found
     */
    PayrollSummaryDTO getPayrollSummary(Long businessId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Update/correct an existing paycheck
     * Only allowed if status is DRAFT
     * 
     * @param paycheckId ID of the paycheck to update
     * @param grossPay Updated gross pay (optional)
     * @param bonus Updated bonus amount (optional)
     * @param taxDeduction Updated tax deduction (optional)
     * @param insuranceDeduction Updated insurance deduction (optional)
     * @return Updated PaycheckDTO
     * @throws ResourceNotFoundException if paycheck not found
     * @throws PayrollCalculationException if update fails or paycheck is not in DRAFT status
     */
    PaycheckDTO updatePaycheck(Long paycheckId, Double grossPay, Double bonus, 
                               Double taxDeduction, Double insuranceDeduction);
    
    /**
     * Delete a paycheck
     * Only allowed if status is DRAFT
     * 
     * @param paycheckId ID of the paycheck to delete
     * @throws ResourceNotFoundException if paycheck not found
     * @throws PayrollCalculationException if paycheck is not in DRAFT status
     */
    void deletePaycheck(Long paycheckId);
    
    /**
     * Update paycheck status
     * 
     * @param paycheckId ID of the paycheck
     * @param newStatus New status to set
     * @return Updated PaycheckDTO
     * @throws ResourceNotFoundException if paycheck not found
     * @throws PayrollCalculationException if status transition is invalid
     */
    PaycheckDTO updatePaycheckStatus(Long paycheckId, PaycheckStatus newStatus);
}