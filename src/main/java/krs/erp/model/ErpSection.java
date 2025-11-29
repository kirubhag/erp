package krs.erp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import krs.erp.enums.EntityType;
import krs.erp.enums.SectionLayoutType;

/**
 * Entity representing a section in a module layout
 * Sections group related fields together and define their display layout
 */
@Entity
@Table(name = "erp_sections")
public class ErpSection extends BaseEntity {

    @Column(name = "entity_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Column(name = "section_name", nullable = false, length = 100)
    private String sectionName;

    @Column(name = "section_label", nullable = false, length = 200)
    private String sectionLabel;

    @Column(name = "layout_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SectionLayoutType layoutType = SectionLayoutType.TWO_COLUMN;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_collapsible")
    private Boolean isCollapsible = false;

    @Column(name = "is_collapsed_by_default")
    private Boolean isCollapsedByDefault = false;

    @Column(name = "show_in_create")
    private Boolean showInCreate = true;

    @Column(name = "show_in_edit")
    private Boolean showInEdit = true;

    @Column(name = "show_in_detail")
    private Boolean showInDetail = true;

    @Column(name = "section_icon", length = 100)
    private String sectionIcon;

    @Column(name = "section_color", length = 50)
    private String sectionColor;

    @Column(name = "css_class", length = 100)
    private String cssClass;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "help_text", columnDefinition = "TEXT")
    private String helpText;

    @Column(name = "organization_id")
    private Long organizationId;

    // Constructors
    public ErpSection() {
    }

    public ErpSection(EntityType entityType, String sectionName, String sectionLabel) {
        this.entityType = entityType;
        this.sectionName = sectionName;
        this.sectionLabel = sectionLabel;
        setIsActive(1);
    }

    // Getters and Setters
    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionLabel() {
        return sectionLabel;
    }

    public void setSectionLabel(String sectionLabel) {
        this.sectionLabel = sectionLabel;
    }

    public SectionLayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(SectionLayoutType layoutType) {
        this.layoutType = layoutType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsCollapsible() {
        return isCollapsible;
    }

    public void setIsCollapsible(Boolean isCollapsible) {
        this.isCollapsible = isCollapsible;
    }

    public Boolean getIsCollapsedByDefault() {
        return isCollapsedByDefault;
    }

    public void setIsCollapsedByDefault(Boolean isCollapsedByDefault) {
        this.isCollapsedByDefault = isCollapsedByDefault;
    }

    public Boolean getShowInCreate() {
        return showInCreate;
    }

    public void setShowInCreate(Boolean showInCreate) {
        this.showInCreate = showInCreate;
    }

    public Boolean getShowInEdit() {
        return showInEdit;
    }

    public void setShowInEdit(Boolean showInEdit) {
        this.showInEdit = showInEdit;
    }

    public Boolean getShowInDetail() {
        return showInDetail;
    }

    public void setShowInDetail(Boolean showInDetail) {
        this.showInDetail = showInDetail;
    }

    public String getSectionIcon() {
        return sectionIcon;
    }

    public void setSectionIcon(String sectionIcon) {
        this.sectionIcon = sectionIcon;
    }

    public String getSectionColor() {
        return sectionColor;
    }

    public void setSectionColor(String sectionColor) {
        this.sectionColor = sectionColor;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String toString() {
        return "ErpSection{" +
                "id=" + getId() +
                ", entityType=" + entityType +
                ", sectionName='" + sectionName + '\'' +
                ", sectionLabel='" + sectionLabel + '\'' +
                ", layoutType=" + layoutType +
                ", displayOrder=" + displayOrder +
                '}';
    }
}
