package krs.erp.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * Entity representing an ERP entity/module in the system.
 * This is used to manage menu visibility and access control for different modules
 * such as Students, Staff, Attendance, etc.
 */
@Entity
@Table(name = "erp_entities", 
       indexes = {
           @Index(name = "idx_singular_name", columnList = "singular_name"),
           @Index(name = "idx_is_active", columnList = "is_active")
       })
public class ErpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_entity_id")
    private Long id;

    @Column(name = "singular_name", nullable = false, unique = true, length = 100)
    private String singularName;

    @Column(name = "plural_name", nullable = false, length = 100)
    private String pluralName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "sequence", nullable = false)
    private Integer sequence = 0;

    @Column(name = "system_name", length = 100)
    private String systemName;

    @Column(name = "presence", nullable = false)
    private Boolean presence = true;

    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "route", length = 255)
    private String route;

    // New columns for dynamic relationship management
    @Column(name = "table_name", length = 100)
    private String tableName;

    @Column(name = "pkid", length = 100)
    private String pkid;

    @Column(name = "display_column", length = 100)
    private String displayColumn;

    @Column(name = "has_rel_table", nullable = false)
    private Boolean hasRelTable = false;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "last_modified_by")
    private Long lastModifiedBy;

    @JsonIgnore
    @OneToMany(mappedBy = "erpEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ErpEntityRoleRelation> roleRelations = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastModifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSingularName() {
        return singularName;
    }

    public void setSingularName(String singularName) {
        this.singularName = singularName;
    }

    public String getPluralName() {
        return pluralName;
    }

    public void setPluralName(String pluralName) {
        this.pluralName = pluralName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public Boolean getPresence() {
        return presence;
    }

    public void setPresence(Boolean presence) {
        this.presence = presence;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
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

    public Set<ErpEntityRoleRelation> getRoleRelations() {
        return roleRelations;
    }

    public void setRoleRelations(Set<ErpEntityRoleRelation> roleRelations) {
        this.roleRelations = roleRelations;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPkid() {
        return pkid;
    }

    public void setPkid(String pkid) {
        this.pkid = pkid;
    }

    public String getDisplayColumn() {
        return displayColumn;
    }

    public void setDisplayColumn(String displayColumn) {
        this.displayColumn = displayColumn;
    }

    public Boolean getHasRelTable() {
        return hasRelTable;
    }

    public void setHasRelTable(Boolean hasRelTable) {
        this.hasRelTable = hasRelTable;
    }

    /**
     * Helper method to add a role relation
     */
    public void addRoleRelation(ErpEntityRoleRelation relation) {
        roleRelations.add(relation);
        relation.setErpEntity(this);
    }

    /**
     * Helper method to remove a role relation
     */
    public void removeRoleRelation(ErpEntityRoleRelation relation) {
        roleRelations.remove(relation);
        relation.setErpEntity(null);
    }
}
