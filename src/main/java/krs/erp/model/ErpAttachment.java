package krs.erp.model;

import jakarta.persistence.Column;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity for tracking all file attachments in the ERP system
 * Stores metadata about uploaded files including avatars, documents, etc.
 */
@Entity
@Table(name = "erp_attachments")
@AttributeOverride(name = "id", column = @Column(name = "erp_attachment_id"))
public class ErpAttachment extends BaseEntity {
    
    @NotBlank(message = "Original filename is required")
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;
    
    @NotBlank(message = "Stored filename is required")
    @Column(name = "stored_filename", nullable = false, length = 255, unique = true)
    private String storedFilename;
    
    @NotBlank(message = "File path is required")
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "mime_type", length = 100)
    private String mimeType;
    
    @NotNull(message = "Attachment type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type", nullable = false, length = 50)
    private AttachmentType attachmentType;
    
    @NotNull(message = "Organization ID is required")
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;
    
    @Column(name = "entity_type", length = 100)
    private String entityType; // e.g., "USER", "STUDENT", "STAFF"
    
    @Column(name = "entity_id")
    private Long entityId; // ID of the related entity
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "uploaded_by")
    private Long uploadedBy;
    
    /**
     * Enum for different types of attachments
     */
    public enum AttachmentType {
        AVATAR,
        DOCUMENT,
        IMAGE,
        VIDEO,
        AUDIO,
        OTHER
    }
    
    // Constructors
    public ErpAttachment() {}
    
    public ErpAttachment(String originalFilename, String storedFilename, String filePath,
                        Long fileSize, String mimeType, AttachmentType attachmentType,
                        Long organizationId, String entityType, Long entityId) {
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.attachmentType = attachmentType;
        this.organizationId = organizationId;
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    // Getters and Setters
    public String getOriginalFilename() {
        return originalFilename;
    }
    
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    
    public String getStoredFilename() {
        return storedFilename;
    }
    
    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public AttachmentType getAttachmentType() {
        return attachmentType;
    }
    
    public void setAttachmentType(AttachmentType attachmentType) {
        this.attachmentType = attachmentType;
    }
    
    public Long getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getUploadedBy() {
        return uploadedBy;
    }
    
    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
    
    /**
     * Get full file path including organization folder
     */
    public String getFullPath() {
        return filePath + "/" + storedFilename;
    }
}
