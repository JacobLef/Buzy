package app.payroll;

import app.payroll.dto.DistributeBonusRequest;
import app.payroll.dto.BonusDistributionResponse;
import app.payroll.dto.DeletePaycheckResponse;
import app.payroll.dto.PaycheckDTO;
import app.payroll.dto.PayrollSummaryDTO;
import app.payroll.dto.TaxStrategiesResponse;
import app.payroll.dto.TaxStrategyResponse;
import app.payroll.dto.TaxStrategySwitchResponse;
import app.payroll.PaycheckStatus;
import app.business.BusinessService;
import app.payroll.PayrollService;
import app.payroll.strategy.TaxCalculationStrategy;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final TaxCalculationStrategy flatTaxStrategy;
    private final TaxCalculationStrategy progressiveTaxStrategy;
    
    /**
     * Constructor injection - Spring provides PayrollService and BusinessService implementations
     */
    public PayrollController(
            PayrollService payrollService, 
            BusinessService businessService,
            @Qualifier("flatTaxStrategy") TaxCalculationStrategy flatTaxStrategy,
            @Qualifier("progressiveTaxStrategy") TaxCalculationStrategy progressiveTaxStrategy) {
        this.payrollService = payrollService;
        this.businessService = businessService;
        this.flatTaxStrategy = flatTaxStrategy;
        this.progressiveTaxStrategy = progressiveTaxStrategy;
        
        // Log initialization
        if (flatTaxStrategy == null) {
            logger.error("CRITICAL: flatTaxStrategy bean is null! Check TaxStrategyConfig.");
        } else {
            logger.info("PayrollController initialized with flatTaxStrategy: {}", flatTaxStrategy.getStrategyName());
        }
        
        if (progressiveTaxStrategy == null) {
            logger.error("CRITICAL: progressiveTaxStrategy bean is null! Check TaxStrategyConfig.");
        } else {
            logger.info("PayrollController initialized with progressiveTaxStrategy: {}", progressiveTaxStrategy.getStrategyName());
        }
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
     * Preview payroll calculation without saving to database
     * 
     * GET /api/payroll/preview/{employeeId}
     * GET /api/payroll/preview/{employeeId}?additionalPay=1000
     * 
     * @param employeeId ID of the employee
     * @param additionalPay Optional additional amount to add to base salary (bonus, overtime, etc.)
     * @return PaycheckDTO with calculated gross, deductions, and net pay (not saved)
     */
    @GetMapping("/preview/{employeeId}")
    public ResponseEntity<PaycheckDTO> previewPayroll(
        @PathVariable Long employeeId,
        @RequestParam(required = false) Double additionalPay
    ) {
        logger.info("Received payroll preview request for employee ID: {} with additional pay: ${}",
            employeeId, additionalPay != null ? additionalPay : 0.0);
        
        PaycheckDTO dto = payrollService.previewPayroll(employeeId, additionalPay);
        
        logger.info("Payroll preview calculated successfully for employee ID: {}", employeeId);
        
        return ResponseEntity.ok(dto);
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
     * @return TaxStrategyResponse DTO with current tax strategy name
     */
    @GetMapping("/tax-strategy")
    public ResponseEntity<TaxStrategyResponse> getCurrentTaxStrategy() {
        try {
            String strategyName = payrollService.getCurrentTaxStrategyName();
            if (strategyName == null || strategyName.isEmpty()) {
                logger.warn("Tax strategy name is null or empty, returning default");
                return ResponseEntity.ok(new TaxStrategyResponse("Flat Tax Strategy"));
            }
            return ResponseEntity.ok(new TaxStrategyResponse(strategyName));
        } catch (Exception e) {
            logger.error("Error getting current tax strategy", e);
            return ResponseEntity.ok(new TaxStrategyResponse("Flat Tax Strategy"));
        }
    }
    
    /**
     * Get available tax strategies
     * 
     * GET /api/payroll/tax-strategies
     * 
     * @return TaxStrategiesResponse DTO with available tax strategies
     */
    @GetMapping("/tax-strategies")
    public ResponseEntity<TaxStrategiesResponse> getAvailableTaxStrategies() {
        try {
            Map<String, String> strategies = new HashMap<>();
            if (flatTaxStrategy != null) {
                strategies.put("flatTaxStrategy", flatTaxStrategy.getStrategyName());
            } else {
                strategies.put("flatTaxStrategy", "Flat Tax Strategy");
            }
            if (progressiveTaxStrategy != null) {
                strategies.put("progressiveTaxStrategy", progressiveTaxStrategy.getStrategyName());
            } else {
                strategies.put("progressiveTaxStrategy", "Progressive Tax Strategy");
            }
            return ResponseEntity.ok(new TaxStrategiesResponse(strategies));
        } catch (Exception e) {
            logger.error("Error getting available tax strategies", e);
            // Return default strategies on error
            Map<String, String> defaultStrategies = new HashMap<>();
            defaultStrategies.put("flatTaxStrategy", "Flat Tax Strategy");
            defaultStrategies.put("progressiveTaxStrategy", "Progressive Tax Strategy");
            return ResponseEntity.ok(new TaxStrategiesResponse(defaultStrategies));
        }
    }
    
    /**
     * Switch tax strategy
     * 
     * PUT /api/payroll/tax-strategy?strategy=flatTaxStrategy
     * PUT /api/payroll/tax-strategy?strategy=progressiveTaxStrategy
     * 
     * @param strategy Strategy name: "flatTaxStrategy" or "progressiveTaxStrategy"
     * @return TaxStrategySwitchResponse DTO with success message and strategy name, or error message
     */
    @PutMapping("/tax-strategy")
    public ResponseEntity<TaxStrategySwitchResponse> switchTaxStrategy(@RequestParam String strategy) {
        try {
            logger.info("Switching tax strategy to: {}", strategy);
            
            TaxCalculationStrategy selectedStrategy;
            switch (strategy.toLowerCase()) {
                case "flattaxstrategy":
                case "flat":
                    if (flatTaxStrategy == null) {
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                            .body(TaxStrategySwitchResponse.error("Flat tax strategy is not available"));
                    }
                    selectedStrategy = flatTaxStrategy;
                    break;
                case "progressivetaxstrategy":
                case "progressive":
                    if (progressiveTaxStrategy == null) {
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                            .body(TaxStrategySwitchResponse.error("Progressive tax strategy is not available"));
                    }
                    selectedStrategy = progressiveTaxStrategy;
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(TaxStrategySwitchResponse.error("Invalid strategy. Use 'flatTaxStrategy' or 'progressiveTaxStrategy'"));
            }
            
            payrollService.setTaxStrategy(selectedStrategy);
            
            TaxStrategySwitchResponse response = TaxStrategySwitchResponse.success(
                "Tax strategy updated successfully",
                selectedStrategy.getStrategyName()
            );
            
            logger.info("Tax strategy switched successfully to: {}", selectedStrategy.getStrategyName());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error switching tax strategy", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(TaxStrategySwitchResponse.error("Failed to switch tax strategy: " + e.getMessage()));
        }
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
    
    /**
     * Delete a paycheck
     * Only allowed if status is DRAFT
     * 
     * DELETE /api/payroll/paycheck/{paycheckId}
     * 
     * @param paycheckId ID of the paycheck to delete
     * @return DeletePaycheckResponse DTO with success status and message
     */
    @DeleteMapping("/paycheck/{paycheckId}")
    public ResponseEntity<DeletePaycheckResponse> deletePaycheck(@PathVariable Long paycheckId) {
        try {
            logger.info("Deleting paycheck ID: {}", paycheckId);
            
            payrollService.deletePaycheck(paycheckId);
            
            logger.info("Paycheck ID: {} deleted successfully", paycheckId);
            
            return ResponseEntity.ok(DeletePaycheckResponse.success("Paycheck deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting paycheck ID: {}", paycheckId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DeletePaycheckResponse.error("Failed to delete paycheck: " + e.getMessage()));
        }
    }
    
    /**
     * Update paycheck status
     * 
     * PUT /api/payroll/paycheck/{paycheckId}/status?status=PAID
     * 
     * @param paycheckId ID of the paycheck
     * @param status New status (DRAFT, PENDING, PAID, VOIDED)
     * @return Updated PaycheckDTO
     */
    @PutMapping("/paycheck/{paycheckId}/status")
    public ResponseEntity<PaycheckDTO> updatePaycheckStatus(
        @PathVariable Long paycheckId,
        @RequestParam PaycheckStatus status
    ) {
        logger.info("Updating paycheck ID: {} status to: {}", paycheckId, status);
        
        PaycheckDTO updated = payrollService.updatePaycheckStatus(paycheckId, status);
        
        logger.info("Paycheck ID: {} status updated successfully", paycheckId);
        
        return ResponseEntity.ok(updated);
    }
}