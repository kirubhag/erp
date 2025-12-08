import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
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

  constructor(private http: HttpClient) { }

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
    return this.http.get<any>(`${this.baseUrl}/batches`, { params }).pipe(
      // Extract content from page object if present
      // The backend returns a Page<StudentPromotionResponse> object
      // The component likely expects an array directly or we need to handle it there.
      // Looking at the component not shown, but assuming valid pattern:
      // If the component expects the full Page object, we leave it. 
      // However, the issue description says "Failed to load batches", often due to type mismatch.
      // Let's assume the component wants the full page for pagination controls, 
      // but maybe the backend error handling in component is strict.
      // Actually, looking at the previous analysis, I suspected a mismatch.
      // But let's look at the component code if I can... 
      // Wait, I didn't see the component code for student promotion, I only looked at settings.
      // Let's assume the safe fix is to pass through, but if the component expects an array, it's broken.
      // Re-reading implementation plan: "Update getAllBatches to return Observable<StudentPromotionResponse[]> and map response.content".
      // Let's do that map.
      map(response => {
        if (response && response.content) {
          return response.content;
        }
        return response;
      })
    );
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
  getStatistics(academicYear?: string): Observable<GradeStatistics> {
    let params = new HttpParams();
    if (academicYear) {
      params = params.set('academicYear', academicYear);
    }
    return this.http.get<GradeStatistics>(`${this.baseUrl}/statistics`, { params });
  }

  /**
   * Get available grade levels
   */
  getGradeLevels(): Observable<{ [key: string]: string }> {
    return this.http.get<{ [key: string]: string }>(`${this.baseUrl}/grade-levels`);
  }
}
