package app.dashboard;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import app.dashboard.dto.ActivityDTO;

/** Controller for dashboard-specific endpoints */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  /** Get recent activity feed GET /api/dashboard/activity?businessId=1 */
  @GetMapping("/activity")
  public ResponseEntity<List<ActivityDTO>> getRecentActivity(@RequestParam Long businessId) {
    List<ActivityDTO> activities = dashboardService.getRecentActivity(businessId);
    return ResponseEntity.ok(activities);
  }
}
