package app.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import app.business.BusinessPerson;
import app.business.BusinessPersonRepository;
import app.business.BusinessRepository;
import app.business.Company;
import app.employee.Employee;
import app.employee.EmployeeRepository;
import app.employer.Employer;
import app.employer.EmployerRepository;
import app.payroll.Paycheck;
import app.payroll.PaycheckRepository;
import app.payroll.PaycheckStatus;
import app.training.Training;
import app.training.TrainingRepository;
import app.user.PersonStatus;
import app.user.User;
import app.user.UserRepository;
import app.user.UserRole;

@Component
public class DatabaseSeeder implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
  private static final CSVParser CSV_PARSER = new CSVParserImpl();
  private static final StringConverter CONVERTER = new StringConverterImpl();
  private static final String CSV_PREFIX = "data/csv";

  // ==================== Log Message Enum ====================

  // @formatter:off
  private enum LogMessage {
    SEEDING_DISABLED("Database seeding is disabled"),
    SEEDING_STARTED("Starting database seeding..."),
    SEEDING_COMPLETED("Database seeding completed successfully"),
    SKIPPING_TABLE_HAS_DATA("Skipping {} - table already has data"),
    NO_DATA_IN_FILE("No data found in {}"),
    SEEDED_COUNT("Seeded {} {}"),
    SEEDED_COUNT_WITH_SKIPPED("Seeded {} {} (skipped {})"),
    FAILED_TO_SEED("Failed to seed {}: {}"),
    PERSON_NOT_FOUND("Person not found for training, person_id: {}"),
    EMPLOYEE_NOT_FOUND("Employee not found for paycheck, employee_id: {}"), MANAGER_NOT_FOUND("Manager not found for employee ID {}, manager_id: {}"), INVALID_STATUS("Invalid status: {}"),
    INVALID_PAYCHECK_STATUS("Invalid paycheck status: {}, using default DRAFT"), AUTO_SET_OWNER("Auto-set {} as Owner for company {}"),
    AUTO_SET_FIRST_OWNER("Auto-set first employer {} as Owner for company {}");

    private final String message;

    LogMessage(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }

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

  public DatabaseSeeder(BusinessRepository businessRepo, EmployeeRepository employeeRepo,
      EmployerRepository employerRepo, TrainingRepository trainingRepo,
      BusinessPersonRepository businessPersonRepo, UserRepository userRepo,
      PaycheckRepository paycheckRepo, PasswordEncoder passwordEncoder) {
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
      logger.info(LogMessage.SEEDING_DISABLED.getMessage());
      return;
    }

    logger.info(LogMessage.SEEDING_STARTED.getMessage());
    seedCompanies();
    seedEmployers();
    seedEmployees();
    seedUsers();
    seedTrainings();
    seedPaychecks();
    logger.info(LogMessage.SEEDING_COMPLETED.getMessage());
  }

  private void seedCompanies() {
    seedEntity("companies", "businesses.csv", businessRepo, this::mapToCompany,
        (company, csvId) -> companyCache.put(csvId, company), this::loadExistingCompanies);
  }

  private void seedEmployers() {
    seedEntityWithCustomLogic("employers", "employers.csv", employerRepo,
        this::loadExistingEmployers, rows -> {
          int count = 0;
          Map<Long, Boolean> companyOwnerSet = new HashMap<>();

          for (Map<String, String> row : rows) {
            Long csvId = CONVERTER.toLong(row.get("id"));
            Long companyId = CONVERTER.toLong(row.get("company_id"));

            Employer employer = mapToEmployer(row);
            applyOwnershipLogic(employer, row, companyId, companyOwnerSet);

            Employer saved = employerRepo.save(employer);
            if (csvId != null) {
              employerCache.put(csvId, saved);
            }
            count++;
          }
          return count;
        });
  }

  private void seedEmployees() {
    seedEntityWithCustomLogic("employees", "employees.csv", employeeRepo,
        this::loadExistingEmployees, rows -> {
          int count = 0;
          Map<Long, Long> managerRelationships = new HashMap<>();

          for (Map<String, String> row : rows) {
            Long csvId = CONVERTER.toLong(row.get("id"));
            Long managerId = CONVERTER.toLong(row.get("manager_id"));

            if (csvId != null && managerId != null) {
              managerRelationships.put(csvId, managerId);
            }

            Employee employee = mapToEmployee(row);
            employee.setManager(null);
            Employee saved = employeeRepo.save(employee);

            if (csvId != null) {
              employeeCache.put(csvId, saved);
            }
            count++;
          }

          setManagerRelationships(managerRelationships);
          return count;
        });
  }

  private void seedTrainings() {
    seedEntityWithValidation("trainings", "trainings.csv", trainingRepo, row -> {
      Training training = new Training(CONVERTER.toString(row.get("training_name")),
          CONVERTER.toString(row.get("description")),
          CONVERTER.toLocalDate(row.get("completion_date")),
          CONVERTER.toLocalDate(row.get("expiry_date")),
          CONVERTER.toBoolean(row.get("is_required")));

      Long personId = CONVERTER.toLong(row.get("person_id"));
      if (personId != null) {
        BusinessPerson person = findPersonById(personId);
        if (person != null) {
          training.setPerson(person);
          return training;
        } else {
          logger.warn(LogMessage.PERSON_NOT_FOUND.getMessage(), personId);
          return null;
        }
      }
      return training;
    });
  }

  private void seedUsers() {
    if (userRepo.count() > 0) {
      logger.info(LogMessage.SKIPPING_TABLE_HAS_DATA.getMessage(), "users");
      return;
    }

    try {
      int userCount = 0;

      userCount += createUsersForEmployers();
      userCount += createUsersForEmployees();

      logger.info(LogMessage.SEEDED_COUNT.getMessage(), userCount, "user accounts");
    } catch (Exception e) {
      logger.error(LogMessage.FAILED_TO_SEED.getMessage(), "users", e.getMessage(), e);
    }
  }

  private void seedPaychecks() {
    seedEntityWithValidation("paychecks", "paychecks.csv", paycheckRepo, row -> {
      Long employeeId = CONVERTER.toLong(row.get("employee_id"));
      if (employeeId == null) {
        return null;
      }

      Employee employee = findOrLoadEmployee(employeeId);
      if (employee == null) {
        logger.warn(LogMessage.EMPLOYEE_NOT_FOUND.getMessage(), employeeId);
        return null;
      }

      return mapToPaycheck(row, employee);
    });
  }

  // ==================== Generic Seeding Methods ====================

  private <T> void seedEntity(String entityName, String csvFileName,
      JpaRepository<T, Long> repository, Function<Map<String, String>, T> mapper,
      CacheUpdater<T> cacheUpdater, Runnable loadExisting) {
    if (repository.count() > 0) {
      logger.info(LogMessage.SKIPPING_TABLE_HAS_DATA.getMessage(), entityName);
      loadExisting.run();
      return;
    }

    try {
      List<Map<String, String>> rows = readCsvFile(csvFileName);
      if (rows == null || rows.isEmpty()) {
        logger.warn(LogMessage.NO_DATA_IN_FILE.getMessage(), csvFileName);
        return;
      }

      int count = 0;
      for (Map<String, String> row : rows) {
        Long csvId = CONVERTER.toLong(row.get("id"));
        T entity = mapper.apply(row);
        T saved = repository.save(entity);

        if (csvId != null && cacheUpdater != null) {
          cacheUpdater.updateCache(saved, csvId);
        }
        count++;
      }
      logger.info(LogMessage.SEEDED_COUNT.getMessage(), count, entityName);
    } catch (Exception e) {
      logger.error(LogMessage.FAILED_TO_SEED.getMessage(), entityName, e.getMessage(), e);
    }
  }

  private <T> void seedEntityWithCustomLogic(String entityName, String csvFileName,
      JpaRepository<T, Long> repository, Runnable loadExisting,
      Function<List<Map<String, String>>, Integer> customLogic) {
    if (repository.count() > 0) {
      logger.info(LogMessage.SKIPPING_TABLE_HAS_DATA.getMessage(), entityName);
      loadExisting.run();
      return;
    }

    try {
      List<Map<String, String>> rows = readCsvFile(csvFileName);
      if (rows == null || rows.isEmpty()) {
        logger.warn(LogMessage.NO_DATA_IN_FILE.getMessage(), csvFileName);
        return;
      }

      int count = customLogic.apply(rows);
      logger.info(LogMessage.SEEDED_COUNT.getMessage(), count, entityName);
    } catch (Exception e) {
      logger.error(LogMessage.FAILED_TO_SEED.getMessage(), entityName, e.getMessage(), e);
    }
  }

  private <T> void seedEntityWithValidation(String entityName, String csvFileName,
      JpaRepository<T, Long> repository, Function<Map<String, String>, T> mapper) {
    if (repository.count() > 0) {
      logger.info(LogMessage.SKIPPING_TABLE_HAS_DATA.getMessage(), entityName);
      return;
    }

    try {
      List<Map<String, String>> rows = readCsvFile(csvFileName);
      if (rows == null || rows.isEmpty()) {
        logger.warn(LogMessage.NO_DATA_IN_FILE.getMessage(), csvFileName);
        return;
      }

      int count = 0;
      int skipped = 0;

      for (Map<String, String> row : rows) {
        T entity = mapper.apply(row);
        if (entity != null) {
          repository.save(entity);
          count++;
        } else {
          skipped++;
        }
      }

      if (skipped > 0) {
        logger.info(LogMessage.SEEDED_COUNT_WITH_SKIPPED.getMessage(), count, entityName, skipped);
      } else {
        logger.info(LogMessage.SEEDED_COUNT.getMessage(), count, entityName);
      }
    } catch (Exception e) {
      logger.error(LogMessage.FAILED_TO_SEED.getMessage(), entityName, e.getMessage(), e);
    }
  }

  // ==================== Helper Methods ====================

  private List<Map<String, String>> readCsvFile(String fileName) throws IOException {
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(new ClassPathResource(CSV_PREFIX + "/" + fileName).getInputStream()));
    return CSV_PARSER.parse(reader);
  }

  private void loadExistingCompanies() {
    businessRepo.findAll().forEach(company -> companyCache.put(company.getId(), company));
  }

  private void loadExistingEmployers() {
    employerRepo.findAll().forEach(employer -> employerCache.put(employer.getId(), employer));
  }

  private void loadExistingEmployees() {
    employeeRepo.findAll().forEach(employee -> employeeCache.put(employee.getId(), employee));
  }

  private BusinessPerson findPersonById(Long personId) {
    Employee employee = employeeCache.get(personId);
    if (employee != null)
      return employee;

    Employer employer = employerCache.get(personId);
    if (employer != null)
      return employer;

    return businessPersonRepo.findById(personId).orElse(null);
  }

  private Employee findOrLoadEmployee(Long employeeId) {
    Employee employee = employeeCache.get(employeeId);
    if (employee == null) {
      employee = employeeRepo.findById(employeeId).orElse(null);
      if (employee != null) {
        employeeCache.put(employeeId, employee);
      }
    }
    return employee;
  }

  // ==================== Mapping Methods ====================

  private Company mapToCompany(Map<String, String> row) {
    return Company.builder().name(CONVERTER.toString(row.get("name")))
        .address(CONVERTER.toString(row.get("address")))
        .industry(CONVERTER.toString(row.get("industry")))
        .foundedDate(CONVERTER.toLocalDate(row.get("founded_date"))).build();
  }

  private Employer mapToEmployer(Map<String, String> row) {
    Employer employer =
        new Employer(CONVERTER.toString(row.get("name")), CONVERTER.toString(row.get("email")),
            CONVERTER.toString(row.get("password")), CONVERTER.toDouble(row.get("salary")),
            CONVERTER.toString(row.get("department")), CONVERTER.toString(row.get("title")));

    setOptionalField(row, "hire_date", CONVERTER::toLocalDate, employer::setHireDate);
    setCompanyRelationship(row, employer);
    setPersonStatus(row, employer::setStatus);
    setAdminAndOwnerFlags(row, employer);

    return employer;
  }

  private Employee mapToEmployee(Map<String, String> row) {
    Employee employee = new Employee(CONVERTER.toString(row.get("name")),
        CONVERTER.toString(row.get("email")), CONVERTER.toString(row.get("password")),
        CONVERTER.toDouble(row.get("salary")), CONVERTER.toString(row.get("position")));

    setOptionalField(row, "hire_date", CONVERTER::toLocalDate, employee::setHireDate);
    setCompanyRelationship(row, employee);
    setPersonStatus(row, employee::setStatus);

    return employee;
  }

  private Paycheck mapToPaycheck(Map<String, String> row, Employee employee) {
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

    setOptionalField(row, "pay_period", CONVERTER::toString, paycheck::setPayPeriod);
    setPaycheckStatus(row, paycheck);
    setOptionalField(row, "created_at", CONVERTER::toLocalDateTime, paycheck::setCreatedAt);

    return paycheck;
  }

  // ==================== Field Setting Helpers ====================

  private <T> void setOptionalField(Map<String, String> row, String fieldName,
      Function<String, T> converter, Consumer<T> setter) {
    T value = converter.apply(row.get(fieldName));
    if (value != null) {
      setter.accept(value);
    }
  }

  private void setCompanyRelationship(Map<String, String> row, BusinessPerson person) {
    Long companyId = CONVERTER.toLong(row.get("company_id"));
    if (companyId != null) {
      Company company = companyCache.get(companyId);
      if (company != null) {
        person.setCompany(company);
      }
    }
  }

  private void setPersonStatus(Map<String, String> row, Consumer<PersonStatus> setter) {
    String status = CONVERTER.toString(row.get("status"));
    if (status != null && !status.isEmpty()) {
      try {
        setter.accept(PersonStatus.valueOf(status));
      } catch (IllegalArgumentException e) {
        logger.warn(LogMessage.INVALID_STATUS.getMessage(), status);
      }
    }
  }

  private void setAdminAndOwnerFlags(Map<String, String> row, Employer employer) {
    Boolean isAdmin = CONVERTER.toBoolean(row.get("is_admin"));
    if (isAdmin != null) {
      employer.setIsAdmin(isAdmin);
    }

    Boolean isOwner = CONVERTER.toBoolean(row.get("is_owner"));
    if (isOwner != null) {
      employer.setIsOwner(isOwner);
      if (isOwner) {
        employer.setIsAdmin(true);
      }
    }
  }

  private void setPaycheckStatus(Map<String, String> row, Paycheck paycheck) {
    String statusStr = CONVERTER.toString(row.get("status"));
    if (statusStr != null && !statusStr.isEmpty()) {
      try {
        paycheck.setStatus(PaycheckStatus.valueOf(statusStr));
      } catch (IllegalArgumentException e) {
        logger.warn(LogMessage.INVALID_PAYCHECK_STATUS.getMessage(), statusStr);
        paycheck.setStatus(PaycheckStatus.DRAFT);
      }
    }
  }

  // ==================== Business Logic Methods ====================

  private void applyOwnershipLogic(Employer employer, Map<String, String> row, Long companyId,
      Map<Long, Boolean> companyOwnerSet) {
    Boolean isOwnerFromCSV = CONVERTER.toBoolean(row.get("is_owner"));

    if ((isOwnerFromCSV == null || !isOwnerFromCSV) && companyId != null) {
      if (!companyOwnerSet.containsKey(companyId)) {
        String title = CONVERTER.toString(row.get("title"));
        if (isExecutiveTitle(title)) {
          employer.setIsOwner(true);
          employer.setIsAdmin(true);
          companyOwnerSet.put(companyId, true);
          logger.debug(LogMessage.AUTO_SET_OWNER.getMessage(), employer.getName(), companyId);
        } else {
          employer.setIsOwner(true);
          employer.setIsAdmin(true);
          companyOwnerSet.put(companyId, true);
          logger.debug(LogMessage.AUTO_SET_FIRST_OWNER.getMessage(), employer.getName(), companyId);
        }
      }
    } else if (isOwnerFromCSV != null && isOwnerFromCSV) {
      companyOwnerSet.put(companyId, true);
    }
  }

  private boolean isExecutiveTitle(String title) {
    if (title == null)
      return false;
    String lowerTitle = title.toLowerCase();
    return lowerTitle.contains("ceo") || lowerTitle.contains("chief")
        || lowerTitle.contains("president") || lowerTitle.contains("managing partner");
  }

  private void setManagerRelationships(Map<Long, Long> managerRelationships) {
    for (Map.Entry<Long, Long> entry : managerRelationships.entrySet()) {
      Long employeeId = entry.getKey();
      Long managerId = entry.getValue();

      Employee employee = employeeCache.get(employeeId);
      if (employee != null) {
        BusinessPerson manager = employerCache.get(managerId);
        if (manager == null) {
          manager = employeeCache.get(managerId);
        }
        if (manager != null) {
          employee.setManager(manager);
          employeeRepo.save(employee);
        } else {
          logger.warn(LogMessage.MANAGER_NOT_FOUND.getMessage(), employeeId, managerId);
        }
      }
    }
  }

  private int createUsersForEmployers() {
    int count = 0;
    List<Employer> employers = employerRepo.findAll();
    for (Employer employer : employers) {
      if (!userRepo.existsByEmail(employer.getEmail())) {
        User user =
            createUser(employer.getEmail(), employer.getPassword(), UserRole.EMPLOYER, employer);
        userRepo.save(user);
        count++;
      }
    }
    return count;
  }

  private int createUsersForEmployees() {
    int count = 0;
    List<Employee> employees = employeeRepo.findAll();
    for (Employee employee : employees) {
      if (!userRepo.existsByEmail(employee.getEmail())) {
        User user =
            createUser(employee.getEmail(), employee.getPassword(), UserRole.EMPLOYEE, employee);
        userRepo.save(user);
        count++;
      }
    }
    return count;
  }

  private User createUser(String email, String password, UserRole role, BusinessPerson person) {
    User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setRole(role);
    user.setBusinessPerson(person);
    user.setEnabled(true);
    return user;
  }

  // ==================== Functional Interfaces ====================

  @FunctionalInterface
  private interface CacheUpdater<T> {
    void updateCache(T entity, Long csvId);
  }
}
