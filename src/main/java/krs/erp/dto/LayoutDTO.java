package krs.erp.dto;

import java.util.List;

/**
 * DTO representing a complete layout with its sections and fields.
 * This provides a hierarchical structure: Layout -> Sections -> Fields
 */
public class LayoutDTO {

    private Long id;
    private String layoutName;
    private String layoutType;
    private Integer layoutColumns;
    private Boolean isDefault;
    private String description;
    private Long erpEntityId;
    private String entityType;
    private List<SectionDTO> sections;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getErpEntityId() {
        return erpEntityId;
    }

    public void setErpEntityId(Long erpEntityId) {
        this.erpEntityId = erpEntityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public List<SectionDTO> getSections() {
        return sections;
    }

    public void setSections(List<SectionDTO> sections) {
        this.sections = sections;
    }

    /**
     * DTO representing a section with its fields
     */
    public static class SectionDTO {
        private Long id;
        private String sectionName;
        private String sectionLabel;
        private String layoutType;
        private Integer displayOrder;
        private Boolean isCollapsible;
        private Boolean isCollapsedByDefault;
        private Boolean showInCreate;
        private Boolean showInEdit;
        private Boolean showInDetail;
        private String sectionIcon;
        private String sectionColor;
        private String cssClass;
        private String description;
        private String helpText;
        private List<FieldDTO> fields;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public String getLayoutType() {
            return layoutType;
        }

        public void setLayoutType(String layoutType) {
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

        public List<FieldDTO> getFields() {
            return fields;
        }

        public void setFields(List<FieldDTO> fields) {
            this.fields = fields;
        }
    }

    /**
     * DTO representing a field within a section
     */
    public static class FieldDTO {
        private Long id;
        private String fieldName;
        private String fieldLabel;
        private String fieldType;
        private Integer uiType;
        private Boolean isRequired;
        private Boolean isSearchable;
        private Boolean isSortable;
        private Integer displayOrder;
        private String fieldDescription;
        private Integer defaultWidth;
        private Integer maxLength;
        private String validationPattern;
        private String picklistOptions;
        private Integer decimalPlaces;
        private Boolean isUnique;
        private Boolean showInList;
        private Boolean showInForm;
        private Integer showType;
        private String fieldProperties;
        private Integer fieldOrder; // Order within the section

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldLabel() {
            return fieldLabel;
        }

        public void setFieldLabel(String fieldLabel) {
            this.fieldLabel = fieldLabel;
        }

        public String getFieldType() {
            return fieldType;
        }

        public void setFieldType(String fieldType) {
            this.fieldType = fieldType;
        }

        public Integer getUiType() {
            return uiType;
        }

        public void setUiType(Integer uiType) {
            this.uiType = uiType;
        }

        public Boolean getIsRequired() {
            return isRequired;
        }

        public void setIsRequired(Boolean isRequired) {
            this.isRequired = isRequired;
        }

        public Boolean getIsSearchable() {
            return isSearchable;
        }

        public void setIsSearchable(Boolean isSearchable) {
            this.isSearchable = isSearchable;
        }

        public Boolean getIsSortable() {
            return isSortable;
        }

        public void setIsSortable(Boolean isSortable) {
            this.isSortable = isSortable;
        }

        public Integer getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
        }

        public String getFieldDescription() {
            return fieldDescription;
        }

        public void setFieldDescription(String fieldDescription) {
            this.fieldDescription = fieldDescription;
        }

        public Integer getDefaultWidth() {
            return defaultWidth;
        }

        public void setDefaultWidth(Integer defaultWidth) {
            this.defaultWidth = defaultWidth;
        }

        public Integer getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(Integer maxLength) {
            this.maxLength = maxLength;
        }

        public String getValidationPattern() {
            return validationPattern;
        }

        public void setValidationPattern(String validationPattern) {
            this.validationPattern = validationPattern;
        }

        public String getPicklistOptions() {
            return picklistOptions;
        }

        public void setPicklistOptions(String picklistOptions) {
            this.picklistOptions = picklistOptions;
        }

        public Integer getDecimalPlaces() {
            return decimalPlaces;
        }

        public void setDecimalPlaces(Integer decimalPlaces) {
            this.decimalPlaces = decimalPlaces;
        }

        public Boolean getIsUnique() {
            return isUnique;
        }

        public void setIsUnique(Boolean isUnique) {
            this.isUnique = isUnique;
        }

        public Boolean getShowInList() {
            return showInList;
        }

        public void setShowInList(Boolean showInList) {
            this.showInList = showInList;
        }

        public Boolean getShowInForm() {
            return showInForm;
        }

        public void setShowInForm(Boolean showInForm) {
            this.showInForm = showInForm;
        }

        public Integer getShowType() {
            return showType;
        }

        public void setShowType(Integer showType) {
            this.showType = showType;
        }

        public String getFieldProperties() {
            return fieldProperties;
        }

        public void setFieldProperties(String fieldProperties) {
            this.fieldProperties = fieldProperties;
        }

        public Integer getFieldOrder() {
            return fieldOrder;
        }

        public void setFieldOrder(Integer fieldOrder) {
            this.fieldOrder = fieldOrder;
        }
    }
}
