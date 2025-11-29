package krs.erp.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entity representing relationships between ERP entities (e.g., Student → Guardian, Student → Medical).
 * This allows dynamic relationship tracking and query building without hard-coding relationships.
 */
@Entity
@Table(name = "erp_entity_relation")
@AttributeOverride(name = "id", column = @Column(name = "erp_entity_relation_id"))
public class ErpEntityRelation extends BaseEntity {

    // Parent table information
    @NotBlank(message = "Parent table name is required")
    @Size(max = 100, message = "Parent table name must not exceed 100 characters")
    @Column(name = "p_table_name", nullable = false, length = 100)
    private String parentTableName;

    @NotBlank(message = "Parent primary key column name is required")
    @Size(max = 100, message = "Parent PKID must not exceed 100 characters")
    @Column(name = "p_pkid", nullable = false, length = 100)
    private String parentPkid;

    @Size(max = 100, message = "Parent display column must not exceed 100 characters")
    @Column(name = "p_display_column", length = 100)
    private String parentDisplayColumn;

    // Child table information
    @NotBlank(message = "Child table name is required")
    @Size(max = 100, message = "Child table name must not exceed 100 characters")
    @Column(name = "c_table_name", nullable = false, length = 100)
    private String childTableName;

    @NotBlank(message = "Child primary key column name is required")
    @Size(max = 100, message = "Child PKID must not exceed 100 characters")
    @Column(name = "c_pkid", nullable = false, length = 100)
    private String childPkid;

    @Size(max = 100, message = "Child display column must not exceed 100 characters")
    @Column(name = "c_display_column", length = 100)
    private String childDisplayColumn;

    // Foreign key column in child table that references parent
    @NotBlank(message = "Foreign key column is required")
    @Size(max = 100, message = "Foreign key column must not exceed 100 characters")
    @Column(name = "fk_column", nullable = false, length = 100)
    private String foreignKeyColumn;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Constructors
    public ErpEntityRelation() {
    }

    public ErpEntityRelation(String parentTableName, String parentPkid, String parentDisplayColumn,
                             String childTableName, String childPkid, String childDisplayColumn,
                             String foreignKeyColumn) {
        this.parentTableName = parentTableName;
        this.parentPkid = parentPkid;
        this.parentDisplayColumn = parentDisplayColumn;
        this.childTableName = childTableName;
        this.childPkid = childPkid;
        this.childDisplayColumn = childDisplayColumn;
        this.foreignKeyColumn = foreignKeyColumn;
    }

    // Getters and Setters
    public String getParentTableName() {
        return parentTableName;
    }

    public void setParentTableName(String parentTableName) {
        this.parentTableName = parentTableName;
    }

    public String getParentPkid() {
        return parentPkid;
    }

    public void setParentPkid(String parentPkid) {
        this.parentPkid = parentPkid;
    }

    public String getParentDisplayColumn() {
        return parentDisplayColumn;
    }

    public void setParentDisplayColumn(String parentDisplayColumn) {
        this.parentDisplayColumn = parentDisplayColumn;
    }

    public String getChildTableName() {
        return childTableName;
    }

    public void setChildTableName(String childTableName) {
        this.childTableName = childTableName;
    }

    public String getChildPkid() {
        return childPkid;
    }

    public void setChildPkid(String childPkid) {
        this.childPkid = childPkid;
    }

    public String getChildDisplayColumn() {
        return childDisplayColumn;
    }

    public void setChildDisplayColumn(String childDisplayColumn) {
        this.childDisplayColumn = childDisplayColumn;
    }

    public String getForeignKeyColumn() {
        return foreignKeyColumn;
    }

    public void setForeignKeyColumn(String foreignKeyColumn) {
        this.foreignKeyColumn = foreignKeyColumn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ErpEntityRelation{" +
                "parentTableName='" + parentTableName + '\'' +
                ", childTableName='" + childTableName + '\'' +
                ", foreignKeyColumn='" + foreignKeyColumn + '\'' +
                '}';
    }
}
