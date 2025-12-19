package app.dashboard;

import java.util.List;
import app.dashboard.dto.ActivityDTO;

/**
 * Service to get the information for all recent activity required for the dashboard presented to a
 * user.
 */
public interface DashBoardService {
  List<ActivityDTO> getRecentActivity(Long businessId);
}
