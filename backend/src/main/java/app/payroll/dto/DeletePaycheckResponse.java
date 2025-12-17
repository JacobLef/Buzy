package app.payroll.dto;

/**
 * DTO for delete paycheck response
 *
 * @author Qing Mi
 */
public record DeletePaycheckResponse(boolean success, String message) {
  /**
   * Create a success response
   */
  public static DeletePaycheckResponse success(String message) {
    return new DeletePaycheckResponse(true, message);
  }

  /**
   * Create an error response
   */
  public static DeletePaycheckResponse error(String message) {
    return new DeletePaycheckResponse(false, message);
  }
}
