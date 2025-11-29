export interface ErpField {
    id: number;
    entityType: string;
    fieldName: string;
    fieldLabel: string;
    fieldType: string;
    uiType?: number;
    // fieldCategory is deprecated - fields are now grouped by sections
    isRequired: boolean;
    isSearchable: boolean;
    isSortable: boolean;
    displayOrder: number;
    fieldDescription?: string;
    defaultWidth?: number;
    maxLength?: number;
    validationPattern?: string;
    picklistOptions?: string;
    decimalPlaces?: number;
    isUnique: boolean;
    showInList: boolean;
    showInForm: boolean;
    columnWidth?: string;
    isActive?: number;
    createdTime?: string;
    modifiedTime?: string;
}

/**
 * Fields grouped by section label
 * Note: Previously named FieldsGroupedByCategory, but fields are now organized by sections
 */
export interface FieldsGroupedBySection {
    [sectionLabel: string]: ErpField[];
}

// Alias for backward compatibility
export type FieldsGroupedByCategory = FieldsGroupedBySection;
