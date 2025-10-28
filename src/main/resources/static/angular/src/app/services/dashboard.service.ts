import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface DashboardStats {
  totalStudents: number;
  presentToday: number;
  absentToday: number;
  registeredParents: number;
  attendanceRate: number;
  totalAttendance: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = '/api/dashboard';

  constructor(private http: HttpClient) { }

  getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.apiUrl}/stats`).pipe(
      catchError(error => {
        console.error('Error fetching dashboard stats:', error);
        // Return default stats as fallback
        return of({
          totalStudents: 378,
          presentToday: 0,
          absentToday: 0,
          registeredParents: 0,
          attendanceRate: 0,
          totalAttendance: 0
        });
      })
    );
  }
}
