import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError, shareReplay, tap } from 'rxjs/operators';
import { ErpLayout, ErpLayoutSection, ErpLayoutField, filterSectionsByMode, filterFieldsByShowType } from '../models/erp-layout.model';

/**
 * Service for managing ERP layouts and the hierarchical structure:
 * Layout -> Sections -> Fields
 * 
 * This service provides the new layout-based API for field management
 */
@Injectable({
    providedIn: 'root'
})
export class ErpLayoutService {
    private apiUrl = '/api/layouts';
    
    // Cache for layouts by entity type
    private layoutCache = new Map<string, Observable<ErpLayout | null>>();

    constructor(private http: HttpClient) { }

    /**
     * Get all layouts for an entity type
     */
    getLayoutsByEntityType(entityType: string): Observable<ErpLayout[]> {
        return this.http.get<ErpLayout[]>(`${this.apiUrl}/entity/${entityType}`);
    }

    /**
     * Get the default layout for an entity type
     */
    getDefaultLayout(entityType: string): Observable<ErpLayout | null> {
        return this.http.get<ErpLayout>(`${this.apiUrl}/entity/${entityType}/default`).pipe(
            catchError(() => of(null))
        );
    }

    /**
     * Get layout by ID
     */
    getLayoutById(layoutId: number): Observable<ErpLayout | null> {
        return this.http.get<ErpLayout>(`${this.apiUrl}/${layoutId}`).pipe(
            catchError(() => of(null))
        );
    }

    /**
     * Get the complete layout with sections and fields
     * This is the main API for the hierarchical structure
     */
    getCompleteLayout(entityType: string, layoutName: string = 'System'): Observable<ErpLayout | null> {
        const cacheKey = `${entityType}:${layoutName}`;
        
        if (!this.layoutCache.has(cacheKey)) {
            const params = new HttpParams().set('layoutName', layoutName);
            const request = this.http.get<ErpLayout>(`${this.apiUrl}/entity/${entityType}/complete`, { params }).pipe(
                shareReplay(1),
                catchError(() => of(null))
            );
            this.layoutCache.set(cacheKey, request);
        }
        
        return this.layoutCache.get(cacheKey)!;
    }

    /**
     * Get the default complete layout for an entity type
     */
    getDefaultCompleteLayout(entityType: string): Observable<ErpLayout | null> {
        return this.getCompleteLayout(entityType, 'System');
    }

    /**
     * Get complete layout by ID
     */
    getCompleteLayoutById(layoutId: number): Observable<ErpLayout | null> {
        return this.http.get<ErpLayout>(`${this.apiUrl}/${layoutId}/complete`).pipe(
            catchError(() => of(null))
        );
    }

    /**
     * Get fields grouped by section (backward compatibility)
     */
    getFieldsGroupedBySection(entityType: string): Observable<{ [sectionLabel: string]: ErpLayoutField[] }> {
        return this.http.get<{ [key: string]: ErpLayoutField[] }>(`${this.apiUrl}/entity/${entityType}/fields-by-section`);
    }

    /**
     * Get sections for create view
     */
    getSectionsForCreate(entityType: string): Observable<ErpLayoutSection[]> {
        return this.getDefaultCompleteLayout(entityType).pipe(
            map(layout => {
                if (!layout?.sections) return [];
                return filterSectionsByMode(layout.sections, 'create');
            })
        );
    }

    /**
     * Get sections for edit view
     */
    getSectionsForEdit(entityType: string): Observable<ErpLayoutSection[]> {
        return this.getDefaultCompleteLayout(entityType).pipe(
            map(layout => {
                if (!layout?.sections) return [];
                return filterSectionsByMode(layout.sections, 'edit');
            })
        );
    }

    /**
     * Get sections for detail view
     */
    getSectionsForDetail(entityType: string): Observable<ErpLayoutSection[]> {
        return this.getDefaultCompleteLayout(entityType).pipe(
            map(layout => {
                if (!layout?.sections) return [];
                return filterSectionsByMode(layout.sections, 'detail');
            })
        );
    }

    /**
     * Get all fields for create view (flat list)
     */
    getFieldsForCreate(entityType: string): Observable<ErpLayoutField[]> {
        return this.getSectionsForCreate(entityType).pipe(
            map(sections => {
                const allFields: ErpLayoutField[] = [];
                for (const section of sections) {
                    if (section.fields) {
                        const filtered = filterFieldsByShowType(section.fields, 'create');
                        allFields.push(...filtered);
                    }
                }
                return allFields;
            })
        );
    }

    /**
     * Get all fields for edit view (flat list)
     */
    getFieldsForEdit(entityType: string): Observable<ErpLayoutField[]> {
        return this.getSectionsForEdit(entityType).pipe(
            map(sections => {
                const allFields: ErpLayoutField[] = [];
                for (const section of sections) {
                    if (section.fields) {
                        const filtered = filterFieldsByShowType(section.fields, 'edit');
                        allFields.push(...filtered);
                    }
                }
                return allFields;
            })
        );
    }

    /**
     * Get all fields for view mode (flat list)
     */
    getFieldsForView(entityType: string): Observable<ErpLayoutField[]> {
        return this.getSectionsForDetail(entityType).pipe(
            map(sections => {
                const allFields: ErpLayoutField[] = [];
                for (const section of sections) {
                    if (section.fields) {
                        const filtered = filterFieldsByShowType(section.fields, 'view');
                        allFields.push(...filtered);
                    }
                }
                return allFields;
            })
        );
    }

    /**
     * Create a new layout
     */
    createLayout(layout: ErpLayout): Observable<ErpLayout> {
        this.clearCache();
        return this.http.post<ErpLayout>(this.apiUrl, layout);
    }

    /**
     * Update an existing layout
     */
    updateLayout(layoutId: number, layout: ErpLayout): Observable<ErpLayout> {
        this.clearCache();
        return this.http.put<ErpLayout>(`${this.apiUrl}/${layoutId}`, layout);
    }

    /**
     * Delete a layout
     */
    deleteLayout(layoutId: number): Observable<void> {
        this.clearCache();
        return this.http.delete<void>(`${this.apiUrl}/${layoutId}`);
    }

    /**
     * Add a section to a layout
     */
    addSectionToLayout(layoutId: number, sectionId: number, sectionOrder?: number): Observable<void> {
        this.clearCache();
        const params = sectionOrder !== undefined 
            ? new HttpParams().set('sectionOrder', sectionOrder.toString())
            : undefined;
        return this.http.post<void>(`${this.apiUrl}/${layoutId}/sections/${sectionId}`, {}, { params });
    }

    /**
     * Remove a section from a layout
     */
    removeSectionFromLayout(layoutId: number, sectionId: number): Observable<void> {
        this.clearCache();
        return this.http.delete<void>(`${this.apiUrl}/${layoutId}/sections/${sectionId}`);
    }

    /**
     * Update section order in a layout
     */
    updateSectionOrder(layoutId: number, sectionIds: number[]): Observable<void> {
        this.clearCache();
        return this.http.put<void>(`${this.apiUrl}/${layoutId}/sections/reorder`, sectionIds);
    }

    /**
     * Clear the layout cache
     */
    clearCache(): void {
        this.layoutCache.clear();
    }

    /**
     * Clear cache for a specific entity type
     */
    clearCacheForEntity(entityType: string): void {
        for (const key of this.layoutCache.keys()) {
            if (key.startsWith(`${entityType}:`)) {
                this.layoutCache.delete(key);
            }
        }
    }
}
