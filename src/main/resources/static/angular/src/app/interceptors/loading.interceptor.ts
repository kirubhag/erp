import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { LoadingService } from '../services/loading.service';
import { finalize } from 'rxjs/operators';

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  const loadingService = inject(LoadingService);
  
  // Show loading bar when request starts
  loadingService.show();
  
  // Hide loading bar when request completes (success or error)
  return next(req).pipe(
    finalize(() => loadingService.hide())
  );
};
