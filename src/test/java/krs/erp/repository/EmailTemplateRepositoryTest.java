package krs.erp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import krs.erp.model.EmailTemplate;

/**
 * Unit tests for EmailTemplateRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmailTemplateRepositoryTest {
    
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;
    
    private EmailTemplate template1;
    private EmailTemplate template2;
    private EmailTemplate template3;
    
    @BeforeEach
    void setUp() {
        emailTemplateRepository.deleteAllInBatch();
        
        // Create template 1 - Student Welcome (Popular)
        template1 = new EmailTemplate();
        template1.setTemplateName("Student Welcome");
        template1.setSubject("Welcome to Our School!");
        template1.setBody("Dear {{firstName}}, Welcome to our school. We're excited to have you!");
        template1.setEntityType(EntityType.STUDENT);
        template1.setDescription("Welcome email for new students");
        template1.setUsageCount(50);
        template1.setLastUsed(LocalDateTime.now().minusDays(2));
        template1.setIsActive(1);
        template1.setCreatedTime(LocalDateTime.now());
        template1 = emailTemplateRepository.save(template1);
        
        // Create template 2 - Parent Notification (Recently Used)
        template2 = new EmailTemplate();
        template2.setTemplateName("Parent Notification");
        template2.setSubject("Important Update About {{studentName}}");
        template2.setBody("Dear Parent, This is to inform you about {{message}}");
        template2.setEntityType(EntityType.PARENT);
        template2.setDescription("General notification template for parents");
        template2.setUsageCount(25);
        template2.setLastUsed(LocalDateTime.now().minusHours(5));
        template2.setIsActive(1);
        template2.setCreatedTime(LocalDateTime.now());
        template2 = emailTemplateRepository.save(template2);
        
        // Create template 3 - Staff Announcement (Inactive/Unused)
        template3 = new EmailTemplate();
        template3.setTemplateName("Staff Announcement");
        template3.setSubject("Staff Meeting: {{topic}}");
        template3.setBody("Dear Staff, Please be informed about {{details}}");
        template3.setEntityType(EntityType.STAFF);
        template3.setDescription("Template for staff announcements");
        template3.setUsageCount(0);
        template3.setIsActive(0);
        template3.setCreatedTime(LocalDateTime.now());
        template3 = emailTemplateRepository.save(template3);
    }
    
    @Test
    void testFindByTemplateName() {
        Optional<EmailTemplate> found = emailTemplateRepository.findByTemplateName("Student Welcome");
        
        assertThat(found).isPresent();
        assertEquals("Welcome to Our School!", found.get().getSubject());
    }
    
    @Test
    void testFindByTemplateNameIgnoreCase() {
        Optional<EmailTemplate> found = emailTemplateRepository.findByTemplateNameIgnoreCase("student welcome");
        
        assertThat(found).isPresent();
        assertEquals("Student Welcome", found.get().getTemplateName());
    }
    
    @Test
    void testFindByEntityType() {
        List<EmailTemplate> studentTemplates = emailTemplateRepository.findByEntityType(EntityType.STUDENT);
        assertThat(studentTemplates).hasSize(1);
        assertEquals("Student Welcome", studentTemplates.get(0).getTemplateName());
    }
    
    @Test
    void testFindByEntityTypeWithPagination() {
        Page<EmailTemplate> page = emailTemplateRepository.findByEntityType(
            EntityType.STUDENT, PageRequest.of(0, 10));
        
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(1);
    }
    
    @Test
    void testFindAllActiveTemplates() {
        List<EmailTemplate> activeTemplates = emailTemplateRepository.findAllActiveTemplates();
        assertThat(activeTemplates).hasSize(2);
        assertThat(activeTemplates).allMatch(t -> t.getIsActive() == 1);
    }
    
    @Test
    void testFindActiveTemplatesByEntityType() {
        List<EmailTemplate> activeStudentTemplates = emailTemplateRepository.findActiveTemplatesByEntityType(
            EntityType.STUDENT);
        assertThat(activeStudentTemplates).hasSize(1);
        
        List<EmailTemplate> activeStaffTemplates = emailTemplateRepository.findActiveTemplatesByEntityType(
            EntityType.STAFF);
        assertThat(activeStaffTemplates).isEmpty();
    }
    
    @Test
    void testFindByCreatedByIgnoreCase() {
        template1.setCreatedBy("admin@test.com");
        emailTemplateRepository.save(template1);
        
        List<EmailTemplate> templates = emailTemplateRepository.findByCreatedByIgnoreCase("admin@test.com");
        assertThat(templates).hasSize(1);
    }
    
    @Test
    void testSearchTemplates() {
        List<EmailTemplate> searchWelcome = emailTemplateRepository.searchTemplates("welcome");
        assertThat(searchWelcome).hasSize(1);
        
        List<EmailTemplate> searchNotification = emailTemplateRepository.searchTemplates("notification");
        assertThat(searchNotification).hasSize(1);
    }
    
    @Test
    void testFindPopularTemplates() {
        List<EmailTemplate> popular = emailTemplateRepository.findPopularTemplates(20);
        assertThat(popular).hasSize(2);
        
        // Verify ordering by usage count
        assertEquals(50, popular.get(0).getUsageCount());
        assertEquals(25, popular.get(1).getUsageCount());
    }
    
    @Test
    void testFindMostUsedTemplates() {
        List<EmailTemplate> mostUsed = emailTemplateRepository.findMostUsedTemplates();
        assertThat(mostUsed).hasSize(2);
        
        // Verify ordering by usage count descending
        assertTrue(mostUsed.get(0).getUsageCount() >= mostUsed.get(1).getUsageCount());
    }
    
    @Test
    void testCountActiveTemplatesByEntityType() {
        Long studentCount = emailTemplateRepository.countActiveTemplatesByEntityType(EntityType.STUDENT);
        assertEquals(1L, studentCount);
        
        Long parentCount = emailTemplateRepository.countActiveTemplatesByEntityType(EntityType.PARENT);
        assertEquals(1L, parentCount);
        
        Long staffCount = emailTemplateRepository.countActiveTemplatesByEntityType(EntityType.STAFF);
        assertEquals(0L, staffCount);
    }
    
    @Test
    void testFindUnusedTemplates() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        List<EmailTemplate> unused = emailTemplateRepository.findUnusedTemplates(cutoffDate);
        
        assertThat(unused).hasSizeGreaterThanOrEqualTo(1);
    }
    
    @Test
    void testExistsByTemplateNameIgnoreCase() {
        boolean exists = emailTemplateRepository.existsByTemplateNameIgnoreCase("Student Welcome", null);
        assertTrue(exists);
        
        boolean notExists = emailTemplateRepository.existsByTemplateNameIgnoreCase("Non-Existent Template", null);
        assertFalse(notExists);
        
        // Test with ID exclusion
        boolean existsExcludingSelf = emailTemplateRepository.existsByTemplateNameIgnoreCase(
            "Student Welcome", template1.getId());
        assertFalse(existsExcludingSelf);
    }
    
    @Test
    void testSaveEmailTemplate() {
        EmailTemplate newTemplate = new EmailTemplate();
        newTemplate.setTemplateName("Fee Reminder");
        newTemplate.setSubject("Fee Payment Reminder");
        newTemplate.setBody("Dear Parent, This is a reminder for pending fees.");
        newTemplate.setEntityType(EntityType.PARENT);
        newTemplate.setDescription("Monthly fee reminder template");
        newTemplate.setUsageCount(0);
        newTemplate.setIsActive(1);
        newTemplate.setCreatedTime(LocalDateTime.now());
        
        EmailTemplate saved = emailTemplateRepository.save(newTemplate);
        
        assertNotNull(saved.getId());
        assertEquals("Fee Reminder", saved.getTemplateName());
    }
    
    @Test
    void testUpdateEmailTemplate() {
        template1.setSubject("Updated Welcome Message");
        template1.setUsageCount(template1.getUsageCount() + 1);
        template1.setLastUsed(LocalDateTime.now());
        template1.setModifiedTime(LocalDateTime.now());
        
        EmailTemplate updated = emailTemplateRepository.save(template1);
        
        assertEquals("Updated Welcome Message", updated.getSubject());
        assertEquals(51, updated.getUsageCount());
    }
    
    @Test
    void testDeleteEmailTemplate() {
        Long templateId = template3.getId();
        emailTemplateRepository.delete(template3);
        
        assertThat(emailTemplateRepository.findById(templateId)).isEmpty();
    }
    
    @Test
    void testIncrementUsageCount() {
        template1.incrementUsageCount();
        EmailTemplate updated = emailTemplateRepository.save(template1);
        
        assertEquals(51, updated.getUsageCount());
        assertNotNull(updated.getLastUsed());
    }
    
    @Test
    void testFindAll() {
        List<EmailTemplate> allTemplates = emailTemplateRepository.findAll();
        assertThat(allTemplates).hasSize(3);
    }
}
