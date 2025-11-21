package krs.erp.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import krs.erp.model.ErpAttachment;
import krs.erp.model.ErpAttachment.AttachmentType;
import krs.erp.model.User;
import krs.erp.repository.ErpAttachmentRepository;
import krs.erp.repository.UserRepository;

/**
 * Service for managing file attachments
 */
@Service
@Transactional
public class ErpAttachmentService {
    
    @Autowired
    private ErpAttachmentRepository attachmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${app.upload.base-dir:uploads}")
    private String baseUploadDir;
    
    private static final String AVATARS_FOLDER = "avatars";
    
    /**
     * Upload an avatar image for a user
     */
    public ErpAttachment uploadAvatar(MultipartFile file, Long userId, Long organizationId, Long uploadedBy) 
            throws IOException {
        
        // Validate file
        validateImageFile(file);
        
        // Delete existing avatar if any
        deleteExistingAvatar(userId, organizationId);
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String storedFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Create directory structure: uploads/avatars/{organizationId}
        Path organizationDir = Paths.get(baseUploadDir, AVATARS_FOLDER, organizationId.toString());
        Files.createDirectories(organizationDir);
        
        // Save file
        Path filePath = organizationDir.resolve(storedFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Create attachment record
        ErpAttachment attachment = new ErpAttachment();
        attachment.setOriginalFilename(originalFilename);
        attachment.setStoredFilename(storedFilename);
        attachment.setFilePath(organizationDir.toString());
        attachment.setFileSize(file.getSize());
        attachment.setMimeType(file.getContentType());
        attachment.setAttachmentType(AttachmentType.AVATAR);
        attachment.setOrganizationId(organizationId);
        attachment.setEntityType("USER");
        attachment.setEntityId(userId);
        attachment.setUploadedBy(uploadedBy);
        attachment.setDescription("User avatar image");
        
        ErpAttachment savedAttachment = attachmentRepository.save(attachment);
        
        // Update user's avatarUrl
        updateUserAvatarUrl(userId, savedAttachment.getId());
        
        return savedAttachment;
    }
    
    /**
     * Get avatar for a user
     */
    public Optional<ErpAttachment> getUserAvatar(Long userId) {
        return attachmentRepository.findFirstByEntityTypeAndEntityIdAndAttachmentType(
            "USER", userId, AttachmentType.AVATAR);
    }
    
    /**
     * Delete existing avatar for a user
     */
    public void deleteExistingAvatar(Long userId, Long organizationId) throws IOException {
        Optional<ErpAttachment> existingAvatar = getUserAvatar(userId);
        
        if (existingAvatar.isPresent()) {
            ErpAttachment avatar = existingAvatar.get();
            
            // Delete physical file
            Path filePath = Paths.get(avatar.getFilePath(), avatar.getStoredFilename());
            Files.deleteIfExists(filePath);
            
            // Delete database record
            attachmentRepository.delete(avatar);
        }
    }
    
    /**
     * Get attachment by ID
     */
    public Optional<ErpAttachment> getAttachmentById(Long id) {
        return attachmentRepository.findById(id);
    }
    
    /**
     * Get attachment by stored filename
     */
    public Optional<ErpAttachment> getAttachmentByFilename(String storedFilename) {
        return attachmentRepository.findByStoredFilename(storedFilename);
    }
    
    /**
     * Get all attachments for an entity
     */
    public List<ErpAttachment> getEntityAttachments(String entityType, Long entityId) {
        return attachmentRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
    
    /**
     * Get all attachments for an organization
     */
    public List<ErpAttachment> getOrganizationAttachments(Long organizationId) {
        return attachmentRepository.findByOrganizationId(organizationId);
    }
    
    /**
     * Delete attachment
     */
    public void deleteAttachment(Long attachmentId) throws IOException {
        Optional<ErpAttachment> attachmentOpt = attachmentRepository.findById(attachmentId);
        
        if (attachmentOpt.isPresent()) {
            ErpAttachment attachment = attachmentOpt.get();
            
            // Delete physical file
            Path filePath = Paths.get(attachment.getFilePath(), attachment.getStoredFilename());
            Files.deleteIfExists(filePath);
            
            // Delete database record
            attachmentRepository.delete(attachment);
        }
    }
    
    /**
     * Get file content
     */
    public byte[] getFileContent(ErpAttachment attachment) throws IOException {
        Path filePath = Paths.get(attachment.getFilePath(), attachment.getStoredFilename());
        return Files.readAllBytes(filePath);
    }
    
    /**
     * Validate image file
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        
        // Check file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }
    }
    
    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
    
    /**
     * Update user's avatar URL
     */
    private void updateUserAvatarUrl(Long userId, Long attachmentId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setAvatarUrl("/api/attachments/avatar/" + userId);
            userRepository.save(user);
        }
    }
}
