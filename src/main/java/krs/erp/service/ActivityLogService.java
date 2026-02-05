package krs.erp.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import krs.erp.enums.ActionType;
import krs.erp.enums.LogSeverity;
import krs.erp.model.ActivityLog;
import krs.erp.model.User;
import krs.erp.repository.ActivityLogRepository;
import krs.erp.config.CustomUserDetails;

/**
 * Service for managing activity logs in the audit trail.
 * All logging operations are asynchronous to prevent performance impact on user
 * operations.
 * 
 * This service provides the "Antigravity" (weightless) logging experience -
 * developers don't need to manually call log.save() in every function.
 */
@Service
public class ActivityLogService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogService.class);

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private HttpServletRequest request;

    /**
     * Log an activity asynchronously
     */
    @Async("activityLogTaskExecutor")
    public void logActivity(ActivityLog log) {
        try {
            activityLogRepository.save(log);
            logger.debug("Activity logged: {} by user {}", log.getActionType(), log.getUsername());
        } catch (Exception e) {
            logger.error("Failed to log activity: {}", e.getMessage(), e);
        }
    }

    /**
     * Log entity creation
     */
    @Async("activityLogTaskExecutor")
    public void logEntityCreate(String entityType, Long entityId, Object newEntity) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(getCurrentUserId());
            log.setUsername(getCurrentUsername());
            log.setIpAddress(getClientIpAddress());
            log.setUserAgent(getUserAgent());
            log.setActionType(ActionType.CREATE);
            log.setSeverity(LogSeverity.INFO);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setNewValue(toJson(newEntity));
            log.setDescription(String.format("Created %s record (ID: %d)", entityType, entityId));

            activityLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Failed to log entity creation: {}", e.getMessage(), e);
        }
    }

    /**
     * Log entity update with delta tracking
     */
    @Async("activityLogTaskExecutor")
    public void logEntityUpdate(String entityType, Long entityId, Object oldEntity, Object newEntity) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(getCurrentUserId());
            log.setUsername(getCurrentUsername());
            log.setIpAddress(getClientIpAddress());
            log.setUserAgent(getUserAgent());
            log.setActionType(ActionType.UPDATE);
            log.setSeverity(LogSeverity.WARNING);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setOldValue(toJson(oldEntity));
            log.setNewValue(toJson(newEntity));
            log.setDeltaSummary(calculateDelta(oldEntity, newEntity));
            log.setDescription(String.format("Updated %s record (ID: %d)", entityType, entityId));

            activityLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Failed to log entity update: {}", e.getMessage(), e);
        }
    }

    /**
     * Log entity deletion (stores snapshot of deleted data)
     */
    @Async("activityLogTaskExecutor")
    public void logEntityDelete(String entityType, Long entityId, Object deletedEntity) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(getCurrentUserId());
            log.setUsername(getCurrentUsername());
            log.setIpAddress(getClientIpAddress());
            log.setUserAgent(getUserAgent());
            log.setActionType(ActionType.DELETE);
            log.setSeverity(LogSeverity.CRITICAL);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setOldValue(toJson(deletedEntity)); // Store snapshot for potential restoration
            log.setDescription(String.format("Deleted %s record (ID: %d)", entityType, entityId));

            activityLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Failed to log entity deletion: {}", e.getMessage(), e);
        }
    }

    /**
     * Log data export operations
     */
    @Async("activityLogTaskExecutor")
    public void logExport(String entityType, Map<String, Object> filters, int recordCount) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(getCurrentUserId());
            log.setUsername(getCurrentUsername());
            log.setIpAddress(getClientIpAddress());
            log.setUserAgent(getUserAgent());
            log.setActionType(ActionType.EXPORT);
            log.setSeverity(LogSeverity.CRITICAL);
            log.setEntityType(entityType);
            log.setMetadata(toJson(filters));
            log.setDescription(String.format("Exported %d %s records", recordCount, entityType));

            activityLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Failed to log export operation: {}", e.getMessage(), e);
        }
    }

    /**
     * Log successful login
     */
    @Async("activityLogTaskExecutor")
    public void logLogin(Long userId, String username, String ipAddress, String userAgent) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(userId);
            log.setUsername(username);
            log.setIpAddress(ipAddress);
            log.setUserAgent(userAgent);
            log.setActionType(ActionType.LOGIN);
            log.setSeverity(LogSeverity.INFO);
            log.setDescription(String.format("User %s logged in successfully", username));

            activityLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Failed to log login: {}", e.getMessage(), e);
        }
    }

    /**
     * Log failed login attempt
     */
    @Async("activityLogTaskExecutor")
    public void logLoginFailed(String username, String ipAddress, String userAgent, String reason) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUsername(username);
            log.setIpAddress(ipAddress);
            log.setUserAgent(userAgent);
            log.setActionType(ActionType.LOGIN_FAILED);
            log.setSeverity(LogSeverity.WARNING);
            log.setDescription(String.format("Failed login attempt for user %s: %s", username, reason));

            activityLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Failed to log failed login: {}", e.getMessage(), e);
        }
    }

    /**
     * Log logout
     */
    @Async("activityLogTaskExecutor")
    public void logLogout(Long userId, String username) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(userId);
            log.setUsername(username);
            log.setIpAddress(getClientIpAddress());
            log.setUserAgent(getUserAgent());
            log.setActionType(ActionType.LOGOUT);
            log.setSeverity(LogSeverity.INFO);
            log.setDescription(String.format("User %s logged out", username));

            activityLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Failed to log logout: {}", e.getMessage(), e);
        }
    }

    /**
     * Log system setting change
     */
    @Async("activityLogTaskExecutor")
    public void logSettingChange(String settingName, Object oldValue, Object newValue) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(getCurrentUserId());
            log.setUsername(getCurrentUsername());
            log.setIpAddress(getClientIpAddress());
            log.setUserAgent(getUserAgent());
            log.setActionType(ActionType.SETTING_CHANGE);
            log.setSeverity(LogSeverity.WARNING);
            log.setOldValue(toJson(oldValue));
            log.setNewValue(toJson(newValue));
            log.setDescription(String.format("Changed setting '%s' from '%s' to '%s'",
                    settingName, oldValue, newValue));

            activityLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Failed to log setting change: {}", e.getMessage(), e);
        }
    }

    /**
     * Log permission change
     */
    @Async("activityLogTaskExecutor")
    public void logPermissionChange(String targetUser, String permission, String action) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(getCurrentUserId());
            log.setUsername(getCurrentUsername());
            log.setIpAddress(getClientIpAddress());
            log.setUserAgent(getUserAgent());
            log.setActionType(ActionType.PERMISSION_CHANGE);
            log.setSeverity(LogSeverity.CRITICAL);
            log.setDescription(String.format("%s %s permission for user %s",
                    action, permission, targetUser));

            activityLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Failed to log permission change: {}", e.getMessage(), e);
        }
    }

    /**
     * Log bulk update operation
     */
    @Async("activityLogTaskExecutor")
    public void logBulkUpdate(String entityType, int recordCount, String criteria) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(getCurrentUserId());
            log.setUsername(getCurrentUsername());
            log.setIpAddress(getClientIpAddress());
            log.setUserAgent(getUserAgent());
            log.setActionType(ActionType.BULK_UPDATE);
            log.setSeverity(LogSeverity.CRITICAL);
            log.setEntityType(entityType);
            log.setDescription(String.format("Bulk updated %d %s records: %s",
                    recordCount, entityType, criteria));

            activityLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Failed to log bulk update: {}", e.getMessage(), e);
        }
    }

    /**
     * Log bulk delete operation
     */
    @Async("activityLogTaskExecutor")
    public void logBulkDelete(String entityType, int recordCount, String criteria) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(getCurrentUserId());
            log.setUsername(getCurrentUsername());
            log.setIpAddress(getClientIpAddress());
            log.setUserAgent(getUserAgent());
            log.setActionType(ActionType.BULK_DELETE);
            log.setSeverity(LogSeverity.CRITICAL);
            log.setEntityType(entityType);
            log.setDescription(String.format("Bulk deleted %d %s records: %s",
                    recordCount, entityType, criteria));

            activityLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Failed to log bulk delete: {}", e.getMessage(), e);
        }
    }

    // ========== Helper Methods ==========

    /**
     * Get current user ID from security context
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                return userDetails.getUserId();
            }
        } catch (Exception e) {
            logger.debug("Could not get current user ID: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Get current username from security context
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                return authentication.getName();
            }
        } catch (Exception e) {
            logger.debug("Could not get current username: {}", e.getMessage());
        }
        return "System";
    }

    /**
     * Get client IP address from HTTP request
     */
    private String getClientIpAddress() {
        if (request == null) {
            return null;
        }

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // Handle multiple IPs (take the first one)
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }

    /**
     * Get user agent from HTTP request
     */
    private String getUserAgent() {
        if (request == null) {
            return null;
        }
        return request.getHeader("User-Agent");
    }

    /**
     * Convert object to JSON string
     */
    private String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.warn("Failed to convert object to JSON: {}", e.getMessage());
            return object.toString();
        }
    }

    /**
     * Calculate delta between old and new values
     * Returns a human-readable summary of changes
     */
    private String calculateDelta(Object oldValue, Object newValue) {
        // TODO: Implement sophisticated delta calculation
        // For now, return a simple message
        return "Record updated";
    }
}
