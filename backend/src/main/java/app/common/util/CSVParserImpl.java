package app.common.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of a CSV Parser such that this class, acting as a functional object,
 * takes input of a path to a CSV file, parses said CSV file, and generates a List of al of the
 * rows of the given CSV file.
 *
 * @author jacoblefkowitz
 */
public class CSVParserImpl implements CSVParser {
  @Override
  public List<Map<String, String>> parse(BufferedReader reader) {
    List<Map<String, String>> results = new ArrayList<>();

    try {
      String headerLine = reader.readLine();
      if (headerLine == null || headerLine.isEmpty()) {
        return results;
      }

      String[] headers = headerLine.split(",");

      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) {
          continue;
        }

        String[] values = line.split(",", -1);
        Map<String, String> row = new HashMap<>();

        for (int i = 0; i < headers.length; i++) {
          String value = i < values.length ? values[i].trim() : "";
          row.put(headers[i].trim(), value);
        }

        results.add(row);
      }
    } catch (IOException e) {
      System.err.println("Error reading file");
      return null;
    }

    return results;
  }
}
