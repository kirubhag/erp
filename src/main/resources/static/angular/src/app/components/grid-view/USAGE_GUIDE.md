/**
 * Grid View Component Usage Guide
 * 
 * A reusable, responsive grid/list view component for displaying entities
 * across the application. Supports both card and table layouts with
 * automatic responsive behavior.
 * 
 * FEATURES:
 * - Responsive card grid (auto-adjusts columns based on screen size)
 * - Table list view with sortable columns
 * - Checkbox selection for bulk actions
 * - Multiple display modes (cards/list)
 * - Avatar generation from text
 * - Badge support for status fields
 * - Custom formatting for different data types
 * - Loading and empty states
 * - Configurable columns and actions
 * 
 * BASIC USAGE:
 * 
 * 1. Import GridViewComponent:
 * 
 *    import { GridViewComponent, GridColumn, GridItem } from './grid-view.component';
 * 
 * 2. Add to component template:
 * 
 *    <app-grid-view
 *      [title]="'Users'"
 *      [items]="users"
 *      [columns]="userColumns"
 *      [loading]="isLoading"
 *      [displayMode]="'cards'"
 *      (itemClick)="onUserClick($event)"
 *      (actionClick)="onActionClick($event)">
 *    </app-grid-view>
 * 
 * 3. Configure in component class:
 * 
 *    export class UserListComponent {
 *      users: GridItem[] = [];
 *      isLoading = false;
 *      
 *      userColumns: GridColumn[] = [
 *        { key: 'name', label: 'Name', type: 'avatar', sortable: true },
 *        { key: 'email', label: 'Email', type: 'email' },
 *        { key: 'status', label: 'Status', type: 'badge' },
 *        { key: 'createdDate', label: 'Created', type: 'date' }
 *      ];
 *    }
 */

// ============================================================================
// EXAMPLE 1: Basic User List with Cards
// ============================================================================

import { Component, OnInit } from '@angular/core';
import { GridViewComponent, GridColumn, GridItem } from '../grid-view/grid-view.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-grid',
  standalone: true,
  imports: [GridViewComponent, CommonModule],
  template: `
    <app-grid-view
      [title]="'Team Members'"
      [items]="users"
      [columns]="columns"
      [loading]="loading"
      [displayMode]="displayMode"
      [cardColumns]="3"
      [showCheckbox]="true"
      [showAvatar]="true"
      [showActions]="true"
      (itemClick)="onItemClick($event)"
      (actionClick)="onActionClick($event)"
      (selectionChange)="onSelectionChange($event)">
    </app-grid-view>
  `
})
export class UserGridComponent implements OnInit {
  users: GridItem[] = [];
  loading = false;
  displayMode: 'cards' | 'list' = 'cards';
  selectedItems: Set<string | number> = new Set();

  columns: GridColumn[] = [
    {
      key: 'name',
      label: 'Name',
      type: 'avatar',
      sortable: true,
      width: '200px'
    },
    {
      key: 'email',
      label: 'Email',
      type: 'email'
    },
    {
      key: 'department',
      label: 'Department',
      type: 'text'
    },
    {
      key: 'status',
      label: 'Status',
      type: 'badge'
    }
  ];

  ngOnInit() {
    this.loadUsers();
  }

  private loadUsers() {
    this.loading = true;
    // Simulate API call
    setTimeout(() => {
      this.users = [
        {
          id: 1,
          name: 'John Doe',
          email: 'john@example.com',
          department: 'Engineering',
          status: 'Active'
        },
        {
          id: 2,
          name: 'Jane Smith',
          email: 'jane@example.com',
          department: 'Marketing',
          status: 'Active'
        },
        {
          id: 3,
          name: 'Mike Johnson',
          email: 'mike@example.com',
          department: 'Sales',
          status: 'Inactive'
        }
      ];
      this.loading = false;
    }, 1000);
  }

  onItemClick(item: GridItem) {
    console.log('Item clicked:', item);
  }

  onActionClick(event: { action: string; item: GridItem }) {
    console.log('Action:', event.action, 'Item:', event.item);
  }

  onSelectionChange(selected: Set<string | number>) {
    this.selectedItems = selected;
  }
}

// ============================================================================
// EXAMPLE 2: Products Grid with Custom Formatting
// ============================================================================

@Component({
  selector: 'app-product-grid',
  standalone: true,
  imports: [GridViewComponent, CommonModule],
  template: `
    <app-grid-view
      [title]="'Products'"
      [items]="products"
      [columns]="productColumns"
      [loading]="loading"
      [displayMode]="'cards'"
      [cardColumns]="4"
      [emptyMessage]="'No products found'"
      (itemClick)="viewProduct($event)"
      (actionClick)="handleProductAction($event)">
    </app-grid-view>
  `
})
export class ProductGridComponent implements OnInit {
  products: GridItem[] = [];
  loading = false;

  productColumns: GridColumn[] = [
    {
      key: 'productName',
      label: 'Product',
      type: 'text'
    },
    {
      key: 'price',
      label: 'Price',
      type: 'currency'
    },
    {
      key: 'inStock',
      label: 'Stock Status',
      type: 'badge'
    },
    {
      key: 'discount',
      label: 'Discount',
      type: 'percentage'
    }
  ];

  ngOnInit() {
    this.loadProducts();
  }

  private loadProducts() {
    this.products = [
      {
        id: 1,
        productName: 'Laptop Pro',
        price: 1299.99,
        inStock: true,
        discount: 0.1
      },
      {
        id: 2,
        productName: 'Wireless Mouse',
        price: 29.99,
        inStock: true,
        discount: 0.05
      },
      {
        id: 3,
        productName: 'USB-C Cable',
        price: 15.99,
        inStock: false,
        discount: 0
      }
    ];
  }

  viewProduct(product: GridItem) {
    console.log('Viewing product:', product);
  }

  handleProductAction(event: { action: string; item: GridItem }) {
    switch (event.action) {
      case 'edit':
        console.log('Edit product:', event.item);
        break;
      case 'view':
        console.log('View product:', event.item);
        break;
      case 'more':
        console.log('More options for:', event.item);
        break;
    }
  }
}

// ============================================================================
// EXAMPLE 3: Students List with Custom Formatting
// ============================================================================

@Component({
  selector: 'app-student-grid',
  standalone: true,
  imports: [GridViewComponent, CommonModule],
  template: `
    <app-grid-view
      [title]="'Students'"
      [items]="students"
      [columns]="studentColumns"
      [loading]="loading"
      [displayMode]="'list'"
      [cardColumns]="3"
      [showCheckbox]="true"
      (itemClick)="viewStudent($event)"
      (actionClick)="handleStudentAction($event)">
    </app-grid-view>
  `
})
export class StudentGridComponent implements OnInit {
  students: GridItem[] = [];
  loading = false;

  studentColumns: GridColumn[] = [
    {
      key: 'rollNumber',
      label: 'Roll Number',
      type: 'text',
      width: '120px'
    },
    {
      key: 'studentName',
      label: 'Student Name',
      type: 'avatar',
      sortable: true
    },
    {
      key: 'email',
      label: 'Email',
      type: 'email'
    },
    {
      key: 'grade',
      label: 'Grade',
      type: 'badge'
    },
    {
      key: 'gpa',
      label: 'GPA',
      type: 'percentage'
    },
    {
      key: 'enrollmentDate',
      label: 'Enrolled',
      type: 'date'
    }
  ];

  ngOnInit() {
    this.loadStudents();
  }

  private loadStudents() {
    this.students = [
      {
        id: 1,
        rollNumber: 'STU001',
        studentName: 'Alice Johnson',
        email: 'alice@school.edu',
        grade: 'A',
        gpa: 0.95,
        enrollmentDate: new Date('2023-01-15')
      },
      {
        id: 2,
        rollNumber: 'STU002',
        studentName: 'Bob Smith',
        email: 'bob@school.edu',
        grade: 'B',
        gpa: 0.85,
        enrollmentDate: new Date('2023-01-15')
      },
      {
        id: 3,
        rollNumber: 'STU003',
        studentName: 'Carol Williams',
        email: 'carol@school.edu',
        grade: 'A',
        gpa: 0.92,
        enrollmentDate: new Date('2023-01-15')
      }
    ];
  }

  viewStudent(student: GridItem) {
    console.log('Viewing student:', student);
  }

  handleStudentAction(event: { action: string; item: GridItem }) {
    switch (event.action) {
      case 'edit':
        console.log('Edit student:', event.item);
        break;
      case 'view':
        console.log('View student details:', event.item);
        break;
      case 'more':
        // Show context menu with more options
        break;
    }
  }
}

// ============================================================================
// COLUMN CONFIGURATION GUIDE
// ============================================================================

/**
 * GridColumn interface defines how to display each data column
 * 
 * Properties:
 * - key: The property name from the item object
 * - label: Display label for the column header
 * - type: Data type for formatting
 *   - 'text': Plain text
 *   - 'email': Email link
 *   - 'badge': Status badge
 *   - 'date': Formatted date
 *   - 'avatar': With avatar circle
 *   - 'currency': Formatted currency
 *   - 'percentage': Percentage format
 *   - 'custom': Custom template
 * - sortable: Can column be sorted (optional, default false)
 * - width: Column width in table view (optional)
 * - formatter: Custom function to format value (optional)
 *   Example: formatter: (value, item) => value.toUpperCase()
 */

// Example column configurations:
const dateColumn: GridColumn = {
  key: 'createdAt',
  label: 'Created Date',
  type: 'date',
  sortable: true,
  width: '150px'
};

const emailColumn: GridColumn = {
  key: 'contactEmail',
  label: 'Contact Email',
  type: 'email',
  sortable: false
};

const statusColumn: GridColumn = {
  key: 'status',
  label: 'Status',
  type: 'badge',
  formatter: (value) => {
    return value === 'active' ? 'Active' : 'Inactive';
  }
};

const currencyColumn: GridColumn = {
  key: 'totalSales',
  label: 'Total Sales',
  type: 'currency',
  formatter: (value) => `$${value.toFixed(2)}`
};

const percentageColumn: GridColumn = {
  key: 'completionRate',
  label: 'Completion',
  type: 'percentage',
  sortable: true
};

const customColumn: GridColumn = {
  key: 'customField',
  label: 'Custom Display',
  type: 'custom',
  formatter: (value, item) => {
    // Custom formatting logic
    return `${item.name} - ${value}`;
  }
};

// ============================================================================
// INPUT PROPERTIES REFERENCE
// ============================================================================

/**
 * @Input() title: string
 * Header title for the grid
 * Default: 'Grid View'
 * 
 * @Input() items: GridItem[]
 * Array of items to display
 * Default: []
 * 
 * @Input() columns: GridColumn[]
 * Column configuration array
 * Default: []
 * 
 * @Input() loading: boolean
 * Show loading spinner when true
 * Default: false
 * 
 * @Input() displayMode: 'cards' | 'list'
 * Initial display mode
 * Default: 'cards'
 * 
 * @Input() cardColumns: number
 * Number of columns in card view (on desktop)
 * Default: 3
 * Options: 1, 2, 3, 4, 5
 * 
 * @Input() showCheckbox: boolean
 * Show selection checkboxes
 * Default: true
 * 
 * @Input() showAvatar: boolean
 * Show avatar circles for names
 * Default: true
 * 
 * @Input() showActions: boolean
 * Show action buttons
 * Default: true
 * 
 * @Input() emptyMessage: string
 * Message when no items exist
 * Default: 'No items found'
 * 
 * @Input() errorMessage: string
 * Error message to display
 * Default: ''
 * 
 * @Input() selectedItems: Set<string | number>
 * Set of selected item IDs
 * Default: new Set()
 * 
 * @Input() enableSelection: boolean
 * Enable item selection
 * Default: true
 * 
 * @Input() hoverEffect: boolean
 * Enable card hover effect
 * Default: true
 */

// ============================================================================
// OUTPUT EVENTS REFERENCE
// ============================================================================

/**
 * @Output() itemClick
 * Emitted when user clicks on an item/card
 * Event: GridItem
 * 
 * @Output() selectionChange
 * Emitted when selection changes
 * Event: Set<string | number>
 * 
 * @Output() actionClick
 * Emitted when action button is clicked
 * Event: { action: string; item: GridItem }
 * Actions: 'view', 'edit', 'more'
 */

// ============================================================================
// RESPONSIVE BREAKPOINTS
// ============================================================================

/**
 * Component automatically adapts to screen sizes:
 * 
 * Desktop (>992px):
 * - Cards: Display 3-4 columns (configurable)
 * - Padding: 1rem
 * - Card height: 350px
 * 
 * Tablet (768px-992px):
 * - Cards: Display 2 columns
 * - Padding: 0.75rem
 * - Card height: 300px
 * 
 * Mobile (<768px):
 * - Cards: Display 1 column
 * - List view preferred
 * - Padding: 0.5rem
 * - Compact spacing
 * 
 * Very Small Mobile (<480px):
 * - Card minimum: 260px
 * - Hide grid view, show list view
 * - Minimal padding and spacing
 */
