package krs.erp.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import krs.erp.model.ErpAttachment;
import krs.erp.model.User;
import krs.erp.repository.UserRepository;
import krs.erp.service.ErpAttachmentService;

/**
 * REST Controller for file attachment operations
 */
@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {
    
    @Autowired
    private ErpAttachmentService attachmentService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Upload avatar for current user
     */
    @PostMapping("/avatar/upload")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("organizationId") Long organizationId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get current user for audit
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long uploadedBy = userId; // Default to the user whose avatar is being uploaded
            
            if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
                String username = auth.getName();
                Optional<User> currentUser = userRepository.findByUsername(username);
                if (currentUser.isPresent()) {
                    uploadedBy = currentUser.get().getId();
                }
            }
            
            // Upload avatar
            ErpAttachment attachment = attachmentService.uploadAvatar(file, userId, organizationId, uploadedBy);
            
            response.put("status", "success");
            response.put("message", "Avatar uploaded successfully");
            response.put("attachment", Map.of(
                "id", attachment.getId(),
                "originalFilename", attachment.getOriginalFilename(),
                "storedFilename", attachment.getStoredFilename(),
                "fileSize", attachment.getFileSize(),
                "mimeType", attachment.getMimeType(),
                "url", "/api/attachments/avatar/" + userId
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get avatar for a user
     */
    @GetMapping("/avatar/{userId}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long userId) {
        try {
            Optional<ErpAttachment> avatarOpt = attachmentService.getUserAvatar(userId);
            
            if (avatarOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            ErpAttachment avatar = avatarOpt.get();
            byte[] fileContent = attachmentService.getFileContent(avatar);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(avatar.getMimeType()));
            headers.setContentLength(fileContent.length);
            headers.setCacheControl("max-age=3600"); // Cache for 1 hour
            
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete avatar for a user
     */
    @DeleteMapping("/avatar/{userId}")
    public ResponseEntity<Map<String, Object>> deleteAvatar(
            @PathVariable Long userId,
            @RequestParam Long organizationId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            attachmentService.deleteExistingAvatar(userId, organizationId);
            
            response.put("status", "success");
            response.put("message", "Avatar deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "Failed to delete avatar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get attachment by ID
     */
    @GetMapping("/{attachmentId}")
    public ResponseEntity<byte[]> getAttachment(@PathVariable Long attachmentId) {
        try {
            Optional<ErpAttachment> attachmentOpt = attachmentService.getAttachmentById(attachmentId);
            
            if (attachmentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            ErpAttachment attachment = attachmentOpt.get();
            byte[] fileContent = attachmentService.getFileContent(attachment);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(attachment.getMimeType()));
            headers.setContentLength(fileContent.length);
            headers.setContentDispositionFormData("attachment", attachment.getOriginalFilename());
            
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get all attachments for an entity
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<ErpAttachment>> getEntityAttachments(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        
        List<ErpAttachment> attachments = attachmentService.getEntityAttachments(entityType, entityId);
        return ResponseEntity.ok(attachments);
    }
    
    /**
     * Delete attachment
     */
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Map<String, Object>> deleteAttachment(@PathVariable Long attachmentId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            attachmentService.deleteAttachment(attachmentId);
            
            response.put("status", "success");
            response.put("message", "Attachment deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "Failed to delete attachment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
