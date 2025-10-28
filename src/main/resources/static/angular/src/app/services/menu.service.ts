import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface MenuItem {
  id: number;
  name: string;
  pluralName: string;
  singularName: string;
  icon: string;
  route: string;
  sequence: number;
  visible: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private apiUrl = 'http://localhost:8081/api/module';

  constructor(private http: HttpClient) { }

  getMenuItems(): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.apiUrl}/list`).pipe(
      catchError(error => {
        console.error('Error fetching menu items:', error);
        // Return default menu items as fallback
        return of(this.getDefaultMenuItems());
      })
    );
  }

  private getDefaultMenuItems(): MenuItem[] {
    return [
      { id: 1, name: 'dashboard', pluralName: 'Dashboard', singularName: 'Dashboard', icon: 'fas fa-home', route: '/dashboard', sequence: 1, visible: true },
      { id: 2, name: 'students', pluralName: 'Students', singularName: 'Student', icon: 'fas fa-user-graduate', route: '/students', sequence: 2, visible: true },
      { id: 3, name: 'staff', pluralName: 'Staff', singularName: 'Staff', icon: 'fas fa-chalkboard-teacher', route: '/staff', sequence: 3, visible: true },
      { id: 4, name: 'attendance', pluralName: 'Attendance', singularName: 'Attendance', icon: 'fas fa-calendar-check', route: '/attendance', sequence: 4, visible: true },
      { id: 5, name: 'parents', pluralName: 'Parents', singularName: 'Parent', icon: 'fas fa-users', route: '/parents', sequence: 5, visible: true },
      { id: 6, name: 'subjects', pluralName: 'Subjects', singularName: 'Subject', icon: 'fas fa-book', route: '/subjects', sequence: 6, visible: true }
    ];
  }
}
