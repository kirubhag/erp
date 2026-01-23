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
 * Entity representing the relationship between layouts and sections.
 * This is a many-to-many relationship table that stores the section order within a layout.
 */
@Entity
@Table(name = "erp_layout_section_rel")
public class ErpLayoutSectionRel {

    @EmbeddedId
    private ErpLayoutSectionRelId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("erpLayoutId")
    @JoinColumn(name = "erp_layout_id")
    private ErpLayout erpLayout;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("erpSectionId")
    @JoinColumn(name = "erp_section_id")
    private ErpSection erpSection;

    @Column(name = "section_order")
    private Integer sectionOrder = 0;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;

    // Constructors
    public ErpLayoutSectionRel() {
        this.id = new ErpLayoutSectionRelId();
    }

    public ErpLayoutSectionRel(ErpLayout layout, ErpSection section) {
        this.id = new ErpLayoutSectionRelId(layout.getId(), section.getId());
        this.erpLayout = layout;
        this.erpSection = section;
    }

    public ErpLayoutSectionRel(ErpLayout layout, ErpSection section, Integer sectionOrder) {
        this(layout, section);
        this.sectionOrder = sectionOrder;
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
    public ErpLayoutSectionRelId getId() {
        return id;
    }

    public void setId(ErpLayoutSectionRelId id) {
        this.id = id;
    }

    public ErpLayout getErpLayout() {
        return erpLayout;
    }

    public void setErpLayout(ErpLayout erpLayout) {
        this.erpLayout = erpLayout;
        if (this.id == null) {
            this.id = new ErpLayoutSectionRelId();
        }
        this.id.setErpLayoutId(erpLayout.getId());
    }

    public ErpSection getErpSection() {
        return erpSection;
    }

    public void setErpSection(ErpSection erpSection) {
        this.erpSection = erpSection;
        if (this.id == null) {
            this.id = new ErpLayoutSectionRelId();
        }
        this.id.setErpSectionId(erpSection.getId());
    }

    public Integer getSectionOrder() {
        return sectionOrder;
    }

    public void setSectionOrder(Integer sectionOrder) {
        this.sectionOrder = sectionOrder;
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
        ErpLayoutSectionRel that = (ErpLayoutSectionRel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Composite primary key class for ErpLayoutSectionRel
     */
    @Embeddable
    public static class ErpLayoutSectionRelId implements Serializable {
        private static final long serialVersionUID = 1L;

        @Column(name = "erp_layout_id")
        private Long erpLayoutId;

        @Column(name = "erp_section_id")
        private Long erpSectionId;

        public ErpLayoutSectionRelId() {
        }

        public ErpLayoutSectionRelId(Long erpLayoutId, Long erpSectionId) {
            this.erpLayoutId = erpLayoutId;
            this.erpSectionId = erpSectionId;
        }

        public Long getErpLayoutId() {
            return erpLayoutId;
        }

        public void setErpLayoutId(Long erpLayoutId) {
            this.erpLayoutId = erpLayoutId;
        }

        public Long getErpSectionId() {
            return erpSectionId;
        }

        public void setErpSectionId(Long erpSectionId) {
            this.erpSectionId = erpSectionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ErpLayoutSectionRelId that = (ErpLayoutSectionRelId) o;
            return Objects.equals(erpLayoutId, that.erpLayoutId) &&
                    Objects.equals(erpSectionId, that.erpSectionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(erpLayoutId, erpSectionId);
        }
    }
}
