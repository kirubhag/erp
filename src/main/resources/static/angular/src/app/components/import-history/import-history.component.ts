import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrganizationService, ImportHistory } from '../../services/organization.service';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

@Component({
  selector: 'app-import-history',
  standalone: true,
  imports: [CommonModule, FormsModule, SettingsSidebarComponent],
  templateUrl: './import-history.component.html',
  styles: [`
    .import-history-page { display: flex; min-height: 100vh; }
    .import-history-sidebar { width: 250px; background-color: #f8f9fa; border-right: 1px solid #e9ecef; overflow-y: auto; }
    .import-history-content { flex: 1; overflow-y: auto; padding-top: 80px; }
    .import-history-wrapper { padding: 30px; max-width: 1200px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
    .header-content h1 { font-size: 2rem; font-weight: 700; margin-bottom: 5px; color: #333; }
    .header-content p { color: #6c757d; margin: 0; }
    .export-btn { background-color: #667eea; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; transition: all 0.3s ease; }
    .export-btn:hover:not(:disabled) { background-color: #5568d3; transform: translateY(-2px); }
    .export-btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .filters-section { display: flex; gap: 15px; margin-bottom: 25px; flex-wrap: wrap; }
    .search-box { position: relative; flex: 1; min-width: 250px; }
    .search-icon { position: absolute; left: 12px; top: 50%; transform: translateY(-50%); color: #999; }
    .search-input { width: 100%; padding: 10px 12px 10px 40px; border: 1px solid #ddd; border-radius: 6px; font-size: 0.95rem; }
    .search-input:focus { outline: none; border-color: #667eea; box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1); }
    .filter-group { display: flex; gap: 12px; flex-wrap: wrap; align-items: center; }
    .filter-item { display: flex; gap: 8px; align-items: center; }
    .filter-item label { font-weight: 500; color: #333; white-space: nowrap; }
    .filter-select { padding: 8px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 0.95rem; background-color: white; cursor: pointer; }
    .filter-select:focus { outline: none; border-color: #667eea; box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1); }
    .clear-btn { background-color: white; color: #333; border: 1px solid #ddd; padding: 8px 16px; border-radius: 6px; cursor: pointer; transition: all 0.3s ease; }
    .clear-btn:hover { background-color: #f8f9fa; }
    .alert { padding: 15px; border-radius: 6px; margin-bottom: 25px; }
    .alert-danger { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    .loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 60px 20px; }
    .spinner { width: 40px; height: 40px; border: 4px solid #f3f3f3; border-top: 4px solid #667eea; border-radius: 50%; animation: spin 1s linear infinite; }
    @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
    .empty-state { text-align: center; padding: 60px 20px; color: #6c757d; }
    .empty-state i { font-size: 3rem; color: #ddd; margin-bottom: 20px; }
    .empty-state h3 { font-size: 1.3rem; font-weight: 600; margin-bottom: 10px; }
    .history-container { background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05); overflow-x: auto; margin-bottom: 25px; }
    .desktop-table { width: 100%; border-collapse: collapse; }
    .desktop-table thead { background-color: #f8f9fa; border-bottom: 2px solid #ddd; }
    .desktop-table th { padding: 15px; text-align: left; font-weight: 600; color: #333; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 0.5px; }
    .desktop-table tbody tr { border-bottom: 1px solid #e9ecef; transition: background-color 0.2s ease; }
    .desktop-table tbody tr:hover { background-color: #f8f9fa; }
    .history-row td { padding: 15px; color: #333; font-size: 0.95rem; }
    .history-row.status-success { border-left: 4px solid #28a745; }
    .history-row.status-failed { border-left: 4px solid #dc3545; }
    .history-row.status-pending { border-left: 4px solid #ffc107; }
    .history-row.status-partial { border-left: 4px solid #fd7e14; }
    .entity-name { font-weight: 600; color: #667eea; }
    .record-count { background-color: #e7f3ff; color: #004085; padding: 4px 12px; border-radius: 4px; font-weight: 600; }
    .badge { display: inline-block; padding: 4px 12px; border-radius: 4px; font-size: 0.85rem; font-weight: 600; }
    .badge-info { background-color: #d1ecf1; color: #0c5460; }
    .status-badge { display: inline-flex; align-items: center; padding: 6px 12px; border-radius: 4px; font-size: 0.85rem; font-weight: 600; white-space: nowrap; }
    .badge-success { background-color: #d4edda; color: #155724; }
    .badge-failed { background-color: #f8d7da; color: #721c24; }
    .badge-pending { background-color: #fff3cd; color: #856404; }
    .badge-partial { background-color: #ffe5d9; color: #783100; }
    .mobile-cards { display: none; }
    .pagination-section { display: flex; justify-content: space-between; align-items: center; margin-top: 25px; padding: 20px; background-color: #f8f9fa; border-radius: 8px; flex-wrap: wrap; gap: 20px; }
    .records-summary { color: #6c757d; font-size: 0.95rem; }
    .pagination-nav { display: flex; gap: 12px; align-items: center; }
    .page-buttons { display: flex; gap: 8px; align-items: center; }
    .page-btn { width: 36px; height: 36px; padding: 0; border: 1px solid #ddd; background-color: white; color: #333; border-radius: 4px; cursor: pointer; font-weight: 500; transition: all 0.3s ease; }
    .page-btn:hover { background-color: #f8f9fa; border-color: #667eea; }
    .page-btn.active { background-color: #667eea; color: white; border-color: #667eea; }
    .page-ellipsis { color: #999; }
    .btn-sm { padding: 8px 16px; font-size: 0.85rem; }
    .btn-outline-secondary { background-color: white; color: #333; border: 1px solid #ddd; border-radius: 6px; cursor: pointer; transition: all 0.3s ease; }
    .btn-outline-secondary:hover:not(:disabled) { background-color: #f8f9fa; }
    .btn-outline-secondary:disabled { opacity: 0.5; cursor: not-allowed; }
    @media (max-width: 768px) {
      .import-history-wrapper { padding: 20px; }
      .page-header { flex-direction: column; align-items: flex-start; gap: 15px; }
      .export-btn { width: 100%; }
      .filters-section { flex-direction: column; }
      .filter-group { flex-direction: column; width: 100%; }
      .filter-item { width: 100%; }
      .filter-select { width: 100%; }
      .desktop-table { display: none; }
      .mobile-cards { display: grid; grid-template-columns: 1fr; gap: 15px; }
      .history-card { background: white; border: 1px solid #e9ecef; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05); }
      .history-card.status-success { border-left: 4px solid #28a745; }
      .history-card.status-failed { border-left: 4px solid #dc3545; }
      .history-card.status-pending { border-left: 4px solid #ffc107; }
      .history-card.status-partial { border-left: 4px solid #fd7e14; }
      .card-header { padding: 15px; background-color: #f8f9fa; border-bottom: 1px solid #e9ecef; display: flex; justify-content: space-between; align-items: center; gap: 10px; }
      .entity-header { display: flex; align-items: center; gap: 8px; flex: 1; }
      .entity-header h4 { margin: 0; font-size: 1rem; color: #667eea; font-weight: 600; }
      .card-body { padding: 15px; }
      .card-row { display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 0.9rem; }
      .card-row .label { font-weight: 600; color: #333; }
      .card-row .value { color: #6c757d; text-align: right; }
      .card-row .value.error { color: #dc3545; font-size: 0.85rem; }
      .pagination-section { flex-direction: column; align-items: stretch; gap: 15px; }
      .pagination-nav { flex-direction: column; width: 100%; }
      .page-buttons { width: 100%; justify-content: center; flex-wrap: wrap; }
      .btn-sm { width: 100%; }
    }
  `]
})
export class ImportHistoryComponent implements OnInit {
  importHistoryRecords: ImportHistory[] = [];
  filteredRecords: ImportHistory[] = [];
  loading = false;
  errorMessage = '';
  currentPage = 1;
  pageSize = 10;
  totalRecords = 0;
  totalPages = 0;
  searchTerm = '';
  filterStatus = '';
  filterType = '';

  statusColors: { [key: string]: string } = {
    'SUCCESS': '#28a745',
    'PENDING': '#ffc107',
    'FAILED': '#dc3545',
    'PARTIAL': '#fd7e14'
  };

  statusIcons: { [key: string]: string } = {
    'SUCCESS': 'fas fa-check-circle',
    'PENDING': 'fas fa-hourglass-half',
    'FAILED': 'fas fa-times-circle',
    'PARTIAL': 'fas fa-exclamation-triangle'
  };

  importTypes: string[] = [
    'SAMPLE_DATA',
    'MANUAL_IMPORT',
    'REGISTRATION',
    'MIGRATION',
    'SYNC'
  ];

  constructor(private organizationService: OrganizationService) {}

  ngOnInit(): void {
    this.loadImportHistory();
  }

  loadImportHistory(): void {
    this.loading = true;
    this.errorMessage = '';

    this.organizationService.getImportHistory(this.currentPage - 1, this.pageSize).subscribe({
      next: (response) => {
        if (response.content) {
          // Handle paginated response
          this.importHistoryRecords = response.content;
          this.totalRecords = response.totalElements;
          this.totalPages = response.totalPages;
        } else if (Array.isArray(response)) {
          // Handle array response
          this.importHistoryRecords = response;
          this.totalRecords = response.length;
          this.totalPages = Math.ceil(this.totalRecords / this.pageSize);
        }
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = 'Failed to load import history. Please try again.';
        console.error('Error loading import history:', error);
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.importHistoryRecords];

    // Filter by search term
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(record =>
        record.entityName.toLowerCase().includes(term) ||
        record.importType.toLowerCase().includes(term) ||
        record.source?.toLowerCase().includes(term) ||
        record.importedBy.toLowerCase().includes(term)
      );
    }

    // Filter by status
    if (this.filterStatus) {
      filtered = filtered.filter(record => record.importStatus === this.filterStatus);
    }

    // Filter by type
    if (this.filterType) {
      filtered = filtered.filter(record => record.importType === this.filterType);
    }

    this.filteredRecords = filtered;
  }

  onSearchChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.searchTerm = target.value;
    this.currentPage = 1;
    this.applyFilters();
  }

  onStatusFilterChange(status: string): void {
    this.filterStatus = status;
    this.currentPage = 1;
    this.applyFilters();
  }

  onTypeFilterChange(type: string): void {
    this.filterType = type;
    this.currentPage = 1;
    this.applyFilters();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.filterStatus = '';
    this.filterType = '';
    this.currentPage = 1;
    this.applyFilters();
  }

  getDurationFormatted(record: ImportHistory): string {
    if (!record.importStartTime || !record.importEndTime) {
      return 'N/A';
    }

    const start = new Date(record.importStartTime).getTime();
    const end = new Date(record.importEndTime).getTime();
    const durationMs = end - start;
    const durationSecs = Math.floor(durationMs / 1000);

    if (durationSecs < 60) {
      return `${durationSecs}s`;
    }
    const durationMins = Math.floor(durationSecs / 60);
    return `${durationMins}m ${durationSecs % 60}s`;
  }

  getStatusBadgeClass(status: string): string {
    return `badge-${status.toLowerCase()}`;
  }

  getStatusIcon(status: string): string {
    return this.statusIcons[status] || 'fas fa-info-circle';
  }

  getStatusColor(status: string): string {
    return this.statusColors[status] || '#6c757d';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(1, this.currentPage - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(this.totalPages, startPage + maxPagesToShow - 1);

    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(1, endPage - maxPagesToShow + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.loadImportHistory();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.goToPage(this.currentPage + 1);
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.goToPage(this.currentPage - 1);
    }
  }

  exportToCSV(): void {
    const headers = ['Entity Name', 'Import Type', 'Record Count', 'Status', 'Imported By', 'Start Time', 'End Time', 'Duration'];
    const rows = this.filteredRecords.map(record => [
      record.entityName,
      record.importType,
      record.recordCount.toString(),
      record.importStatus,
      record.importedBy,
      this.formatDate(record.importStartTime),
      record.importEndTime ? this.formatDate(record.importEndTime) : 'N/A',
      this.getDurationFormatted(record)
    ]);

    let csv = headers.join(',') + '\n';
    rows.forEach(row => {
      csv += row.map(cell => `"${cell}"`).join(',') + '\n';
    });

    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `import-history-${new Date().toISOString().split('T')[0]}.csv`;
    link.click();
  }

  getDisplayRecords(): ImportHistory[] {
    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredRecords.slice(start, end);
  }

  getRecordSummary(): string {
    const start = (this.currentPage - 1) * this.pageSize + 1;
    const end = Math.min(start + this.pageSize - 1, this.filteredRecords.length);
    return `Showing ${start} to ${end} of ${this.filteredRecords.length} records`;
  }
}
