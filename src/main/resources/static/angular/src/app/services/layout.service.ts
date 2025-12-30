import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, shareReplay, tap } from 'rxjs/operators';
import { Router, NavigationEnd } from '@angular/router';

export interface TabGroup {
  id: number;
  name: string;
  code: string;
  icon: string;
  sequence: number;
  description: string;
  entities: ErpEntity[];
}

export interface ErpEntity {
  id: number;
  singularName: string;
  pluralName: string;
  icon: string;
  route: string;
  sequence: number;
}

@Injectable({
  providedIn: 'root'
})
export class LayoutService {
  private apiUrl = '/api/module/groups';
  
  // State
  private sidebarCollapsed = new BehaviorSubject<boolean>(false);
  private activeGroup = new BehaviorSubject<TabGroup | null>(null);
  
  // Data Cache
  private groups$ = new BehaviorSubject<TabGroup[]>([]);
  
  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadGroups();
    
    // Auto-detect Active Group on Route Change
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.updateActiveGroupFromRoute(this.router.url);
      }
    });
  }

  loadGroups() {
    this.http.get<TabGroup[]>(this.apiUrl)
      .pipe(
        tap(groups => {
          this.groups$.next(groups);
          // Set default group (Core Platform) if none selected
          if (!this.activeGroup.value && groups.length > 0) {
             const coreGroup = groups.find(g => g.code === 'CORE') || groups[0];
             this.setActiveGroup(coreGroup);
          }
        }),
        shareReplay(1)
      ).subscribe();
  }

  getGroups(): Observable<TabGroup[]> {
    return this.groups$.asObservable();
  }

  getActiveGroup(): Observable<TabGroup | null> {
    return this.activeGroup.asObservable();
  }

  setActiveGroup(group: TabGroup) {
    this.activeGroup.next(group);
  }

  getSidebarCollapsed(): Observable<boolean> {
    return this.sidebarCollapsed.asObservable();
  }

  toggleSidebar() {
    this.sidebarCollapsed.next(!this.sidebarCollapsed.value);
  }

  private updateActiveGroupFromRoute(url: string) {
    // Simple logic: check if current route belongs to any entity in groups
    // If exact match found, update active group.
    // Otherwise keep current group.
    
    // Skip for root path
    if (url === '/' || url === '/dashboard') return; 
    
    const groups = this.groups$.value;
    for (const group of groups) {
      if (group.entities.some(e => url.includes(e.route))) {
        if (this.activeGroup.value?.id !== group.id) {
            this.setActiveGroup(group);
        }
        return;
      }
    }
  }
}
