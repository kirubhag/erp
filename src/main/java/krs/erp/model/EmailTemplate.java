package krs.erp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "email_templates")
public class EmailTemplate extends BaseEntity {
    
    @NotBlank(message = "Template name is required")
    @Size(min = 3, max = 100, message = "Template name must be between 3 and 100 characters")
    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;
    
    @NotBlank(message = "Subject is required")
    @Size(min = 3, max = 200, message = "Subject must be between 3 and 200 characters")
    @Column(name = "subject", nullable = false, length = 200)
    private String subject;
    
    @NotBlank(message = "Body is required")
    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;
    
    @NotNull(message = "Entity type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "last_used")
    private LocalDateTime lastUsed;
    
    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;
    
    // Available template variables for replacement
    @Column(name = "available_variables", columnDefinition = "TEXT")
    private String availableVariables;
    
    // Enums
    public enum EntityType {
        STUDENT("Student", "Student related templates"),
        PARENT("Parent", "Parent related templates"),
        ATTENDANCE("Attendance", "Attendance related templates"),
        HEALTH("Health", "Health record related templates"),
        GENERAL("General", "General purpose templates"),
        NOTIFICATION("Notification", "System notification templates");
        
        private final String displayName;
        private final String description;
        
        EntityType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    // Constructors
    public EmailTemplate() {}
    
    public EmailTemplate(String templateName, String subject, String body, EntityType entityType) {
        this.templateName = templateName;
        this.subject = subject;
        this.body = body;
        this.entityType = entityType;
    }
    
    // Getters and Setters
    public String getTemplateName() {
        return templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public EntityType getEntityType() {
        return entityType;
    }
    
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getLastUsed() {
        return lastUsed;
    }
    
    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }
    
    public Integer getUsageCount() {
        return usageCount;
    }
    
    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }
    
    public String getAvailableVariables() {
        return availableVariables;
    }
    
    public void setAvailableVariables(String availableVariables) {
        this.availableVariables = availableVariables;
    }
    
    // Helper methods
    public void incrementUsageCount() {
        if (this.usageCount == null) {
            this.usageCount = 0;
        }
        this.usageCount++;
        this.lastUsed = LocalDateTime.now();
    }
    
    public String getEntityTypeDisplayName() {
        return entityType != null ? entityType.getDisplayName() : "";
    }
}