package krs.erp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import krs.erp.enums.EntityType;

/**
 * Entity for managing auto-number sequences for different entity types and fields.
 * This table stores the configuration and current state of auto-number fields,
 * allowing unique number generation with configurable prefix and suffix.
 * 
 * Uses optimistic locking (@Version) to prevent race conditions and deadlocks
 * during concurrent number generation.
 * 
 * Links to ErpField via FK relationship - field_name is derived from erpField.fieldName
 */
@Entity
@Table(name = "erp_auto_numbers", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "erp_field_id" })
})
@AttributeOverride(name = "id", column = @Column(name = "erp_auto_number_id"))
public class ErpAutoNumber extends BaseEntity {

    /**
     * Reference to the ErpField this auto-number is configured for.
     * The entity_type and field_name can be derived from this relationship.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "erp_field_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "section" })
    private ErpField erpField;

    @Column(name = "prefix", length = 50)
    private String prefix;

    @Column(name = "suffix", length = 50)
    private String suffix;

    @Column(name = "next_number", nullable = false)
    private Long nextNumber = 1L;

    @Column(name = "padding_length")
    private Integer paddingLength = 4;

    @Column(name = "description", length = 500)
    private String description;

    /**
     * Version field for optimistic locking to prevent deadlocks
     * during concurrent number generation
     */
    @Version
    @Column(name = "version")
    private Long version;

    // Constructors
    public ErpAutoNumber() {
    }

    public ErpAutoNumber(ErpField erpField, String prefix, String suffix, Long nextNumber) {
        this.erpField = erpField;
        this.prefix = prefix;
        this.suffix = suffix;
        this.nextNumber = nextNumber;
    }

    // Getters and Setters
    
    public ErpField getErpField() {
        return erpField;
    }

    public void setErpField(ErpField erpField) {
        this.erpField = erpField;
    }

    /**
     * Get entity type from the linked ErpField
     */
    public EntityType getEntityType() {
        return erpField != null ? erpField.getEntityType() : null;
    }

    /**
     * Get field name from the linked ErpField
     */
    public String getFieldName() {
        return erpField != null ? erpField.getFieldName() : null;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Long getNextNumber() {
        return nextNumber;
    }

    public void setNextNumber(Long nextNumber) {
        this.nextNumber = nextNumber;
    }

    public Integer getPaddingLength() {
        return paddingLength;
    }

    public void setPaddingLength(Integer paddingLength) {
        this.paddingLength = paddingLength;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Generate the next auto-number value based on current configuration.
     * This method increments the nextNumber but does NOT persist the change.
     * The caller must save the entity after calling this method.
     * 
     * @return The formatted auto-number string (e.g., "STU-0001" or "INV-2024-0001-Q1")
     */
    public String generateNextNumber() {
        String paddedNumber = String.format("%0" + (paddingLength != null ? paddingLength : 4) + "d", nextNumber);
        
        StringBuilder result = new StringBuilder();
        if (prefix != null && !prefix.isEmpty()) {
            result.append(prefix);
        }
        result.append(paddedNumber);
        if (suffix != null && !suffix.isEmpty()) {
            result.append(suffix);
        }
        
        // Increment for next use (optimistic locking will handle concurrent access)
        nextNumber++;
        
        return result.toString();
    }

    /**
     * Preview what the next number will look like without incrementing
     * 
     * @return The formatted preview string
     */
    public String previewNextNumber() {
        String paddedNumber = String.format("%0" + (paddingLength != null ? paddingLength : 4) + "d", nextNumber);
        
        StringBuilder result = new StringBuilder();
        if (prefix != null && !prefix.isEmpty()) {
            result.append(prefix);
        }
        result.append(paddedNumber);
        if (suffix != null && !suffix.isEmpty()) {
            result.append(suffix);
        }
        
        return result.toString();
    }

    @Override
    public String toString() {
        return "ErpAutoNumber{" +
                "erpFieldId=" + (erpField != null ? erpField.getId() : null) +
                ", entityType=" + getEntityType() +
                ", fieldName='" + getFieldName() + '\'' +
                ", prefix='" + prefix + '\'' +
                ", suffix='" + suffix + '\'' +
                ", nextNumber=" + nextNumber +
                ", paddingLength=" + paddingLength +
                '}';
    }
}
