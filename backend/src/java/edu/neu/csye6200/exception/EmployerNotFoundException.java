package edu.neu.csye6200.exception;

public class EmployerNotFoundException extends RuntimeException {
  public EmployerNotFoundException(Long employerId) {
    super("Employer with id " + employerId + " not found");
  }

  public EmployerNotFoundException(String message) {
    super(message);
  }
}
