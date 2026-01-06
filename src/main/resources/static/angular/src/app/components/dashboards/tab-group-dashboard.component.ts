import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { LayoutService, TabGroup } from '../../services/layout.service';
import { DashboardWidget, DashboardWidgetComponent } from './dashboard-widget.component';
import { Subscription, filter } from 'rxjs';

@Component({
    selector: 'app-tab-group-dashboard',
    standalone: true,
    imports: [CommonModule, DashboardWidgetComponent],
    template: `
    <div class="container-fluid py-4">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col-12">
            <div class="d-flex align-items-center">
                <div class="icon-box me-3 bg-white shadow-sm rounded-3 p-3">
                    <i [class]="currentTabGroup?.icon || 'fas fa-columns'" class="fs-4 text-primary"></i>
                </div>
                <div>
                    <h2 class="mb-0 fw-bold text-dark">{{ currentTabGroup?.name || 'Dashboard' }}</h2>
                    <p class="text-muted mb-0 small">{{ currentTabGroup?.description || 'Overview and analytics' }}</p>
                </div>
            </div>
        </div>
      </div>

      <!-- Widgets Grid -->
      <div class="row g-4">
        <!-- Loading State -->
        <div *ngIf="loading" class="col-12 text-center py-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-2 text-muted">Loading dashboard data...</p>
        </div>

        <!-- Render Widgets -->
        <ng-container *ngIf="!loading">
            <div *ngFor="let widget of widgets" [ngClass]="getColumnClass(widget.size)">
                <app-dashboard-widget [config]="widget"></app-dashboard-widget>
            </div>
        </ng-container>

        <!-- Empty State -->
        <div *ngIf="!loading && widgets.length === 0" class="col-12">
            <div class="alert alert-info text-center">
                <i class="fas fa-info-circle me-2"></i>
                No widgets configured for this dashboard yet.
            </div>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .icon-box {
        min-width: 56px;
        min-height: 56px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    :host {
        display: block;
        padding-top: 70px;
    }
  `]
})
export class TabGroupDashboardComponent implements OnInit, OnDestroy {
    currentTabGroup: TabGroup | null = null;
    widgets: DashboardWidget[] = [];
    loading = false;
    private routeSub: Subscription | undefined;
    private routerSub: Subscription | undefined;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private layoutService: LayoutService
    ) { }

    ngOnInit(): void {
        // Listen to route changes to update dashboard
        this.routerSub = this.router.events.pipe(
            filter(event => event instanceof NavigationEnd)
        ).subscribe(() => {
            this.resolveTabGroup();
        });

        // Also try initial resolve
        this.resolveTabGroup();
    }

    ngOnDestroy(): void {
        if (this.routeSub) this.routeSub.unsubscribe();
        if (this.routerSub) this.routerSub.unsubscribe();
    }

    private resolveTabGroup() {
        // 1. Get current URL path
        const url = this.router.url;

        // 2. Find matching TabGroup from LayoutService
        this.layoutService.getGroups().subscribe(groups => {
            // Find group where routePath matches the start of current URL
            // e.g. /core-platform matches /core-platform/settings
            const group = groups.find(g => g.routePath && url.startsWith(g.routePath));

            if (group) {
                this.currentTabGroup = group;
                this.layoutService.setActiveGroup(group);
                this.loadWidgetsForGroup(group);
            } else {
                // Fallback: Use active group if set, or redirect/show error?
                // For now, if no match, maybe we are on a detail page or something not mapped?
                // But this component IS the dashboard, so it should match.
            }
        });
    }

    private loadWidgetsForGroup(group: TabGroup) {
        this.loading = true;
        // Simulate API call or load config based on group code
        setTimeout(() => {
            this.widgets = this.getMockWidgets(group.code);
            this.loading = false;
        }, 300);
    }

    getColumnClass(size?: string): string {
        switch (size) {
            case 'small': return 'col-md-3 col-sm-6';
            case 'large': return 'col-md-8 col-sm-12';
            case 'full': return 'col-12';
            case 'medium':
            default: return 'col-md-4 col-sm-6';
        }
    }

    // --- Mock Data Generator ---
    // In real app, this would come from a DashboardService API
    private getMockWidgets(groupCode: string): DashboardWidget[] {
        const widgets: DashboardWidget[] = [];

        // Default Common Widgets
        widgets.push({
            id: 'w1', type: 'stat', title: 'Total Entities',
            icon: 'fas fa-layer-group',
            data: { value: Math.floor(Math.random() * 100) + 5 },
            description: 'Active records in this module'
        });

        switch (groupCode) {
            case 'CORE':
                widgets.push(
                    { id: 'c1', type: 'stat', title: 'System Users', icon: 'fas fa-users', data: { value: 24 }, className: 'text-primary' },
                    { id: 'c2', type: 'stat', title: 'Organizations', icon: 'fas fa-building', data: { value: 12 }, className: 'text-success' },
                    { id: 'c3', type: 'action', title: 'Quick Setup', icon: 'fas fa-magic', actionText: 'Go to Setup', actionLink: '/setup', description: 'Configure system settings' },
                    { id: 'c4', type: 'list', title: 'Recent Logins', size: 'medium', data: { items: [{ label: 'Admin', value: '2m ago' }, { label: 'Principal', value: '1h ago' }] } }
                );
                break;
            case 'ACADEMIC':
                widgets.push(
                    { id: 'a1', type: 'stat', title: 'Start Students', icon: 'fas fa-user-graduate', data: { value: 450 }, className: 'text-info' },
                    { id: 'a2', type: 'chart', title: 'Attendance Trends', size: 'large' },
                    { id: 'a3', type: 'list', title: 'Upcoming Exams', size: 'medium', data: { items: [{ label: 'Math Midterm', value: 'Oct 24' }, { label: 'Science Final', value: 'Nov 12' }] } }
                );
                break;
            case 'FINANCE':
                widgets.push(
                    { id: 'f1', type: 'stat', title: 'Revenue (YTD)', icon: 'fas fa-dollar-sign', data: { value: '$124,500' }, className: 'text-success' },
                    { id: 'f2', type: 'stat', title: 'Pending Expenses', icon: 'fas fa-file-invoice-dollar', data: { value: '$3,200' }, className: 'text-warning' },
                    { id: 'f3', type: 'chart', title: 'Cash Flow', size: 'large' },
                    { id: 'f4', type: 'action', title: 'New Invoice', icon: 'fas fa-plus', actionLink: '/finance/invoices/create' }
                );
                break;
            case 'HR':
                widgets.push(
                    { id: 'h1', type: 'stat', title: 'Total Staff', icon: 'fas fa-id-badge', data: { value: 38 } },
                    { id: 'h2', type: 'list', title: 'Leave Requests', size: 'medium', data: { items: [{ label: 'John Doe', value: 'Sick Leave' }, { label: 'Jane Smith', value: 'Vacation' }] } }
                );
                break;
            // Add more specific widgets for other groups...
            default:
                widgets.push(
                    { id: 'd1', type: 'action', title: 'Explore Module', icon: 'fas fa-compass', description: 'Get started with this module' }
                );
        }

        return widgets;
    }
}
