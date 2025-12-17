package app.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Concrete implementation of a StringConverter.
 *
 * @author jacoblefkowitz
 */
public class StringConverterImpl implements StringConverter {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
  private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  @Override
  public LocalDate toLocalDate(String dateStr) {
    if (dateStr == null || dateStr.trim().isEmpty()) {
      return null; // Return null for empty dates, don't auto-set to current date
    }
    try {
      return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
    } catch (DateTimeParseException e) {
      System.err.println("Failed to parse date: " + dateStr + ", returning null");
      return null; // Return null on parse error instead of current date
    }
  }

  @Override
  public LocalDateTime toLocalDateTime(String dateTimeStr) {
    if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
      return LocalDateTime.now();
    }
    try {
      return LocalDateTime.parse(dateTimeStr.trim(), DATETIME_FORMATTER);
    } catch (DateTimeParseException e) {
      System.err.println("Failed to parse datetime: " + dateTimeStr + ", using current datetime");
      return LocalDateTime.now();
    }
  }

  @Override
  public Double toDouble(String value) {
    if (value == null || value.trim().isEmpty()) {
      return 0.0;
    }
    try {
      return Double.parseDouble(value.trim());
    } catch (NumberFormatException e) {
      System.err.println("Failed to parse double: " + value + ", using 0.0");
      return 0.0;
    }
  }

  @Override
  public Long toLong(String value) {
    if (value == null || value.trim().isEmpty()) {
      return 0L;
    }
    try {
      return Long.parseLong(value.trim());
    } catch (NumberFormatException e) {
      System.err.println("Failed to parse long: " + value + ", using 0");
      return 0L;
    }
  }

  @Override
  public Integer toInteger(String value) {
    if (value == null || value.trim().isEmpty()) {
      return 0;
    }
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      System.err.println("Failed to parse integer: " + value + ", using 0");
      return 0;
    }
  }

  @Override
  public Boolean toBoolean(String value) {
    if (value == null || value.trim().isEmpty()) {
      return false;
    }
    String trimmed = value.trim().toLowerCase();
    return trimmed.equals("true") || trimmed.equals("1") || trimmed.equals("yes");
  }

  @Override
  public String toString(String value) {
    if (value == null) {
      return "";
    }
    return value.trim();
  }
}
