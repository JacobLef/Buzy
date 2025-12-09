package edu.neu.csye6200.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String email) {
    super("User not found: " + email);
  }
}
