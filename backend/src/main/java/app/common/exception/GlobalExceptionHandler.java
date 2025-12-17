package app.common.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import app.auth.InvalidCredentialsException;
import app.auth.InvalidTokenException;
import app.business.BusinessNotFoundException;
import app.business.BusinessValidationException;
import app.business.InvalidBusinessException;
import app.common.dto.ErrorResponse;
import app.employee.EmployeeNotFoundException;
import app.employer.EmployerNotFoundException;
import app.payroll.PayrollCalculationException;
import app.user.UserDisabledException;
import app.user.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String NOT_FOUND = "NOT FOUND";

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
      InvalidCredentialsException e) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), "Unauthorized", e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), "Unauthorized", e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
  }

  @ExceptionHandler(UserDisabledException.class)
  public ResponseEntity<ErrorResponse> handleUserDisabledException(UserDisabledException e) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), "Forbidden", e.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
  }

  @ExceptionHandler(EmployerNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEmployerNotFoundException(
      EmployerNotFoundException e) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
  }

  @ExceptionHandler(EmployeeNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEmployeeNotFoundException(
      EmployeeNotFoundException e) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
  }

  @ExceptionHandler(BusinessNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleBusinessNotFoundException(
      BusinessNotFoundException e) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
  }

  @ExceptionHandler(InvalidBusinessException.class)
  public ResponseEntity<ErrorResponse> handleInvalidBusinessException(InvalidBusinessException e) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException e, WebRequest request) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            request.getDescription(false).replace("uri=", ""),
            e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
  }

  @ExceptionHandler(PayrollCalculationException.class)
  public ResponseEntity<ErrorResponse> handlePayrollCalculationException(
      PayrollCalculationException e, WebRequest request) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getDescription(false).replace("uri=", ""),
            "Payroll calculation failed: " + e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
  }

  @ExceptionHandler(BusinessValidationException.class)
  public ResponseEntity<ErrorResponse> handleBusinessValidationException(
      BusinessValidationException e, WebRequest request) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false).replace("uri=", ""),
            e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    ErrorResponse res =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
  }
}
