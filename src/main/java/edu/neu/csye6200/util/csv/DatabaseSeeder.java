package edu.neu.csye6200.util.csv;

import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.model.domain.Training;
import edu.neu.csye6200.model.domain.PersonStatus;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.repository.EmployeeRepository;
import edu.neu.csye6200.repository.EmployerRepository;
import edu.neu.csye6200.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Given the repositories, the Database seeder seeds the database with initial values for a
 * production demo. This class runs at startup of the application.
 *
 * @author jacoblefkowitz
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

  private static final CSVParser CSV_PARSER = new CSVParserImpl();
  private static final TableSeederFactory SEED_FACTORY = new TableSeederFactory();
  private static final StringConverter CONVERTER = new StringConverterImpl();
  private static final String CSV_PREFIX = "data/csv";

  @Value("${app.seed.enabled:true}")
  private boolean seedEnabled;

  private final List<TableSeeder<?>> seeders;

  private final BusinessRepository businessRepo;
  private final EmployerRepository employerRepo;

  private final Map<Long, Company> businessCache = new HashMap<>();
  private final Map<Long, Employer> employerCache = new HashMap<>();

  public DatabaseSeeder(
      BusinessRepository businessRepo,
      EmployeeRepository employeeRepo,
      EmployerRepository employerRepo,
      TrainingRepository trainingRepo
  ) {
    this.businessRepo = businessRepo;
    this.employerRepo = employerRepo;

    this.seeders = List.of(
        SEED_FACTORY.create(CSV_PREFIX + "/businesses.csv", businessRepo, this::mapToBusiness),
        SEED_FACTORY.create(CSV_PREFIX + "/employers.csv", employerRepo, this::mapToEmployer),
        SEED_FACTORY.create(CSV_PREFIX + "/employees.csv", employeeRepo, this::mapToEmployee),
        SEED_FACTORY.create(CSV_PREFIX + "/trainings.csv", trainingRepo, this::mapToTraining)
    );
  }

  @Override
  public void run(String... args) {
    if (!seedEnabled) {
      System.out.println("Database seeding is disabled");
      return;
    }

    System.out.println("Starting database seeding...");
    for (TableSeeder<?> seeder : seeders) {
      seeder.seed();
    }
    System.out.println("Database seeding completed");
  }

  private Company mapToBusiness(Map<String, String> row) {
    Company company = Company.builder()
        .name(CONVERTER.toString(row.get("name")))
        .address(CONVERTER.toString(row.get("address")))
        .industry(CONVERTER.toString(row.get("industry")))
        .foundedDate(CONVERTER.toLocalDate(row.get("founded_date")))
        .build();

    if (row.containsKey("id")) {
      Long id = CONVERTER.toLong(row.get("id"));
      if (id != null) {
        businessCache.put(id, company);
      }
    }

    return company;
  }

  private Employer mapToEmployer(Map<String, String> row) {
    Employer employer = new Employer(
        CONVERTER.toString(row.get("name")),
        CONVERTER.toString(row.get("email")),
        CONVERTER.toString(row.get("password")),
        CONVERTER.toString(row.get("department")),
        CONVERTER.toString(row.get("title"))
    );

    Long companyId = CONVERTER.toLong(row.get("company_id"));
    if (companyId != null) {
      Company company = businessCache.get(companyId);
      if (company == null) {
        company = businessRepo.findById(companyId).orElse(null);
      }
      if (company != null) {
        employer.setCompany(company);
      }
    }

    String status = CONVERTER.toString(row.get("status"));
    if (status != null) {
      employer.setStatus(PersonStatus.valueOf(status));
    }

    if (row.containsKey("id")) {
      Long id = CONVERTER.toLong(row.get("id"));
      if (id != null) {
        employerCache.put(id, employer);
      }
    }

    return employer;
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
      Company company = businessCache.get(companyId);
      if (company == null) {
        company = businessRepo.findById(companyId).orElse(null);
      }
      if (company != null) {
        employee.setCompany(company);
      }
    }

    Long managerId = CONVERTER.toLong(row.get("manager_id"));
    if (managerId != null) {
      Employer manager = employerCache.get(managerId);
      if (manager == null) {
        manager = employerRepo.findById(managerId).orElse(null);
      }
      if (manager != null) {
        employee.setManager(manager);
      }
    }

    String status = CONVERTER.toString(row.get("status"));
    if (status != null) {
      employee.setStatus(PersonStatus.valueOf(status));
    }

    return employee;
  }

  private Training mapToTraining(Map<String, String> row) {
    Training training = new Training(
        CONVERTER.toString(row.get("training_name")),
        CONVERTER.toString(row.get("description")),
        CONVERTER.toLocalDate(row.get("completion_date")),
        CONVERTER.toLocalDate(row.get("expiry_date")),
        CONVERTER.toBoolean(row.get("is_required"))
    );

    return training;
  }

  /**
   * Data structure used to store the information needed to seed a table properly. Any given
   * table seeder, acting more like a C-like struct data-structure with attached function
   * members, must define the functionality required to properly seed the correct repository in
   * this application.
   * @param csvPath the path to the CSV file whose information is used to seed the given repository.
   * @param repository the repository that will be seeded.
   * @param mapper the function that will convert the parsed CSV file's information into the
   *               appropriate Domain object model.
   * @param <T> the type of Domain object model which this TableSeeder will seed with.
   */
  private record TableSeeder<T>(
      String csvPath,
      JpaRepository<T, ?> repository,
      Function<Map<String, String>, T> mapper
  ) {
    public void seed() {
      if (repository.count() > 0) {
        System.out.println("Skipping " + csvPath + " - table already has data");
        return;
      }

      try {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                new ClassPathResource(csvPath).getInputStream()
            )
        );

        List<Map<String, String>> rows = CSV_PARSER.parse(reader);

        if (rows != null && !rows.isEmpty()) {
          List<T> entities = rows.stream()
              .map(mapper)
              .collect(Collectors.toUnmodifiableList());
          repository.saveAll(entities);
          System.out.println("Seeded " + entities.size() + " records from " + csvPath);
        } else {
          System.out.println("No data found in " + csvPath);
        }
      } catch (Exception e) {
        System.err.println("Failed to seed from " + csvPath + ": " + e.getMessage());
      }
    }
  }

  private static class TableSeederFactory {
    public <T> TableSeeder<T> create(
        String csvPath,
        JpaRepository<T, ?> repo,
        Function<Map<String, String>, T> mapper
    ) {
      return new TableSeeder<>(csvPath, repo, mapper);
    }
  }
}