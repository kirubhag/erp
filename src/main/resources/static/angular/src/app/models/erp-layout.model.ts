/**
 * Layout model representing a complete form layout with sections and fields
 * This provides the hierarchical structure: Layout -> Sections -> Fields
 */
export interface ErpLayout {
    id?: number;
    layoutName: string;
    layoutType: LayoutType;
    layoutColumns: number;
    isDefault: boolean;
    description?: string;
    erpEntityId?: number;
    entityType?: string;
    sections?: ErpLayoutSection[];
    
    // Activity tracking
    isActive?: number;
    createdTime?: string;
    modifiedTime?: string;
}

/**
 * Layout type enum
 */
export type LayoutType = 'FORM' | 'LIST' | 'DETAIL';

/**
 * Section within a layout with its fields
 */
export interface ErpLayoutSection {
    id?: number;
    sectionName: string;
    sectionLabel: string;
    layoutType: SectionLayoutType;
    displayOrder: number;
    isCollapsible: boolean;
    isCollapsedByDefault: boolean;
    showInCreate: boolean;
    showInEdit: boolean;
    showInDetail: boolean;
    sectionIcon?: string;
    sectionColor?: string;
    cssClass?: string;
    description?: string;
    helpText?: string;
    fields?: ErpLayoutField[];
}

/**
 * Section layout type enum
 */
export type SectionLayoutType =
    | 'SINGLE_COLUMN'
    | 'TWO_COLUMN'
    | 'THREE_COLUMN'
    | 'FOUR_COLUMN';

/**
 * Field within a section
 */
export interface ErpLayoutField {
    id?: number;
    fieldName: string;
    fieldLabel: string;
    fieldType: string;
    uiType?: number;
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
    isUnique?: boolean;
    showInList: boolean;
    showInForm: boolean;
    showType?: number;
    fieldProperties?: string;
    fieldOrder?: number; // Order within the section
}

/**
 * Helper to get column count from section layout type
 */
export function getSectionColumnCount(layoutType: SectionLayoutType): number {
    switch (layoutType) {
        case 'SINGLE_COLUMN': return 1;
        case 'TWO_COLUMN': return 2;
        case 'THREE_COLUMN': return 3;
        case 'FOUR_COLUMN': return 4;
        default: return 2;
    }
}

/**
 * Helper to convert Layout sections to flat field array
 */
export function flattenLayoutFields(layout: ErpLayout): ErpLayoutField[] {
    if (!layout.sections) return [];
    
    const fields: ErpLayoutField[] = [];
    for (const section of layout.sections) {
        if (section.fields) {
            fields.push(...section.fields);
        }
    }
    return fields;
}

/**
 * Helper to filter sections by view mode
 */
export function filterSectionsByMode(
    sections: ErpLayoutSection[],
    mode: 'create' | 'edit' | 'detail'
): ErpLayoutSection[] {
    return sections.filter(section => {
        switch (mode) {
            case 'create': return section.showInCreate;
            case 'edit': return section.showInEdit;
            case 'detail': return section.showInDetail;
            default: return true;
        }
    });
}

/**
 * Helper to filter fields by show type
 */
export function filterFieldsByShowType(
    fields: ErpLayoutField[],
    mode: 'create' | 'edit' | 'view'
): ErpLayoutField[] {
    return fields.filter(field => {
        const showType = field.showType ?? 0;
        switch (mode) {
            case 'create':
            case 'edit':
                // Exclude view-only and hidden fields
                return showType === 0;
            case 'view':
                // Exclude only hidden fields
                return showType !== 2;
            default:
                return true;
        }
    });
}
