package edu.neu.csye6200.util.csv;

import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.model.domain.Training;
import edu.neu.csye6200.model.domain.BusinessPerson;
import edu.neu.csye6200.model.domain.PersonStatus;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.repository.EmployeeRepository;
import edu.neu.csye6200.repository.EmployerRepository;
import edu.neu.csye6200.repository.TrainingRepository;
import edu.neu.csye6200.repository.BusinessPersonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseSeeder implements CommandLineRunner {

  private static final CSVParser CSV_PARSER = new CSVParserImpl();
  private static final StringConverter CONVERTER = new StringConverterImpl();
  private static final String CSV_PREFIX = "data/csv";

  @Value("${app.seed.enabled:true}")
  private boolean seedEnabled;

  private final BusinessRepository businessRepo;
  private final EmployeeRepository employeeRepo;
  private final EmployerRepository employerRepo;
  private final TrainingRepository trainingRepo;
  private final BusinessPersonRepository businessPersonRepo;

  private final Map<Long, Company> companyCache = new HashMap<>();
  private final Map<Long, Employer> employerCache = new HashMap<>();
  private final Map<Long, Employee> employeeCache = new HashMap<>();

  public DatabaseSeeder(
      BusinessRepository businessRepo,
      EmployeeRepository employeeRepo,
      EmployerRepository employerRepo,
      TrainingRepository trainingRepo,
      BusinessPersonRepository businessPersonRepo
  ) {
    this.businessRepo = businessRepo;
    this.employeeRepo = employeeRepo;
    this.employerRepo = employerRepo;
    this.trainingRepo = trainingRepo;
    this.businessPersonRepo = businessPersonRepo;
  }

  @Override
  public void run(String... args) {
    if (!seedEnabled) {
      System.out.println("Database seeding is disabled");
      return;
    }

    System.out.println("Starting database seeding...");
    seedCompanies();
    seedEmployers();
    seedEmployees();
    seedTrainings();
    System.out.println("Database seeding completed");
  }

  private void seedCompanies() {
    if (businessRepo.count() > 0) {
      System.out.println("Skipping companies - table already has data");
      loadExistingCompanies();
      return;
    }

    try {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(
              new ClassPathResource(CSV_PREFIX + "/businesses.csv").getInputStream()
          )
      );

      List<Map<String, String>> rows = CSV_PARSER.parse(reader);

      if (rows != null && !rows.isEmpty()) {
        int count = 0;
        for (Map<String, String> row : rows) {
          Long csvId = CONVERTER.toLong(row.get("id"));
          Company company = mapToCompany(row);
          Company saved = businessRepo.save(company);

          if (csvId != null) {
            companyCache.put(csvId, saved);
          }
          count++;
        }
        System.out.println("Seeded " + count + " companies");
      }
    } catch (Exception e) {
      System.err.println("Failed to seed companies: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void loadExistingCompanies() {
    List<Company> companies = businessRepo.findAll();
    for (Company company : companies) {
      companyCache.put(company.getId(), company);
    }
  }

  private Company mapToCompany(Map<String, String> row) {
    return Company.builder()
        .name(CONVERTER.toString(row.get("name")))
        .address(CONVERTER.toString(row.get("address")))
        .industry(CONVERTER.toString(row.get("industry")))
        .foundedDate(CONVERTER.toLocalDate(row.get("founded_date")))
        .build();
  }

  private void seedEmployers() {
    if (employerRepo.count() > 0) {
      System.out.println("Skipping employers - table already has data");
      loadExistingEmployers();
      return;
    }

    try {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(
              new ClassPathResource(CSV_PREFIX + "/employers.csv").getInputStream()
          )
      );

      List<Map<String, String>> rows = CSV_PARSER.parse(reader);

      if (rows != null && !rows.isEmpty()) {
        int count = 0;
        for (Map<String, String> row : rows) {
          Long csvId = CONVERTER.toLong(row.get("id"));
          Employer employer = mapToEmployer(row);
          Employer saved = employerRepo.save(employer);

          if (csvId != null) {
            employerCache.put(csvId, saved);
          }
          count++;
        }
        System.out.println("Seeded " + count + " employers");
      }
    } catch (Exception e) {
      System.err.println("Failed to seed employers: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void loadExistingEmployers() {
    List<Employer> employers = employerRepo.findAll();
    for (Employer employer : employers) {
      employerCache.put(employer.getId(), employer);
    }
  }

  private Employer mapToEmployer(Map<String, String> row) {
    Employer employer = new Employer(
        CONVERTER.toString(row.get("name")),
        CONVERTER.toString(row.get("email")),
        CONVERTER.toString(row.get("password")),
        CONVERTER.toDouble(row.get("salary")),
        CONVERTER.toString(row.get("department")),
        CONVERTER.toString(row.get("title"))
    );

    LocalDate hireDate = CONVERTER.toLocalDate(row.get("hire_date"));
    if (hireDate != null) {
      employer.setHireDate(hireDate);
    }

    Long companyId = CONVERTER.toLong(row.get("company_id"));
    if (companyId != null) {
      Company company = companyCache.get(companyId);
      if (company != null) {
        employer.setCompany(company);
      }
    }

    String status = CONVERTER.toString(row.get("status"));
    if (status != null && !status.isEmpty()) {
      try {
        employer.setStatus(PersonStatus.valueOf(status));
      } catch (IllegalArgumentException e) {
        System.err.println("Invalid status: " + status);
      }
    }

    return employer;
  }

  private void seedEmployees() {
    if (employeeRepo.count() > 0) {
      System.out.println("Skipping employees - table already has data");
      loadExistingEmployees();
      return;
    }

    try {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(
              new ClassPathResource(CSV_PREFIX + "/employees.csv").getInputStream()
          )
      );

      List<Map<String, String>> rows = CSV_PARSER.parse(reader);

      if (rows != null && !rows.isEmpty()) {
        int count = 0;
        for (Map<String, String> row : rows) {
          Long csvId = CONVERTER.toLong(row.get("id"));
          Employee employee = mapToEmployee(row);
          Employee saved = employeeRepo.save(employee);

          if (csvId != null) {
            employeeCache.put(csvId, saved);
          }
          count++;
        }
        System.out.println("Seeded " + count + " employees");
      }
    } catch (Exception e) {
      System.err.println("Failed to seed employees: " + e.getMessage());
    }
  }

  private void loadExistingEmployees() {
    List<Employee> employees = employeeRepo.findAll();
    for (Employee employee : employees) {
      employeeCache.put(employee.getId(), employee);
    }
  }

  private Employee mapToEmployee(Map<String, String> row) {
    Employee employee = new Employee(
        CONVERTER.toString(row.get("name")),
        CONVERTER.toString(row.get("email")),
        CONVERTER.toString(row.get("password")),
        CONVERTER.toDouble(row.get("salary")),
        CONVERTER.toString(row.get("position"))
    );

    LocalDate hireDate = CONVERTER.toLocalDate(row.get("hire_date"));
    if (hireDate != null) {
      employee.setHireDate(hireDate);
    }

    Long companyId = CONVERTER.toLong(row.get("company_id"));
    if (companyId != null) {
      Company company = companyCache.get(companyId);
      if (company != null) {
        employee.setCompany(company);
      }
    }

    Long managerId = CONVERTER.toLong(row.get("manager_id"));
    if (managerId != null) {
      Employer manager = employerCache.get(managerId);
      if (manager != null) {
        employee.setManager(manager);
      }
    }

    String status = CONVERTER.toString(row.get("status"));
    if (status != null && !status.isEmpty()) {
      try {
        employee.setStatus(PersonStatus.valueOf(status));
      } catch (IllegalArgumentException e) {
        System.err.println("Invalid status: " + status);
      }
    }

    return employee;
  }

  private void seedTrainings() {
    if (trainingRepo.count() > 0) {
      System.out.println("Skipping trainings - table already has data");
      return;
    }

    try {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(
              new ClassPathResource(CSV_PREFIX + "/trainings.csv").getInputStream()
          )
      );

      List<Map<String, String>> rows = CSV_PARSER.parse(reader);

      if (rows != null && !rows.isEmpty()) {
        int count = 0;
        int skipped = 0;

        for (Map<String, String> row : rows) {
          Training training = new Training(
              CONVERTER.toString(row.get("training_name")),
              CONVERTER.toString(row.get("description")),
              CONVERTER.toLocalDate(row.get("completion_date")),
              CONVERTER.toLocalDate(row.get("expiry_date")),
              CONVERTER.toBoolean(row.get("is_required"))
          );

          Long personId = CONVERTER.toLong(row.get("person_id"));
          if (personId != null) {
            BusinessPerson person = findPersonById(personId);

            if (person != null) {
              training.setPerson(person);
              trainingRepo.save(training);
              count++;
            } else {
              System.err.println("Person not found for training, person_id: " + personId);
              skipped++;
            }
          } else {
            trainingRepo.save(training);
            count++;
          }
        }
        System.out.println("Seeded " + count + " trainings" + (skipped > 0 ? " (skipped " + skipped + ")" : ""));
      }
    } catch (Exception e) {
      System.err.println("Failed to seed trainings: " + e.getMessage());
    }
  }

  private BusinessPerson findPersonById(Long personId) {
    Employee employee = employeeCache.get(personId);
    if (employee != null) {
      return employee;
    }

    Employer employer = employerCache.get(personId);
    if (employer != null) {
      return employer;
    }

    return businessPersonRepo.findById(personId).orElse(null);
  }
}