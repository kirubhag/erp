import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface OrganizationRegistrationRequest {
  name: string;
  type: string;
  code: string;
  description?: string;
  email: string;
  phone: string;
  fax?: string;
  website?: string;
  streetAddress?: string;
  city: string;
  state?: string;
  postalCode?: string;
  country: string;
  registrationNumber?: string;
  taxId?: string;
  establishedYear?: number;
  accreditation?: string;
  loadSampleData: boolean;
}

export interface Organization {
  id: number;
  name: string;
  type: string;
  code: string;
  description?: string;
  email: string;
  phone: string;
  fax?: string;
  website?: string;
  streetAddress?: string;
  city: string;
  state?: string;
  postalCode?: string;
  country: string;
  registrationNumber?: string;
  taxId?: string;
  establishedYear?: number;
  accreditation?: string;
  isActive: boolean;
  createdTime: string;
}

export interface ImportHistory {
  id: number;
  entityName: string;
  importType: string;
  recordCount: number;
  source: string;
  importedBy: string;
  importStartTime: string;
  importEndTime?: string;
  importStatus: string;
  errorMessage?: string;
  createdAt: string;
}

export interface SampleDataPopulationResponse {
  totalEntities: number;
  successfulImports: number;
  skippedImports: number;
  failedImports: number;
  importHistoryIds: number[];
  summary: string;
}

@Injectable({
  providedIn: 'root'
})
export class OrganizationService {
  private apiUrl = 'http://localhost:8081';
  private currentOrganization = new BehaviorSubject<Organization | null>(null);
  public currentOrganization$ = this.currentOrganization.asObservable();

  constructor(private http: HttpClient) {
    this.loadCurrentOrganization();
  }

  /**
   * Register a new organization
   */
  registerOrganization(request: OrganizationRegistrationRequest): Observable<Organization> {
    return this.http.post<Organization>(`${this.apiUrl}/api/organizations/register`, request).pipe(
      tap(organization => {
        this.currentOrganization.next(organization);
        localStorage.setItem('currentOrganization', JSON.stringify(organization));
      })
    );
  }

  /**
   * Get current organization
   */
  getCurrentOrganization(): Organization | null {
    return this.currentOrganization.value;
  }

  /**
   * Get organization by ID
   */
  getOrganizationById(id: number): Observable<Organization> {
    return this.http.get<Organization>(`${this.apiUrl}/api/organizations/${id}`);
  }

  /**
   * Update organization
   */
  updateOrganization(id: number, organization: Organization): Observable<Organization> {
    return this.http.put<Organization>(`${this.apiUrl}/api/organizations/${id}`, organization).pipe(
      tap(updatedOrg => {
        this.currentOrganization.next(updatedOrg);
        localStorage.setItem('currentOrganization', JSON.stringify(updatedOrg));
      })
    );
  }

  /**
   * Set current organization
   */
  setCurrentOrganization(organization: Organization): void {
    this.currentOrganization.next(organization);
    localStorage.setItem('currentOrganization', JSON.stringify(organization));
  }

  /**
   * Load current organization from localStorage
   */
  private loadCurrentOrganization(): void {
    const stored = localStorage.getItem('currentOrganization');
    if (stored) {
      try {
        const organization = JSON.parse(stored);
        this.currentOrganization.next(organization);
      } catch (e) {
        console.error('Failed to parse stored organization', e);
      }
    }
  }

  /**
   * Check if entity has been imported
   */
  checkEntityImported(entityName: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/api/v1/import-history/check/${entityName}`);
  }

  /**
   * Get all imported entities
   */
  getImportedEntities(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/api/v1/import-history/entities`);
  }

  /**
   * Get sample data summary
   */
  getSampleDataSummary(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/api/v1/import-history/summary/sample-data`);
  }

  /**
   * Populate sample data (will be implemented on backend)
   */
  populateSampleData(entityNames: string[]): Observable<SampleDataPopulationResponse> {
    return this.http.post<SampleDataPopulationResponse>(
      `${this.apiUrl}/api/v1/sample-data/populate`,
      { entityNames }
    );
  }

  /**
   * Get import history for an entity
   */
  getEntityImportHistory(entityName: string): Observable<ImportHistory[]> {
    return this.http.get<ImportHistory[]>(
      `${this.apiUrl}/api/v1/import-history/entity/${entityName}`
    );
  }

  /**
   * Get all import history with pagination
   */
  getImportHistory(page: number = 0, size: number = 20): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/api/v1/import-history?page=${page}&size=${size}`
    );
  }

  /**
   * Logout and clear organization
   */
  logout(): void {
    this.currentOrganization.next(null);
    localStorage.removeItem('currentOrganization');
  }
}
