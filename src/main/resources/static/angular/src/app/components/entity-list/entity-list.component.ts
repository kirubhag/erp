import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

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
  imports: [CommonModule, FormsModule, RouterModule],
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
  
  get totalRecords(): number {
    return this.pagination.totalItems;
  }

  ngOnInit() {
    this.updatePagination();
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
}