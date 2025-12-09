package edu.neu.csye6200.exception;

public class BusinessNotFoundException extends RuntimeException {
  public BusinessNotFoundException(Long id) {
    super("Could not find business with id " + id);
  }

  public BusinessNotFoundException(String message) {
    super(message);
  }
}
