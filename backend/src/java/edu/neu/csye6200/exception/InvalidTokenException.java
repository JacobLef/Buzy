package edu.neu.csye6200.exception;

public class InvalidTokenException extends RuntimeException {
  public InvalidTokenException() {
    super("Invalid or expired token");
  }
}
