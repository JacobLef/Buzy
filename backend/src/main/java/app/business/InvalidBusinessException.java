package app.business;

/**
 * Custom exception for invalid business operations
 *
 * @author Team 10
 */
public class InvalidBusinessException extends RuntimeException {

  public InvalidBusinessException(String message) {
    super(message);
  }

  public InvalidBusinessException(String message, Throwable cause) {
    super(message, cause);
  }
}
