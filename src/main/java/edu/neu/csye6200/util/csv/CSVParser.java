package edu.neu.csye6200.util.csv;

/**
 * Functional interface for CSV parsing operations
 * 
 * @author Team 10
 */
@FunctionalInterface
public interface CSVParser {
	void parse(String filePath);
}

