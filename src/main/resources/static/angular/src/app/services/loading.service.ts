import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$: Observable<boolean> = this.loadingSubject.asObservable();
  
  private requestCount = 0;

  show(): void {
    this.requestCount++;
    if (this.requestCount === 1) {
      // Use setTimeout to defer the state change and avoid ExpressionChangedAfterItHasBeenCheckedError
      setTimeout(() => this.loadingSubject.next(true), 0);
    }
  }

  hide(): void {
    this.requestCount--;
    if (this.requestCount <= 0) {
      this.requestCount = 0;
      // Use setTimeout to defer the state change
      setTimeout(() => this.loadingSubject.next(false), 0);
    }
  }
}
