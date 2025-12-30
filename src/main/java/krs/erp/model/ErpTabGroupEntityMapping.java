package krs.erp.model;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entity mapping a Tab Group to an implementation Entity (Tab/Page).
 */
@Entity
@Table(name = "erp_tab_group_entity_rel", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "tab_group_id", "entity_id" })
}, indexes = {
        @Index(name = "idx_rel_tab_group", columnList = "tab_group_id"),
        @Index(name = "idx_rel_entity", columnList = "entity_id")
})
public class ErpTabGroupEntityMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_tab_group_entity_rel_id")
    private Long id;

    @Column(name = "tab_group_id", nullable = false)
    private Long tabGroupId;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "sequence", nullable = false)
    private Integer sequence = 0;

    // Audit fields (not inherited from BaseEntity to avoid column name conflicts)
    @Column(name = "is_active")
    private Integer isActive = 1;

    @Column(name = "created_by")
    private String createdBy;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTabGroupId() {
        return tabGroupId;
    }

    public void setTabGroupId(Long tabGroupId) {
        this.tabGroupId = tabGroupId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ErpTabGroupEntityMapping that = (ErpTabGroupEntityMapping) o;
        return Objects.equals(tabGroupId, that.tabGroupId) && Objects.equals(entityId, that.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tabGroupId, entityId);
    }
}
