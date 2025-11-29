import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ErpSection } from '../models/erp-section.model';

/**
 * Service for managing ERP sections
 * Provides methods to interact with section API endpoints
 */
@Injectable({
    providedIn: 'root'
})
export class SectionService {
    private apiUrl = '/api/sections';

    constructor(private http: HttpClient) { }

    /**
     * Get all sections for a specific entity type
     */
    getSectionsByEntityType(entityType: string): Observable<ErpSection[]> {
        return this.http.get<ErpSection[]>(`${this.apiUrl}/${entityType}`);
    }

    /**
     * Get sections for a specific entity type and organization
     */
    getSectionsByEntityTypeAndOrganization(
        entityType: string,
        organizationId: number
    ): Observable<ErpSection[]> {
        return this.http.get<ErpSection[]>(
            `${this.apiUrl}/${entityType}/organization/${organizationId}`
        );
    }

    /**
     * Get sections visible in create view
     */
    getSectionsForCreate(entityType: string): Observable<ErpSection[]> {
        return this.http.get<ErpSection[]>(`${this.apiUrl}/${entityType}/create`);
    }

    /**
     * Get sections visible in edit view
     */
    getSectionsForEdit(entityType: string): Observable<ErpSection[]> {
        return this.http.get<ErpSection[]>(`${this.apiUrl}/${entityType}/edit`);
    }

    /**
     * Get sections visible in detail view
     */
    getSectionsForDetail(entityType: string): Observable<ErpSection[]> {
        return this.http.get<ErpSection[]>(`${this.apiUrl}/${entityType}/detail`);
    }

    /**
     * Get a specific section by ID
     */
    getSectionById(id: number): Observable<ErpSection> {
        return this.http.get<ErpSection>(`${this.apiUrl}/id/${id}`);
    }

    /**
     * Create a new section
     */
    createSection(section: ErpSection): Observable<ErpSection> {
        return this.http.post<ErpSection>(this.apiUrl, section);
    }

    /**
     * Update an existing section
     */
    updateSection(id: number, section: ErpSection): Observable<ErpSection> {
        return this.http.put<ErpSection>(`${this.apiUrl}/${id}`, section);
    }

    /**
     * Delete a section (soft delete)
     */
    deleteSection(id: number): Observable<{ message: string }> {
        return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
    }

    /**
     * Reorder sections for an entity type
     */
    reorderSections(entityType: string, sectionIds: number[]): Observable<{ message: string }> {
        return this.http.post<{ message: string }>(
            `${this.apiUrl}/${entityType}/reorder`,
            sectionIds
        );
    }

    /**
     * Check if a section name exists
     */
    checkSectionExists(
        entityType: string,
        sectionName: string,
        organizationId: number
    ): Observable<{ exists: boolean }> {
        const params = new HttpParams()
            .set('sectionName', sectionName)
            .set('organizationId', organizationId.toString());

        return this.http.get<{ exists: boolean }>(
            `${this.apiUrl}/${entityType}/exists`,
            { params }
        );
    }

    /**
     * Save complete module layout including sections and field positions
     */
    saveModuleLayout(entityType: string, layoutData: any): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/${entityType}/layout`, layoutData);
    }
}
