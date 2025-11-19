import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { 
  ImportFile, 
  ImportSettings, 
  ImportSession, 
  ImportResult, 
  ImportPreview,
  FieldMappingTemplate,
  ImportStatistics,
  FieldMapping
} from '../models/import.model';

/**
 * Import Service
 * Manages the complete import workflow including file upload,
 * preview, field mapping, and data import
 */
@Injectable({
  providedIn: 'root'
})
export class ImportService {
  private apiUrl = '/api/import';
  private currentSession = new BehaviorSubject<ImportSession | null>(null);
  public currentSession$ = this.currentSession.asObservable();

  private currentStep = new BehaviorSubject<number>(1);
  public currentStep$ = this.currentStep.asObservable();

  private importProgress = new BehaviorSubject<number>(0);
  public importProgress$ = this.importProgress.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Set current step in the import wizard
   */
  setCurrentStep(step: number): void {
    this.currentStep.next(step);
  }

  /**
   * Upload import file with preview
   */
  uploadFile(file: File, entityType: string): Observable<ImportPreview> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('entityType', entityType);
    
    return this.http.post<ImportPreview>(
      `${this.apiUrl}/upload-preview`,
      formData
    );
  }

  /**
   * Create import session with file and settings
   */
  createImportSession(
    file: File,
    entityType: string,
    settings: ImportSettings
  ): Observable<ImportSession> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('entityType', entityType);
    formData.append('settings', JSON.stringify(settings));

    return this.http.post<ImportSession>(
      `${this.apiUrl}/sessions`,
      formData
    );
  }

  /**
   * Get available field mapping template for entity type
   */
  getFieldMappingTemplate(entityType: string): Observable<FieldMappingTemplate> {
    return this.http.get<FieldMappingTemplate>(
      `${this.apiUrl}/mapping-templates/${entityType}`
    );
  }

  /**
   * Save field mappings for current session
   */
  saveFieldMappings(
    sessionId: string,
    mappings: FieldMapping[]
  ): Observable<ImportSession> {
    return this.http.put<ImportSession>(
      `${this.apiUrl}/sessions/${sessionId}/mappings`,
      { mappings }
    );
  }

  /**
   * Get current import session
   */
  getSession(sessionId: string): Observable<ImportSession> {
    return this.http.get<ImportSession>(
      `${this.apiUrl}/sessions/${sessionId}`
    );
  }

  /**
   * Start importing records
   */
  startImport(sessionId: string): Observable<HttpEvent<any>> {
    const request = new HttpRequest(
      'POST',
      `${this.apiUrl}/sessions/${sessionId}/import`,
      {},
      { reportProgress: true }
    );

    return this.http.request<any>(request);
  }

  /**
   * Get import summary
   */
  getImportSummary(sessionId: string): Observable<ImportSession> {
    return this.http.get<ImportSession>(
      `${this.apiUrl}/sessions/${sessionId}/summary`
    );
  }

  /**
   * Undo last import
   */
  undoImport(sessionId: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/sessions/${sessionId}/undo`,
      {}
    );
  }

  /**
   * Get import history for entity type
   */
  getImportHistory(entityType: string, limit: number = 10): Observable<ImportSession[]> {
    return this.http.get<ImportSession[]>(
      `${this.apiUrl}/history/${entityType}?limit=${limit}`
    );
  }

  /**
   * Validate field mappings
   */
  validateMappings(sessionId: string): Observable<{ valid: boolean; errors?: string[] }> {
    return this.http.post<{ valid: boolean; errors?: string[] }>(
      `${this.apiUrl}/sessions/${sessionId}/validate-mappings`,
      {}
    );
  }

  /**
   * Auto-detect field mappings based on column headers
   */
  autoDetectMappings(sessionId: string): Observable<FieldMapping[]> {
    return this.http.post<FieldMapping[]>(
      `${this.apiUrl}/sessions/${sessionId}/auto-detect-mappings`,
      {}
    );
  }

  /**
   * Cancel import session
   */
  cancelSession(sessionId: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/sessions/${sessionId}/cancel`,
      {}
    );
  }

  /**
   * Set current session
   */
  setCurrentSession(session: ImportSession): void {
    this.currentSession.next(session);
  }

  /**
   * Get current session
   */
  getCurrentSession(): ImportSession | null {
    return this.currentSession.value;
  }

  /**
   * Update import progress
   */
  updateProgress(progress: number): void {
    this.importProgress.next(progress);
  }

  /**
   * Detect file format
   */
  detectFileFormat(fileName: string): 'xlsx' | 'xls' | 'csv' | 'vcf' {
    const ext = fileName.split('.').pop()?.toLowerCase();
    switch (ext) {
      case 'xlsx': return 'xlsx';
      case 'xls': return 'xls';
      case 'csv': return 'csv';
      case 'vcf': return 'vcf';
      default: return 'csv';
    }
  }

  /**
   * Format file size for display
   */
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  }

  /**
   * Check if file format is supported
   */
  isSupportedFormat(format: string): boolean {
    return ['xlsx', 'xls', 'csv', 'vcf'].includes(format.toLowerCase());
  }

  /**
   * Validate file size
   */
  isValidFileSize(sizeInBytes: number): boolean {
    const maxSize = 50 * 1024 * 1024; // 50 MB
    return sizeInBytes <= maxSize;
  }
}
