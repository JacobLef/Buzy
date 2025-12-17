package app.common.util;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

/**
 * Functional interface for CSV parsing operations.
 *
 * @author jacoblefkowitz
 */
@FunctionalInterface
public interface CSVParser {
  /**
   * Parses the CSV file at the provided file path.
   *
   * @param reader the BufferedReader to be read from.
   * @return a list of all the rows in the given CSV file mapped to their specific header.
   * @implNote if there is any error found in finding the file or parsing the file, {@code null} is
   *     returned and a message is printed to the error console. No errors are ever propagated from
   *     this Functional Interface.
   */
  List<Map<String, String>> parse(BufferedReader reader);
}
