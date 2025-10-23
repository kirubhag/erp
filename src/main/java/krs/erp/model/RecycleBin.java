package krs.erp.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import krs.erp.enums.EntityType;

/**
 * RecycleBin entity to track soft-deleted records
 */
@Entity
@Table(name = "recycle_bin")
public class RecycleBin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recycle_bin_id")
    private Long recycleBinId;
    
    @NotNull(message = "Entity ID is required")
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @NotBlank(message = "Entity name is required")
    @Size(min = 1, max = 100, message = "Entity name must be between 1 and 100 characters")
    @Column(name = "entity_name", nullable = false, length = 100)
    private String entityName;
    
    @NotNull(message = "Entity type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;
    
    @NotBlank(message = "Deleted by is required")
    @Size(min = 1, max = 100, message = "Deleted by must be between 1 and 100 characters")
    @Column(name = "deleted_by", nullable = false, length = 100)
    private String deletedBy;
    
    @CreationTimestamp
    @Column(name = "deleted_time", nullable = false)
    private LocalDateTime deletedTime;
    
    // Additional context information
    @Column(name = "deletion_reason", length = 500)
    private String deletionReason;
    
    @Column(name = "entity_data", columnDefinition = "TEXT")
    private String entityData; // JSON representation of the deleted entity
    
    @Column(name = "related_entity_count")
    private Integer relatedEntityCount = 0; // Count of related entities also deleted
    
    // Constructors
    public RecycleBin() {}
    
    public RecycleBin(Long entityId, String entityName, EntityType entityType, String deletedBy) {
        this.entityId = entityId;
        this.entityName = entityName;
        this.entityType = entityType;
        this.deletedBy = deletedBy;
    }
    
    public RecycleBin(Long entityId, String entityName, EntityType entityType, String deletedBy, String deletionReason) {
        this(entityId, entityName, entityType, deletedBy);
        this.deletionReason = deletionReason;
    }
    
    // Getters and Setters
    public Long getRecycleBinId() {
        return recycleBinId;
    }
    
    public void setRecycleBinId(Long recycleBinId) {
        this.recycleBinId = recycleBinId;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public String getEntityName() {
        return entityName;
    }
    
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
    
    public EntityType getEntityType() {
        return entityType;
    }
    
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    
    public String getDeletedBy() {
        return deletedBy;
    }
    
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
    
    public LocalDateTime getDeletedTime() {
        return deletedTime;
    }
    
    public void setDeletedTime(LocalDateTime deletedTime) {
        this.deletedTime = deletedTime;
    }
    
    public String getDeletionReason() {
        return deletionReason;
    }
    
    public void setDeletionReason(String deletionReason) {
        this.deletionReason = deletionReason;
    }
    
    public String getEntityData() {
        return entityData;
    }
    
    public void setEntityData(String entityData) {
        this.entityData = entityData;
    }
    
    public Integer getRelatedEntityCount() {
        return relatedEntityCount;
    }
    
    public void setRelatedEntityCount(Integer relatedEntityCount) {
        this.relatedEntityCount = relatedEntityCount;
    }
    
    // Helper methods
    public String getDisplayInfo() {
        return String.format("%s (ID: %d) - %s", 
            entityName, entityId, entityType.getDisplayName());
    }
    
    @Override
    public String toString() {
        return "RecycleBin{" +
                "recycleBinId=" + recycleBinId +
                ", entityId=" + entityId +
                ", entityName='" + entityName + '\'' +
                ", entityType=" + entityType +
                ", deletedBy='" + deletedBy + '\'' +
                ", deletedTime=" + deletedTime +
                '}';
    }
}