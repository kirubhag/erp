/**
 * Show Type enum for field visibility control
 */
export enum ShowType {
    SHOW_EVERYWHERE = 0,  // Show in Create, Edit, and View
    VIEW_ONLY = 1,        // Show only in View (system fields)
    HIDDEN = 2            // Hidden completely (internal only)
}

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
    showType?: number;  // 0=show everywhere, 1=view only, 2=hidden
    isActive?: number;
    createdTime?: string;
    modifiedTime?: string;
}

/**
 * Helper functions for ErpField visibility
 */
export class ErpFieldHelper {
    static isShowInCreate(field: ErpField): boolean {
        return field.showType === undefined || field.showType === ShowType.SHOW_EVERYWHERE;
    }

    static isShowInEdit(field: ErpField): boolean {
        return field.showType === undefined || field.showType === ShowType.SHOW_EVERYWHERE;
    }

    static isShowInView(field: ErpField): boolean {
        return field.showType !== ShowType.HIDDEN;
    }

    static isSystemField(field: ErpField): boolean {
        return field.showType === ShowType.VIEW_ONLY;
    }

    static isHiddenField(field: ErpField): boolean {
        return field.showType === ShowType.HIDDEN;
    }
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
