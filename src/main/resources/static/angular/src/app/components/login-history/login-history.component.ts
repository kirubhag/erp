import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

interface LoginHistory {
  id: number;
  userId: number;
  username: string;
  loginTime: string;
  ipAddress: string;
  browser: string;
  status: string;
  failureReason?: string;
}

interface PageResponse {
  content: LoginHistory[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Component({
  selector: 'app-login-history',
  standalone: true,
  imports: [CommonModule, FormsModule, SettingsSidebarComponent],
  templateUrl: './login-history.component.html',
  styleUrls: ['./login-history.component.css']
})
export class LoginHistoryComponent implements OnInit {
  loginHistory: LoginHistory[] = [];
  loading = false;

  // Pagination
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;

  // Sorting
  sortBy = 'loginTime';
  sortDirection = 'desc';

  // Filtering
  searchTerm = '';
  statusFilter = 'ALL';

  // Stats
  successCount = 0;
  failedCount = 0;
  uniqueUsers = 0;

  // Math for template
  Math = Math;

  private apiUrl = '/api/login-history';

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.loadLoginHistory();
  }

  loadLoginHistory(): void {
    this.loading = true;

    let params = new HttpParams()
      .set('page', this.currentPage.toString())
      .set('size', this.pageSize.toString())
      .set('sortBy', this.sortBy)
      .set('direction', this.sortDirection);

    this.http.get<PageResponse>(this.apiUrl, { params })
      .subscribe({
        next: (response) => {
          this.loginHistory = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.calculateStats();
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading login history:', error);
          this.loading = false;
        }
      });
  }

  calculateStats(): void {
    this.successCount = this.loginHistory.filter(item => item.status === 'SUCCESS').length;
    this.failedCount = this.loginHistory.filter(item => item.status === 'FAILURE').length;

    // Calculate unique users
    const uniqueUserIds = new Set(this.loginHistory.map(item => item.userId));
    this.uniqueUsers = uniqueUserIds.size;
  }

  onSort(column: string): void {
    if (this.sortBy === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = column;
      this.sortDirection = 'desc';
    }
    this.currentPage = 0;
    this.loadLoginHistory();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadLoginHistory();
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadLoginHistory();
  }

  onStatusFilterChange(): void {
    this.currentPage = 0;
    this.loadLoginHistory();
  }

  get filteredHistory(): LoginHistory[] {
    let filtered = this.loginHistory;

    // Filter by search term
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(item =>
        item.username.toLowerCase().includes(term) ||
        item.ipAddress?.toLowerCase().includes(term) ||
        item.browser?.toLowerCase().includes(term)
      );
    }

    // Filter by status
    if (this.statusFilter !== 'ALL') {
      filtered = filtered.filter(item => item.status === this.statusFilter);
    }

    return filtered;
  }

  getUserInitials(username: string): string {
    if (!username) return 'U';
    const parts = username.split(/[\s@._-]/);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return username.substring(0, 2).toUpperCase();
  }

  getDeviceIcon(browser: string): string {
    if (!browser) return 'fas fa-laptop';
    const browserLower = browser.toLowerCase();
    if (browserLower.includes('mobile') || browserLower.includes('android') || browserLower.includes('ios')) {
      return 'fas fa-mobile-alt';
    }
    if (browserLower.includes('tablet') || browserLower.includes('ipad')) {
      return 'fas fa-tablet-alt';
    }
    return 'fas fa-laptop';
  }

  getDeviceType(browser: string): string {
    if (!browser) return 'Desktop';
    const browserLower = browser.toLowerCase();
    if (browserLower.includes('mobile') || browserLower.includes('android') || browserLower.includes('ios')) {
      return 'Mobile';
    }
    if (browserLower.includes('tablet') || browserLower.includes('ipad')) {
      return 'Tablet';
    }
    return 'Desktop';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getSortIcon(column: string): string {
    if (this.sortBy !== column) return 'fas fa-sort';
    return this.sortDirection === 'asc' ? 'fas fa-sort-up' : 'fas fa-sort-down';
  }

  get pageNumbers(): number[] {
    const pages: number[] = [];
    const maxVisible = 5;
    let start = Math.max(0, this.currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(this.totalPages, start + maxVisible);

    if (end - start < maxVisible) {
      start = Math.max(0, end - maxVisible);
    }

    for (let i = start; i < end; i++) {
      pages.push(i);
    }

    return pages;
  }
}
