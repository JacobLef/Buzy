package edu.neu.csye6200.util.csv;

import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import edu.neu.csye6200.model.domain.Training;
import edu.neu.csye6200.model.domain.BusinessPerson;
import edu.neu.csye6200.model.domain.PersonStatus;
import edu.neu.csye6200.model.domain.User;
import edu.neu.csye6200.model.domain.UserRole;
import edu.neu.csye6200.model.payroll.Paycheck;
import edu.neu.csye6200.model.payroll.PaycheckStatus;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.repository.EmployeeRepository;
import edu.neu.csye6200.repository.EmployerRepository;
import edu.neu.csye6200.repository.TrainingRepository;
import edu.neu.csye6200.repository.BusinessPersonRepository;
import edu.neu.csye6200.repository.UserRepository;
import edu.neu.csye6200.repository.PaycheckRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
  private final UserRepository userRepo;
  private final PaycheckRepository paycheckRepo;
  private final PasswordEncoder passwordEncoder;

  private final Map<Long, Company> companyCache = new HashMap<>();
  private final Map<Long, Employer> employerCache = new HashMap<>();
  private final Map<Long, Employee> employeeCache = new HashMap<>();

  public DatabaseSeeder(
      BusinessRepository businessRepo,
      EmployeeRepository employeeRepo,
      EmployerRepository employerRepo,
      TrainingRepository trainingRepo,
      BusinessPersonRepository businessPersonRepo,
      UserRepository userRepo,
      PaycheckRepository paycheckRepo,
      PasswordEncoder passwordEncoder
  ) {
    this.businessRepo = businessRepo;
    this.employeeRepo = employeeRepo;
    this.employerRepo = employerRepo;
    this.trainingRepo = trainingRepo;
    this.businessPersonRepo = businessPersonRepo;
    this.userRepo = userRepo;
    this.paycheckRepo = paycheckRepo;
    this.passwordEncoder = passwordEncoder;
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
    seedUsers();
    seedTrainings();
    seedPaychecks();
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
        // Track first employer per company to set as owner if not specified in CSV
        Map<Long, Boolean> companyOwnerSet = new HashMap<>();
        
        for (Map<String, String> row : rows) {
          Long csvId = CONVERTER.toLong(row.get("id"));
          Long companyId = CONVERTER.toLong(row.get("company_id"));
          
          Employer employer = mapToEmployer(row);
          
          // If is_owner is not set in CSV and this is the first employer for this company,
          // set them as owner (and admin)
          Boolean isOwnerFromCSV = CONVERTER.toBoolean(row.get("is_owner"));
          if ((isOwnerFromCSV == null || !isOwnerFromCSV) && companyId != null) {
            if (!companyOwnerSet.containsKey(companyId)) {
              // Check if title contains CEO, Chief, President, or Managing Partner
              String title = CONVERTER.toString(row.get("title"));
              if (title != null && (
                  title.toLowerCase().contains("ceo") ||
                  title.toLowerCase().contains("chief") ||
                  title.toLowerCase().contains("president") ||
                  title.toLowerCase().contains("managing partner")
              )) {
                employer.setIsOwner(true);
                employer.setIsAdmin(true);
                companyOwnerSet.put(companyId, true);
                System.out.println("Auto-set " + employer.getName() + " as Owner for company " + companyId);
              } else {
                // Set first employer as owner if no CEO/Chief found
                employer.setIsOwner(true);
                employer.setIsAdmin(true);
                companyOwnerSet.put(companyId, true);
                System.out.println("Auto-set first employer " + employer.getName() + " as Owner for company " + companyId);
              }
            }
          } else if (isOwnerFromCSV != null && isOwnerFromCSV) {
            companyOwnerSet.put(companyId, true);
          }
          
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

    // Set is_admin field
    Boolean isAdmin = CONVERTER.toBoolean(row.get("is_admin"));
    if (isAdmin != null) {
      employer.setIsAdmin(isAdmin);
    }

    // Set is_owner field
    Boolean isOwner = CONVERTER.toBoolean(row.get("is_owner"));
    if (isOwner != null) {
      employer.setIsOwner(isOwner);
      // Owner is automatically an admin
      if (isOwner) {
        employer.setIsAdmin(true);
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
        // First pass: Create all employees without manager relationships
        Map<Long, Long> managerRelationships = new HashMap<>();
        for (Map<String, String> row : rows) {
          Long csvId = CONVERTER.toLong(row.get("id"));
          Long managerId = CONVERTER.toLong(row.get("manager_id"));
          
          // Store manager relationship for later
          if (csvId != null && managerId != null) {
            managerRelationships.put(csvId, managerId);
          }
          
          Employee employee = mapToEmployee(row);
          // Don't set manager in first pass
          employee.setManager(null);
          Employee saved = employeeRepo.save(employee);

          if (csvId != null) {
            employeeCache.put(csvId, saved);
          }
          count++;
        }
        
        // Second pass: Set manager relationships
        for (Map.Entry<Long, Long> entry : managerRelationships.entrySet()) {
          Long employeeId = entry.getKey();
          Long managerId = entry.getValue();
          
          Employee employee = employeeCache.get(employeeId);
          if (employee != null) {
            // Try to find manager in employer cache first, then employee cache
            BusinessPerson manager = employerCache.get(managerId);
            if (manager == null) {
              manager = employeeCache.get(managerId);
            }
            if (manager != null) {
              employee.setManager(manager);
              employeeRepo.save(employee);
            } else {
              System.err.println("Warning: Manager not found for employee ID " + employeeId + ", manager_id: " + managerId);
            }
          }
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

    // Manager relationship will be set in second pass after all employees are created
    // This allows employees to manage other employees

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

  private void seedUsers() {
    if (userRepo.count() > 0) {
      System.out.println("Skipping users - table already has data");
      return;
    }

    try {
      int userCount = 0;
      
      // Create User accounts for all Employers
      List<Employer> employers = employerRepo.findAll();
      for (Employer employer : employers) {
        if (!userRepo.existsByEmail(employer.getEmail())) {
          User user = new User();
          user.setEmail(employer.getEmail());
          user.setPassword(passwordEncoder.encode(employer.getPassword()));
          user.setRole(UserRole.EMPLOYER);
          user.setBusinessPerson(employer);
          user.setEnabled(true);
          userRepo.save(user);
          userCount++;
        }
      }
      
      // Create User accounts for all Employees
      List<Employee> employees = employeeRepo.findAll();
      for (Employee employee : employees) {
        if (!userRepo.existsByEmail(employee.getEmail())) {
          User user = new User();
          user.setEmail(employee.getEmail());
          user.setPassword(passwordEncoder.encode(employee.getPassword()));
          user.setRole(UserRole.EMPLOYEE);
          user.setBusinessPerson(employee);
          user.setEnabled(true);
          userRepo.save(user);
          userCount++;
        }
      }
      
      System.out.println("Seeded " + userCount + " user accounts");
    } catch (Exception e) {
      System.err.println("Failed to seed users: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void seedPaychecks() {
    if (paycheckRepo.count() > 0) {
      System.out.println("Skipping paychecks - table already has data");
      return;
    }

    try {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(
              new ClassPathResource(CSV_PREFIX + "/paychecks.csv").getInputStream()
          )
      );

      List<Map<String, String>> rows = CSV_PARSER.parse(reader);

      if (rows != null && !rows.isEmpty()) {
        int count = 0;
        int skipped = 0;

        for (Map<String, String> row : rows) {
          Long employeeId = CONVERTER.toLong(row.get("employee_id"));
          if (employeeId == null) {
            skipped++;
            continue;
          }

          Employee employee = employeeCache.get(employeeId);
          if (employee == null) {
            employee = employeeRepo.findById(employeeId).orElse(null);
            if (employee != null) {
              employeeCache.put(employeeId, employee);
            }
          }

          if (employee == null) {
            System.err.println("Employee not found for paycheck, employee_id: " + employeeId);
            skipped++;
            continue;
          }

          Paycheck paycheck = new Paycheck();
          paycheck.setEmployee(employee);
          paycheck.setGrossPay(CONVERTER.toDouble(row.get("gross_pay")));
          paycheck.setTaxDeduction(CONVERTER.toDouble(row.get("tax_deduction")));
          paycheck.setInsuranceDeduction(CONVERTER.toDouble(row.get("insurance_deduction")));
          
          String bonusStr = row.get("bonus");
          if (bonusStr != null && !bonusStr.isEmpty() && !bonusStr.equals("0.00")) {
            paycheck.setBonus(CONVERTER.toDouble(bonusStr));
          }
          
          paycheck.setNetPay(CONVERTER.toDouble(row.get("net_pay")));
          paycheck.setPayDate(CONVERTER.toLocalDate(row.get("pay_date")));
          
          String payPeriod = CONVERTER.toString(row.get("pay_period"));
          if (payPeriod != null && !payPeriod.isEmpty()) {
            paycheck.setPayPeriod(payPeriod);
          }
          
          String statusStr = CONVERTER.toString(row.get("status"));
          if (statusStr != null && !statusStr.isEmpty()) {
            try {
              paycheck.setStatus(PaycheckStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
              System.err.println("Invalid status: " + statusStr + ", using default DRAFT");
              paycheck.setStatus(PaycheckStatus.DRAFT);
            }
          }
          
          String createdAtStr = row.get("created_at");
          if (createdAtStr != null && !createdAtStr.isEmpty()) {
            LocalDateTime createdAt = CONVERTER.toLocalDateTime(createdAtStr);
            paycheck.setCreatedAt(createdAt);
          }

          paycheckRepo.save(paycheck);
          count++;
        }
        System.out.println("Seeded " + count + " paychecks" + (skipped > 0 ? " (skipped " + skipped + ")" : ""));
      }
    } catch (Exception e) {
      System.err.println("Failed to seed paychecks: " + e.getMessage());
      e.printStackTrace();
    }
  }
}