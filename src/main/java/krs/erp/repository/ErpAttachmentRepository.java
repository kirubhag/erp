package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.ErpAttachment;
import krs.erp.model.ErpAttachment.AttachmentType;

/**
 * Repository for ErpAttachment entity
 */
@Repository
public interface ErpAttachmentRepository extends JpaRepository<ErpAttachment, Long> {
    
    /**
     * Find attachment by stored filename
     */
    Optional<ErpAttachment> findByStoredFilename(String storedFilename);
    
    /**
     * Find all attachments for a specific entity
     */
    List<ErpAttachment> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    /**
     * Find all attachments by type for a specific entity
     */
    List<ErpAttachment> findAllByEntityTypeAndEntityIdAndAttachmentType(
        String entityType, Long entityId, AttachmentType attachmentType);
    
    /**
     * Find all attachments for an organization
     */
    List<ErpAttachment> findByOrganizationId(Long organizationId);
    
    /**
     * Find single attachment (e.g., avatar) for a specific entity
     */
    Optional<ErpAttachment> findFirstByEntityTypeAndEntityIdAndAttachmentType(
        String entityType, Long entityId, AttachmentType attachmentType);
    
    /**
     * Find all attachments uploaded by a specific user
     */
    List<ErpAttachment> findByUploadedBy(Long uploadedBy);
    
    /**
     * Delete all attachments for a specific entity
     */
    void deleteByEntityTypeAndEntityId(String entityType, Long entityId);
    
    /**
     * Count attachments for an organization
     */
    Long countByOrganizationId(Long organizationId);
}
