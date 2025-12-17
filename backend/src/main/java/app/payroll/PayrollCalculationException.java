package app.payroll;

/**
 * Exception thrown when payroll calculation fails
 *
 * @author Qing Mi
 */
public class PayrollCalculationException extends RuntimeException {

  private final Long employeeId;

  public PayrollCalculationException(String message, Long employeeId) {
    super(message);
    this.employeeId = employeeId;
  }

  public PayrollCalculationException(String message, Long employeeId, Throwable cause) {
    super(message, cause);
    this.employeeId = employeeId;
  }

  public Long getEmployeeId() {
    return employeeId;
  }
}
