import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ErpField, FieldsGroupedBySection, FieldsGroupedByCategory } from '../models/erp-field.model';

/**
 * Service for managing ERP field metadata
 * Provides methods to fetch field configurations from the backend
 */
@Injectable({
    providedIn: 'root'
})
export class FieldService {
    private apiUrl = '/api/fields';

    constructor(private http: HttpClient) { }

    /**
     * Get all fields for a specific entity type
     */
    getFieldsByEntityType(entityType: string): Observable<ErpField[]> {
        return this.http.get<ErpField[]>(`${this.apiUrl}/${entityType}`);
    }

    /**
     * Get fields grouped by section for a specific entity type
     * Fields are organized by their section labels
     */
    getFieldsGroupedBySection(entityType: string): Observable<FieldsGroupedBySection> {
        return this.http.get<FieldsGroupedBySection>(`${this.apiUrl}/${entityType}/grouped`);
    }

    /**
     * @deprecated Use getFieldsGroupedBySection instead
     * Get fields grouped by category for a specific entity type
     */
    getFieldsGroupedByCategory(entityType: string): Observable<FieldsGroupedByCategory> {
        return this.getFieldsGroupedBySection(entityType);
    }

    /**
     * Get searchable fields for a specific entity type
     */
    getSearchableFields(entityType: string): Observable<ErpField[]> {
        return this.http.get<ErpField[]>(`${this.apiUrl}/${entityType}/searchable`);
    }

    /**
     * Get sortable fields for a specific entity type
     */
    getSortableFields(entityType: string): Observable<ErpField[]> {
        return this.http.get<ErpField[]>(`${this.apiUrl}/${entityType}/sortable`);
    }

    /**
     * @deprecated Field categories are deprecated in favor of sections
     * Use ErpSectionService to get sections instead
     * This endpoint returns empty array for backward compatibility
     */
    getFieldCategories(entityType: string): Observable<string[]> {
        return this.http.get<string[]>(`${this.apiUrl}/${entityType}/categories`);
    }

    /**
     * Get a specific field by entity type and field name
     */
    getFieldByName(entityType: string, fieldName: string): Observable<ErpField> {
        return this.http.get<ErpField>(`${this.apiUrl}/${entityType}/field/${fieldName}`);
    }

    /**
     * Create or update a field
     */
    saveField(field: ErpField): Observable<ErpField> {
        return this.http.post<ErpField>(this.apiUrl, field);
    }

    /**
     * Update field configuration
     */
    updateField(fieldId: number, field: ErpField): Observable<ErpField> {
        return this.http.put<ErpField>(`${this.apiUrl}/${fieldId}`, field);
    }

    /**
     * Delete a field (soft delete)
     */
    deleteField(fieldId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${fieldId}`);
    }

    /**
     * Get field count for an entity type
     */
    getFieldCount(entityType: string): Observable<number> {
        return this.http.get<number>(`${this.apiUrl}/${entityType}/count`);
    }

    /**
     * Check if a field exists
     */
    fieldExists(entityType: string, fieldName: string): Observable<boolean> {
        return this.http.get<boolean>(`${this.apiUrl}/${entityType}/exists/${fieldName}`);
    }
}
