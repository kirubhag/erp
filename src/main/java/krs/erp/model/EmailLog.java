package krs.erp.model;

import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import krs.erp.enums.EntityType;

@Entity
@Table(name = "erp_email_logs")
@AttributeOverride(name = "id", column = @Column(name = "email_log_id"))
public class EmailLog extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private EmailTemplate emailTemplate;
    
    @NotNull(message = "Entity type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;
    
    @NotNull(message = "Entity ID is required")
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    @Column(name = "recipient_email", nullable = false, length = 255)
    private String recipientEmail;
    
    @Column(name = "recipient_name", length = 200)
    private String recipientName;
    
    @NotBlank(message = "Subject is required")
    @Size(max = 500, message = "Subject must not exceed 500 characters")
    @Column(name = "subject", nullable = false, length = 500)
    private String subject;
    
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;
    
    @NotNull(message = "Email status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmailStatus status;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "opened_at")
    private LocalDateTime openedAt;
    
    @Column(name = "failed_at")
    private LocalDateTime failedAt;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    @Column(name = "sent_by", length = 100)
    private String sentBy;
    
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;
    
    @Column(name = "priority", nullable = false)
    private Integer priority = 1; // 1 = high, 2 = medium, 3 = low
    
    @Column(name = "email_provider", length = 50)
    private String emailProvider;
    
    @Column(name = "message_id", length = 255)
    private String messageId;
    
    // Additional metadata in JSON format
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
    
    // Enums
    public enum EmailStatus {
        PENDING("Pending", "Email is queued for sending"),
        SENT("Sent", "Email was successfully sent"),
        DELIVERED("Delivered", "Email was delivered to recipient"),
        OPENED("Opened", "Email was opened by recipient"),
        FAILED("Failed", "Email sending failed"),
        BOUNCED("Bounced", "Email bounced back"),
        SPAM("Spam", "Email marked as spam"),
        UNSUBSCRIBED("Unsubscribed", "Recipient unsubscribed");
        
        private final String displayName;
        private final String description;
        
        EmailStatus(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    // Constructors
    public EmailLog() {}
    
    public EmailLog(EntityType entityType, Long entityId, String recipientEmail, 
                   String subject, String body) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.body = body;
        this.status = EmailStatus.PENDING;
        this.setCreatedTime(LocalDateTime.now());
    }
    
    // Getters and Setters
    public EmailTemplate getEmailTemplate() {
        return emailTemplate;
    }
    
    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }
    
    public EntityType getEntityType() {
        return entityType;
    }
    
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    public String getRecipientName() {
        return recipientName;
    }
    
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
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
    
    public EmailStatus getStatus() {
        return status;
    }
    
    public void setStatus(EmailStatus status) {
        this.status = status;
        
        // Automatically set timestamp based on status
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case SENT:
                this.sentAt = now;
                break;
            case DELIVERED:
                this.deliveredAt = now;
                break;
            case OPENED:
                this.openedAt = now;
                break;
            case FAILED:
            case BOUNCED:
            case SPAM:
                this.failedAt = now;
                break;
            case PENDING:
            case UNSUBSCRIBED:
                // No timestamp update needed for these statuses
                break;
        }
        
        this.setModifiedTime(now);
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public LocalDateTime getOpenedAt() {
        return openedAt;
    }
    
    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }
    
    public LocalDateTime getFailedAt() {
        return failedAt;
    }
    
    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getSentBy() {
        return sentBy;
    }
    
    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public String getEmailProvider() {
        return emailProvider;
    }
    
    public void setEmailProvider(String emailProvider) {
        this.emailProvider = emailProvider;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    // Helper methods
    public void incrementRetryCount() {
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
        this.retryCount++;
    }
    
    public boolean isDelivered() {
        return this.status == EmailStatus.DELIVERED || this.status == EmailStatus.OPENED;
    }
    
    public boolean isFailed() {
        return this.status == EmailStatus.FAILED || this.status == EmailStatus.BOUNCED;
    }
    
    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "";
    }
    
    public String getEntityTypeDisplayName() {
        return entityType != null ? entityType.getDisplayName() : "";
    }
    
    public String getPriorityText() {
        switch (priority) {
            case 1: return "High";
            case 2: return "Medium";
            case 3: return "Low";
            default: return "Unknown";
        }
    }
}