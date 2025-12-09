package edu.neu.csye6200.controller;

import edu.neu.csye6200.dto.response.ActivityDTO;
import edu.neu.csye6200.service.impl.DashboardServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for dashboard-specific endpoints
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    private final DashboardServiceImpl dashboardService;
    
    public DashboardController(DashboardServiceImpl dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    /**
     * Get recent activity feed
     * GET /api/dashboard/activity?businessId=1
     */
    @GetMapping("/activity")
    public ResponseEntity<List<ActivityDTO>> getRecentActivity(
        @RequestParam Long businessId
    ) {
        List<ActivityDTO> activities = dashboardService.getRecentActivity(businessId);
        return ResponseEntity.ok(activities);
    }
}

