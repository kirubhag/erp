package krs.erp.dto;

import java.util.List;

/**
 * DTO for saving module layout configuration
 */
public class LayoutSaveDTO {

    private String entityType;
    private List<SectionLayoutDTO> sections;

    // Constructors
    public LayoutSaveDTO() {
    }

    public LayoutSaveDTO(String entityType, List<SectionLayoutDTO> sections) {
        this.entityType = entityType;
        this.sections = sections;
    }

    // Getters and Setters
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public List<SectionLayoutDTO> getSections() {
        return sections;
    }

    public void setSections(List<SectionLayoutDTO> sections) {
        this.sections = sections;
    }

    /**
     * Inner class for section layout details
     */
    public static class SectionLayoutDTO {
        private String sectionName;
        private String sectionLabel;
        private Integer displayOrder;
        private List<FieldPositionDTO> fields;

        // Constructors
        public SectionLayoutDTO() {
        }

        public SectionLayoutDTO(String sectionName, String sectionLabel, Integer displayOrder,
                List<FieldPositionDTO> fields) {
            this.sectionName = sectionName;
            this.sectionLabel = sectionLabel;
            this.displayOrder = displayOrder;
            this.fields = fields;
        }

        // Getters and Setters
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

        public Integer getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
        }

        public List<FieldPositionDTO> getFields() {
            return fields;
        }

        public void setFields(List<FieldPositionDTO> fields) {
            this.fields = fields;
        }
    }

    /**
     * Inner class for field positioning details
     */
    public static class FieldPositionDTO {
        private String fieldName;
        private Integer rowPosition;
        private Integer columnPosition;
        private String fieldProperties;

        // Constructors
        public FieldPositionDTO() {
        }

        public FieldPositionDTO(String fieldName, Integer rowPosition, Integer columnPosition) {
            this.fieldName = fieldName;
            this.rowPosition = rowPosition;
            this.columnPosition = columnPosition;
        }

        public FieldPositionDTO(String fieldName, Integer rowPosition, Integer columnPosition, String fieldProperties) {
            this.fieldName = fieldName;
            this.rowPosition = rowPosition;
            this.columnPosition = columnPosition;
            this.fieldProperties = fieldProperties;
        }

        // Getters and Setters
        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public Integer getRowPosition() {
            return rowPosition;
        }

        public void setRowPosition(Integer rowPosition) {
            this.rowPosition = rowPosition;
        }

        public Integer getColumnPosition() {
            return columnPosition;
        }

        public void setColumnPosition(Integer columnPosition) {
            this.columnPosition = columnPosition;
        }

        public String getFieldProperties() {
            return fieldProperties;
        }

        public void setFieldProperties(String fieldProperties) {
            this.fieldProperties = fieldProperties;
        }
    }
}
