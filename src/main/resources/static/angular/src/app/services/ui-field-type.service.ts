import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, shareReplay, tap } from 'rxjs/operators';
import { UIFieldType, UIFieldTypeMetadata, FieldValidationResult, UIFieldTypeId } from '../models/ui-field-type.model';
import { ErpField } from '../models/erp-field.model';

/**
 * Service for managing UI Field Type metadata and operations
 */
@Injectable({
    providedIn: 'root'
})
export class UIFieldTypeService {
    private apiUrl = '/api/fields';
    private uiTypesCache$?: Observable<UIFieldType[]>;
    private metadataCache$?: Observable<UIFieldTypeMetadata>;

    constructor(private http: HttpClient) { }

    /**
     * Get all UI field types (cached)
     */
    getUIFieldTypes(): Observable<UIFieldType[]> {
        if (!this.uiTypesCache$) {
            this.uiTypesCache$ = this.http.get<UIFieldType[]>(`${this.apiUrl}/ui-types`)
                .pipe(shareReplay(1));
        }
        return this.uiTypesCache$;
    }

    /**
     * Get UI field type by ID
     */
    getUIFieldType(typeId: number): Observable<UIFieldType> {
        return this.http.get<UIFieldType>(`${this.apiUrl}/ui-types/${typeId}`);
    }

    /**
     * Get complete UI field type metadata (cached)
     */
    getUIFieldTypeMetadata(): Observable<UIFieldTypeMetadata> {
        if (!this.metadataCache$) {
            this.metadataCache$ = this.http.get<UIFieldTypeMetadata>(`${this.apiUrl}/ui-types/metadata`)
                .pipe(shareReplay(1));
        }
        return this.metadataCache$;
    }

    /**
     * Validate a field value against its UI type configuration
     */
    validateFieldValue(entityType: string, fieldName: string, value: any): Observable<FieldValidationResult> {
        const request = {
            entityType,
            fieldName,
            value
        };
        return this.http.post<FieldValidationResult>(`${this.apiUrl}/validate-value`, request);
    }

    /**
     * Client-side validation for a field value
     * This provides immediate feedback without server roundtrip
     */
    validateFieldValueLocal(field: ErpField, value: any): FieldValidationResult {
        const errors: string[] = [];

        // Check required field
        if (field.isRequired && (value === null || value === undefined || value === '')) {
            errors.push(`${field.fieldLabel} is required`);
            return { isValid: false, errors };
        }

        // If value is empty and not required, it's valid
        if (value === null || value === undefined || value === '') {
            return { isValid: true, errors: [] };
        }

        // Validate based on UI type
        if (field.uiType) {
            switch (field.uiType) {
                case UIFieldTypeId.EMAIL:
                    if (!this.isValidEmail(value)) {
                        errors.push(`${field.fieldLabel} must be a valid email address`);
                    }
                    break;

                case UIFieldTypeId.PHONE:
                    if (!this.isValidPhone(value)) {
                        errors.push(`${field.fieldLabel} must be a valid phone number`);
                    }
                    break;

                case UIFieldTypeId.URL:
                    if (!this.isValidUrl(value)) {
                        errors.push(`${field.fieldLabel} must be a valid URL`);
                    }
                    break;

                case UIFieldTypeId.NUMBER:
                case UIFieldTypeId.LONG_INTEGER:
                    if (!this.isValidInteger(value)) {
                        errors.push(`${field.fieldLabel} must be a valid integer`);
                    }
                    break;

                case UIFieldTypeId.DECIMAL:
                case UIFieldTypeId.CURRENCY:
                    if (!this.isValidDecimal(value)) {
                        errors.push(`${field.fieldLabel} must be a valid decimal number`);
                    }
                    break;

                case UIFieldTypeId.PERCENT:
                    const percent = parseFloat(value);
                    if (isNaN(percent) || percent < 0 || percent > 100) {
                        errors.push(`${field.fieldLabel} must be between 0 and 100`);
                    }
                    break;

                case UIFieldTypeId.DATE:
                    if (!this.isValidDate(value)) {
                        errors.push(`${field.fieldLabel} must be a valid date`);
                    }
                    break;

                case UIFieldTypeId.DATETIME:
                    if (!this.isValidDateTime(value)) {
                        errors.push(`${field.fieldLabel} must be a valid date and time`);
                    }
                    break;
            }
        }

        // Check max length
        if (field.maxLength && String(value).length > field.maxLength) {
            errors.push(`${field.fieldLabel} must not exceed ${field.maxLength} characters`);
        }

        // Check validation pattern if exists
        if (field.validationPattern) {
            const pattern = new RegExp(field.validationPattern);
            if (!pattern.test(String(value))) {
                errors.push(`${field.fieldLabel} format is invalid`);
            }
        }

        return {
            isValid: errors.length === 0,
            errors
        };
    }

    /**
     * Format a value for display based on UI type
     */
    formatValueForDisplay(value: any, uiType?: number): string {
        if (value === null || value === undefined) {
            return '—';
        }

        if (!uiType) {
            return String(value);
        }

        switch (uiType) {
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

            case UIFieldTypeId.MULTI_SELECT:
                return Array.isArray(value) ? value.join(', ') : value;

            default:
                return String(value);
        }
    }

    // Private validation helper methods
    private isValidEmail(value: string): boolean {
        const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        return emailPattern.test(value);
    }

    private isValidPhone(value: string): boolean {
        const phonePattern = /^[+]?[0-9\-\s\(\)]{7,20}$/;
        return phonePattern.test(value);
    }

    private isValidUrl(value: string): boolean {
        try {
            new URL(value);
            return true;
        } catch {
            return false;
        }
    }

    private isValidInteger(value: any): boolean {
        return Number.isInteger(Number(value)) && !isNaN(Number(value));
    }

    private isValidDecimal(value: any): boolean {
        return !isNaN(parseFloat(value)) && isFinite(value);
    }

    private isValidDate(value: any): boolean {
        const date = new Date(value);
        return date instanceof Date && !isNaN(date.getTime());
    }

    private isValidDateTime(value: any): boolean {
        return this.isValidDate(value);
    }

    /**
     * Clear the cache (useful when UI types are updated)
     */
    clearCache(): void {
        this.uiTypesCache$ = undefined;
        this.metadataCache$ = undefined;
    }
}
