import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LayoutService, TabGroup } from '../../../services/layout.service';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-sidebar',
    standalone: true,
    imports: [CommonModule, RouterModule],
    template: `
    <div class="sidebar-wrapper" [class.collapsed]="(collapsed$ | async)">
      <div class="sidebar-header">
        <i class="fas fa-layer-group logo-icon"></i>
        <span class="logo-text">ERP System</span>
        <button class="toggle-btn" (click)="toggleSidebar()">
          <i class="fas" [class.fa-chevron-left]="!(collapsed$ | async)" [class.fa-chevron-right]="(collapsed$ | async)"></i>
        </button>
      </div>

      <nav class="sidebar-nav">
        <ul>
          <li *ngFor="let group of groups$ | async" 
              [class.active]="(activeGroup$ | async)?.id === group.id"
              (click)="selectGroup(group)">
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
      height: 100vh;
      position: fixed;
      left: 0;
      top: 0;
      z-index: 1040;
    }

    .sidebar-wrapper {
      width: 260px;
      height: 100%;
      background: #1a1f2c; /* Updated Dark Theme Background */
      color: #b0bfd6; /* Sidebar Text Color */
      transition: width 0.3s ease;
      display: flex;
      flex-direction: column;
      box-shadow: 2px 0 10px rgba(0,0,0,0.1);
      border-right: 1px solid #2d3646;
    }

    .sidebar-wrapper.collapsed {
      width: 70px;
      
      .logo-text, .nav-text {
        display: none;
      }
      
      .sidebar-header {
        padding: 20px 0;
        justify-content: center;
      }
      
      .toggle-btn {
        position: absolute;
        right: -12px;
        top: 25px;
        background: #3b82f6;
        border-radius: 50%;
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        border: 2px solid #1a1f2c;
      }
    }

    .sidebar-header {
      height: 70px;
      display: flex;
      align-items: center;
      padding: 0 24px;
      border-bottom: 1px solid #2d3646;
      position: relative;
      margin-bottom: 10px;
    }

    .logo-icon {
      font-size: 24px;
      color: #3b82f6;
      margin-right: 12px;
      min-width: 24px;
    }

    .logo-text {
      font-size: 18px;
      font-weight: 600;
      color: white;
      white-space: nowrap;
    }

    .toggle-btn {
      background: none;
      border: none;
      color: #64748b;
      cursor: pointer;
      margin-left: auto;
      font-size: 14px;
      display: none; /* Hidden by default, shown in collapsed logic or larger screens if needed */
    }

    .sidebar-nav {
      flex: 1;
      overflow-y: auto;
      padding: 10px 12px;
      
      /* Scrollbar styling */
      &::-webkit-scrollbar {
        width: 6px;
      }
      &::-webkit-scrollbar-track {
        background: transparent;
      }
      &::-webkit-scrollbar-thumb {
        background: #334155;
        border-radius: 3px;
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
        background: rgba(59, 130, 246, 0.1);
        color: white;
      }

      &.active {
        background: #3b82f6; /* Active Item Background */
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
    }

    .nav-icon {
      font-size: 18px;
      min-width: 24px;
      margin-right: 12px;
      text-align: center;
      color: #94a3b8;
      transition: color 0.2s;
    }
    
    .collapsed .nav-icon {
       margin-right: 0;
    }
    
    .nav-text {
      font-size: 14px;
      font-weight: 500;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  `]
})
export class SidebarComponent {
    groups$: Observable<TabGroup[]>;
    activeGroup$: Observable<TabGroup | null>;
    collapsed$: Observable<boolean>;

    constructor(private layoutService: LayoutService) {
        this.groups$ = this.layoutService.getGroups();
        this.activeGroup$ = this.layoutService.getActiveGroup();
        this.collapsed$ = this.layoutService.getSidebarCollapsed();
    }

    selectGroup(group: TabGroup) {
        this.layoutService.setActiveGroup(group);
    }

    toggleSidebar() {
        this.layoutService.toggleSidebar();
    }
}
