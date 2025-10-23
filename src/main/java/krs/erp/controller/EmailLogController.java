package krs.erp.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.EmailLog;
import krs.erp.model.EmailTemplate;
import krs.erp.repository.EmailLogRepository;
import krs.erp.service.EmailService;

@RestController
@RequestMapping("/api/email-logs")
@CrossOrigin(origins = "*")
public class EmailLogController {
    
    @Autowired
    private EmailLogRepository emailLogRepository;
    
    @Autowired
    private EmailService emailService;
    
    // Get all email logs with pagination
    @GetMapping
    public ResponseEntity<Page<EmailLog>> getAllEmailLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String entityType) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<EmailLog> emailLogs;
        
        if (status != null) {
            try {
                EmailLog.EmailStatus emailStatus = EmailLog.EmailStatus.valueOf(status.toUpperCase());
                emailLogs = emailLogRepository.findByStatusOrderByCreatedTimeDesc(emailStatus, pageable);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else if (entityType != null) {
            try {
                EmailTemplate.EntityType type = EmailTemplate.EntityType.valueOf(entityType.toUpperCase());
                emailLogs = emailLogRepository.findByEntityTypeOrderByCreatedTimeDesc(type, pageable);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            emailLogs = emailLogRepository.findAll(pageable);
        }
        
        return ResponseEntity.ok(emailLogs);
    }
    
    // Get email log by ID
    @GetMapping("/{id}")
    public ResponseEntity<EmailLog> getEmailLogById(@PathVariable Long id) {
        Optional<EmailLog> emailLog = emailLogRepository.findById(id);
        return emailLog.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    // Get email logs for specific entity
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<EmailLog>> getEmailLogsForEntity(@PathVariable String entityType, @PathVariable Long entityId) {
        try {
            EmailTemplate.EntityType type = EmailTemplate.EntityType.valueOf(entityType.toUpperCase());
            List<EmailLog> emailLogs = emailLogRepository.findByEntityTypeAndEntityIdOrderByCreatedTimeDesc(type, entityId);
            return ResponseEntity.ok(emailLogs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get email logs for specific entity with pagination
    @GetMapping("/entity/{entityType}/{entityId}/paginated")
    public ResponseEntity<Page<EmailLog>> getEmailLogsForEntityPaginated(
            @PathVariable String entityType, 
            @PathVariable Long entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            EmailTemplate.EntityType type = EmailTemplate.EntityType.valueOf(entityType.toUpperCase());
            Pageable pageable = PageRequest.of(page, size);
            Page<EmailLog> emailLogs = emailLogRepository.findByEntityTypeAndEntityIdOrderByCreatedTimeDesc(type, entityId, pageable);
            return ResponseEntity.ok(emailLogs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get email logs for specific recipient
    @GetMapping("/recipient/{email}")
    public ResponseEntity<List<EmailLog>> getEmailLogsForRecipient(@PathVariable String email) {
        List<EmailLog> emailLogs = emailLogRepository.findByRecipientEmailOrderByCreatedTimeDesc(email);
        return ResponseEntity.ok(emailLogs);
    }
    
    // Get email logs for specific recipient with pagination
    @GetMapping("/recipient/{email}/paginated")
    public ResponseEntity<Page<EmailLog>> getEmailLogsForRecipientPaginated(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EmailLog> emailLogs = emailLogRepository.findByRecipientEmailOrderByCreatedTimeDesc(email, pageable);
        return ResponseEntity.ok(emailLogs);
    }
    
    // Get failed emails
    @GetMapping("/failed")
    public ResponseEntity<List<EmailLog>> getFailedEmails() {
        List<EmailLog> failedEmails = emailLogRepository.findByStatusOrderByCreatedTimeDesc(EmailLog.EmailStatus.FAILED);
        return ResponseEntity.ok(failedEmails);
    }
    
    // Get pending emails
    @GetMapping("/pending")
    public ResponseEntity<List<EmailLog>> getPendingEmails() {
        List<EmailLog> pendingEmails = emailLogRepository.findPendingEmails();
        return ResponseEntity.ok(pendingEmails);
    }
    
    // Get recent emails (last 24 hours)
    @GetMapping("/recent")
    public ResponseEntity<List<EmailLog>> getRecentEmails() {
        LocalDateTime since = LocalDateTime.now().minusDays(1);
        List<EmailLog> recentEmails = emailLogRepository.findRecentEmails(since);
        return ResponseEntity.ok(recentEmails);
    }
    
    // Get emails sent today
    @GetMapping("/today")
    public ResponseEntity<List<EmailLog>> getEmailsSentToday() {
        List<EmailLog> todaysEmails = emailLogRepository.findEmailsSentToday();
        return ResponseEntity.ok(todaysEmails);
    }
    
    // Search email logs
    @GetMapping("/search")
    public ResponseEntity<List<EmailLog>> searchEmailLogs(@RequestParam String term) {
        List<EmailLog> emailLogs = emailLogRepository.searchEmails(term);
        return ResponseEntity.ok(emailLogs);
    }
    
    // Get emails with errors
    @GetMapping("/errors")
    public ResponseEntity<List<EmailLog>> getEmailsWithErrors() {
        List<EmailLog> emailsWithErrors = emailLogRepository.findEmailsWithErrors();
        return ResponseEntity.ok(emailsWithErrors);
    }
    
    // Retry failed email
    @PostMapping("/{id}/retry")
    public ResponseEntity<Map<String, Object>> retryFailedEmail(@PathVariable Long id) {
        try {
            boolean success = emailService.retryFailedEmail(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Email retry initiated successfully" : "Failed to retry email");
            response.put("emailLogId", id);
            
            return success ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrying email: " + e.getMessage());
            errorResponse.put("emailLogId", id);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Send direct email (without template)
    @PostMapping("/send-direct")
    public ResponseEntity<Map<String, Object>> sendDirectEmail(@RequestBody Map<String, Object> request) {
        try {
            EmailTemplate.EntityType entityType = EmailTemplate.EntityType.valueOf(request.get("entityType").toString().toUpperCase());
            Long entityId = Long.valueOf(request.get("entityId").toString());
            String recipientEmail = request.get("recipientEmail").toString();
            String recipientName = request.get("recipientName") != null ? request.get("recipientName").toString() : "";
            String subject = request.get("subject").toString();
            String body = request.get("body").toString();
            String sentBy = request.get("sentBy") != null ? request.get("sentBy").toString() : "System";
            
            boolean success = emailService.sendDirectEmail(entityType, entityId, recipientEmail, recipientName, subject, body, sentBy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Email sent successfully" : "Failed to send email");
            response.put("entityType", entityType);
            response.put("entityId", entityId);
            response.put("recipientEmail", recipientEmail);
            
            return success ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error sending email: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Get email statistics
    @GetMapping("/statistics")
    public ResponseEntity<EmailService.EmailStatistics> getEmailStatistics(@RequestParam(required = false) Integer daysSince) {
        LocalDateTime since = daysSince != null ? LocalDateTime.now().minusDays(daysSince) : LocalDateTime.now().minusDays(30);
        EmailService.EmailStatistics statistics = emailService.getEmailStatistics(since);
        return ResponseEntity.ok(statistics);
    }
    
    // Get email count for entity
    @GetMapping("/count/{entityType}/{entityId}")
    public ResponseEntity<Map<String, Object>> getEmailCountForEntity(@PathVariable String entityType, @PathVariable Long entityId) {
        try {
            EmailTemplate.EntityType type = EmailTemplate.EntityType.valueOf(entityType.toUpperCase());
            
            Long totalCount = emailLogRepository.countByEntityTypeAndEntityId(type, entityId);
            Long deliveredCount = emailLogRepository.countDeliveredEmailsForEntity(type, entityId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("entityType", entityType);
            response.put("entityId", entityId);
            response.put("totalEmails", totalCount);
            response.put("deliveredEmails", deliveredCount);
            response.put("deliveryRate", totalCount > 0 ? (deliveredCount.doubleValue() / totalCount * 100) : 0.0);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get email status options
    @GetMapping("/statuses")
    public ResponseEntity<List<Map<String, String>>> getEmailStatuses() {
        List<Map<String, String>> statuses = java.util.Arrays.stream(EmailLog.EmailStatus.values())
            .map(status -> {
                Map<String, String> statusInfo = new HashMap<>();
                statusInfo.put("value", status.name());
                statusInfo.put("displayName", status.getDisplayName());
                statusInfo.put("description", status.getDescription());
                return statusInfo;
            })
            .toList();
        
        return ResponseEntity.ok(statuses);
    }
}