import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
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

      return throwError(() => error);
    })
  );
};
