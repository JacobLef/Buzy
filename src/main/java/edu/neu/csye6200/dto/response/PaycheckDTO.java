package edu.neu.csye6200.dto.response;

import java.time.LocalDate;

/**
 * DTO for Paycheck response
 * 
 * @author Qing Mi
 */
public record PaycheckDTO(
    Long id,
    Long employeeId,
    String employeeName,
    Double grossPay,              // Base salary (before bonus)
    Double bonus,                 // Bonus amount (null if no bonus)
    Double taxDeduction,
    Double insuranceDeduction,
    Double totalDeductions,
    Double netPay,
    LocalDate payDate,
    String taxStrategyUsed
) {
    public PaycheckDTO {
        // Calculate totalDeductions if not provided
        if (totalDeductions == null) {
            totalDeductions = taxDeduction + insuranceDeduction;
        }
    }
}