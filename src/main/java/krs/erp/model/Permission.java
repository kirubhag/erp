package krs.erp.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "permissions")
public class Permission extends BaseEntity {
    
    @NotBlank(message = "Permission name is required")
    @Size(min = 2, max = 100, message = "Permission name must be between 2 and 100 characters")
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "resource", length = 50)
    private String resource;
    
    @Column(name = "action", length = 50)
    private String action;
    
    @Column(name = "system_permission", nullable = false)
    private Boolean systemPermission = false;
    
    // Relationships
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();
    
    // Constructors
    public Permission() {}
    
    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public Permission(String name, String description, String resource, String action) {
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getResource() {
        return resource;
    }
    
    public void setResource(String resource) {
        this.resource = resource;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public Boolean getSystemPermission() {
        return systemPermission;
    }
    
    public void setSystemPermission(Boolean systemPermission) {
        this.systemPermission = systemPermission;
    }
    
    public Set<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
    // Helper methods
    public void addRole(Role role) {
        roles.add(role);
        role.getPermissions().add(this);
    }
    
    public void removeRole(Role role) {
        roles.remove(role);
        role.getPermissions().remove(this);
    }
    
    public String getFullName() {
        if (resource != null && action != null) {
            return resource + ":" + action;
        }
        return name;
    }
    
    @Override
    public String toString() {
        return "Permission{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                ", systemPermission=" + systemPermission +
                '}';
    }
}