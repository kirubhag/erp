/**
 * Interface representing a module section configuration
 * Maps to ErpSection entity on the backend
 */
export interface ErpSection {
    id?: number;
    entityType: string;
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
    organizationId?: number;

    // Activity tracking
    isActive?: number;
    createdBy?: string;
    createdTime?: string;
    modifiedBy?: string;
    modifiedTime?: string;
}

/**
 * Section layout type enum
 * Determines the number of columns in the section
 */
export type SectionLayoutType =
    | 'SINGLE_COLUMN'
    | 'TWO_COLUMN'
    | 'THREE_COLUMN'
    | 'FOUR_COLUMN';

/**
 * Helper to get column count from layout type
 */
export function getColumnCount(layoutType: SectionLayoutType): number {
    switch (layoutType) {
        case 'SINGLE_COLUMN': return 1;
        case 'TWO_COLUMN': return 2;
        case 'THREE_COLUMN': return 3;
        case 'FOUR_COLUMN': return 4;
        default: return 2;
    }
}

/**
 * Helper to get layout type from column count
 */
export function getLayoutTypeFromColumns(columns: number): SectionLayoutType {
    switch (columns) {
        case 1: return 'SINGLE_COLUMN';
        case 2: return 'TWO_COLUMN';
        case 3: return 'THREE_COLUMN';
        case 4: return 'FOUR_COLUMN';
        default: return 'TWO_COLUMN';
    }
}

/**
 * Section layout options for dropdowns
 */
export const SECTION_LAYOUT_OPTIONS = [
    { value: 'SINGLE_COLUMN', label: 'Single Column', columns: 1 },
    { value: 'TWO_COLUMN', label: 'Two Columns', columns: 2 },
    { value: 'THREE_COLUMN', label: 'Three Columns', columns: 3 },
    { value: 'FOUR_COLUMN', label: 'Four Columns', columns: 4 }
];
