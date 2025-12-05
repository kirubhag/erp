/**
 * UI Field Type model matching backend UIFieldType enum
 * Represents the different types of fields that can be rendered in the UI
 */
export interface UIFieldType {
    typeId: number;
    displayName: string;
    dataType: string;
    category: string;
    htmlInputType: string;
    maxLength?: number;
    validationPattern?: string;
    hasOptions: boolean;
    multiSelect: boolean;
    relationship: boolean;
    fileUpload: boolean;
    numeric: boolean;
    dateTime: boolean;
    defaultDecimalPlaces?: number;
}

/**
 * Enum for UI Field Type IDs (100-119 range)
 * Matches the backend UIFieldType enum
 */
export enum UIFieldTypeId {
    SINGLE_LINE_TEXT = 100,
    MULTI_LINE_TEXT = 101,
    EMAIL = 102,
    PHONE = 103,
    PICKLIST = 104,
    MULTI_SELECT = 105,
    DATE = 106,
    DATETIME = 107,
    NUMBER = 108,
    AUTO_NUMBER = 109,
    CURRENCY = 110,
    DECIMAL = 111,
    PERCENT = 112,
    LONG_INTEGER = 113,
    CHECKBOX = 114,
    LOOKUP = 115,
    RADIO = 116,
    FILE_UPLOAD = 117,
    IMAGE_UPLOAD = 118,
    URL = 119,
    RICH_TEXT = 120
}

/**
 * Field validation result
 */
export interface FieldValidationResult {
    isValid: boolean;
    errors: string[];
}

/**
 * UI Field Type metadata response
 */
export interface UIFieldTypeMetadata {
    types: UIFieldType[];
    categories: string[];
    totalCount: number;
}

/**
 * Helper class for UI Field Type operations
 */
export class UIFieldTypeHelper {

    /**
     * Get display name for a UI type ID
     */
    static getDisplayName(typeId: number, types: UIFieldType[]): string {
        const type = types.find(t => t.typeId === typeId);
        return type ? type.displayName : 'Unknown';
    }

    /**
     * Check if a UI type supports options (picklist, radio, etc.)
     */
    static hasOptions(typeId: number): boolean {
        return typeId === UIFieldTypeId.PICKLIST ||
            typeId === UIFieldTypeId.MULTI_SELECT ||
            typeId === UIFieldTypeId.RADIO;
    }

    /**
     * Check if a UI type is numeric
     */
    static isNumeric(typeId: number): boolean {
        return typeId === UIFieldTypeId.NUMBER ||
            typeId === UIFieldTypeId.AUTO_NUMBER ||
            typeId === UIFieldTypeId.CURRENCY ||
            typeId === UIFieldTypeId.DECIMAL ||
            typeId === UIFieldTypeId.PERCENT ||
            typeId === UIFieldTypeId.LONG_INTEGER;
    }

    /**
     * Check if a UI type is date/time related
     */
    static isDateTime(typeId: number): boolean {
        return typeId === UIFieldTypeId.DATE ||
            typeId === UIFieldTypeId.DATETIME;
    }

    /**
     * Check if a UI type is a file upload
     */
    static isFileUpload(typeId: number): boolean {
        return typeId === UIFieldTypeId.FILE_UPLOAD ||
            typeId === UIFieldTypeId.IMAGE_UPLOAD;
    }

    /**
     * Get HTML input type for a UI type ID
     */
    static getHtmlInputType(typeId: number): string {
        const typeMap: { [key: number]: string } = {
            [UIFieldTypeId.SINGLE_LINE_TEXT]: 'text',
            [UIFieldTypeId.MULTI_LINE_TEXT]: 'textarea',
            [UIFieldTypeId.EMAIL]: 'email',
            [UIFieldTypeId.PHONE]: 'tel',
            [UIFieldTypeId.PICKLIST]: 'select',
            [UIFieldTypeId.MULTI_SELECT]: 'multi-select',
            [UIFieldTypeId.DATE]: 'date',
            [UIFieldTypeId.DATETIME]: 'datetime-local',
            [UIFieldTypeId.NUMBER]: 'number',
            [UIFieldTypeId.CURRENCY]: 'number',
            [UIFieldTypeId.DECIMAL]: 'number',
            [UIFieldTypeId.PERCENT]: 'number',
            [UIFieldTypeId.LONG_INTEGER]: 'number',
            [UIFieldTypeId.CHECKBOX]: 'checkbox',
            [UIFieldTypeId.RADIO]: 'radio',
            [UIFieldTypeId.FILE_UPLOAD]: 'file',
            [UIFieldTypeId.IMAGE_UPLOAD]: 'file',
            [UIFieldTypeId.URL]: 'url'
        };
        return typeMap[typeId] || 'text';
    }

    /**
     * Format a value for display based on UI type
     */
    static formatValueForDisplay(value: any, typeId: number): string {
        if (value === null || value === undefined) {
            return '—';
        }

        switch (typeId) {
            case UIFieldTypeId.CHECKBOX:
                return value ? 'Yes' : 'No';

            case UIFieldTypeId.DATE:
                return new Date(value).toLocaleDateString();

            case UIFieldTypeId.DATETIME:
                return new Date(value).toLocaleString();

            case UIFieldTypeId.CURRENCY:
                return new Intl.NumberFormat('en-US', {
                    style: 'currency',
                    currency: 'USD'
                }).format(value);

            case UIFieldTypeId.PERCENT:
                return `${value}%`;

            case UIFieldTypeId.DECIMAL:
                return Number(value).toFixed(2);

            case UIFieldTypeId.EMAIL:
                return value; // Could add mailto: link in component

            case UIFieldTypeId.URL:
                return value; // Could add clickable link in component

            case UIFieldTypeId.MULTI_SELECT:
                return Array.isArray(value) ? value.join(', ') : value;

            default:
                return String(value);
        }
    }
}
