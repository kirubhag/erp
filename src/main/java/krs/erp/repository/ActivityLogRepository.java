package krs.erp.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.enums.ActionType;
import krs.erp.enums.LogSeverity;
import krs.erp.model.ActivityLog;

/**
 * Repository for Activity Log entities.
 * Provides read-only access to the immutable audit trail.
 * 
 * IMPORTANT: No update or delete methods are provided to ensure log
 * immutability.
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    /**
     * Find activity logs by user ID, ordered by timestamp descending
     */
    Page<ActivityLog> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);

    /**
     * Find activity logs by entity type, ordered by timestamp descending
     */
    Page<ActivityLog> findByEntityTypeOrderByTimestampDesc(String entityType, Pageable pageable);

    /**
     * Find activity logs by action type, ordered by timestamp descending
     */
    Page<ActivityLog> findByActionTypeOrderByTimestampDesc(ActionType actionType, Pageable pageable);

    /**
     * Find activity logs within a date range, ordered by timestamp descending
     */
    Page<ActivityLog> findByTimestampBetweenOrderByTimestampDesc(
            LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Find activity logs with comprehensive filtering
     * All parameters are optional (can be null)
     */
    @Query("SELECT a FROM ActivityLog a WHERE " +
            "(:userId IS NULL OR a.userId = :userId) AND " +
            "(:entityType IS NULL OR a.entityType = :entityType) AND " +
            "(:actionType IS NULL OR a.actionType = :actionType) AND " +
            "(:severity IS NULL OR a.severity = :severity) AND " +
            "(:startDate IS NULL OR a.timestamp >= :startDate) AND " +
            "(:endDate IS NULL OR a.timestamp <= :endDate) " +
            "ORDER BY a.timestamp DESC")
    Page<ActivityLog> findWithFilters(
            @Param("userId") Long userId,
            @Param("entityType") String entityType,
            @Param("actionType") ActionType actionType,
            @Param("severity") LogSeverity severity,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Find activity logs for a specific entity (entity type + entity ID)
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.timestamp DESC")
    Page<ActivityLog> findByEntityTypeAndEntityId(
            @Param("entityType") String entityType,
            @Param("entityId") Long entityId,
            Pageable pageable);

    /**
     * Find critical activity logs (severity = CRITICAL)
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.severity = 'CRITICAL' ORDER BY a.timestamp DESC")
    Page<ActivityLog> findCriticalLogs(Pageable pageable);

    /**
     * Count activity logs by user
     */
    Long countByUserId(Long userId);

    /**
     * Count activity logs by action type
     */
    Long countByActionType(ActionType actionType);

    /**
     * Count activity logs within a date range
     */
    Long countByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find recent activity logs (last N days)
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.timestamp >= :since ORDER BY a.timestamp DESC")
    Page<ActivityLog> findRecentLogs(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Find failed login attempts for a specific username
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.actionType = 'LOGIN_FAILED' AND a.username = :username ORDER BY a.timestamp DESC")
    Page<ActivityLog> findFailedLoginsByUsername(@Param("username") String username, Pageable pageable);

    /**
     * Find export operations
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.actionType = 'EXPORT' ORDER BY a.timestamp DESC")
    Page<ActivityLog> findExportLogs(Pageable pageable);

    /**
     * Find logs older than a specific date (for archiving/retention)
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.timestamp < :cutoffDate ORDER BY a.timestamp ASC")
    Page<ActivityLog> findLogsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate, Pageable pageable);
}
