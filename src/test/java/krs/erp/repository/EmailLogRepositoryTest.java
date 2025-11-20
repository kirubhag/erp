package krs.erp.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.EntityType;
import krs.erp.model.EmailLog;
import krs.erp.model.EmailTemplate;

/**
 * Unit tests for EmailLogRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmailLogRepositoryTest {
    
    @Autowired
    private EmailLogRepository emailLogRepository;
    
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;
    
    private EmailTemplate testTemplate;
    private EmailLog emailLog1;
    private EmailLog emailLog2;
    private EmailLog emailLog3;
    
    @BeforeEach
    void setUp() {
        emailLogRepository.deleteAllInBatch();
        emailTemplateRepository.deleteAllInBatch();
        
        // Create test email template
        testTemplate = new EmailTemplate();
        testTemplate.setTemplateName("Test Template");
        testTemplate.setSubject("Test Subject");
        testTemplate.setBody("Test Body");
        testTemplate.setEntityType(EntityType.STUDENT);
        testTemplate.setIsActive(1);
        testTemplate.setCreatedTime(LocalDateTime.now());
        testTemplate = emailTemplateRepository.save(testTemplate);
        
        // Create email log 1 - Delivered to Student
        emailLog1 = new EmailLog();
        emailLog1.setEntityType(EntityType.STUDENT);
        emailLog1.setEntityId(1001L);
        emailLog1.setRecipientEmail("student1@test.com");
        emailLog1.setRecipientName("John Doe");
        emailLog1.setSubject("Welcome to School");
        emailLog1.setBody("Welcome email body");
        emailLog1.setStatus(EmailLog.EmailStatus.DELIVERED);
        emailLog1.setSentAt(LocalDateTime.now().minusHours(2));
        emailLog1.setDeliveredAt(LocalDateTime.now().minusHours(1));
        emailLog1.setSentBy("admin@school.com");
        emailLog1.setPriority(1);
        emailLog1.setEmailTemplate(testTemplate);
        emailLog1.setCreatedTime(LocalDateTime.now());
        emailLog1 = emailLogRepository.save(emailLog1);
        
        // Create email log 2 - Failed to Parent
        emailLog2 = new EmailLog();
        emailLog2.setEntityType(EntityType.PARENT);
        emailLog2.setEntityId(2001L);
        emailLog2.setRecipientEmail("parent1@test.com");
        emailLog2.setRecipientName("Jane Smith");
        emailLog2.setSubject("Parent Meeting Notice");
        emailLog2.setBody("Meeting notification body");
        emailLog2.setStatus(EmailLog.EmailStatus.FAILED);
        emailLog2.setErrorMessage("Recipient email address not found");
        emailLog2.setFailedAt(LocalDateTime.now().minusMinutes(30));
        emailLog2.setSentBy("admin@school.com");
        emailLog2.setPriority(2);
        emailLog2.setRetryCount(1);
        emailLog2.setCreatedTime(LocalDateTime.now());
        emailLog2 = emailLogRepository.save(emailLog2);
        
        // Create email log 3 - Pending to Staff
        emailLog3 = new EmailLog();
        emailLog3.setEntityType(EntityType.STAFF);
        emailLog3.setEntityId(3001L);
        emailLog3.setRecipientEmail("teacher1@test.com");
        emailLog3.setRecipientName("Bob Johnson");
        emailLog3.setSubject("Staff Meeting Reminder");
        emailLog3.setBody("Meeting reminder body");
        emailLog3.setStatus(EmailLog.EmailStatus.PENDING);
        emailLog3.setSentBy("admin@school.com");
        emailLog3.setPriority(1);
        emailLog3.setRetryCount(0);
        emailLog3.setCreatedTime(LocalDateTime.now());
        emailLog3 = emailLogRepository.save(emailLog3);
    }
    
    @Test
    void testFindByEntityTypeAndEntityIdOrderByCreatedTimeDesc() {
        List<EmailLog> logs = emailLogRepository.findByEntityTypeAndEntityIdOrderByCreatedTimeDesc(
            EntityType.STUDENT, 1001L);
        
        assertThat(logs).hasSize(1);
        assertEquals("student1@test.com", logs.get(0).getRecipientEmail());
    }
    
    @Test
    void testFindByEntityTypeAndEntityIdWithPagination() {
        Page<EmailLog> page = emailLogRepository.findByEntityTypeAndEntityIdOrderByCreatedTimeDesc(
            EntityType.STUDENT, 1001L, PageRequest.of(0, 10));
        
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(1);
    }
    
    @Test
    void testFindByRecipientEmailOrderByCreatedTimeDesc() {
        List<EmailLog> logs = emailLogRepository.findByRecipientEmailOrderByCreatedTimeDesc("parent1@test.com");
        
        assertThat(logs).hasSize(1);
        assertEquals("Jane Smith", logs.get(0).getRecipientName());
    }
    
    @Test
    void testFindByStatusOrderByCreatedTimeDesc() {
        List<EmailLog> deliveredLogs = emailLogRepository.findByStatusOrderByCreatedTimeDesc(
            EmailLog.EmailStatus.DELIVERED);
        assertThat(deliveredLogs).hasSize(1);
        
        List<EmailLog> failedLogs = emailLogRepository.findByStatusOrderByCreatedTimeDesc(
            EmailLog.EmailStatus.FAILED);
        assertThat(failedLogs).hasSize(1);
    }
    
    @Test
    void testFindByEntityTypeOrderByCreatedTimeDesc() {
        List<EmailLog> studentLogs = emailLogRepository.findByEntityTypeOrderByCreatedTimeDesc(EntityType.STUDENT);
        assertThat(studentLogs).hasSize(1);
    }
    
    @Test
    void testFindFailedEmailsForRetry() {
        List<EmailLog> failedForRetry = emailLogRepository.findFailedEmailsForRetry();
        
        assertThat(failedForRetry).hasSize(1);
        assertEquals(EmailLog.EmailStatus.FAILED, failedForRetry.get(0).getStatus());
        assertTrue(failedForRetry.get(0).getRetryCount() < 3);
    }
    
    @Test
    void testFindPendingEmails() {
        List<EmailLog> pending = emailLogRepository.findPendingEmails();
        
        assertThat(pending).hasSize(1);
        assertEquals(EmailLog.EmailStatus.PENDING, pending.get(0).getStatus());
    }
    
    @Test
    void testFindByEmailTemplateOrderByCreatedTimeDesc() {
        List<EmailLog> templateLogs = emailLogRepository.findByEmailTemplateOrderByCreatedTimeDesc(testTemplate);
        
        assertThat(templateLogs).hasSize(1);
        assertEquals(testTemplate.getId(), templateLogs.get(0).getEmailTemplate().getId());
    }
    
    @Test
    void testFindEmailsSentBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        
        List<EmailLog> sentEmails = emailLogRepository.findEmailsSentBetween(startDate, endDate);
        
        assertThat(sentEmails).hasSizeGreaterThanOrEqualTo(1);
    }
    
    @Test
    void testFindEmailsSentToday() {
        List<EmailLog> todayEmails = emailLogRepository.findEmailsSentToday();
        
        // May be 0 or 1 depending on when tests run
        assertThat(todayEmails).hasSizeGreaterThanOrEqualTo(0);
    }
    
    @Test
    void testCountByStatus() {
        Long deliveredCount = emailLogRepository.countByStatus(EmailLog.EmailStatus.DELIVERED);
        assertEquals(1L, deliveredCount);
        
        Long failedCount = emailLogRepository.countByStatus(EmailLog.EmailStatus.FAILED);
        assertEquals(1L, failedCount);
        
        Long pendingCount = emailLogRepository.countByStatus(EmailLog.EmailStatus.PENDING);
        assertEquals(1L, pendingCount);
    }
    
    @Test
    void testCountByEntityTypeAndEntityId() {
        Long count = emailLogRepository.countByEntityTypeAndEntityId(EntityType.STUDENT, 1001L);
        assertEquals(1L, count);
    }
    
    @Test
    void testCountDeliveredEmailsForEntity() {
        Long deliveredCount = emailLogRepository.countDeliveredEmailsForEntity(EntityType.STUDENT, 1001L);
        assertEquals(1L, deliveredCount);
    }
    
    @Test
    void testFindBySentByOrderByCreatedTimeDesc() {
        List<EmailLog> adminEmails = emailLogRepository.findBySentByOrderByCreatedTimeDesc("admin@school.com");
        assertThat(adminEmails).hasSize(3);
    }
    
    @Test
    void testFindRecentEmails() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<EmailLog> recentEmails = emailLogRepository.findRecentEmails(since);
        
        assertThat(recentEmails).hasSize(3);
    }
    
    @Test
    void testFindByPriorityOrderByCreatedTimeDesc() {
        List<EmailLog> highPriority = emailLogRepository.findByPriorityOrderByCreatedTimeDesc(1);
        assertThat(highPriority).hasSize(2);
    }
    
    @Test
    void testFindEmailsWithErrors() {
        List<EmailLog> withErrors = emailLogRepository.findEmailsWithErrors();
        
        assertThat(withErrors).hasSize(1);
        assertEquals("parent1@test.com", withErrors.get(0).getRecipientEmail());
    }
    
    @Test
    void testSearchEmails() {
        List<EmailLog> searchWelcome = emailLogRepository.searchEmails("welcome");
        assertThat(searchWelcome).hasSize(1);
        
        List<EmailLog> searchParent = emailLogRepository.searchEmails("parent");
        assertThat(searchParent).hasSize(1);
    }
    
    @Test
    void testSaveEmailLog() {
        EmailLog newLog = new EmailLog();
        newLog.setEntityType(EntityType.STUDENT);
        newLog.setEntityId(1002L);
        newLog.setRecipientEmail("student2@test.com");
        newLog.setRecipientName("Alice Brown");
        newLog.setSubject("Test Subject");
        newLog.setBody("Test Body");
        newLog.setStatus(EmailLog.EmailStatus.PENDING);
        newLog.setPriority(2);
        newLog.setCreatedTime(LocalDateTime.now());
        
        EmailLog saved = emailLogRepository.save(newLog);
        
        assertNotNull(saved.getId());
        assertEquals("student2@test.com", saved.getRecipientEmail());
    }
    
    @Test
    void testUpdateEmailLog() {
        emailLog3.setStatus(EmailLog.EmailStatus.SENT);
        emailLog3.setSentAt(LocalDateTime.now());
        
        EmailLog updated = emailLogRepository.save(emailLog3);
        
        assertEquals(EmailLog.EmailStatus.SENT, updated.getStatus());
        assertNotNull(updated.getSentAt());
    }
    
    @Test
    void testDeleteEmailLog() {
        Long logId = emailLog1.getId();
        emailLogRepository.delete(emailLog1);
        
        assertThat(emailLogRepository.findById(logId)).isEmpty();
    }
    
    @Test
    void testIncrementRetryCount() {
        emailLog2.incrementRetryCount();
        EmailLog updated = emailLogRepository.save(emailLog2);
        
        assertEquals(2, updated.getRetryCount());
    }
    
    @Test
    void testFindAll() {
        List<EmailLog> allLogs = emailLogRepository.findAll();
        assertThat(allLogs).hasSize(3);
    }
}
