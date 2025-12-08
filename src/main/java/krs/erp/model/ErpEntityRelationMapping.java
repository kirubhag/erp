package krs.erp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing relationships between ERP entities in IAM_MasterDB.
 * This is a system configuration table that stores entity-level relationships
 * with foreign key references to erp_entities table.
 * 
 * Note: This is different from ErpEntityRelation which is used in tenant databases.
 */
@Entity
@Table(name = "erp_entity_relations", schema = "IAM_MasterDB")
public class ErpEntityRelationMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relation_id")
    private Long relationId;

    @NotNull
    @Column(name = "parent_entity_id", nullable = false)
    private Long parentEntityId;

    @NotNull
    @Column(name = "child_entity_id", nullable = false)
    private Long childEntityId;

    @NotNull
    @Column(name = "relation_type", nullable = false, length = 50)
    private String relationType;

    @Column(name = "relation_name", length = 255)
    private String relationName;

    @Column(name = "foreign_key_column", length = 100)
    private String foreignKeyColumn;

    @Column(name = "is_mandatory", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isMandatory = false;

    @Column(name = "cascade_delete", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean cascadeDelete = false;

    @Column(name = "display_order", columnDefinition = "INT DEFAULT 0")
    private Integer displayOrder = 0;

    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;

    // Constructors
    public ErpEntityRelationMapping() {
        this.createdTime = LocalDateTime.now();
        this.modifiedTime = LocalDateTime.now();
    }

    public ErpEntityRelationMapping(Long parentEntityId, Long childEntityId, String relationType) {
        this();
        this.parentEntityId = parentEntityId;
        this.childEntityId = childEntityId;
        this.relationType = relationType;
    }

    // Getters and Setters
    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public Long getParentEntityId() {
        return parentEntityId;
    }

    public void setParentEntityId(Long parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public Long getChildEntityId() {
        return childEntityId;
    }

    public void setChildEntityId(Long childEntityId) {
        this.childEntityId = childEntityId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getForeignKeyColumn() {
        return foreignKeyColumn;
    }

    public void setForeignKeyColumn(String foreignKeyColumn) {
        this.foreignKeyColumn = foreignKeyColumn;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public Boolean getCascadeDelete() {
        return cascadeDelete;
    }

    public void setCascadeDelete(Boolean cascadeDelete) {
        this.cascadeDelete = cascadeDelete;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
}
