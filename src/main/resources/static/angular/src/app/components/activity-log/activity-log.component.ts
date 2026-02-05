import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface ActivityLog {
    id: number;
    userId: number | null;
    username: string;
    ipAddress: string;
    userAgent: string;
    actionType: string;
    severity: string;
    entityType: string;
    entityId: number | null;
    moduleName: string;
    oldValue: string;
    newValue: string;
    deltaSummary: string;
    description: string;
    metadata: string;
    timestamp: string;
}

interface PageResponse {
    content: ActivityLog[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}

@Component({
    selector: 'app-activity-log',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './activity-log.component.html',
    styleUrls: ['./activity-log.component.css']
})
export class ActivityLogComponent implements OnInit {
    logs: ActivityLog[] = [];
    filteredLogs: ActivityLog[] = [];

    // Pagination
    currentPage = 0;
    pageSize = 50;
    totalPages = 0;
    totalElements = 0;

    // Filters
    searchQuery = '';
    selectedUser: string | null = null;
    selectedEntityType: string | null = null;
    selectedActionType: string | null = null;
    selectedSeverity: string | null = null;
    startDate: string | null = null;
    endDate: string | null = null;

    // Filter options
    actionTypes = ['CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'LOGIN_FAILED',
        'SETTING_CHANGE', 'PERMISSION_CHANGE', 'EXPORT', 'BULK_UPDATE', 'BULK_DELETE'];
    severities = ['INFO', 'WARNING', 'CRITICAL'];

    loading = false;
    error: string | null = null;

    constructor(private http: HttpClient) { }

    ngOnInit(): void {
        this.loadLogs();
    }

    loadLogs(): void {
        this.loading = true;
        this.error = null;

        let params = new HttpParams()
            .set('page', this.currentPage.toString())
            .set('size', this.pageSize.toString());

        if (this.selectedActionType) {
            params = params.set('actionType', this.selectedActionType);
        }
        if (this.selectedSeverity) {
            params = params.set('severity', this.selectedSeverity);
        }
        if (this.selectedEntityType) {
            params = params.set('entityType', this.selectedEntityType);
        }
        if (this.startDate) {
            params = params.set('startDate', this.startDate);
        }
        if (this.endDate) {
            params = params.set('endDate', this.endDate);
        }

        this.http.get<PageResponse>('/api/activity-logs', { params })
            .subscribe({
                next: (response) => {
                    this.logs = response.content;
                    this.filteredLogs = this.logs;
                    this.totalPages = response.totalPages;
                    this.totalElements = response.totalElements;
                    this.loading = false;
                    this.applySearch();
                },
                error: (err) => {
                    console.error('Error loading activity logs:', err);
                    this.error = 'Failed to load activity logs. Please try again.';
                    this.loading = false;
                }
            });
    }

    applySearch(): void {
        if (!this.searchQuery) {
            this.filteredLogs = this.logs;
            return;
        }

        const query = this.searchQuery.toLowerCase();
        this.filteredLogs = this.logs.filter(log =>
            log.username?.toLowerCase().includes(query) ||
            log.description?.toLowerCase().includes(query) ||
            log.entityType?.toLowerCase().includes(query) ||
            log.actionType?.toLowerCase().includes(query)
        );
    }

    onSearchChange(): void {
        this.applySearch();
    }

    onFilterChange(): void {
        this.currentPage = 0;
        this.loadLogs();
    }

    clearFilters(): void {
        this.selectedUser = null;
        this.selectedEntityType = null;
        this.selectedActionType = null;
        this.selectedSeverity = null;
        this.startDate = null;
        this.endDate = null;
        this.searchQuery = '';
        this.currentPage = 0;
        this.loadLogs();
    }

    nextPage(): void {
        if (this.currentPage < this.totalPages - 1) {
            this.currentPage++;
            this.loadLogs();
        }
    }

    previousPage(): void {
        if (this.currentPage > 0) {
            this.currentPage--;
            this.loadLogs();
        }
    }

    goToPage(page: number): void {
        this.currentPage = page;
        this.loadLogs();
    }

    getSeverityClass(severity: string): string {
        switch (severity) {
            case 'INFO':
                return 'severity-info';
            case 'WARNING':
                return 'severity-warning';
            case 'CRITICAL':
                return 'severity-critical';
            default:
                return '';
        }
    }

    getSeverityIcon(severity: string): string {
        switch (severity) {
            case 'INFO':
                return 'fas fa-info-circle';
            case 'WARNING':
                return 'fas fa-exclamation-triangle';
            case 'CRITICAL':
                return 'fas fa-exclamation-circle';
            default:
                return 'fas fa-circle';
        }
    }

    getActionIcon(actionType: string): string {
        switch (actionType) {
            case 'CREATE':
                return 'fas fa-plus-circle';
            case 'UPDATE':
                return 'fas fa-edit';
            case 'DELETE':
                return 'fas fa-trash-alt';
            case 'LOGIN':
                return 'fas fa-sign-in-alt';
            case 'LOGOUT':
                return 'fas fa-sign-out-alt';
            case 'LOGIN_FAILED':
                return 'fas fa-times-circle';
            case 'EXPORT':
                return 'fas fa-file-export';
            case 'SETTING_CHANGE':
                return 'fas fa-cog';
            case 'PERMISSION_CHANGE':
                return 'fas fa-user-shield';
            default:
                return 'fas fa-circle';
        }
    }

    formatTimestamp(timestamp: string): string {
        const date = new Date(timestamp);
        return date.toLocaleString();
    }

    formatActionType(actionType: string): string {
        return actionType.replace(/_/g, ' ');
    }

    getPageNumbers(): number[] {
        const pages: number[] = [];
        const maxPages = 5;
        let startPage = Math.max(0, this.currentPage - Math.floor(maxPages / 2));
        let endPage = Math.min(this.totalPages - 1, startPage + maxPages - 1);

        if (endPage - startPage < maxPages - 1) {
            startPage = Math.max(0, endPage - maxPages + 1);
        }

        for (let i = startPage; i <= endPage; i++) {
            pages.push(i);
        }

        return pages;
    }
}
