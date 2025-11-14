import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface UserDetails {
  id?: number;
  username: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  phone?: string;
  userType?: string;
  enabled?: boolean;
  accountNonExpired?: boolean;
  credentialsNonExpired?: boolean;
  accountNonLocked?: boolean;
  lastLoginDate?: string;
  passwordChangeDate?: string;
  roles?: any[];
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8081/settings';
  private currentUserSubject = new BehaviorSubject<UserDetails | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private isLoggedOut = false;

  constructor(private http: HttpClient) {
    this.loadCurrentUser();
  }

  /**
   * Load current user from localStorage or fetch from backend
   */
  private loadCurrentUser(): void {
    // Don't load user if we're explicitly logged out
    if (this.isLoggedOut) {
      return;
    }
    
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      try {
        const user = JSON.parse(storedUser);
        this.currentUserSubject.next(user);
      } catch (e) {
        console.error('Failed to parse stored user', e);
      }
    }
  }

  /**
   * Get current user synchronously
   */
  getCurrentUser(): UserDetails | null {
    return this.currentUserSubject.value;
  }

  /**
   * Fetch user details from backend by username
   */
  getUserByUsername(username: string): Observable<UserDetails> {
    return this.http.get<UserDetails>(`${this.apiUrl}/users/username/${username}`).pipe(
      tap(user => {
        this.currentUserSubject.next(user);
        localStorage.setItem('currentUser', JSON.stringify(user));
      })
    );
  }

  /**
   * Get current logged-in user from Spring Security
   */
  getAuthenticatedUser(): Observable<UserDetails> {
    return this.http.get<UserDetails>(`${this.apiUrl}/auth/current-user`);
  }

  /**
   * Login user with username/email and password
   * Supports both username and email as login identifier
   */
  login(usernameOrEmail: string, password: string): Observable<any> {
    // Send as 'username' parameter - backend will handle both username and email
    return this.http.post<any>(`${this.apiUrl}/auth/login`, { username: usernameOrEmail, password }).pipe(
      tap((response: any) => {
        // Reset logout flag when logging in
        this.isLoggedOut = false;
        if (response.user) {
          this.currentUserSubject.next(response.user);
          localStorage.setItem('currentUser', JSON.stringify(response.user));
        }
        if (response.token) {
          localStorage.setItem('authToken', response.token);
        }
      })
    );
  }

  /**
   * Update user profile
   */
  updateUserProfile(userId: number, userData: Partial<UserDetails>): Observable<UserDetails> {
    return this.http.put<UserDetails>(`${this.apiUrl}/users/${userId}`, userData).pipe(
      tap(user => {
        this.currentUserSubject.next(user);
        localStorage.setItem('currentUser', JSON.stringify(user));
      })
    );
  }

  /**
   * Logout user - calls backend logout endpoint to clear server session
   */
  logout(): Observable<any> {
    // Set logout flag FIRST to prevent auto-redirect in home component
    this.isLoggedOut = true;
    // Clear user immediately to prevent home component redirect
    this.currentUserSubject.next(null);
    localStorage.removeItem('currentUser');
    localStorage.removeItem('authToken');
    
    return this.http.post<any>(`${this.apiUrl}/auth/logout`, {}).pipe(
      tap(() => {
        // Additional cleanup after server confirms logout
        console.log('Server logout confirmed');
      })
    );
  }

  /**
   * Set current user (after login)
   */
  setCurrentUser(user: UserDetails): void {
    this.isLoggedOut = false;
    this.currentUserSubject.next(user);
    localStorage.setItem('currentUser', JSON.stringify(user));
  }
}
