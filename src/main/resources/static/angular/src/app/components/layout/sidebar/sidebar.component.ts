import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { LayoutService, TabGroup } from '../../../services/layout.service';
import { ThemeService } from '../../../services/theme.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="sidebar-wrapper" 
         [class.collapsed]="(collapsed$ | async)"
         [style.--theme-primary]="currentTheme$ | async">
      <div class="sidebar-header">
        <button class="toggle-btn" (click)="toggleSidebar()" 
                [title]="(collapsed$ | async) ? 'Show sidebar' : 'Hide sidebar'">
          <i class="fas" [class.fa-angles-left]="!(collapsed$ | async)" [class.fa-angles-right]="(collapsed$ | async)"></i>
          <span class="toggle-text">{{ (collapsed$ | async) ? '' : 'Hide' }}</span>
        </button>
      </div>

      <nav class="sidebar-nav">
        <ul>
          <li *ngFor="let group of groups$ | async" 
              [class.active]="(activeGroup$ | async)?.id === group.id"
              (click)="selectGroup(group)"
              [title]="group.name">
            <div class="nav-item">
              <i [class]="group.icon" class="nav-icon"></i>
              <span class="nav-text">{{ group.name }}</span>
            </div>
          </li>
        </ul>
      </nav>
      
      <div class="sidebar-footer">
        <!-- Optional Footer content -->
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      height: calc(100vh - 56px);
      position: fixed;
      left: 0;
      top: 56px;
      z-index: 1030;
    }

    .sidebar-wrapper {
      width: 260px;
      height: 100%;
      background: var(--sidebar-bg, #1a1f2c);
      color: rgba(255, 255, 255, 0.85) !important;
      transition: width 0.3s ease, transform 0.3s ease;
      display: flex;
      flex-direction: column;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .sidebar-wrapper.collapsed {
      width: 70px;
      
      .nav-text, .toggle-text {
        opacity: 0;
        width: 0;
        overflow: hidden;
      }
      
      .sidebar-header {
        padding: 20px 10px;
        justify-content: center;
      }
      
      .toggle-btn {
        position: static;
        margin: 0 auto;
        padding: 8px;
        gap: 0;
      }
      
      .nav-item {
        justify-content: center;
        padding: 12px;
      }
      
      .nav-icon {
        margin-right: 0;
      }
    }

    .sidebar-header {
      height: 70px;
      display: flex;
      align-items: center;
      justify-content: flex-start;
      padding: 0 24px;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      position: relative;
      background: var(--app-primary, #0891B2);
    }

    .toggle-btn {
      background: rgba(255, 255, 255, 0.15);
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: white;
      cursor: pointer;
      font-size: 14px;
      padding: 8px 12px;
      border-radius: 6px;
      transition: all 0.2s ease;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      font-weight: 500;
      white-space: nowrap;
      
      &:hover {
        background: rgba(255, 255, 255, 0.25);
        border-color: white;
        transform: translateX(-2px);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
      }
      
      &:active {
        transform: scale(0.95);
      }
      
      i {
        font-size: 14px;
      }
      
      .toggle-text {
        font-size: 13px;
        letter-spacing: 0.3px;
      }
    }

    .sidebar-nav {
      flex: 1;
      overflow-y: auto;
      padding: 10px 12px;
      background: var(--app-primary, #0891B2);
      
      /* Scrollbar styling */
      &::-webkit-scrollbar {
        width: 6px;
      }
      &::-webkit-scrollbar-track {
        background: transparent;
      }
      &::-webkit-scrollbar-thumb {
        background: rgba(255, 255, 255, 0.3);
        border-radius: 3px;
        
        &:hover {
          background: rgba(255, 255, 255, 0.5);
        }
      }
    }

    ul {
      list-style: none;
      padding: 0;
      margin: 0;
    }

    li {
      margin-bottom: 4px;
      cursor: pointer;
      border-radius: 8px;
      transition: all 0.2s;

      &:hover {
        background: rgba(255, 255, 255, 0.15);
        color: white;
        
        .nav-icon {
          color: white;
        }
      }

      &.active {
        background: rgba(255, 255, 255, 0.25);
        color: white;
        box-shadow: 0 1px 3px rgba(0,0,0,0.2);
        
        .nav-icon {
          color: white;
        }
      }
    }

    .nav-item {
      display: flex;
      align-items: center;
      padding: 12px;
      height: 48px;
      transition: all 0.3s ease;
    }

    .nav-icon {
      font-size: 18px;
      min-width: 24px;
      margin-right: 12px;
      text-align: center;
      color: rgba(255, 255, 255, 0.8);
      transition: all 0.3s ease;
    }
    
    .nav-text {
      font-size: 14px;
      font-weight: 500;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      transition: opacity 0.3s ease, width 0.3s ease;
    }
    
    /* Floating toggle button */
    .floating-toggle {
      position: fixed;
      left: 70px;
      top: 76px;
      z-index: 1031;
      background: var(--theme-primary, #3b82f6);
      color: white;
      border: none;
      width: 36px;
      height: 36px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      box-shadow: 0 2px 8px rgba(0,0,0,0.2);
      transition: all 0.3s ease;
      
      &:hover {
        transform: scale(1.1);
        box-shadow: 0 4px 12px rgba(0,0,0,0.3);
      }
      
      i {
        font-size: 14px;
      }
    }
  `]
})
export class SidebarComponent implements OnInit {
  groups$: Observable<TabGroup[]>;
  activeGroup$: Observable<TabGroup | null>;
  collapsed$: Observable<boolean>;
  currentTheme$: Observable<string>;

  constructor(
    private layoutService: LayoutService,
    private themeService: ThemeService,
    private router: Router // Added Router injection
  ) {
    this.groups$ = this.layoutService.getGroups();
    this.activeGroup$ = this.layoutService.getActiveGroup();
    this.collapsed$ = this.layoutService.getSidebarCollapsed();
    this.currentTheme$ = this.themeService.getTheme$();
  }

  ngOnInit(): void {
    // Component initialization if needed
  }

  selectGroup(group: TabGroup) {
    this.layoutService.setActiveGroup(group);
    if (group.routePath) {
      this.router.navigate([group.routePath]);
    }
  }

  toggleSidebar() {
    this.layoutService.toggleSidebar();
  }
}
