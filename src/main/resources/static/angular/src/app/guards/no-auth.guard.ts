import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

/**
 * NoAuthGuard (GuestGuard)
 * 
 * This guard prevents authenticated users from accessing guest-only pages
 * such as login and register. If a logged-in user tries to access these
 * pages, they will be redirected to the dashboard.
 * 
 * Use this guard on routes that should only be accessible to unauthenticated users.
 */
@Injectable({
  providedIn: 'root'
})
export class NoAuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    return this.authService.currentUser$.pipe(
      take(1),
      map(user => {
        // If user is logged in, redirect to dashboard
        if (user && user.id) {
          console.log('User is already logged in, redirecting to dashboard');
          this.router.navigate(['/dashboard']);
          return false;
        }
        // User is not logged in, allow access to the route
        return true;
      })
    );
  }
}
