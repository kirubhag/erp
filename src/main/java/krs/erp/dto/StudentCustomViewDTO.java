package krs.erp.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * DTO for Student Custom View operations
 */
public class StudentCustomViewDTO {
    
    private Long id;
    
    @NotBlank(message = "View name is required")
    @Size(min = 2, max = 100, message = "View name must be between 2 and 100 characters")
    private String viewName;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotEmpty(message = "At least one field must be selected")
    private List<String> selectedFields = new ArrayList<>();
    
    private Boolean isDefault = false;
    
    private String createdByUser;
    
    private Boolean isPublic = false;
    
    private Boolean isActive = true;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private String updatedBy;
    
    // Field counts for UI display
    @JsonProperty("fieldCount")
    public int getFieldCount() {
        return selectedFields != null ? selectedFields.size() : 0;
    }
    
    // Constructors
    public StudentCustomViewDTO() {}
    
    public StudentCustomViewDTO(String viewName, String description, List<String> selectedFields) {
        this.viewName = viewName;
        this.description = description;
        this.selectedFields = selectedFields != null ? new ArrayList<>(selectedFields) : new ArrayList<>();
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
    
    public List<String> getSelectedFields() {
        return selectedFields;
    }
    
    public void setSelectedFields(List<String> selectedFields) {
        this.selectedFields = selectedFields != null ? new ArrayList<>(selectedFields) : new ArrayList<>();
    }
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public String getCreatedByUser() {
        return createdByUser;
    }
    
    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}