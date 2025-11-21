package krs.erp.controller;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import krs.erp.enums.EntityType;
import krs.erp.model.EmailTemplate;
import krs.erp.service.EmailService;
import krs.erp.service.EmailTemplateService;

@RestController
@RequestMapping("/api/email-templates")
public class EmailTemplateController {
    
    @Autowired
    private EmailTemplateService emailTemplateService;
    
    @Autowired
    private EmailService emailService;
    
    // Get all email templates with pagination
    @GetMapping
    public ResponseEntity<Page<EmailTemplate>> getAllTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String entityType) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<EmailTemplate> templates = emailTemplateService.getAllTemplates(pageable);
        
        return ResponseEntity.ok(templates);
    }
    
    // Get template by ID
    @GetMapping("/{id}")
    public ResponseEntity<EmailTemplate> getTemplateById(@PathVariable Long id) {
        Optional<EmailTemplate> template = emailTemplateService.getTemplateById(id);
        return template.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new email template
    @PostMapping
    public ResponseEntity<EmailTemplate> createTemplate(@Valid @RequestBody EmailTemplate template) {
        try {
            EmailTemplate savedTemplate = emailTemplateService.createTemplate(template);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTemplate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // Update email template
    @PutMapping("/{id}")
    public ResponseEntity<EmailTemplate> updateTemplate(@PathVariable Long id, @Valid @RequestBody EmailTemplate templateDetails) {
        try {
            EmailTemplate updatedTemplate = emailTemplateService.updateTemplate(id, templateDetails);
            return ResponseEntity.ok(updatedTemplate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Delete email template
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        try {
            emailTemplateService.deleteTemplate(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Get active templates by entity type
    @GetMapping("/entity-type/{entityType}")
    public ResponseEntity<List<EmailTemplate>> getActiveTemplatesByEntityType(@PathVariable String entityType) {
        try {
            EntityType type = EntityType.valueOf(entityType.toUpperCase());
            List<EmailTemplate> templates = emailTemplateService.getActiveTemplatesByEntityType(type);
            return ResponseEntity.ok(templates);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get all active templates
    @GetMapping("/active")
    public ResponseEntity<List<EmailTemplate>> getAllActiveTemplates() {
        List<EmailTemplate> templates = emailTemplateService.getAllActiveTemplates();
        return ResponseEntity.ok(templates);
    }
    
    // Search templates
    @GetMapping("/search")
    public ResponseEntity<List<EmailTemplate>> searchTemplates(@RequestParam String term) {
        List<EmailTemplate> templates = emailTemplateService.searchTemplates(term);
        return ResponseEntity.ok(templates);
    }
    
    // Get popular templates
    @GetMapping("/popular")
    public ResponseEntity<List<EmailTemplate>> getPopularTemplates(@RequestParam(defaultValue = "5") Integer minUsage) {
        List<EmailTemplate> templates = emailTemplateService.getPopularTemplates(minUsage);
        return ResponseEntity.ok(templates);
    }
    
    // Preview template with variables
    @PostMapping("/{id}/preview")
    public ResponseEntity<Map<String, String>> previewTemplate(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Long entityId = request.get("entityId") != null ? Long.valueOf(request.get("entityId").toString()) : null;
            
            @SuppressWarnings("unchecked")
            Map<String, String> customVariables = (Map<String, String>) request.get("customVariables");
            
            EmailTemplateService.ProcessedTemplate processedTemplate;
            
            if (entityId != null) {
                processedTemplate = emailTemplateService.processTemplate(id, entityId);
            } else if (customVariables != null) {
                processedTemplate = emailTemplateService.processTemplate(id, customVariables);
            } else {
                return ResponseEntity.badRequest().build();
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("subject", processedTemplate.getSubject());
            response.put("body", processedTemplate.getBody());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Send email using template
    @PostMapping("/{id}/send")
    public ResponseEntity<Map<String, Object>> sendEmailUsingTemplate(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Long entityId = Long.valueOf(request.get("entityId").toString());
            String recipientEmail = request.get("recipientEmail").toString();
            String recipientName = request.get("recipientName") != null ? request.get("recipientName").toString() : "";
            String sentBy = request.get("sentBy") != null ? request.get("sentBy").toString() : "System";
            
            boolean success = emailService.sendEmailUsingTemplate(id, entityId, recipientEmail, recipientName, sentBy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Email sent successfully" : "Failed to send email");
            response.put("templateId", id);
            response.put("entityId", entityId);
            response.put("recipientEmail", recipientEmail);
            
            return success ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error sending email: " + e.getMessage());
            errorResponse.put("templateId", id);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Send email using template with custom variables
    @PostMapping("/{id}/send-custom")
    public ResponseEntity<Map<String, Object>> sendEmailUsingTemplateWithCustomVariables(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> customVariables = (Map<String, String>) request.get("customVariables");
            
            EntityType entityType = EntityType.valueOf(request.get("entityType").toString().toUpperCase());
            Long entityId = request.get("entityId") != null ? Long.valueOf(request.get("entityId").toString()) : 0L;
            String recipientEmail = request.get("recipientEmail").toString();
            String recipientName = request.get("recipientName") != null ? request.get("recipientName").toString() : "";
            String sentBy = request.get("sentBy") != null ? request.get("sentBy").toString() : "System";
            
            boolean success = emailService.sendEmailUsingTemplate(id, customVariables, entityType, entityId, recipientEmail, recipientName, sentBy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Email sent successfully" : "Failed to send email");
            response.put("templateId", id);
            response.put("entityType", entityType);
            response.put("entityId", entityId);
            response.put("recipientEmail", recipientEmail);
            
            return success ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error sending email: " + e.getMessage());
            errorResponse.put("templateId", id);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Get entity types
    @GetMapping("/entity-types")
    public ResponseEntity<List<Map<String, String>>> getEntityTypes() {
        List<Map<String, String>> entityTypes = java.util.Arrays.stream(EntityType.values())
            .map(type -> {
                Map<String, String> typeInfo = new HashMap<>();
                typeInfo.put("value", type.name());
                typeInfo.put("displayName", type.getDisplayName());
                return typeInfo;
            })
            .toList();
        
        return ResponseEntity.ok(entityTypes);
    }
}