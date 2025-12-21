package app.dashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import app.dashboard.dto.ActivityDTO;
import app.employee.Employee;
import app.employee.EmployeeRepository;
import app.payroll.Paycheck;
import app.payroll.PaycheckRepository;
import app.training.Training;
import app.training.TrainingRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

  private static final int RECENT_ACTIVITY_DAYS = 7;
  private static final int TRAINING_WARNING_DAYS = 7;

  private final PaycheckRepository paycheckRepository;
  private final EmployeeRepository employeeRepository;
  private final TrainingRepository trainingRepository;

  public DashboardServiceImpl(PaycheckRepository paycheckRepository,
      EmployeeRepository employeeRepository, TrainingRepository trainingRepository) {
    this.paycheckRepository = paycheckRepository;
    this.employeeRepository = employeeRepository;
    this.trainingRepository = trainingRepository;
  }

  @Override
  public List<ActivityDTO> getRecentActivity(Long businessId) {
    List<ActivityDTO> activities = new ArrayList<>();

    activities.addAll(getPayrollActivities(businessId));
    activities.addAll(getNewHireActivities(businessId));
    activities.addAll(getExpiringTrainingActivities(businessId));

    return activities.stream().sorted(Comparator.comparing(ActivityDTO::timestamp).reversed())
        .toList();
  }

  private List<ActivityDTO> getPayrollActivities(Long businessId) {
    LocalDate sevenDaysAgo = LocalDate.now().minusDays(RECENT_ACTIVITY_DAYS);
    List<Paycheck> recentPaychecks =
        paycheckRepository.findByBusinessIdAndPayDateAfter(businessId, sevenDaysAgo);

    Map<LocalDate, List<Paycheck>> paychecksByDate =
        recentPaychecks.stream().collect(Collectors.groupingBy(Paycheck::getPayDate));

    List<ActivityDTO> activities = new ArrayList<>();

    for (Map.Entry<LocalDate, List<Paycheck>> entry : paychecksByDate.entrySet()) {
      LocalDate payDate = entry.getKey();
      List<Paycheck> paychecksForDate = entry.getValue();

      activities.add(createPayrollActivity(payDate, paychecksForDate));
    }

    return activities;
  }

  private ActivityDTO createPayrollActivity(LocalDate payDate, List<Paycheck> paychecks) {
    LocalDateTime payDateTime = payDate.atStartOfDay();
    int employeeCount = paychecks.size();

    String title;
    if (employeeCount == 1) {
      Paycheck paycheck = paychecks.get(0);
      String employeeName =
          paycheck.getEmployee() != null ? paycheck.getEmployee().getName() : "Employee";
      title = "Payroll generated for " + employeeName;
    } else {
      double totalNetPay = paychecks.stream().mapToDouble(Paycheck::getNetPay).sum();
      title = String.format("Payroll generated for %d employees (Total: $%.2f)", employeeCount,
          totalNetPay);
    }

    return new ActivityDTO(payDate.toEpochDay(), "PAYROLL", title,
        TimeFormatter.formatRelativeTime(payDateTime), payDateTime, "completed");
  }

  private List<ActivityDTO> getNewHireActivities(Long businessId) {
    LocalDate sevenDaysAgo = LocalDate.now().minusDays(RECENT_ACTIVITY_DAYS);
    List<Employee> recentHires =
        employeeRepository.findByCompanyIdAndHireDateAfter(businessId, sevenDaysAgo);

    List<ActivityDTO> activities = new ArrayList<>();

    for (Employee employee : recentHires) {
      LocalDateTime hireDateTime = employee.getHireDate().atStartOfDay();

      activities
          .add(new ActivityDTO(employee.getId(), "EMPLOYEE", "New Hire: " + employee.getName(),
              TimeFormatter.formatRelativeTime(hireDateTime), hireDateTime, "info"));
    }

    return activities;
  }

  private List<ActivityDTO> getExpiringTrainingActivities(Long businessId) {
    LocalDate today = LocalDate.now();
    LocalDate weekFromNow = today.plusDays(TRAINING_WARNING_DAYS);

    List<Training> expiringTrainings =
        trainingRepository.findExpiringBetween(businessId, today, weekFromNow);

    List<ActivityDTO> activities = new ArrayList<>();

    for (Training training : expiringTrainings) {
      LocalDateTime expiryDateTime = training.getExpiryDate().atStartOfDay();
      String daysUntilText = TimeFormatter.formatDaysUntil(today, training.getExpiryDate());

      activities.add(new ActivityDTO(training.getId(), "TRAINING",
          training.getTrainingName() + " expires soon", daysUntilText, expiryDateTime, "warning"));
    }

    return activities;
  }

  private static class TimeFormatter {

    static String formatDaysUntil(LocalDate from, LocalDate to) {
      long days = java.time.temporal.ChronoUnit.DAYS.between(from, to);

      if (days == 0) {
        return "Due today";
      } else if (days == 1) {
        return "Due in 1 day";
      } else {
        return "Due in " + days + " days";
      }
    }

    static String formatRelativeTime(LocalDateTime timestamp) {
      LocalDateTime now = LocalDateTime.now();
      boolean isFuture = timestamp.isAfter(now);

      long minutes = Math.abs(java.time.temporal.ChronoUnit.MINUTES.between(timestamp, now));
      if (minutes < 1) {
        return isFuture ? "due now" : "just now";
      }
      if (minutes < 60) {
        return formatTimeUnit(minutes, "minute", isFuture);
      }

      long hours = Math.abs(java.time.temporal.ChronoUnit.HOURS.between(timestamp, now));
      if (hours < 24) {
        return formatTimeUnit(hours, "hour", isFuture);
      }

      long days = Math.abs(java.time.temporal.ChronoUnit.DAYS.between(timestamp, now));
      if (days == 0) {
        return formatTimeUnit(hours, "hour", isFuture);
      }
      if (days < 7) {
        return formatTimeUnit(days, "day", isFuture);
      }

      long weeks = Math.abs(java.time.temporal.ChronoUnit.WEEKS.between(timestamp, now));
      if (weeks < 4) {
        return formatTimeUnit(weeks, "week", isFuture);
      }

      long months = Math.abs(java.time.temporal.ChronoUnit.MONTHS.between(timestamp, now));
      return formatTimeUnit(months, "month", isFuture);
    }

    private static String formatTimeUnit(long value, String unit, boolean isFuture) {
      String timeStr = value == 1 ? "1 " + unit : value + " " + unit + "s";
      return isFuture ? "due in " + timeStr : timeStr + " ago";
    }
  }
}
