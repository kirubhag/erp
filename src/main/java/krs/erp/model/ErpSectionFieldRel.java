package krs.erp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * Entity representing the relationship between sections and fields.
 * This is a many-to-many relationship table that also stores the field order within a section.
 */
@Entity
@Table(name = "erp_sections_field_rel")
public class ErpSectionFieldRel {

    @EmbeddedId
    private ErpSectionFieldRelId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("erpSectionId")
    @JoinColumn(name = "erp_section_id")
    private ErpSection erpSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("erpFieldId")
    @JoinColumn(name = "erp_field_id")
    private ErpField erpField;

    @Column(name = "field_order")
    private Integer fieldOrder = 0;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;

    // Constructors
    public ErpSectionFieldRel() {
        this.id = new ErpSectionFieldRelId();
    }

    public ErpSectionFieldRel(ErpSection section, ErpField field) {
        this.id = new ErpSectionFieldRelId(section.getId(), field.getId());
        this.erpSection = section;
        this.erpField = field;
    }

    public ErpSectionFieldRel(ErpSection section, ErpField field, Integer fieldOrder) {
        this(section, field);
        this.fieldOrder = fieldOrder;
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
    public ErpSectionFieldRelId getId() {
        return id;
    }

    public void setId(ErpSectionFieldRelId id) {
        this.id = id;
    }

    public ErpSection getErpSection() {
        return erpSection;
    }

    public void setErpSection(ErpSection erpSection) {
        this.erpSection = erpSection;
        if (this.id == null) {
            this.id = new ErpSectionFieldRelId();
        }
        this.id.setErpSectionId(erpSection.getId());
    }

    public ErpField getErpField() {
        return erpField;
    }

    public void setErpField(ErpField erpField) {
        this.erpField = erpField;
        if (this.id == null) {
            this.id = new ErpSectionFieldRelId();
        }
        this.id.setErpFieldId(erpField.getId());
    }

    public Integer getFieldOrder() {
        return fieldOrder;
    }

    public void setFieldOrder(Integer fieldOrder) {
        this.fieldOrder = fieldOrder;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErpSectionFieldRel that = (ErpSectionFieldRel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Composite primary key class for ErpSectionFieldRel
     */
    @Embeddable
    public static class ErpSectionFieldRelId implements Serializable {
        private static final long serialVersionUID = 1L;

        @Column(name = "erp_section_id")
        private Long erpSectionId;

        @Column(name = "erp_field_id")
        private Long erpFieldId;

        public ErpSectionFieldRelId() {
        }

        public ErpSectionFieldRelId(Long erpSectionId, Long erpFieldId) {
            this.erpSectionId = erpSectionId;
            this.erpFieldId = erpFieldId;
        }

        public Long getErpSectionId() {
            return erpSectionId;
        }

        public void setErpSectionId(Long erpSectionId) {
            this.erpSectionId = erpSectionId;
        }

        public Long getErpFieldId() {
            return erpFieldId;
        }

        public void setErpFieldId(Long erpFieldId) {
            this.erpFieldId = erpFieldId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ErpSectionFieldRelId that = (ErpSectionFieldRelId) o;
            return Objects.equals(erpSectionId, that.erpSectionId) &&
                    Objects.equals(erpFieldId, that.erpFieldId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(erpSectionId, erpFieldId);
        }
    }
}
