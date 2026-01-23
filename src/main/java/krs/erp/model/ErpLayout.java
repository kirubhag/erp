package krs.erp.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entity representing a form layout configuration.
 * Layouts define how sections are arranged for a specific entity type.
 */
@Entity
@Table(name = "erp_layout", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "erp_entity_id", "layout_name" })
})
public class ErpLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_layout_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "erp_entity_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private ErpEntity erpEntity;

    @Column(name = "layout_name", nullable = false, length = 200)
    private String layoutName;

    @Column(name = "layout_type", nullable = false, length = 250)
    private String layoutType = "FORM";

    @Column(name = "layout_columns")
    private Integer layoutColumns = 2;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "is_active")
    private Integer isActive = 1;

    // Constructors
    public ErpLayout() {
    }

    public ErpLayout(ErpEntity erpEntity, String layoutName) {
        this.erpEntity = erpEntity;
        this.layoutName = layoutName;
    }

    public ErpLayout(ErpEntity erpEntity, String layoutName, String layoutType, Integer layoutColumns) {
        this.erpEntity = erpEntity;
        this.layoutName = layoutName;
        this.layoutType = layoutType;
        this.layoutColumns = layoutColumns;
    }

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        modifiedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedTime = LocalDateTime.now();
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

    public String getLayoutName() {
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    public String getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(String layoutType) {
        this.layoutType = layoutType;
    }

    public Integer getLayoutColumns() {
        return layoutColumns;
    }

    public void setLayoutColumns(Integer layoutColumns) {
        this.layoutColumns = layoutColumns;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
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

    @Override
    public String toString() {
        return "ErpLayout{" +
                "id=" + id +
                ", layoutName='" + layoutName + '\'' +
                ", layoutType='" + layoutType + '\'' +
                ", layoutColumns=" + layoutColumns +
                ", isDefault=" + isDefault +
                ", isActive=" + isActive +
                '}';
    }
}
