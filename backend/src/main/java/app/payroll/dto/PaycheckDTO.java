package app.payroll.dto;

import java.time.LocalDate;

import app.payroll.PaycheckStatus;

/**
 * DTO for Paycheck response
 *
 * @author Qing Mi
 */
public record PaycheckDTO(
    Long id,
    Long employeeId,
    String employeeName,
    Double grossPay, // Base
    // salary
    // (before
    // bonus)
    Double bonus, // Bonus amount (null if no bonus)
    Double taxDeduction,
    Double insuranceDeduction,
    Double totalDeductions,
    Double netPay,
    LocalDate payDate,
    String taxStrategyUsed,
    PaycheckStatus status // Status: DRAFT, PENDING,
    // PAID, VOIDED
    ) {
  public PaycheckDTO {
    // Calculate totalDeductions if not provided
    if (totalDeductions == null) {
      totalDeductions = taxDeduction + insuranceDeduction;
    }
    // Default status to DRAFT if not provided
    if (status == null) {
      status = PaycheckStatus.DRAFT;
    }
  }
}
