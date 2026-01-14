package krs.erp.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import krs.erp.enums.EntityType;

/**
 * Generic Entity representing a custom view configuration for any entity type.
 * A custom view defines which fields should be displayed when viewing entities.
 */
@Entity
@Table(name = "erp_custom_views")
@AttributeOverride(name = "id", column = @Column(name = "custom_view_id"))
public class CustomView extends BaseEntity {
    
    @NotBlank(message = "View name is required")
    @Size(min = 2, max = 100, message = "View name must be between 2 and 100 characters")
    @Column(name = "view_name", nullable = false, length = 100)
    private String viewName;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @NotNull(message = "Entity type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 50)
    private EntityType entityType; // e.g., STUDENT, PARENT, ATTENDANCE, HEALTH, etc.
    
    @NotEmpty(message = "At least one field must be selected")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "erp_custom_view_fields",
        joinColumns = @JoinColumn(name = "custom_view_id")
    )
    @Column(name = "field_name")
    private List<String> selectedFields = new ArrayList<>();
    
    @Column(name = "is_default")
    private Boolean isDefault = false;
    
    @Column(name = "created_by_user")
    private String createdByUser;
    
    @Column(name = "is_public")
    private Boolean isPublic = false;
    
    // Constructors
    public CustomView() {}
    
    public CustomView(String viewName, String description, EntityType entityType, List<String> selectedFields) {
        this.viewName = viewName;
        this.description = description;
        this.entityType = entityType;
        this.selectedFields = selectedFields != null ? new ArrayList<>(selectedFields) : new ArrayList<>();
    }
    
    // Getters and Setters
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
    
    // Helper methods
    public void addField(String fieldName) {
        if (fieldName != null && !selectedFields.contains(fieldName)) {
            selectedFields.add(fieldName);
        }
    }
    
    public void removeField(String fieldName) {
        selectedFields.remove(fieldName);
    }
    
    public boolean hasField(String fieldName) {
        return selectedFields.contains(fieldName);
    }
    
    public int getFieldCount() {
        return selectedFields.size();
    }
    
    @Override
    public String toString() {
        return "CustomView{" +
                "id=" + getId() +
                ", viewName='" + viewName + '\'' +
                ", description='" + description + '\'' +
                ", entityType='" + entityType + '\'' +
                ", fieldCount=" + selectedFields.size() +
                ", isDefault=" + isDefault +
                ", isPublic=" + isPublic +
                '}';
    }
}