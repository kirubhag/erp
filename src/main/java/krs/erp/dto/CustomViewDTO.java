package krs.erp.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import krs.erp.enums.EntityType;

public class CustomViewDTO {
    
    private Long id;
    private String viewName;
    private String description;
    private EntityType entityType;
    private List<String> selectedFields;
    private Boolean isDefault;
    private Boolean isPublic;
    private String createdBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    private String modifiedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedTime;
    
    // Constructors
    public CustomViewDTO() {}
    
    public CustomViewDTO(Long id, String viewName, String description, EntityType entityType, 
                        List<String> selectedFields, Boolean isDefault, Boolean isPublic,
                        String createdBy, LocalDateTime createdTime, String modifiedBy, LocalDateTime modifiedTime) {
        this.id = id;
        this.viewName = viewName;
        this.description = description;
        this.entityType = entityType;
        this.selectedFields = selectedFields;
        this.isDefault = isDefault;
        this.isPublic = isPublic;
        this.createdBy = createdBy;
        this.createdTime = createdTime;
        this.modifiedBy = modifiedBy;
        this.modifiedTime = modifiedTime;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getViewName() {
        return viewName;
    }
    
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public EntityType getEntityType() {
        return entityType;
    }
    
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    
    public List<String> getSelectedFields() {
        return selectedFields;
    }
    
    public void setSelectedFields(List<String> selectedFields) {
        this.selectedFields = selectedFields;
    }
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public String getModifiedBy() {
        return modifiedBy;
    }
    
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
    
    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }
    
    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
    
    @Override
    public String toString() {
        return "CustomViewDTO{" +
                "id=" + id +
                ", viewName='" + viewName + '\'' +
                ", description='" + description + '\'' +
                ", entityType=" + entityType +
                ", selectedFields=" + selectedFields +
                ", isDefault=" + isDefault +
                ", isPublic=" + isPublic +
                ", createdBy='" + createdBy + '\'' +
                ", createdTime=" + createdTime +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", modifiedTime=" + modifiedTime +
                '}';
    }
}