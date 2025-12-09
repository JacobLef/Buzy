package app.dashboard;

import app.dashboard.dto.ActivityDTO;
import app.payroll.Paycheck;
import app.payroll.PaycheckRepository;
import app.employee.EmployeeRepository;
import app.training.TrainingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl {
    
    private final PaycheckRepository paycheckRepository;
    private final EmployeeRepository employeeRepository;
    private final TrainingRepository trainingRepository;
    
    public DashboardServiceImpl(
        PaycheckRepository paycheckRepository,
        EmployeeRepository employeeRepository,
        TrainingRepository trainingRepository
    ) {
        this.paycheckRepository = paycheckRepository;
        this.employeeRepository = employeeRepository;
        this.trainingRepository = trainingRepository;
    }
    
    /**
     * Get recent activity feed (last 10 events)
     */
    public List<ActivityDTO> getRecentActivity(Long businessId) {
        List<ActivityDTO> activities = new ArrayList<>();
        
        LocalDate monthAgoForPaychecks = LocalDate.now().minusDays(7);
        var recentPaychecks = paycheckRepository.findByBusinessIdAndPayDateAfter(businessId, monthAgoForPaychecks);
        
        var paychecksByDate = recentPaychecks.stream()
            .collect(Collectors.groupingBy(Paycheck::getPayDate));
        
        for (var entry : paychecksByDate.entrySet()) {
            LocalDate payDate = entry.getKey();
            List<Paycheck> paychecksForDate = entry.getValue();
            LocalDateTime payDateTime = payDate.atStartOfDay();
            
            double totalNetPay = paychecksForDate.stream()
                .mapToDouble(Paycheck::getNetPay)
                .sum();
            
            int count = paychecksForDate.size();
            String title = count == 1 
                ? "Payroll generated for " + (paychecksForDate.get(0).getEmployee() != null 
                    ? paychecksForDate.get(0).getEmployee().getName() 
                    : "Employee")
                : String.format("Payroll generated for %d employees (Total: $%.2f)", count, totalNetPay);
            
            activities.add(new ActivityDTO(
                payDate.toEpochDay(), 
                "PAYROLL",
                title,
                formatRelativeTime(payDateTime),
                payDateTime,
                "completed"
            ));
        }
        
        LocalDate threeMonthsAgo = LocalDate.now().minusDays(7);
        var recentHires = employeeRepository.findByCompanyIdAndHireDateAfter(businessId, threeMonthsAgo);
        
        for (var employee : recentHires) {
            LocalDateTime hireDateTime = employee.getHireDate().atStartOfDay();
            
            activities.add(new ActivityDTO(
                employee.getId(),
                "EMPLOYEE",
                "New Hire: " + employee.getName(),
                formatRelativeTime(hireDateTime),
                hireDateTime,
                "info"
            ));
        }
        
        LocalDate weekFromNow = LocalDate.now().plusDays(7);
        var expiringTrainings = trainingRepository.findExpiringBetween(
            businessId,
            LocalDate.now(), 
            weekFromNow
        );
        
        for (var training : expiringTrainings) {
            LocalDateTime expiryDateTime = training.getExpiryDate().atStartOfDay();
            long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), training.getExpiryDate());
            
            activities.add(new ActivityDTO(
                training.getId(),
                "TRAINING",
                training.getTrainingName() + " expires soon",
                "Due in " + daysUntilExpiry + (daysUntilExpiry == 1 ? " day" : " days"),
                expiryDateTime,
                "warning"
            ));
        }
        
        return activities.stream()
            .sorted(Comparator.comparing(ActivityDTO::timestamp).reversed())
            .toList(); 
    }
    
    /**
     * Format timestamp as relative time ("2 hours ago" or "Due in 3 days")
     */
    private String formatRelativeTime(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        boolean isFuture = timestamp.isAfter(now);
        long minutes = Math.abs(ChronoUnit.MINUTES.between(timestamp, now));
        
        if (minutes < 1) {
            return isFuture ? "due now" : "just now";
        }
        if (minutes < 60) {
            String timeStr = minutes == 1 ? "1 minute" : minutes + " minutes";
            return isFuture ? "due in " + timeStr : timeStr + " ago";
        }
        
        long hours = Math.abs(ChronoUnit.HOURS.between(timestamp, now));
        if (hours < 24) {
            String timeStr = hours == 1 ? "1 hour" : hours + " hours";
            return isFuture ? "due in " + timeStr : timeStr + " ago";
        }
        
        long days = Math.abs(ChronoUnit.DAYS.between(timestamp, now));
        if (days == 0) {
            String timeStr = hours == 1 ? "1 hour" : hours + " hours";
            return isFuture ? "due in " + timeStr : timeStr + " ago";
        }
        if (days < 7) {
            String timeStr = days == 1 ? "1 day" : days + " days";
            return isFuture ? "due in " + timeStr : timeStr + " ago";
        }
        
        long weeks = Math.abs(ChronoUnit.WEEKS.between(timestamp, now));
        if (weeks < 4) {
            String timeStr = weeks == 1 ? "1 week" : weeks + " weeks";
            return isFuture ? "due in " + timeStr : timeStr + " ago";
        }
        
        long months = Math.abs(ChronoUnit.MONTHS.between(timestamp, now));
        String timeStr = months == 1 ? "1 month" : months + " months";
        return isFuture ? "due in " + timeStr : timeStr + " ago";
    }
}

