package krs.erp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.EmailLog;
import krs.erp.model.EmailTemplate;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    
    // Find by entity type and entity ID
    List<EmailLog> findByEntityTypeAndEntityIdOrderByCreatedTimeDesc(EmailTemplate.EntityType entityType, Long entityId);
    
    // Find by entity type and entity ID with pagination
    Page<EmailLog> findByEntityTypeAndEntityIdOrderByCreatedTimeDesc(EmailTemplate.EntityType entityType, Long entityId, Pageable pageable);
    
    // Find by recipient email
    List<EmailLog> findByRecipientEmailOrderByCreatedTimeDesc(String recipientEmail);
    
    // Find by recipient email with pagination
    Page<EmailLog> findByRecipientEmailOrderByCreatedTimeDesc(String recipientEmail, Pageable pageable);
    
    // Find by status
    List<EmailLog> findByStatusOrderByCreatedTimeDesc(EmailLog.EmailStatus status);
    
    // Find by status with pagination
    Page<EmailLog> findByStatusOrderByCreatedTimeDesc(EmailLog.EmailStatus status, Pageable pageable);
    
    // Find by entity type
    List<EmailLog> findByEntityTypeOrderByCreatedTimeDesc(EmailTemplate.EntityType entityType);
    
    // Find by entity type with pagination
    Page<EmailLog> findByEntityTypeOrderByCreatedTimeDesc(EmailTemplate.EntityType entityType, Pageable pageable);
    
    // Find failed emails for retry
    @Query("SELECT e FROM EmailLog e WHERE e.status = 'FAILED' AND e.retryCount < 3 ORDER BY e.createdTime ASC")
    List<EmailLog> findFailedEmailsForRetry();
    
    // Find pending emails
    @Query("SELECT e FROM EmailLog e WHERE e.status = 'PENDING' ORDER BY e.priority ASC, e.createdTime ASC")
    List<EmailLog> findPendingEmails();
    
    // Find emails by template
    List<EmailLog> findByEmailTemplateOrderByCreatedTimeDesc(EmailTemplate emailTemplate);
    
    // Find emails sent between dates
    @Query("SELECT e FROM EmailLog e WHERE e.sentAt BETWEEN :startDate AND :endDate ORDER BY e.sentAt DESC")
    List<EmailLog> findEmailsSentBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find emails sent today
    @Query("SELECT e FROM EmailLog e WHERE e.sentAt IS NOT NULL AND FUNCTION('DATE', e.sentAt) = CURRENT_DATE ORDER BY e.sentAt DESC")
    List<EmailLog> findEmailsSentToday();
    
    // Count emails by status
    @Query("SELECT COUNT(e) FROM EmailLog e WHERE e.status = :status")
    Long countByStatus(@Param("status") EmailLog.EmailStatus status);
    
    // Count emails for entity
    @Query("SELECT COUNT(e) FROM EmailLog e WHERE e.entityType = :entityType AND e.entityId = :entityId")
    Long countByEntityTypeAndEntityId(@Param("entityType") EmailTemplate.EntityType entityType, @Param("entityId") Long entityId);
    
    // Count delivered emails for entity
    @Query("SELECT COUNT(e) FROM EmailLog e WHERE e.entityType = :entityType AND e.entityId = :entityId AND e.status IN ('DELIVERED', 'OPENED')")
    Long countDeliveredEmailsForEntity(@Param("entityType") EmailTemplate.EntityType entityType, @Param("entityId") Long entityId);
    
    // Find emails by sent by
    List<EmailLog> findBySentByOrderByCreatedTimeDesc(String sentBy);
    
    // Find recent emails (last 24 hours)
    @Query("SELECT e FROM EmailLog e WHERE e.createdTime >= :since ORDER BY e.createdTime DESC")
    List<EmailLog> findRecentEmails(@Param("since") LocalDateTime since);
    
    // Get email statistics
    @Query("SELECT new krs.erp.dto.EmailStatistics(" +
           "COUNT(e), " +
           "SUM(CASE WHEN e.status = 'SENT' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN e.status = 'DELIVERED' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN e.status = 'OPENED' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN e.status = 'FAILED' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN e.status = 'BOUNCED' THEN 1 ELSE 0 END)" +
           ") FROM EmailLog e WHERE e.createdTime >= :since")
    Object getEmailStatistics(@Param("since") LocalDateTime since);
    
    // Find emails by priority
    List<EmailLog> findByPriorityOrderByCreatedTimeDesc(Integer priority);
    
    // Delete old logs (cleanup)
    @Query("DELETE FROM EmailLog e WHERE e.createdTime < :cutoffDate AND e.status NOT IN ('PENDING', 'FAILED')")
    void deleteOldLogs(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Find emails with errors
    @Query("SELECT e FROM EmailLog e WHERE e.errorMessage IS NOT NULL ORDER BY e.createdTime DESC")
    List<EmailLog> findEmailsWithErrors();
    
    // Search emails by subject or recipient
    @Query("SELECT e FROM EmailLog e WHERE LOWER(e.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.recipientEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.recipientName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY e.createdTime DESC")
    List<EmailLog> searchEmails(@Param("searchTerm") String searchTerm);
}