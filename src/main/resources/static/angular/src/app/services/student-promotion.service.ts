import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  StudentPromotionRequest,
  StudentPromotionResponse,
  PromotionRecordSummary,
  StudentEligible,
  GradeStatistics
} from '../models/student-promotion.model';

@Injectable({
  providedIn: 'root'
})
export class StudentPromotionService {
  private baseUrl = '/api/promotions';

  constructor(private http: HttpClient) {}

  /**
   * Execute promotion batch (async - returns immediately with batch ID)
   */
  executePromotion(request: StudentPromotionRequest): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/execute`, request);
  }

  /**
   * Get batch progress (for polling during processing)
   */
  getBatchProgress(batchId: number): Observable<StudentPromotionResponse> {
    return this.http.get<StudentPromotionResponse>(`${this.baseUrl}/batch/${batchId}/progress`);
  }

  /**
   * Get batch details by ID
   */
  getBatchDetails(batchId: number): Observable<StudentPromotionResponse> {
    return this.http.get<StudentPromotionResponse>(`${this.baseUrl}/batch/${batchId}`);
  }

  /**
   * Get all batches with pagination
   */
  getAllBatches(page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.baseUrl}/batches`, { params });
  }

  /**
   * Get batches by status
   */
  getBatchesByStatus(status: string): Observable<StudentPromotionResponse[]> {
    return this.http.get<StudentPromotionResponse[]>(`${this.baseUrl}/batches/status/${status}`);
  }

  /**
   * Rollback promotion batch
   */
  rollbackBatch(batchId: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/batch/${batchId}/rollback`, {});
  }

  /**
   * Get student promotion history
   */
  getStudentHistory(studentId: number): Observable<PromotionRecordSummary[]> {
    return this.http.get<PromotionRecordSummary[]>(`${this.baseUrl}/student/${studentId}/history`);
  }

  /**
   * Get eligible students for promotion with filters
   */
  getEligibleStudents(gradeLevel?: string, section?: string, search?: string, page: number = 0, size: number = 50): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (gradeLevel) {
      params = params.set('gradeLevel', gradeLevel);
    }
    if (section) {
      params = params.set('section', section);
    }
    if (search) {
      params = params.set('search', search);
    }
    
    return this.http.get<any>(`${this.baseUrl}/eligible`, { params });
  }

  /**
   * Get promotion statistics
   */
  getStatistics(): Observable<GradeStatistics> {
    return this.http.get<GradeStatistics>(`${this.baseUrl}/statistics`);
  }

  /**
   * Get available grade levels
   */
  getGradeLevels(): Observable<{[key: string]: string}> {
    return this.http.get<{[key: string]: string}>(`${this.baseUrl}/grade-levels`);
  }
}
