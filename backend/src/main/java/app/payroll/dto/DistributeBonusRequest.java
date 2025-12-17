package app.payroll.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * Request DTO for distributing bonuses to employees
 *
 * @author Qing Mi
 */
public record DistributeBonusRequest(

    @NotNull(message = "Business ID is required") Long businessId,

    @NotNull(message = "Bonus amount is required") @Positive(message = "Bonus amount must be positive") Double bonusAmount,

    // Optional: filter by specific employee IDs
    List<Long> employeeIds,

    // Optional: filter by department
    String department,

    // Optional: description for the bonus
    String description) {
  // Compact constructor for additional validation
  public DistributeBonusRequest {
    if (bonusAmount != null && bonusAmount <= 0) {
      throw new IllegalArgumentException("Bonus amount must be positive");
    }
  }
}
