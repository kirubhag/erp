package krs.erp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.EmailLog;
import krs.erp.model.EmailTemplate;
import krs.erp.repository.EmailLogRepository;

@Service
@Transactional
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Autowired
    private EmailLogRepository emailLogRepository;
    
    @Autowired
    private EmailTemplateService emailTemplateService;
    
    /**
     * Send email using template
     */
    public boolean sendEmailUsingTemplate(Long templateId, Long entityId, String recipientEmail, String recipientName, String sentBy) {
        try {
            EmailTemplateService.ProcessedTemplate processedTemplate = emailTemplateService.processTemplate(templateId, entityId);
            
            return sendEmail(
                processedTemplate.getTemplate().getEntityType(),
                entityId,
                recipientEmail,
                recipientName,
                processedTemplate.getSubject(),
                processedTemplate.getBody(),
                sentBy,
                processedTemplate.getTemplate()
            );
            
        } catch (Exception e) {
            logger.error("Failed to send email using template ID {} for entity ID {}: {}", templateId, entityId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Send email using template with custom variables
     */
    public boolean sendEmailUsingTemplate(Long templateId, Map<String, String> customVariables, 
                                        EmailTemplate.EntityType entityType, Long entityId,
                                        String recipientEmail, String recipientName, String sentBy) {
        try {
            EmailTemplateService.ProcessedTemplate processedTemplate = emailTemplateService.processTemplate(templateId, customVariables);
            
            return sendEmail(
                entityType,
                entityId,
                recipientEmail,
                recipientName,
                processedTemplate.getSubject(),
                processedTemplate.getBody(),
                sentBy,
                processedTemplate.getTemplate()
            );
            
        } catch (Exception e) {
            logger.error("Failed to send email using template ID {} with custom variables: {}", templateId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Send direct email without template
     */
    public boolean sendDirectEmail(EmailTemplate.EntityType entityType, Long entityId,
                                 String recipientEmail, String recipientName,
                                 String subject, String body, String sentBy) {
        return sendEmail(entityType, entityId, recipientEmail, recipientName, subject, body, sentBy, null);
    }
    
    /**
     * Core email sending method with logging
     */
    private boolean sendEmail(EmailTemplate.EntityType entityType, Long entityId,
                            String recipientEmail, String recipientName,
                            String subject, String body, String sentBy, EmailTemplate template) {
        // Create email log entry
        EmailLog emailLog = new EmailLog(entityType, entityId, recipientEmail, subject, body);
        emailLog.setRecipientName(recipientName);
        emailLog.setSentBy(sentBy);
        emailLog.setEmailTemplate(template);
        emailLog.setStatus(EmailLog.EmailStatus.PENDING);
        
        // Save initial log
        emailLog = emailLogRepository.save(emailLog);
        
        try {
            // Validate inputs
            if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
                emailLog.setStatus(EmailLog.EmailStatus.FAILED);
                emailLog.setErrorMessage("Recipient email is empty or null");
                emailLogRepository.save(emailLog);
                logger.warn("Cannot send email - no recipient email provided for entity {} ID {}", entityType, entityId);
                return false;
            }
            
            // Check if email sender is available
            if (mailSender == null) {
                emailLog.setStatus(EmailLog.EmailStatus.FAILED);
                emailLog.setErrorMessage("Email service is not configured - JavaMailSender not available");
                emailLogRepository.save(emailLog);
                logger.warn("Cannot send email - JavaMailSender not configured for entity {} ID {}", entityType, entityId);
                return false;
            }
            
            // Create and send email message
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@erp.school.edu"); // Configure this in properties
            
            mailSender.send(message);
            
            // Update log with success
            emailLog.setStatus(EmailLog.EmailStatus.SENT);
            emailLog.setSentAt(LocalDateTime.now());
            emailLog.setEmailProvider("Gmail SMTP"); // Configure this dynamically
            emailLogRepository.save(emailLog);
            
            logger.info("Email sent successfully to: {} for {} ID: {} with subject: {}", 
                       recipientEmail, entityType, entityId, subject);
            
            return true;
            
        } catch (Exception e) {
            // Update log with failure
            emailLog.setStatus(EmailLog.EmailStatus.FAILED);
            emailLog.setErrorMessage(e.getMessage());
            emailLog.setFailedAt(LocalDateTime.now());
            emailLogRepository.save(emailLog);
            
            logger.error("Failed to send email to: {} for {} ID: {}. Error: {}", 
                        recipientEmail, entityType, entityId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Send test email
     */
    public boolean sendTestEmail(String recipientEmail) {
        try {
            // Check if email sender is available
            if (mailSender == null) {
                logger.warn("Cannot send test email - JavaMailSender not configured");
                return false;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject("Test Email - ERP System");
            message.setText("This is a test email from the School ERP System.\n\n" +
                          "If you received this email, the email configuration is working correctly.\n\n" +
                          "Sent at: " + LocalDateTime.now());
            message.setFrom("noreply@erp.school.edu");
            
            mailSender.send(message);
            
            // Log test email
            EmailLog emailLog = new EmailLog(EmailTemplate.EntityType.GENERAL, 0L, recipientEmail, 
                                           message.getSubject(), message.getText());
            emailLog.setRecipientName("Test Recipient");
            emailLog.setSentBy("System");
            emailLog.setStatus(EmailLog.EmailStatus.SENT);
            emailLog.setSentAt(LocalDateTime.now());
            emailLogRepository.save(emailLog);
            
            logger.info("Test email sent successfully to: {}", recipientEmail);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to send test email to: {}. Error: {}", recipientEmail, e.getMessage());
            return false;
        }
    }
    
    /**
     * Get email logs for specific entity
     */
    public List<EmailLog> getEmailLogsForEntity(EmailTemplate.EntityType entityType, Long entityId) {
        return emailLogRepository.findByEntityTypeAndEntityIdOrderByCreatedTimeDesc(entityType, entityId);
    }
    
    /**
     * Get email logs for specific recipient
     */
    public List<EmailLog> getEmailLogsForRecipient(String recipientEmail) {
        return emailLogRepository.findByRecipientEmailOrderByCreatedTimeDesc(recipientEmail);
    }
    
    /**
     * Get failed emails for retry
     */
    public List<EmailLog> getFailedEmailsForRetry() {
        return emailLogRepository.findFailedEmailsForRetry();
    }
    
    /**
     * Retry sending failed email
     */
    public boolean retryFailedEmail(Long emailLogId) {
        Optional<EmailLog> emailLogOpt = emailLogRepository.findById(emailLogId);
        if (!emailLogOpt.isPresent()) {
            logger.error("Email log not found for retry: {}", emailLogId);
            return false;
        }
        
        EmailLog emailLog = emailLogOpt.get();
        
        // Check if it's eligible for retry
        if (emailLog.getRetryCount() >= 3) {
            logger.warn("Email log {} has exceeded maximum retry count", emailLogId);
            return false;
        }
        
        if (emailLog.getStatus() != EmailLog.EmailStatus.FAILED) {
            logger.warn("Email log {} is not in FAILED status, cannot retry", emailLogId);
            return false;
        }
        
        try {
            // Check if email sender is available
            if (mailSender == null) {
                emailLog.setStatus(EmailLog.EmailStatus.FAILED);
                emailLog.setErrorMessage("Email service not configured - JavaMailSender not available");
                emailLogRepository.save(emailLog);
                logger.warn("Cannot retry email {} - JavaMailSender not configured", emailLogId);
                return false;
            }
            
            // Increment retry count
            emailLog.incrementRetryCount();
            emailLog.setStatus(EmailLog.EmailStatus.PENDING);
            emailLog.setErrorMessage(null);
            emailLogRepository.save(emailLog);
            
            // Attempt to send email again
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailLog.getRecipientEmail());
            message.setSubject(emailLog.getSubject());
            message.setText(emailLog.getBody());
            message.setFrom("noreply@erp.school.edu");
            
            mailSender.send(message);
            
            // Update with success
            emailLog.setStatus(EmailLog.EmailStatus.SENT);
            emailLog.setSentAt(LocalDateTime.now());
            emailLogRepository.save(emailLog);
            
            logger.info("Successfully retried email to: {} (retry count: {})", 
                       emailLog.getRecipientEmail(), emailLog.getRetryCount());
            
            return true;
            
        } catch (Exception e) {
            // Update with failure
            emailLog.setStatus(EmailLog.EmailStatus.FAILED);
            emailLog.setErrorMessage("Retry failed: " + e.getMessage());
            emailLog.setFailedAt(LocalDateTime.now());
            emailLogRepository.save(emailLog);
            
            logger.error("Failed to retry email ID {}: {}", emailLogId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Get email statistics
     */
    public EmailStatistics getEmailStatistics(LocalDateTime since) {
        // Get counts for different statuses
        Long totalEmails = emailLogRepository.count();
        Long sentEmails = emailLogRepository.countByStatus(EmailLog.EmailStatus.SENT);
        Long deliveredEmails = emailLogRepository.countByStatus(EmailLog.EmailStatus.DELIVERED);
        Long openedEmails = emailLogRepository.countByStatus(EmailLog.EmailStatus.OPENED);
        Long failedEmails = emailLogRepository.countByStatus(EmailLog.EmailStatus.FAILED);
        Long bouncedEmails = emailLogRepository.countByStatus(EmailLog.EmailStatus.BOUNCED);
        
        return new EmailStatistics(totalEmails, sentEmails, deliveredEmails, openedEmails, failedEmails, bouncedEmails);
    }
    
    /**
     * Mark email as delivered (webhook callback)
     */
    public void markEmailAsDelivered(String messageId) {
        // Implementation for email delivery webhooks
        logger.info("Email delivered: {}", messageId);
    }
    
    /**
     * Mark email as opened (tracking pixel callback)
     */
    public void markEmailAsOpened(String messageId) {
        // Implementation for email open tracking
        logger.info("Email opened: {}", messageId);
    }
    
    /**
     * Inner class for email statistics
     */
    public static class EmailStatistics {
        private final Long totalEmails;
        private final Long sentEmails;
        private final Long deliveredEmails;
        private final Long openedEmails;
        private final Long failedEmails;
        private final Long bouncedEmails;
        
        public EmailStatistics(Long totalEmails, Long sentEmails, Long deliveredEmails, 
                              Long openedEmails, Long failedEmails, Long bouncedEmails) {
            this.totalEmails = totalEmails;
            this.sentEmails = sentEmails;
            this.deliveredEmails = deliveredEmails;
            this.openedEmails = openedEmails;
            this.failedEmails = failedEmails;
            this.bouncedEmails = bouncedEmails;
        }
        
        public Long getTotalEmails() { return totalEmails; }
        public Long getSentEmails() { return sentEmails; }
        public Long getDeliveredEmails() { return deliveredEmails; }
        public Long getOpenedEmails() { return openedEmails; }
        public Long getFailedEmails() { return failedEmails; }
        public Long getBouncedEmails() { return bouncedEmails; }
        
        public double getDeliveryRate() {
            if (totalEmails == null || totalEmails == 0) return 0.0;
            return ((double) (deliveredEmails + openedEmails)) / totalEmails * 100;
        }
        
        public double getOpenRate() {
            if (deliveredEmails == null || deliveredEmails == 0) return 0.0;
            return ((double) openedEmails) / deliveredEmails * 100;
        }
        
        public double getFailureRate() {
            if (totalEmails == null || totalEmails == 0) return 0.0;
            return ((double) (failedEmails + bouncedEmails)) / totalEmails * 100;
        }
    }
}