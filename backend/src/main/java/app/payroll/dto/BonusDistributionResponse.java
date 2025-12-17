package app.payroll.dto;

import java.util.List;

/**
 * Response DTO for bulk bonus distribution
 *
 * @author Qing Mi
 */
public record BonusDistributionResponse(Long businessId, String businessName,
    Integer totalEmployeesProcessed, Integer successfulPaychecks, Integer failedPaychecks,
    Double totalGrossPaid, Double totalNetPaid, Double totalTaxDeducted,
    Double totalInsuranceDeducted, List<PaycheckDTO> paychecks, List<String> errors) {
  /**
   * Create a successful response with no errors
   */
  public static BonusDistributionResponse success(Long businessId, String businessName,
      List<PaycheckDTO> paychecks) {
    // Calculate total gross paid: base salary + bonus for each paycheck
    double totalGross = paychecks.stream()
        .mapToDouble(p -> p.grossPay() + (p.bonus() != null ? p.bonus() : 0.0)).sum();

    double totalNet = paychecks.stream().mapToDouble(PaycheckDTO::netPay).sum();

    double totalTax = paychecks.stream().mapToDouble(PaycheckDTO::taxDeduction).sum();

    double totalInsurance = paychecks.stream().mapToDouble(PaycheckDTO::insuranceDeduction).sum();

    return new BonusDistributionResponse(businessId, businessName, paychecks.size(),
        paychecks.size(), 0, totalGross, totalNet, totalTax, totalInsurance, paychecks, List.of());
  }
}
