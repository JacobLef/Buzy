package app.business;

/**
 * Exception thrown when business validation fails
 *
 * @author Qing Mi
 */
public class BusinessValidationException extends RuntimeException {

  public BusinessValidationException(String message) {
    super(message);
  }

  public BusinessValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
