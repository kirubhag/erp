package krs.erp.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.enums.ActionType;
import krs.erp.enums.LogSeverity;
import krs.erp.model.ActivityLog;
import krs.erp.repository.ActivityLogRepository;

/**
 * REST Controller for Activity Log operations.
 * Provides read-only access to the immutable audit trail.
 * 
 * IMPORTANT: No POST, PUT, or DELETE endpoints are provided to ensure log
 * immutability.
 */
@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    /**
     * Get activity logs with optional filtering
     * All filter parameters are optional
     * 
     * @param userId     Filter by user ID
     * @param entityType Filter by entity type (e.g., "Student", "Staff")
     * @param actionType Filter by action type (CREATE, UPDATE, DELETE, etc.)
     * @param severity   Filter by severity (INFO, WARNING, CRITICAL)
     * @param startDate  Filter by start date (inclusive)
     * @param endDate    Filter by end date (inclusive)
     * @param page       Page number (0-indexed)
     * @param size       Page size (default: 50)
     * @return Page of activity logs
     */
    @GetMapping
    public ResponseEntity<Page<ActivityLog>> getActivityLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) ActionType actionType,
            @RequestParam(required = false) LogSeverity severity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLog> logs = activityLogRepository.findWithFilters(
                userId, entityType, actionType, severity, startDate, endDate, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get a specific activity log by ID
     * 
     * @param id Activity log ID
     * @return Activity log details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActivityLog> getActivityLog(@PathVariable Long id) {
        return activityLogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get activity logs for a specific entity
     * 
     * @param entityType Entity type (e.g., "Student", "Staff")
     * @param entityId   Entity ID
     * @param page       Page number (0-indexed)
     * @param size       Page size (default: 50)
     * @return Page of activity logs for the entity
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<Page<ActivityLog>> getEntityLogs(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLog> logs = activityLogRepository.findByEntityTypeAndEntityId(
                entityType, entityId, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get activity logs for a specific user
     * 
     * @param userId User ID
     * @param page   Page number (0-indexed)
     * @param size   Page size (default: 50)
     * @return Page of activity logs for the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ActivityLog>> getUserLogs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLog> logs = activityLogRepository.findByUserIdOrderByTimestampDesc(
                userId, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get critical activity logs (severity = CRITICAL)
     * 
     * @param page Page number (0-indexed)
     * @param size Page size (default: 50)
     * @return Page of critical activity logs
     */
    @GetMapping("/critical")
    public ResponseEntity<Page<ActivityLog>> getCriticalLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLog> logs = activityLogRepository.findCriticalLogs(pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get recent activity logs (last N days)
     * 
     * @param days Number of days to look back (default: 7)
     * @param page Page number (0-indexed)
     * @param size Page size (default: 50)
     * @return Page of recent activity logs
     */
    @GetMapping("/recent")
    public ResponseEntity<Page<ActivityLog>> getRecentLogs(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLog> logs = activityLogRepository.findRecentLogs(since, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get failed login attempts for a specific username
     * 
     * @param username Username to search for
     * @param page     Page number (0-indexed)
     * @param size     Page size (default: 50)
     * @return Page of failed login attempts
     */
    @GetMapping("/failed-logins/{username}")
    public ResponseEntity<Page<ActivityLog>> getFailedLogins(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLog> logs = activityLogRepository.findFailedLoginsByUsername(username, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get export operation logs
     * 
     * @param page Page number (0-indexed)
     * @param size Page size (default: 50)
     * @return Page of export operation logs
     */
    @GetMapping("/exports")
    public ResponseEntity<Page<ActivityLog>> getExportLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLog> logs = activityLogRepository.findExportLogs(pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get activity log statistics
     * 
     * @return Statistics about activity logs
     */
    @GetMapping("/stats")
    public ResponseEntity<ActivityLogStats> getStats() {
        ActivityLogStats stats = new ActivityLogStats();
        stats.setTotalLogs(activityLogRepository.count());
        stats.setCriticalLogs(activityLogRepository.countByTimestampBetween(
                LocalDateTime.now().minusDays(30), LocalDateTime.now()));
        return ResponseEntity.ok(stats);
    }

    /**
     * Inner class for activity log statistics
     */
    public static class ActivityLogStats {
        private Long totalLogs;
        private Long criticalLogs;

        public Long getTotalLogs() {
            return totalLogs;
        }

        public void setTotalLogs(Long totalLogs) {
            this.totalLogs = totalLogs;
        }

        public Long getCriticalLogs() {
            return criticalLogs;
        }

        public void setCriticalLogs(Long criticalLogs) {
            this.criticalLogs = criticalLogs;
        }
    }
}
