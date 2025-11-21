import { Component, Input, Output, EventEmitter, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { EntityAvatarComponent } from '../entity-avatar/entity-avatar.component';

/**
 * Grid column configuration for responsive display
 */
export interface GridColumn {
  key: string;
  label: string;
  type: 'text' | 'email' | 'badge' | 'date' | 'avatar' | 'custom' | 'currency' | 'percentage';
  sortable?: boolean;
  width?: string;
  customTemplate?: string;
  formatter?: (value: any, item?: any) => string;
}

/**
 * Grid item configuration
 */
export interface GridItem {
  id: string | number;
  [key: string]: any;
}

/**
 * Common responsive grid view component
 * Supports both card and table display modes
 * Adapts automatically to screen size
 */
@Component({
  selector: 'app-grid-view',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, EntityAvatarComponent],
  templateUrl: './grid-view.component.html',
  styleUrls: ['./grid-view.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GridViewComponent implements OnInit {
  @Input() title: string = 'Grid View';
  @Input() items: GridItem[] = [];
  @Input() columns: GridColumn[] = [];
  @Input() loading: boolean = false;
  @Input() displayMode: 'cards' | 'list' = 'cards';
  @Input() cardColumns: number = 3; // Number of columns on desktop
  @Input() showCheckbox: boolean = true;
  @Input() showAvatar: boolean = true;
  @Input() emptyMessage: string = 'No items found';
  @Input() errorMessage: string = '';
  @Input() selectedItems: Set<string | number> = new Set();
  @Input() enableSelection: boolean = true;
  @Input() hoverEffect: boolean = true;
  @Input() showActions: boolean = true;
  @Input() actionsTemplate?: any;
  @Input() entityType: string = '';
  @Input() organizationId?: number;

  @Output() itemClick = new EventEmitter<GridItem>();
  @Output() selectionChange = new EventEmitter<Set<string | number>>();
  @Output() actionClick = new EventEmitter<{ action: string; item: GridItem }>();

  displayModeInternal: 'cards' | 'list' = 'cards';
  allSelected: boolean = false;

  ngOnInit() {
    this.displayModeInternal = this.displayMode;
    this.updateAllSelectedState();
  }

  /**
   * Get responsive grid column classes
   */
  getGridColClasses(): string {
    const colClasses: { [key: number]: string } = {
      1: 'col-12',
      2: 'col-lg-6 col-md-6 col-sm-12',
      3: 'col-lg-4 col-md-6 col-sm-12',
      4: 'col-lg-3 col-md-6 col-sm-12',
      5: 'col-lg-2-4 col-md-6 col-sm-12'
    };
    return colClasses[this.cardColumns] || colClasses[3];
  }

  /**
   * Switch display mode
   */
  switchMode(mode: 'cards' | 'list') {
    this.displayModeInternal = mode;
  }

  /**
   * Get first column (usually the main identifier)
   */
  getFirstColumn(): GridColumn | null {
    return this.columns.length > 0 ? this.columns[0] : null;
  }

  /**
   * Get card display columns (2-3 columns max)
   */
  getCardColumns(): GridColumn[] {
    return this.columns.slice(1, 4);
  }

  /**
   * Get cell value with formatting
   */
  getCellValue(item: GridItem, column: GridColumn): string {
    const value = item[column.key];
    if (column.formatter) {
      return column.formatter(value, item);
    }
    return this.formatValue(value, column.type);
  }

  /**
   * Format value based on type
   */
  private formatValue(value: any, type: string): string {
    if (value === null || value === undefined) {
      return '';
    }

    switch (type) {
      case 'date':
        return new Date(value).toLocaleDateString();
      case 'currency':
        return new Intl.NumberFormat('en-US', {
          style: 'currency',
          currency: 'USD'
        }).format(value);
      case 'percentage':
        return (value * 100).toFixed(2) + '%';
      default:
        return String(value);
    }
  }

  /**
   * Generate avatar text from name
   */
  getAvatarText(text: string): string {
    return text ? text.substring(0, 2).toUpperCase() : '??';
  }

  /**
   * Generate avatar background class
   */
  getAvatarClass(text: string): string {
    const colors = [
      'bg-primary', 'bg-secondary', 'bg-success', 
      'bg-danger', 'bg-warning', 'bg-info'
    ];
    const index = text ? text.charCodeAt(0) % colors.length : 0;
    return colors[index];
  }

  /**
   * Get badge color class based on value
   */
  getBadgeClass(value: any): string {
    if (value === true || value === 'Active' || value === 'In-progress' || value === 'Yes') {
      return 'badge-success';
    } else if (value === false || value === 'Inactive' || value === 'No') {
      return 'badge-danger';
    }
    return 'badge-secondary';
  }

  /**
   * Toggle item selection
   */
  toggleItem(item: GridItem) {
    if (this.selectedItems.has(item.id)) {
      this.selectedItems.delete(item.id);
    } else {
      this.selectedItems.add(item.id);
    }
    this.updateAllSelectedState();
    this.selectionChange.emit(this.selectedItems);
  }

  /**
   * Toggle select all
   */
  toggleSelectAll() {
    if (this.allSelected) {
      this.selectedItems.clear();
    } else {
      this.items.forEach(item => this.selectedItems.add(item.id));
    }
    this.updateAllSelectedState();
    this.selectionChange.emit(this.selectedItems);
  }

  /**
   * Update all selected state
   */
  private updateAllSelectedState() {
    this.allSelected = this.items.length > 0 && 
                       this.selectedItems.size === this.items.length;
  }

  /**
   * Check if item is selected
   */
  isSelected(item: GridItem): boolean {
    return this.selectedItems.has(item.id);
  }

  /**
   * Handle card click
   */
  onCardClick(item: GridItem) {
    this.itemClick.emit(item);
  }

  /**
   * Handle action click
   */
  onActionClick(action: string, item: GridItem, event: Event) {
    event.stopPropagation();
    this.actionClick.emit({ action, item });
  }

  /**
   * Track by function for ngFor optimization
   */
  trackByFn(index: number, item: GridItem): any {
    return item.id;
  }

  /**
   * Get badge text
   */
  getBadgeText(value: any): string {
    if (typeof value === 'boolean') {
      return value ? 'Active' : 'Inactive';
    }
    return String(value);
  }

  /**
   * Truncate text to specified length
   */
  truncate(text: string, length: number = 50): string {
    if (!text) return '';
    return text.length > length ? text.substring(0, length) + '...' : text;
  }
}
