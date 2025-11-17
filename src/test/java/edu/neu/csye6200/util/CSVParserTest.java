package edu.neu.csye6200.util;

import edu.neu.csye6200.util.csv.CSVParser;
import edu.neu.csye6200.util.csv.CSVParserImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tester class for CSVParser objects.
 */
public class CSVParserTest {
  private static final String business;
  private static final String employees;
  private static final String training;
  private static final CSVParser csvParser = new CSVParserImpl();

  static {
    business = "src/test/java/edu/neu/csye6200/util/tester_csv/business-test.csv";
    employees = "src/test/java/edu/neu/csye6200/util/tester_csv/employees-test.csv";
    training = "src/test/java/edu/neu/csye6200/util/tester_csv/training-test.csv";
  }

  @Test
  public void testParseBusinessCSV() {
    List<Map<String, String>> businesses = null;
    try {
      businesses = csvParser.parse(
          new BufferedReader(new FileReader(business))
      );
    } catch (FileNotFoundException e) {
      Assertions.fail("Cannot find file path: " + business);
    }

    assertNotNull(businesses);
    assertEquals(3, businesses.size());

    Map<String, String> firstBusiness = businesses.get(0);
    assertEquals("1", firstBusiness.get("id"));
    assertEquals("TechStart Solutions", firstBusiness.get("name"));
    assertEquals("123 Innovation Dr Boston MA", firstBusiness.get("address"));
    assertEquals("Technology", firstBusiness.get("industry"));
    assertEquals("2018-03-15", firstBusiness.get("founded_date"));

    Map<String, String> secondBusiness = businesses.get(1);
    assertEquals("2", secondBusiness.get("id"));
    assertEquals("HealthCare Plus", secondBusiness.get("name"));
    assertEquals("Healthcare", secondBusiness.get("industry"));

    Map<String, String> thirdBusiness = businesses.get(2);
    assertEquals("3", thirdBusiness.get("id"));
    assertEquals("Finance Pro LLC", thirdBusiness.get("name"));
    assertEquals("Finance", thirdBusiness.get("industry"));
  }

  @Test
  public void testParseEmployeesCSV() {
    List<Map<String, String>> people = null;
    try {
      people = csvParser.parse(
          new BufferedReader(new FileReader(employees))
      );
    } catch (FileNotFoundException e) {
      Assertions.fail("Cannot find file path: " + employees);
    }
    assertNotNull(people);
    assertEquals(7, people.size());

    Map<String, String> employer1 = people.get(0);
    assertEquals("1", employer1.get("id"));
    assertEquals("Employer", employer1.get("person_type"));
    assertEquals("Sarah Chen", employer1.get("name"));
    assertEquals("sarah.chen@techstart.com", employer1.get("email"));
    assertEquals("1", employer1.get("business_id"));
    assertEquals("Engineering", employer1.get("department"));
    assertEquals("CTO", employer1.get("title"));
    assertTrue(employer1.get("salary").isEmpty() || employer1.get("salary") == null);

    Map<String, String> employee1 = people.get(1);
    assertEquals("2", employee1.get("id"));
    assertEquals("Employee", employee1.get("person_type"));
    assertEquals("Michael Rodriguez", employee1.get("name"));
    assertEquals("95000", employee1.get("salary"));
    assertEquals("1", employee1.get("manager_id"));
    assertEquals("2020-06-15", employee1.get("hire_date"));
    assertEquals("Senior Software Engineer", employee1.get("position"));
    assertEquals("Active", employee1.get("status"));

    assertTrue(employee1.get("department").isEmpty() || employee1.get("department") == null);
    assertTrue(employee1.get("title").isEmpty() || employee1.get("title") == null);
  }

  @Test
  public void testParseTrainingCSV() {
    List<Map<String, String>> trainings = null;
    try {
      trainings = csvParser.parse(
          new BufferedReader(new FileReader(training))
      );
    } catch (FileNotFoundException e) {
      Assertions.fail("Cannot find file path: " + training);
    }

    assertNotNull(trainings);
    assertEquals(10, trainings.size());

    Map<String, String> training1 = trainings.get(0);
    assertEquals("1", training1.get("id"));
    assertEquals("Workplace Safety", training1.get("training_name"));
    assertEquals("OSHA workplace safety certification", training1.get("description"));
    assertEquals("2024-01-15", training1.get("completion_date"));
    assertEquals("2025-01-15", training1.get("expiry_date"));
    assertEquals("2", training1.get("person_id"));
    assertEquals("true", training1.get("is_required"));

    Map<String, String> training3 = trainings.get(2);
    assertEquals("3", training3.get("id"));
    assertEquals("Java Programming", training3.get("training_name"));
    assertEquals("false", training3.get("is_required"));

    assertEquals("2023-11-05", training3.get("completion_date"));
    assertEquals("2024-11-05", training3.get("expiry_date"));

    Map<String, String> hipaa = trainings.get(5);
    assertEquals("HIPAA Compliance", hipaa.get("training_name"));
    assertEquals("5", hipaa.get("person_id"));
  }

  @Test
  public void testParseEmptyFields() {
    List<Map<String, String>> people = null;
    try {
      people = csvParser.parse(
          new BufferedReader(new FileReader(employees))
      );
    } catch (FileNotFoundException e) {
      Assertions.fail("Cannot find file path: " + employees);
    }

    Map<String, String> employer = people.get(0);
    assertEquals("Employer", employer.get("person_type"));
    assertTrue(employer.get("salary") == null || employer.get("salary").isEmpty());
    assertTrue(employer.get("manager_id") == null || employer.get("manager_id").isEmpty());

    Map<String, String> employee = people.get(1);
    assertEquals("Employee", employee.get("person_type"));
    assertTrue(employee.get("department") == null || employee.get("department").isEmpty());
    assertTrue(employee.get("title") == null || employee.get("title").isEmpty());
  }

  @Test
  public void testCSVHeaderParsing() {
    List<Map<String, String>> businesses = null;
    try {
      businesses = csvParser.parse(
          new BufferedReader(new FileReader(business))
      );
    } catch (FileNotFoundException e) {
      Assertions.fail("Cannot find file path: " + business);
    }

    Map<String, String> firstBusiness = businesses.get(0);
    assertTrue(firstBusiness.containsKey("id"));
    assertTrue(firstBusiness.containsKey("name"));
    assertTrue(firstBusiness.containsKey("address"));
    assertTrue(firstBusiness.containsKey("industry"));
    assertTrue(firstBusiness.containsKey("founded_date"));
  }

  @Test
  public void testCSVRowCount() {
    List<Map<String, String>> businesses = null;
    List<Map<String, String>> people = null;
    List<Map<String, String>> trainings = null;

    try {
      businesses = csvParser.parse(
          new BufferedReader(new FileReader(business))
      );
      people = csvParser.parse(
          new BufferedReader(new FileReader(employees))
      );
      trainings = csvParser.parse(
          new BufferedReader(new FileReader(training))
      );
    } catch (IOException e) {
      Assertions.fail("Cannot find file path of one of: " + business
          + ", " + employees + ", " + training);
    }

    assertEquals(3, businesses.size(), "Should have 3 businesses");
    assertEquals(7, people.size(), "Should have 7 people");
    assertEquals(10, trainings.size(), "Should have 10 trainings");
  }
}