import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { GridViewComponent } from '../grid-view/grid-view.component';

export interface EntityColumn {
  key: string;
  label: string;
  type: 'text' | 'email' | 'badge' | 'date' | 'avatar' | 'custom';
  sortable?: boolean;
  width?: string;
  customTemplate?: string;
}

export interface EntityFilter {
  key: string;
  label: string;
  type: 'checkbox' | 'text' | 'select' | 'date';
  options?: { value: any; label: string }[];
  value?: any;
}

export interface EntityAction {
  label: string;
  icon: string;
  action: string;
  variant: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info';
}

export interface PaginationInfo {
  currentPage: number;
  itemsPerPage: number;
  totalItems: number;
  totalPages: number;
}

@Component({
  selector: 'app-entity-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, GridViewComponent],
  templateUrl: './entity-list.component.html',
  styleUrls: ['./entity-list.component.css']
})
export class EntityListComponent implements OnInit {
  @Input() title: string = 'Entity List';
  @Input() entityName: string = 'Item';
  @Input() entityNamePlural: string = 'Items';
  @Input() entityType: string = ''; // For navigation to detail page
  @Input() columns: EntityColumn[] = [];
  @Input() data: any[] = [];
  @Input() filters: EntityFilter[] = [];
  @Input() actions: EntityAction[] = [];
  @Input() pagination: PaginationInfo = {
    currentPage: 1,
    itemsPerPage: 100,
    totalItems: 0,
    totalPages: 1
  };
  @Input() loading: boolean = false;
  @Input() showBulkActions: boolean = true;
  @Input() showViewToggle: boolean = true;
  @Input() showAddButton: boolean = true;
  @Input() showFilters: boolean = true;
  @Input() userId?: number;
  @Input() organizationId?: number;

  @Output() searchChange = new EventEmitter<string>();
  @Output() filterChange = new EventEmitter<{ [key: string]: any }>();
  @Output() sortChange = new EventEmitter<{ column: string; direction: 'asc' | 'desc' }>();
  @Output() pageChange = new EventEmitter<number>();
  @Output() itemsPerPageChange = new EventEmitter<number>();
  @Output() actionClick = new EventEmitter<{ action: string; item?: any; selectedItems?: any[] }>();
  @Output() selectionChange = new EventEmitter<any[]>();
  @Output() rowClick = new EventEmitter<{ entityType: string; item: any }>();

  searchTerm: string = '';
  selectedItems: Set<any> = new Set();
  allSelected: boolean = false;
  sortColumn: string = '';
  sortDirection: 'asc' | 'desc' = 'asc';
  viewMode: 'table' | 'grid' = 'table';
  showTotalCount: boolean = false;
  entityFieldFilters: { [key: string]: boolean } = {};
  sidebarVisible: boolean = true;
  private savingViewMode: boolean = false;
  private savingItemsPerPage: boolean = false;
  private savingSidebarState: boolean = false;
  
  // Helper methods for template
  isTableView(): boolean {
    return this.viewMode === 'table';
  }

  isGridView(): boolean {
    return this.viewMode === 'grid';
  }

  toggleTableView() {
    this.viewMode = 'table';
    console.log('Switching to table view, userId:', this.userId, 'orgId:', this.organizationId);
    this.saveViewModePreference();
  }

  toggleGridView() {
    this.viewMode = 'grid';
    console.log('Switching to grid view, userId:', this.userId, 'orgId:', this.organizationId);
    this.saveViewModePreference();
  }
  
  get totalRecords(): number {
    return this.pagination.totalItems;
  }

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.updatePagination();
    this.loadUserPreferences();
    // Set initial sidebar visibility
    this.sidebarVisible = true;
  }

  /**
   * Load user preferences from database
   */
  private loadUserPreferences() {
    if (!this.userId || !this.organizationId) {
      console.log('No user/organization context for preferences');
      return;
    }

    console.log('Loading user preferences for user:', this.userId, 'org:', this.organizationId);
    
    this.http.get(`/api/user-settings/${this.userId}/${this.organizationId}`)
      .subscribe({
        next: (settings: any) => {
          console.log('User preferences loaded:', settings);
          
          // Load default list view from database
          if (settings.defaultListView) {
            this.viewMode = settings.defaultListView === 'card' ? 'grid' : 'table';
            console.log('Loaded viewMode:', this.viewMode);
          }
          
          // Load records per page from database
          if (settings.recordsPerPage && settings.recordsPerPage > 0) {
            this.pagination.itemsPerPage = settings.recordsPerPage;
            this.updatePagination();
            console.log('Loaded itemsPerPage:', this.pagination.itemsPerPage);
          }
          
          // Load sidebar visibility from database
          if (settings.listSidebarExpanded !== null && settings.listSidebarExpanded !== undefined) {
            this.sidebarVisible = settings.listSidebarExpanded === true || settings.listSidebarExpanded === 1;
            this.showFilters = this.sidebarVisible;
            console.log('Loaded sidebarVisible:', this.sidebarVisible);
          }
        },
        error: (err) => {
          console.warn('Could not load user preferences:', err);
        }
      });
  }

  /**
   * Save view mode preference to database
   */
  private saveViewModePreference() {
    if (!this.userId || !this.organizationId || this.savingViewMode) {
      console.log('Cannot save view mode - missing context or already saving', { 
        userId: this.userId, 
        organizationId: this.organizationId, 
        saving: this.savingViewMode 
      });
      return;
    }

    this.savingViewMode = true;
    const viewModeValue = this.viewMode === 'grid' ? 'card' : 'table';
    console.log('Saving view mode preference:', viewModeValue);
    
    this.http.put(
      `/api/user-settings/${this.userId}/${this.organizationId}`,
      { defaultListView: viewModeValue }
    ).subscribe({
      next: () => {
        console.log('View mode preference saved successfully:', viewModeValue);
        this.savingViewMode = false;
      },
      error: (err) => {
        console.error('Failed to save view mode preference:', err);
        this.savingViewMode = false;
      }
    });
  }

  /**
   * Save records per page preference to database
   */
  private saveItemsPerPagePreference() {
    if (!this.userId || !this.organizationId || this.savingItemsPerPage) {
      console.log('Cannot save items per page - missing context or already saving', {
        userId: this.userId,
        organizationId: this.organizationId,
        saving: this.savingItemsPerPage
      });
      return;
    }

    this.savingItemsPerPage = true;
    console.log('Saving items per page preference:', this.pagination.itemsPerPage);

    this.http.put(
      `/api/user-settings/${this.userId}/${this.organizationId}`,
      { recordsPerPage: this.pagination.itemsPerPage }
    ).subscribe({
      next: () => {
        console.log('Items per page preference saved successfully:', this.pagination.itemsPerPage);
        this.savingItemsPerPage = false;
      },
      error: (err) => {
        console.error('Failed to save items per page preference:', err);
        this.savingItemsPerPage = false;
      }
    });
  }

  onSearch() {
    this.searchChange.emit(this.searchTerm);
  }

  onFilterChange() {
    const filterValues: { [key: string]: any } = {};
    this.filters.forEach(filter => {
      if (filter.value !== undefined && filter.value !== null && filter.value !== '') {
        filterValues[filter.key] = filter.value;
      }
    });
    this.filterChange.emit(filterValues);
  }

  /**
   * Handle entity field filter changes
   */
  onEntityFieldFilterChange(fieldKey: string) {
    const activeFields = Object.keys(this.entityFieldFilters)
      .filter(key => this.entityFieldFilters[key]);
    
    console.log('Active entity field filters:', activeFields);
    
    // Emit filter change event with active fields
    this.filterChange.emit({ 
      entityFields: activeFields 
    });
  }

  onSort(column: EntityColumn) {
    if (!column.sortable) return;
    
    if (this.sortColumn === column.key) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column.key;
      this.sortDirection = 'asc';
    }
    
    this.sortChange.emit({ column: column.key, direction: this.sortDirection });
  }

  onPageChange(page: number) {
    if (page >= 1 && page <= this.pagination.totalPages) {
      this.pagination.currentPage = page;
      this.pageChange.emit(page);
    }
  }

  onItemsPerPageChange() {
    this.itemsPerPageChange.emit(this.pagination.itemsPerPage);
    this.saveItemsPerPagePreference();
    this.updatePagination();
  }

  toggleSelectAll() {
    if (this.allSelected) {
      this.selectedItems.clear();
    } else {
      this.data.forEach(item => this.selectedItems.add(item));
    }
    this.allSelected = !this.allSelected;
    this.selectionChange.emit(Array.from(this.selectedItems));
  }

  toggleSelectItem(item: any) {
    if (this.selectedItems.has(item)) {
      this.selectedItems.delete(item);
    } else {
      this.selectedItems.add(item);
    }
    this.allSelected = this.selectedItems.size === this.data.length;
    this.selectionChange.emit(Array.from(this.selectedItems));
  }

  isSelected(item: any): boolean {
    return this.selectedItems.has(item);
  }

  onAction(action: string, item?: any) {
    this.actionClick.emit({ 
      action, 
      item, 
      selectedItems: item ? undefined : Array.from(this.selectedItems)
    });
  }

  onBulkAction(action: string) {
    this.actionClick.emit({
      action,
      selectedItems: Array.from(this.selectedItems)
    });
  }

  onRowClick(item: any) {
    if (this.entityType) {
      this.rowClick.emit({ entityType: this.entityType, item });
    }
  }

  clearFilters() {
    this.filters.forEach(filter => {
      filter.value = filter.type === 'checkbox' ? false : '';
    });
    this.onFilterChange();
  }

  applyFilters() {
    this.onFilterChange();
  }

  toggleTotalRecords() {
    this.showTotalCount = !this.showTotalCount;
  }

  refreshData() {
    // Emit refresh event to parent component
    this.actionClick.emit({ action: 'refresh' });
    // For demo purposes, we'll just reload the current data
    window.location.reload();
  }

  getCellValue(item: any, column: EntityColumn): any {
    return item[column.key];
  }

  getAvatarText(text: string): string {
    return text ? text.substring(0, 2).toUpperCase() : '??';
  }

  getAvatarClass(text: string): string {
    const colors = ['bg-primary', 'bg-secondary', 'bg-success', 'bg-danger', 'bg-warning', 'bg-info'];
    const index = text ? text.charCodeAt(0) % colors.length : 0;
    return colors[index];
  }

  getPaginationPages(): number[] {
    const pages: number[] = [];
    const current = this.pagination.currentPage;
    const total = this.pagination.totalPages;
    
    // Always show first page
    if (current > 3) {
      pages.push(1);
      if (current > 4) {
        pages.push(-1); // Ellipsis
      }
    }
    
    // Show pages around current
    for (let i = Math.max(1, current - 2); i <= Math.min(total, current + 2); i++) {
      pages.push(i);
    }
    
    // Always show last page
    if (current < total - 2) {
      if (current < total - 3) {
        pages.push(-1); // Ellipsis
      }
      pages.push(total);
    }
    
    return pages;
  }

  private updatePagination() {
    this.pagination.totalPages = Math.ceil(this.pagination.totalItems / this.pagination.itemsPerPage);
  }

  toggleViewMode() {
    this.viewMode = this.viewMode === 'table' ? 'grid' : 'table';
    this.saveViewModePreference();
  }

  toggleSidebar() {
    this.sidebarVisible = !this.sidebarVisible;
    this.showFilters = this.sidebarVisible;
    this.saveSidebarVisibility();
  }

  /**
   * Save sidebar visibility state to database
   */
  private saveSidebarVisibility() {
    if (!this.userId || !this.organizationId || this.savingSidebarState) {
      return;
    }

    this.savingSidebarState = true;

    this.http.put(
      `/api/user-settings/${this.userId}/${this.organizationId}`,
      { listSidebarExpanded: this.sidebarVisible }
    ).subscribe({
      next: () => {
        console.log('Sidebar visibility saved:', this.sidebarVisible);
        this.savingSidebarState = false;
      },
      error: (err) => {
        console.error('Failed to save sidebar visibility:', err);
        this.savingSidebarState = false;
      }
    });
  }

  trackByFn(index: number, item: any): any {
    return item.id || index;
  }

  getEndItem(): number {
    return Math.min(this.pagination.currentPage * this.pagination.itemsPerPage, this.pagination.totalItems);
  }

  getFirstColumn(): EntityColumn | null {
    return this.columns.length > 0 ? this.columns[0] : null;
  }

  getFirstColumnValue(item: any): string {
    const firstColumn = this.getFirstColumn();
    return firstColumn ? (item[firstColumn.key] || '') : '';
  }

  getGridColumns(): EntityColumn[] {
    return this.columns.slice(1, 4);
  }

  /**
   * Handle grid view selection changes
   */
  onGridSelectionChange(selectedSet: Set<string | number>) {
    this.selectedItems = selectedSet;
    const selectedArray = Array.from(selectedSet).map(id => {
      return this.data.find(item => item.id === id);
    }).filter(item => item !== undefined);
    this.selectionChange.emit(selectedArray);
  }

  /**
   * Handle grid view action clicks
   */
  onActionClick(event: { action: string; item: any }) {
    this.actionClick.emit({ action: event.action, item: event.item });
  }
}