package krs.erp.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "roles")
@AttributeOverride(name = "id", column = @Column(name = "role_id"))
public class Role extends BaseEntity {
    
    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "system_role", nullable = false)
    private Boolean systemRole = false;
    
    // Relationships
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
    
    // Constructors
    public Role() {}
    
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public Role(String name, String description, Boolean systemRole) {
        this.name = name;
        this.description = description;
        this.systemRole = systemRole;
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
    
    public Boolean getSystemRole() {
        return systemRole;
    }
    
    public void setSystemRole(Boolean systemRole) {
        this.systemRole = systemRole;
    }
    
    public Set<User> getUsers() {
        return users;
    }
    
    public void setUsers(Set<User> users) {
        this.users = users;
    }
    
    public Set<Permission> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    
    // Helper methods
    public void addPermission(Permission permission) {
        permissions.add(permission);
        permission.getRoles().add(this);
    }
    
    public void removePermission(Permission permission) {
        permissions.remove(permission);
        permission.getRoles().remove(this);
    }
    
    public void addUser(User user) {
        users.add(user);
        user.getRoles().add(this);
    }
    
    public void removeUser(User user) {
        users.remove(user);
        user.getRoles().remove(this);
    }
    
    public boolean hasPermission(String permissionName) {
        return permissions.stream()
                         .anyMatch(permission -> permission.getName().equals(permissionName));
    }
    
    @Override
    public String toString() {
        return "Role{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", systemRole=" + systemRole +
                '}';
    }
}