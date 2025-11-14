import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, timeout } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface MenuItem {
  id: number;
  systemName: string;
  pluralName: string;
  singularName: string;
  icon: string;
  route: string;
  sequence: number;
  isActive: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private apiUrl = '/api/module';

  constructor(private http: HttpClient) { }

  getMenuItems(): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.apiUrl}/list`).pipe(
      timeout(5000), // 5 second timeout
      catchError(error => {
        console.error('Error fetching menu items:', error);
        // Return default menu items as fallback
        return of(this.getDefaultMenuItems());
      })
    );
  }

  private getDefaultMenuItems(): MenuItem[] {
    return [
      { id: 1, systemName: 'dashboard', pluralName: 'Dashboard', singularName: 'Dashboard', icon: 'fas fa-home', route: '/dashboard', sequence: 1, isActive: true },
      { id: 2, systemName: 'students', pluralName: 'Students', singularName: 'Student', icon: 'fas fa-user-graduate', route: '/students', sequence: 2, isActive: true },
      { id: 3, systemName: 'staff', pluralName: 'Staff', singularName: 'Staff', icon: 'fas fa-chalkboard-teacher', route: '/staff', sequence: 3, isActive: true },
      { id: 4, systemName: 'attendance', pluralName: 'Attendance', singularName: 'Attendance', icon: 'fas fa-calendar-check', route: '/attendance', sequence: 4, isActive: true },
      { id: 5, systemName: 'parents', pluralName: 'Parents', singularName: 'Parent', icon: 'fas fa-users', route: '/parents', sequence: 5, isActive: true },
      { id: 6, systemName: 'subjects', pluralName: 'Subjects', singularName: 'Subject', icon: 'fas fa-book', route: '/subjects', sequence: 6, isActive: true }
    ];
  }
}
