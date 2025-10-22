package krs.erp.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.EmailTemplate;
import krs.erp.model.Student;
import krs.erp.repository.EmailTemplateRepository;
import krs.erp.repository.StudentRepository;

@Service
@Transactional
public class EmailTemplateService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailTemplateService.class);
    
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    /**
     * Create new email template
     */
    public EmailTemplate createTemplate(EmailTemplate template) {
        // Check if template name already exists
        if (emailTemplateRepository.existsByTemplateNameIgnoreCase(template.getTemplateName(), null)) {
            throw new RuntimeException("Template with name '" + template.getTemplateName() + "' already exists");
        }
        
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        template.setUsageCount(0);
        
        // Set available variables based on entity type
        template.setAvailableVariables(getAvailableVariablesForEntityType(template.getEntityType()));
        
        EmailTemplate savedTemplate = emailTemplateRepository.save(template);
        logger.info("Created email template: {} for entity type: {}", savedTemplate.getTemplateName(), savedTemplate.getEntityType());
        
        return savedTemplate;
    }
    
    /**
     * Update existing email template
     */
    public EmailTemplate updateTemplate(Long id, EmailTemplate templateDetails) {
        Optional<EmailTemplate> optionalTemplate = emailTemplateRepository.findById(id);
        
        if (optionalTemplate.isPresent()) {
            EmailTemplate template = optionalTemplate.get();
            
            // Check if template name already exists (excluding current template)
            if (emailTemplateRepository.existsByTemplateNameIgnoreCase(templateDetails.getTemplateName(), id)) {
                throw new RuntimeException("Template with name '" + templateDetails.getTemplateName() + "' already exists");
            }
            
            template.setTemplateName(templateDetails.getTemplateName());
            template.setSubject(templateDetails.getSubject());
            template.setBody(templateDetails.getBody());
            template.setEntityType(templateDetails.getEntityType());
            template.setDescription(templateDetails.getDescription());
            template.setIsActive(templateDetails.getIsActive());
            template.setUpdatedAt(LocalDateTime.now());
            
            // Update available variables based on entity type
            template.setAvailableVariables(getAvailableVariablesForEntityType(template.getEntityType()));
            
            EmailTemplate updatedTemplate = emailTemplateRepository.save(template);
            logger.info("Updated email template: {} (ID: {})", updatedTemplate.getTemplateName(), id);
            
            return updatedTemplate;
        }
        
        throw new RuntimeException("Template not found with id: " + id);
    }
    
    /**
     * Get template by ID
     */
    public Optional<EmailTemplate> getTemplateById(Long id) {
        return emailTemplateRepository.findById(id);
    }
    
    /**
     * Get all templates with pagination
     */
    public Page<EmailTemplate> getAllTemplates(Pageable pageable) {
        return emailTemplateRepository.findAll(pageable);
    }
    
    /**
     * Get active templates by entity type
     */
    public List<EmailTemplate> getActiveTemplatesByEntityType(EmailTemplate.EntityType entityType) {
        return emailTemplateRepository.findActiveTemplatesByEntityType(entityType);
    }
    
    /**
     * Get all active templates
     */
    public List<EmailTemplate> getAllActiveTemplates() {
        return emailTemplateRepository.findAllActiveTemplates();
    }
    
    /**
     * Search templates
     */
    public List<EmailTemplate> searchTemplates(String searchTerm) {
        return emailTemplateRepository.searchTemplates(searchTerm);
    }
    
    /**
     * Get popular templates
     */
    public List<EmailTemplate> getPopularTemplates(Integer minUsage) {
        return emailTemplateRepository.findPopularTemplates(minUsage != null ? minUsage : 5);
    }
    
    /**
     * Delete template
     */
    public void deleteTemplate(Long id) {
        if (emailTemplateRepository.existsById(id)) {
            emailTemplateRepository.deleteById(id);
            logger.info("Deleted email template with ID: {}", id);
        } else {
            throw new RuntimeException("Template not found with id: " + id);
        }
    }
    
    /**
     * Process template with variable replacement
     */
    public ProcessedTemplate processTemplate(Long templateId, Long entityId) {
        Optional<EmailTemplate> templateOpt = emailTemplateRepository.findById(templateId);
        if (!templateOpt.isPresent()) {
            throw new RuntimeException("Template not found with id: " + templateId);
        }
        
        EmailTemplate template = templateOpt.get();
        Map<String, String> variables = getEntityVariables(template.getEntityType(), entityId);
        
        String processedSubject = replaceVariables(template.getSubject(), variables);
        String processedBody = replaceVariables(template.getBody(), variables);
        
        // Increment usage count
        template.incrementUsageCount();
        emailTemplateRepository.save(template);
        
        return new ProcessedTemplate(processedSubject, processedBody, template);
    }
    
    /**
     * Process template with custom variables
     */
    public ProcessedTemplate processTemplate(Long templateId, Map<String, String> customVariables) {
        Optional<EmailTemplate> templateOpt = emailTemplateRepository.findById(templateId);
        if (!templateOpt.isPresent()) {
            throw new RuntimeException("Template not found with id: " + templateId);
        }
        
        EmailTemplate template = templateOpt.get();
        
        String processedSubject = replaceVariables(template.getSubject(), customVariables);
        String processedBody = replaceVariables(template.getBody(), customVariables);
        
        // Increment usage count
        template.incrementUsageCount();
        emailTemplateRepository.save(template);
        
        return new ProcessedTemplate(processedSubject, processedBody, template);
    }
    
    /**
     * Get entity variables based on entity type and ID
     */
    private Map<String, String> getEntityVariables(EmailTemplate.EntityType entityType, Long entityId) {
        Map<String, String> variables = new HashMap<>();
        
        switch (entityType) {
            case STUDENT:
                Optional<Student> studentOpt = studentRepository.findById(entityId);
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    variables.put("FIRST_NAME", student.getFirstName() != null ? student.getFirstName() : "");
                    variables.put("LAST_NAME", student.getLastName() != null ? student.getLastName() : "");
                    variables.put("FULL_NAME", student.getFullName() != null ? student.getFullName() : "");
                    variables.put("STUDENT_ID", student.getStudentId() != null ? student.getStudentId() : "");
                    variables.put("EMAIL", student.getEmail() != null ? student.getEmail() : "");
                    variables.put("PHONE", student.getPhone() != null ? student.getPhone() : "");
                    variables.put("GRADE_LEVEL", student.getGradeLevel() != null ? student.getGradeLevel().toString() : "");
                    variables.put("ADDRESS", student.getFullAddress() != null ? student.getFullAddress() : "");
                    variables.put("CITY", student.getCity() != null ? student.getCity() : "");
                    variables.put("STATE", student.getState() != null ? student.getState() : "");
                    variables.put("POSTAL_CODE", student.getPostalCode() != null ? student.getPostalCode() : "");
                    variables.put("COUNTRY", student.getCountry() != null ? student.getCountry() : "");
                    variables.put("DATE_OF_BIRTH", student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : "");
                    variables.put("ENROLLMENT_DATE", student.getEnrollmentDate() != null ? student.getEnrollmentDate().toString() : "");
                    variables.put("ENROLLMENT_STATUS", student.getEnrollmentStatus() != null ? student.getEnrollmentStatus().toString() : "");
                    variables.put("GENDER", student.getGender() != null ? student.getGender().toString() : "");
                    variables.put("AGE", String.valueOf(student.getAge()));
                    variables.put("EMERGENCY_CONTACT_NAME", student.getEmergencyContactName() != null ? student.getEmergencyContactName() : "");
                    variables.put("EMERGENCY_CONTACT_PHONE", student.getEmergencyContactPhone() != null ? student.getEmergencyContactPhone() : "");
                    variables.put("EMERGENCY_CONTACT_RELATION", student.getEmergencyContactRelation() != null ? student.getEmergencyContactRelation() : "");
                }
                break;
            case PARENT:
                // Add parent-specific variables when Parent entity is available
                break;
            case ATTENDANCE:
                // Add attendance-specific variables when Attendance entity is available
                break;
            case HEALTH:
                // Add health-specific variables when Health entity is available
                break;
            case GENERAL:
                // General templates don't need specific entity variables
                break;
            case NOTIFICATION:
                // Notification templates use custom variables
                break;
        }
        
        // Add common system variables
        variables.put("CURRENT_DATE", LocalDateTime.now().toLocalDate().toString());
        variables.put("CURRENT_TIME", LocalDateTime.now().toLocalTime().toString());
        variables.put("SYSTEM_NAME", "School ERP System");
        
        return variables;
    }
    
    /**
     * Replace variables in text using {{VARIABLE_NAME}} format
     */
    private String replaceVariables(String text, Map<String, String> variables) {
        if (text == null || variables == null) {
            return text;
        }
        
        String result = text;
        Pattern pattern = Pattern.compile("\\{\\{([^}]+)\\}\\}");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            String variableValue = variables.getOrDefault(variableName, "{{" + variableName + "}}");
            result = result.replace("{{" + variableName + "}}", variableValue);
        }
        
        return result;
    }
    
    /**
     * Get available variables for entity type
     */
    private String getAvailableVariablesForEntityType(EmailTemplate.EntityType entityType) {
        StringBuilder variables = new StringBuilder();
        
        switch (entityType) {
            case STUDENT:
                variables.append("{{FIRST_NAME}} - Student's first name\n");
                variables.append("{{LAST_NAME}} - Student's last name\n");
                variables.append("{{FULL_NAME}} - Student's full name\n");
                variables.append("{{STUDENT_ID}} - Student's ID\n");
                variables.append("{{EMAIL}} - Student's email address\n");
                variables.append("{{PHONE}} - Student's phone number\n");
                variables.append("{{GRADE_LEVEL}} - Student's grade level\n");
                variables.append("{{ADDRESS}} - Student's full address\n");
                variables.append("{{CITY}} - Student's city\n");
                variables.append("{{STATE}} - Student's state\n");
                variables.append("{{POSTAL_CODE}} - Student's postal code\n");
                variables.append("{{COUNTRY}} - Student's country\n");
                variables.append("{{DATE_OF_BIRTH}} - Student's date of birth\n");
                variables.append("{{ENROLLMENT_DATE}} - Student's enrollment date\n");
                variables.append("{{ENROLLMENT_STATUS}} - Student's enrollment status\n");
                variables.append("{{GENDER}} - Student's gender\n");
                variables.append("{{AGE}} - Student's age\n");
                variables.append("{{EMERGENCY_CONTACT_NAME}} - Emergency contact name\n");
                variables.append("{{EMERGENCY_CONTACT_PHONE}} - Emergency contact phone\n");
                variables.append("{{EMERGENCY_CONTACT_RELATION}} - Emergency contact relation\n");
                break;
            case PARENT:
                variables.append("{{PARENT_NAME}} - Parent's name\n");
                variables.append("{{PARENT_EMAIL}} - Parent's email\n");
                variables.append("{{PARENT_PHONE}} - Parent's phone\n");
                variables.append("{{RELATIONSHIP}} - Relationship to student\n");
                break;
            case ATTENDANCE:
                variables.append("{{ATTENDANCE_DATE}} - Attendance date\n");
                variables.append("{{STATUS}} - Attendance status\n");
                variables.append("{{REMARKS}} - Attendance remarks\n");
                break;
            case HEALTH:
                variables.append("{{HEALTH_RECORD_DATE}} - Health record date\n");
                variables.append("{{HEALTH_STATUS}} - Health status\n");
                variables.append("{{NOTES}} - Health notes\n");
                break;
            case GENERAL:
                variables.append("Use system variables only for general templates\n");
                break;
            case NOTIFICATION:
                variables.append("{{TITLE}} - Notification title\n");
                variables.append("{{MESSAGE}} - Notification message\n");
                variables.append("{{SEVERITY}} - Notification severity\n");
                break;
        }
        
        // Add common variables
        variables.append("{{CURRENT_DATE}} - Current date\n");
        variables.append("{{CURRENT_TIME}} - Current time\n");
        variables.append("{{SYSTEM_NAME}} - System name\n");
        
        return variables.toString();
    }
    
    /**
     * Inner class for processed template result
     */
    public static class ProcessedTemplate {
        private final String subject;
        private final String body;
        private final EmailTemplate template;
        
        public ProcessedTemplate(String subject, String body, EmailTemplate template) {
            this.subject = subject;
            this.body = body;
            this.template = template;
        }
        
        public String getSubject() { return subject; }
        public String getBody() { return body; }
        public EmailTemplate getTemplate() { return template; }
    }
}