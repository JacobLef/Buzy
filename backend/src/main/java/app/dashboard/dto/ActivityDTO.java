package app.dashboard.dto;

import java.time.LocalDateTime;

/**
 * DTO for recent activity feed items
 */
public record ActivityDTO(Long id, String type, // "PAYROLL", "EMPLOYEE", "TRAINING"
		String title, // Human-readable description
		String date, // Formatted relative time ("2 hours ago")
		LocalDateTime timestamp, // Actual timestamp for sorting
		String status // "completed", "warning", "alert", "info"
) {
}
