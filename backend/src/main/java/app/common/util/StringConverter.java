package app.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Converts any of the given string values into its respective type and parsed
 * value.
 *
 * @implNote If there is an error parsing the given string value for any of the
 *           inputs, then the default value for that type is returned (now for
 *           time, 0 for numerical values, false for booleans, and an empty
 *           string for Strings).
 *
 * @author jacoblefkowitz
 */
public interface StringConverter {
	LocalDate toLocalDate(String dateStr);

	LocalDateTime toLocalDateTime(String dateTimeStr);

	Double toDouble(String value);

	Long toLong(String value);

	Integer toInteger(String value);

	Boolean toBoolean(String value);

	String toString(String value);

}
