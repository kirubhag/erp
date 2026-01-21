import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Log the error details for debugging
      console.error('HTTP Error:', {
        url: req.url,
        method: req.method,
        status: error.status,
        statusText: error.statusText,
        error: error.error
      });

      // Check if we received HTML instead of JSON
      if (error.error instanceof ProgressEvent) {
        console.error('Failed to parse response as JSON. URL:', req.url);
      } else if (typeof error.error === 'string' && error.error.includes('<!doctype')) {
        console.error('Received HTML instead of JSON for URL:', req.url);
      }

      // Handle tenant database not found error - logout and redirect to login
      if (isTenantDbNotFoundError(error)) {
        console.warn('Tenant database not found. Logging out user.');
        handleTenantDbNotFound(router);
      }

      // Handle 401 Unauthorized - session expired or invalid
      if (error.status === 401 && !req.url.includes('/auth/login') && !req.url.includes('/auth/logout')) {
        console.warn('Session expired or unauthorized. Redirecting to login.');
        handleSessionExpired(router);
      }

      return throwError(() => error);
    })
  );
};

/**
 * Check if the error is related to tenant database not found
 */
function isTenantDbNotFoundError(error: HttpErrorResponse): boolean {
  // Check for specific error messages indicating tenant DB issues
  const errorMessage = error.error?.message || error.error?.error || '';
  const errorString = typeof errorMessage === 'string' ? errorMessage.toLowerCase() : '';
  
  // Common tenant DB not found error patterns
  const tenantDbErrorPatterns = [
    'tenant database not found',
    'tenant not found',
    'unknown database',
    'database does not exist',
    'tenant_db_not_found',
    'no tenant database',
    'invalid tenant',
    'tenant db not found'
  ];
  
  // Check if error message contains any tenant DB error patterns
  if (tenantDbErrorPatterns.some(pattern => errorString.includes(pattern))) {
    return true;
  }

  // Also check for specific HTTP status codes with certain conditions
  // 500 with database-related errors, or custom error codes
  if ((error.status === 500 || error.status === 503) && 
      (errorString.includes('database') || errorString.includes('tenant'))) {
    return true;
  }

  return false;
}

/**
 * Handle tenant database not found - clear session and redirect to login
 */
function handleTenantDbNotFound(router: Router): void {
  // Clear all session data
  localStorage.removeItem('currentUser');
  localStorage.removeItem('authToken');
  sessionStorage.clear();
  
  // Redirect to login with error message
  router.navigate(['/login'], { 
    queryParams: { 
      error: 'tenant_db_not_found',
      message: 'Your session has expired or your account is no longer active. Please login again.'
    }
  });
}

/**
 * Handle session expired - clear session and redirect to login
 */
function handleSessionExpired(router: Router): void {
  // Clear all session data
  localStorage.removeItem('currentUser');
  localStorage.removeItem('authToken');
  sessionStorage.clear();
  
  // Redirect to login
  router.navigate(['/login'], { 
    queryParams: { 
      error: 'session_expired',
      message: 'Your session has expired. Please login again.'
    }
  });
}
