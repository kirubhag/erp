import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

export interface DashboardWidget {
    id: string;
    type: 'stat' | 'chart' | 'list' | 'action';
    title: string;
    icon?: string;
    description?: string;
    data?: any;
    actionText?: string;
    actionFn?: () => void;
    actionLink?: string;
    className?: string; // For custom styling (colors)
    size?: 'small' | 'medium' | 'large' | 'full'; // For grid sizing
}

@Component({
    selector: 'app-dashboard-widget',
    standalone: true,
    imports: [CommonModule, RouterModule],
    template: `
    <div class="card h-100 widget-card" [ngClass]="config.className">
      <div class="card-body">
        
        <!-- Stat Widget -->
        <div *ngIf="config.type === 'stat'" class="d-flex align-items-center">
            <div class="widget-icon me-3" *ngIf="config.icon">
                <i [class]="config.icon"></i>
            </div>
            <div>
                <h6 class="card-subtitle mb-1 text-muted">{{ config.title }}</h6>
                <h3 class="card-title mb-0">{{ config.data?.value }}</h3>
                <small *ngIf="config.description" class="text-muted">{{ config.description }}</small>
            </div>
        </div>

        <!-- Action Widget -->
        <div *ngIf="config.type === 'action'" class="text-center">
            <div class="widget-icon-large mb-3" *ngIf="config.icon">
                <i [class]="config.icon"></i>
            </div>
            <h5 class="card-title">{{ config.title }}</h5>
            <p class="card-text text-muted small" *ngIf="config.description">{{ config.description }}</p>
            <a *ngIf="config.actionLink" [routerLink]="config.actionLink" class="btn btn-primary btn-sm mt-2">
                {{ config.actionText || 'Go' }}
            </a>
            <button *ngIf="config.actionFn" (click)="config.actionFn()" class="btn btn-primary btn-sm mt-2">
                {{ config.actionText || 'Action' }}
            </button>
        </div>

        <!-- List Widget -->
         <div *ngIf="config.type === 'list'">
            <h6 class="card-title mb-3">
                <i [class]="config.icon" *ngIf="config.icon" class="me-2"></i> {{ config.title }}
            </h6>
            <ul class="list-group list-group-flush">
                <li *ngFor="let item of config.data?.items" class="list-group-item px-0 py-2 d-flex justify-content-between align-items-center">
                    <span>{{ item.label }}</span>
                    <span class="badge bg-light text-dark rounded-pill">{{ item.value }}</span>
                </li>
            </ul>
             <div *ngIf="!config.data?.items?.length" class="text-center py-3 text-muted">
                <small>No items found</small>
            </div>
        </div>
        
        <!-- Placeholder for simple Chart -->
        <div *ngIf="config.type === 'chart'">
             <h6 class="card-title mb-3">{{ config.title }}</h6>
             <div class="chart-placeholder d-flex align-items-center justify-content-center bg-light rounded" style="height: 150px;">
                <span class="text-muted"><i class="fas fa-chart-line me-2"></i> Chart Visualization</span>
             </div>
        </div>

      </div>
    </div>
  `,
    styles: [`
    .widget-card {
        border: none;
        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        transition: transform 0.2s, box-shadow 0.2s;
    }
    .widget-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
    .widget-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        background: rgba(var(--bs-primary-rgb), 0.1);
        color: var(--bs-primary);
    }
    .widget-icon-large {
        font-size: 32px;
        color: var(--bs-primary);
        opacity: 0.8;
    }
    
    /* Custom Color Classes */
    .bg-gradient-primary .widget-icon { background: rgba(255,255,255,0.2); color: white; }
    .bg-gradient-success .widget-icon { background: rgba(255,255,255,0.2); color: white; }
    .bg-gradient-warning .widget-icon { background: rgba(255,255,255,0.2); color: white; }
    .bg-gradient-danger .widget-icon { background: rgba(255,255,255,0.2); color: white; }

    .chart-placeholder {
        border: 1px dashed #dee2e6;
    }
  `]
})
export class DashboardWidgetComponent {
    @Input() config!: DashboardWidget;
}
