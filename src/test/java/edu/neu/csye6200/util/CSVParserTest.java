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

  private static final String BUSINESS_CSV = "src/test/java/edu/neu/csye6200/util/tester_csv/business-test.csv";
  private static final String EMPLOYERS_CSV = "src/test/java/edu/neu/csye6200/util/tester_csv/employers-test.csv";
  private static final String EMPLOYEES_CSV = "src/test/java/edu/neu/csye6200/util/tester_csv/employees-test.csv";
  private static final String TRAINING_CSV = "src/test/java/edu/neu/csye6200/util/tester_csv/training-test.csv";

  private static final CSVParser csvParser = new CSVParserImpl();

  @Test
  public void testParseBusinessCSV() {
    List<Map<String, String>> businesses = null;
    try {
      businesses = csvParser.parse(
          new BufferedReader(new FileReader(BUSINESS_CSV))
      );
    } catch (FileNotFoundException e) {
      Assertions.fail("Cannot find file path: " + BUSINESS_CSV);
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
  public void testParseEmployersCSV() {
    List<Map<String, String>> employers = null;
    try {
      employers = csvParser.parse(
          new BufferedReader(new FileReader(EMPLOYERS_CSV))
      );
    } catch (FileNotFoundException e) {
      Assertions.fail("Cannot find file path: " + EMPLOYERS_CSV);
    }

    assertNotNull(employers);
    assertEquals(3, employers.size());

    Map<String, String> employer1 = employers.get(0);
    assertEquals("1", employer1.get("id"));
    assertEquals("Sarah Chen", employer1.get("name"));
    assertEquals("sarah.chen@techstart.com", employer1.get("email"));
    assertEquals("150000", employer1.get("salary"));
    assertEquals("1", employer1.get("company_id"));
    assertEquals("Engineering", employer1.get("department"));
    assertEquals("CTO", employer1.get("title"));
    assertEquals("2018-03-15", employer1.get("hire_date"));
    assertEquals("Active", employer1.get("status"));

    Map<String, String> employer2 = employers.get(1);
    assertEquals("4", employer2.get("id"));
    assertEquals("Jennifer Walsh", employer2.get("name"));
    assertEquals("Chief Medical Officer", employer2.get("title"));
    assertEquals("140000", employer2.get("salary"));
  }

  @Test
  public void testParseEmployeesCSV() {
    List<Map<String, String>> employees = null;
    try {
      employees = csvParser.parse(
          new BufferedReader(new FileReader(EMPLOYEES_CSV))
      );
    } catch (FileNotFoundException e) {
      Assertions.fail("Cannot find file path: " + EMPLOYEES_CSV);
    }

    assertNotNull(employees);
    assertEquals(4, employees.size());

    Map<String, String> employee1 = employees.get(0);
    assertEquals("2", employee1.get("id"));
    assertEquals("Michael Rodriguez", employee1.get("name"));
    assertEquals("michael.r@techstart.com", employee1.get("email"));
    assertEquals("95000", employee1.get("salary"));
    assertEquals("1", employee1.get("manager_id"));
    assertEquals("2020-06-15", employee1.get("hire_date"));
    assertEquals("Senior Software Engineer", employee1.get("position"));
    assertEquals("Active", employee1.get("status"));

    Map<String, String> employee2 = employees.get(1);
    assertEquals("3", employee2.get("id"));
    assertEquals("Emily Thompson", employee2.get("name"));
    assertEquals("75000", employee2.get("salary"));
    assertEquals("Junior Developer", employee2.get("position"));
  }

  @Test
  public void testParseTrainingCSV() {
    List<Map<String, String>> trainings = null;
    try {
      trainings = csvParser.parse(
          new BufferedReader(new FileReader(TRAINING_CSV))
      );
    } catch (FileNotFoundException e) {
      Assertions.fail("Cannot find file path: " + TRAINING_CSV);
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
  public void testEmployerHasSalaryAndHireDate() {
    List<Map<String, String>> employers = null;
    try {
      employers = csvParser.parse(
          new BufferedReader(new FileReader(EMPLOYERS_CSV))
      );
    } catch (FileNotFoundException e) {
      Assertions.fail("Cannot find file path: " + EMPLOYERS_CSV);
    }

    for (Map<String, String> employer : employers) {
      assertNotNull(employer.get("salary"), "Employer should have salary");
      assertFalse(employer.get("salary").isEmpty(), "Employer salary should not be empty");
      assertNotNull(employer.get("hire_date"), "Employer should have hire_date");
      assertFalse(employer.get("hire_date").isEmpty(), "Employer hire_date should not be empty");
    }
  }

  @Test
  public void testEmployeeHasSalaryAndHireDate() {
    List<Map<String, String>> employees = null;
    try {
      employees = csvParser.parse(
          new BufferedReader(new FileReader(EMPLOYEES_CSV))
      );
    } catch (FileNotFoundException e) {
      Assertions.fail("Cannot find file path: " + EMPLOYEES_CSV);
    }

    for (Map<String, String> employee : employees) {
      assertNotNull(employee.get("salary"), "Employee should have salary");
      assertFalse(employee.get("salary").isEmpty(), "Employee salary should not be empty");
      assertNotNull(employee.get("hire_date"), "Employee should have hire_date");
      assertFalse(employee.get("hire_date").isEmpty(), "Employee hire_date should not be empty");
    }
  }

  @Test
  public void testCSVHeaderParsing() {
    List<Map<String, String>> businesses = null;
    try {
      businesses = csvParser.parse(
          new BufferedReader(new FileReader(BUSINESS_CSV))
      );
    } catch (FileNotFoundException e) {
      Assertions.fail("Cannot find file path: " + BUSINESS_CSV);
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
    List<Map<String, String>> employers = null;
    List<Map<String, String>> employees = null;
    List<Map<String, String>> trainings = null;

    try {
      businesses = csvParser.parse(new BufferedReader(new FileReader(BUSINESS_CSV)));
      employers = csvParser.parse(new BufferedReader(new FileReader(EMPLOYERS_CSV)));
      employees = csvParser.parse(new BufferedReader(new FileReader(EMPLOYEES_CSV)));
      trainings = csvParser.parse(new BufferedReader(new FileReader(TRAINING_CSV)));
    } catch (IOException e) {
      Assertions.fail("Cannot find one of the CSV files");
    }

    assertEquals(3, businesses.size(), "Should have 3 businesses");
    assertEquals(3, employers.size(), "Should have 3 employers");
    assertEquals(4, employees.size(), "Should have 4 employees");
    assertEquals(10, trainings.size(), "Should have 10 trainings");
  }
}