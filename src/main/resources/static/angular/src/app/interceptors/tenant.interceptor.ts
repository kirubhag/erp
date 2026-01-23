import { HttpInterceptorFn } from '@angular/common/http';

/**
 * HTTP Interceptor that adds X-Tenant-ID header to all API requests.
 * The tenant ID is retrieved from the logged-in user's organization stored in localStorage.
 * This ensures multi-tenant routing works correctly for all API calls.
 */
export const tenantInterceptor: HttpInterceptorFn = (req, next) => {
  // Only add header for API requests
  if (!req.url.startsWith('/api')) {
    return next(req);
  }

  // Try to get the current user from localStorage
  const storedUser = localStorage.getItem('currentUser');
  if (storedUser) {
    try {
      const user = JSON.parse(storedUser);
      // Get tenant ID from the user's organization
      // The backend User entity has tenantId field
      const tenantId = user.tenantId || user.organizationId;
      
      if (tenantId) {
        const clonedRequest = req.clone({
          setHeaders: {
            'X-Tenant-ID': tenantId.toString()
          }
        });
        return next(clonedRequest);
      }
    } catch (e) {
      console.error('Failed to parse stored user for tenant header', e);
    }
  }

  // If no user or tenant ID, proceed without the header
  // The backend will use the default/master database
  return next(req);
};
