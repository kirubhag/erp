import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Student } from '../components/student-list/student-list.component';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private baseUrl = '/settings/students';

  constructor(private http: HttpClient) {}

  getStudents(page: number, size: number, sort?: string): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (sort) {
      params = params.set('sort', sort);
    }

    return this.http.get(this.baseUrl, { params });
  }

  searchStudents(query: string, page: number, size: number): Observable<any> {
    const params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get(`${this.baseUrl}/search`, { params });
  }

  getStudentsByGrade(grade: string, page: number, size: number): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get(`${this.baseUrl}/grade/${grade}`, { params });
  }

  getStudentsByStatus(status: string, page: number, size: number): Observable<any> {
    const params = new HttpParams()
      .set('status', status)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get(`${this.baseUrl}`, { params });
  }
}