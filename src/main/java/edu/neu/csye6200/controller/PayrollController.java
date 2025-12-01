package edu.neu.csye6200.controller;

import edu.neu.csye6200.dto.request.DistributeBonusRequest;
import edu.neu.csye6200.dto.response.BonusDistributionResponse;
import edu.neu.csye6200.dto.response.PaycheckDTO;
import edu.neu.csye6200.dto.response.PayrollSummaryDTO;
import edu.neu.csye6200.service.interfaces.BusinessService;
import edu.neu.csye6200.service.interfaces.PayrollService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for payroll operations
 * Handles payroll calculation and bonus distribution endpoints
 * 
 * @author Qing Mi
 */
@RestController
@RequestMapping("/api/payroll")
public class PayrollController {
    
    private static final Logger logger = LoggerFactory.getLogger(PayrollController.class);
    
    private final PayrollService payrollService;
    private final BusinessService businessService;
    
    /**
     * Constructor injection - Spring provides PayrollService and BusinessService implementations
     */
    public PayrollController(PayrollService payrollService, BusinessService businessService) {
        this.payrollService = payrollService;
        this.businessService = businessService;
    }
    
    /**
     * Calculate payroll for a single employee
     * 
     * POST /api/payroll/calculate/{employeeId}
     * POST /api/payroll/calculate/{employeeId}?additionalPay=1000
     * 
     * @param employeeId ID of the employee
     * @param additionalPay Optional additional amount to add to base salary (bonus, overtime, etc.)
     * @return PaycheckDTO with calculated gross, deductions, and net pay
     */
    @PostMapping("/calculate/{employeeId}")
    public ResponseEntity<PaycheckDTO> calculatePayroll(
        @PathVariable Long employeeId,
        @RequestParam(required = false) Double additionalPay
    ) {
        if (additionalPay != null) {
            logger.info("Received payroll calculation request for employee ID: {} with additional pay: ${}",
                employeeId, additionalPay);
            
            PaycheckDTO dto = payrollService.calculatePayrollWithAdditionalPay(
                employeeId, 
                additionalPay
            );
            
            logger.info("Payroll with additional pay calculated successfully for employee ID: {}", 
                employeeId);
            
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } else {
            logger.info("Received payroll calculation request for employee ID: {}", employeeId);
            
            PaycheckDTO dto = payrollService.calculatePayroll(employeeId);
            
            logger.info("Payroll calculated successfully for employee ID: {}", employeeId);
            
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        }
    }
    
    /**
     * Distribute bonuses to employees in a business
     * 
     * POST /api/payroll/bonuses
     * Request Body:
     * {
     *   "businessId": 1,
     *   "bonusAmount": 1000.00,
     *   "employeeIds": [1, 2, 3],  // Optional: specific employees
     *   "department": "Engineering", // Optional: filter by department
     *   "description": "Q4 Performance Bonus"
     * }
     * 
     * @param request Bonus distribution request with filters
     * @return BonusDistributionResponse with summary and individual paychecks
     */
    @PostMapping("/bonuses")
    public ResponseEntity<BonusDistributionResponse> distributeBonuses(
        @Valid @RequestBody DistributeBonusRequest request
    ) {
        logger.info("Received bonus distribution request for business ID: {} with amount: ${}",
            request.businessId(), request.bonusAmount());
        
        List<PaycheckDTO> paycheckDTOs = payrollService.distributeBonuses(request);
        
        // Get business name from Company entity
        String businessName = businessService.getBusiness(request.businessId()).getName();
        
        BonusDistributionResponse response = BonusDistributionResponse.success(
            request.businessId(),
            businessName,
            paycheckDTOs
        );
        
        logger.info("Bonus distribution completed successfully: {} paychecks generated",
            paycheckDTOs.size());
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Get current tax strategy information
     * 
     * GET /api/payroll/tax-strategy
     * 
     * @return Name/description of current tax strategy
     */
    @GetMapping("/tax-strategy")
    public ResponseEntity<String> getCurrentTaxStrategy() {
        String strategyName = payrollService.getCurrentTaxStrategyName();
        return ResponseEntity.ok(strategyName);
    }
    
    /**
     * Get payroll history for a specific employee
     * 
     * GET /api/payroll/history/{employeeId}
     * 
     * @param employeeId ID of the employee
     * @return List of PaycheckDTOs for the employee
     */
    @GetMapping("/history/{employeeId}")
    public ResponseEntity<List<PaycheckDTO>> getPayrollHistory(
        @PathVariable Long employeeId
    ) {
        logger.info("Fetching payroll history for employee ID: {}", employeeId);
        
        List<PaycheckDTO> history = payrollService.getPayrollHistory(employeeId);
        
        logger.info("Retrieved {} paychecks for employee ID: {}", history.size(), employeeId);
        
        return ResponseEntity.ok(history);
    }
    
    /**
     * Get payroll history for an employee within a date range
     * 
     * GET /api/payroll/history/{employeeId}?startDate=2024-01-01&endDate=2024-12-31
     * 
     * @param employeeId ID of the employee
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of PaycheckDTOs for the employee in the date range
     */
    @GetMapping("/history/{employeeId}/range")
    public ResponseEntity<List<PaycheckDTO>> getPayrollHistoryByDateRange(
        @PathVariable Long employeeId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        logger.info("Fetching payroll history for employee ID: {} from {} to {}", 
            employeeId, startDate, endDate);
        
        List<PaycheckDTO> history = payrollService.getPayrollHistory(employeeId, startDate, endDate);
        
        logger.info("Retrieved {} paychecks for employee ID: {} in date range", 
            history.size(), employeeId);
        
        return ResponseEntity.ok(history);
    }
    
    /**
     * Get payroll summary/report for a business
     * 
     * GET /api/payroll/summary/{businessId}?startDate=2024-01-01&endDate=2024-12-31
     * 
     * @param businessId ID of the business
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return PayrollSummaryDTO with aggregated statistics
     */
    @GetMapping("/summary/{businessId}")
    public ResponseEntity<PayrollSummaryDTO> getPayrollSummary(
        @PathVariable Long businessId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        logger.info("Generating payroll summary for business ID: {} from {} to {}", 
            businessId, startDate, endDate);
        
        PayrollSummaryDTO summary = payrollService.getPayrollSummary(businessId, startDate, endDate);
        
        logger.info("Payroll summary generated: {} paychecks, {} employees, Total Net: ${}", 
            summary.totalPaychecks(), summary.totalEmployees(), summary.totalNetPay());
        
        return ResponseEntity.ok(summary);
    }
    
    /**
     * Update/correct an existing paycheck
     * 
     * PUT /api/payroll/paycheck/{paycheckId}
     * Request Body (all fields optional):
     * {
     *   "grossPay": 55000.0,
     *   "bonus": 2000.0,
     *   "taxDeduction": 8250.0,
     *   "insuranceDeduction": 2750.0
     * }
     * 
     * @param paycheckId ID of the paycheck to update
     * @param grossPay Updated gross pay (optional)
     * @param bonus Updated bonus amount (optional)
     * @param taxDeduction Updated tax deduction (optional, recalculated if grossPay changes)
     * @param insuranceDeduction Updated insurance deduction (optional, recalculated if grossPay changes)
     * @return Updated PaycheckDTO
     */
    @PutMapping("/paycheck/{paycheckId}")
    public ResponseEntity<PaycheckDTO> updatePaycheck(
        @PathVariable Long paycheckId,
        @RequestParam(required = false) Double grossPay,
        @RequestParam(required = false) Double bonus,
        @RequestParam(required = false) Double taxDeduction,
        @RequestParam(required = false) Double insuranceDeduction
    ) {
        logger.info("Updating paycheck ID: {}", paycheckId);
        
        PaycheckDTO updated = payrollService.updatePaycheck(
            paycheckId, grossPay, bonus, taxDeduction, insuranceDeduction);
        
        logger.info("Paycheck ID: {} updated successfully", paycheckId);
        
        return ResponseEntity.ok(updated);
    }
}