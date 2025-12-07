package krs.erp.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "created_by")
    private String createdBy;
    
    // Additional audit fields as per requirements
    @Column(name = "modified_by")
    private String modifiedBy;
    
    @CreationTimestamp
    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;
    
    @UpdateTimestamp
    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;
    
    @Column(name = "owner_id")
    private Long ownerId;
    
    /**
     * Active status:
     * 1 = Active/Normal
     * 0 = Inactive/Disabled
     * -1 = Soft Deleted/In Recycle Bin
     */
    @Column(name = "is_active")
    private Integer isActive = 1;
    
    // Constructors
    public BaseEntity() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getModifiedBy() {
        return modifiedBy;
    }
    
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }
    
    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public Integer getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
    
    // Convenience methods for active status
    public boolean isActive() {
        return isActive != null && isActive == 1;
    }
    
    public boolean isDeleted() {
        return isActive != null && isActive == -1;
    }
    
    public boolean isInactive() {
        return isActive != null && isActive == 0;
    }
    
    public void markAsActive() {
        this.isActive = 1;
    }
    
    public void markAsInactive() {
        this.isActive = 0;
    }
    
    public void markAsDeleted() {
        this.isActive = -1;
    }
}