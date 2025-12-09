package app.exception;

public class UserDisabledException extends RuntimeException {
  public UserDisabledException() {
    super("User account is disabled");
  }
}