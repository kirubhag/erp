package krs.erp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for EmailTemplate model
 */
class EmailTemplateTest {

    private EmailTemplate emailTemplate;

    @BeforeEach
    void setUp() {
        emailTemplate = new EmailTemplate();
    }

    @Test
    void testEmailTemplateCreation() {
        emailTemplate.setTemplateName("Welcome Template");
        emailTemplate.setSubject("Welcome to Our School");
        emailTemplate.setBody("Dear {{name}}, welcome to our school!");
        emailTemplate.setEntityType(krs.erp.enums.EntityType.STUDENT);

        assertEquals("Welcome Template", emailTemplate.getTemplateName());
        assertEquals("Welcome to Our School", emailTemplate.getSubject());
        assertEquals("Dear {{name}}, welcome to our school!", emailTemplate.getBody());
        assertEquals(krs.erp.enums.EntityType.STUDENT, emailTemplate.getEntityType());
    }

    @Test
    void testEmailTemplateStatus() {
        // Test default active status
        assertEquals(1, emailTemplate.getIsActive());

        emailTemplate.setIsActive(0);
        assertEquals(0, emailTemplate.getIsActive());
    }

    @Test
    void testEmailTemplateVariables() {
        String variables = "{{name}}, {{email}}, {{date}}";
        emailTemplate.setAvailableVariables(variables);
        assertEquals(variables, emailTemplate.getAvailableVariables());
    }

    @Test
    void testEmailTemplateDescription() {
        String description = "Template for welcoming new students";
        emailTemplate.setDescription(description);
        assertEquals(description, emailTemplate.getDescription());
    }

    @Test
    void testEmailTemplateAuditFields() {
        emailTemplate.setCreatedBy("admin");
        emailTemplate.setModifiedBy("user1");
        emailTemplate.setOwnerId(400L);

        assertEquals("admin", emailTemplate.getCreatedBy());
        assertEquals("user1", emailTemplate.getModifiedBy());
        assertEquals(400L, emailTemplate.getOwnerId());
    }

    @Test
    void testEmailTemplateEquality() {
        EmailTemplate template1 = new EmailTemplate();
        EmailTemplate template2 = new EmailTemplate();
        
        template1.setId(1L);
        template2.setId(1L);
        
        assertEquals(template1.getId(), template2.getId());
    }

    @Test
    void testEmailTemplateToString() {
        emailTemplate.setTemplateName("Test Template");
        emailTemplate.setSubject("Test Subject");
        
        String toString = emailTemplate.toString();
        assertNotNull(toString);
    }

    @Test
    void testEmailTemplateValidation() {
        // Test required fields
        emailTemplate.setTemplateName("Required Name");
        emailTemplate.setSubject("Required Subject");
        
        assertNotNull(emailTemplate.getTemplateName());
        assertNotNull(emailTemplate.getSubject());
        
        // Name and subject should not be empty
        assertFalse(emailTemplate.getTemplateName().isEmpty());
        assertFalse(emailTemplate.getSubject().isEmpty());
    }
}