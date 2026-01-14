package krs.erp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entity representing the relationship between ERP entities and user roles.
 * This mapping determines which entities (menu items) are visible to users with specific roles.
 */
@Entity
@Table(name = "erp_entities_role_relation",
       uniqueConstraints = {
           @UniqueConstraint(name = "unique_entity_role", columnNames = {"entity_id", "role_id"})
       },
       indexes = {
           @Index(name = "idx_entity_id", columnList = "entity_id"),
           @Index(name = "idx_role_id", columnList = "role_id")
       })
public class ErpEntityRoleRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_entity_role_relation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", nullable = false)
    private ErpEntity erpEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "last_modified_by")
    private Long lastModifiedBy;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastModifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDateTime.now();
    }

    // Constructors
    public ErpEntityRoleRelation() {
    }

    public ErpEntityRoleRelation(ErpEntity erpEntity, Role role) {
        this.erpEntity = erpEntity;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpEntity getErpEntity() {
        return erpEntity;
    }

    public void setErpEntity(ErpEntity erpEntity) {
        this.erpEntity = erpEntity;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErpEntityRoleRelation)) return false;
        
        ErpEntityRoleRelation that = (ErpEntityRoleRelation) o;
        
        if (erpEntity != null && that.erpEntity != null && 
            erpEntity.getId() != null && that.erpEntity.getId() != null) {
            if (!erpEntity.getId().equals(that.erpEntity.getId())) return false;
        }
        
        if (role != null && that.role != null && 
            role.getId() != null && that.role.getId() != null) {
            return role.getId().equals(that.role.getId());
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int result = erpEntity != null && erpEntity.getId() != null ? erpEntity.getId().hashCode() : 0;
        result = 31 * result + (role != null && role.getId() != null ? role.getId().hashCode() : 0);
        return result;
    }
}
