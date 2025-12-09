package edu.neu.csye6200.exception;

public class UserDisabledException extends RuntimeException {
  public UserDisabledException() {
    super("User account is disabled");
  }
}