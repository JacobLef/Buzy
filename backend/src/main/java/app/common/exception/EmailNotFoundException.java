package app.common.exception;

import app.business.BusinessPerson;
import app.user.User;

/**
 * Custom Exception that is thrown whenever the email of a {@link User} or {@link BusinessPerson}
 * has an email which cannot be found.
 */
public class EmailNotFoundException extends RuntimeException {
  public EmailNotFoundException(String message) {
    super(message);
  }

  public EmailNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
