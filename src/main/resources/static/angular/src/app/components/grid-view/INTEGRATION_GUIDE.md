# Grid View Integration Guide

How to integrate the new `GridViewComponent` into existing entity list components.

## Quick Migration Path

### Before: Using EntityListComponent
```typescript
<app-entity-list
  [title]="'Users'"
  [entityName]="'User'"
  [entityNamePlural]="'Users'"
  [columns]="columns"
  [data]="users"
  [pagination]="pagination"
  [loading]="loading">
</app-entity-list>
```

### After: Using GridViewComponent
```typescript
<app-grid-view
  [title]="'Users'"
  [items]="users"
  [columns]="gridColumns"
  [loading]="loading"
  [displayMode]="'cards'"
  (itemClick)="onUserClick($event)"
  (actionClick)="onActionClick($event)">
</app-grid-view>
```

## Step-by-Step Migration

### Step 1: Import the Component

```typescript
import { GridViewComponent, GridColumn, GridItem } from '../grid-view/grid-view.component';

@Component({
  imports: [
    GridViewComponent,
    CommonModule,
    FormsModule
    // ... other imports
  ]
})
export class UserListComponent {
  // ...
}
```

### Step 2: Convert Column Format

**Old EntityColumn:**
```typescript
const columns: EntityColumn[] = [
  {
    key: 'name',
    label: 'Full Name',
    type: 'text',
    sortable: true,
    width: '200px'
  },
  {
    key: 'email',
    label: 'Email Address',
    type: 'email'
  }
];
```

**New GridColumn:**
```typescript
const gridColumns: GridColumn[] = [
  {
    key: 'name',
    label: 'Full Name',
    type: 'avatar',  // Changed for better display
    sortable: true,
    width: '200px'
  },
  {
    key: 'email',
    label: 'Email Address',
    type: 'email'
  }
];
```

### Step 3: Transform Data Format

Ensure items have at least an `id` property (required by GridItem interface):

```typescript
// Make sure each item has an id
const users: GridItem[] = userData.map(user => ({
  ...user,
  id: user.id || user.userId // Ensure id exists
}));
```

### Step 4: Update Template

Replace the old component with the new one:

```html
<!-- OLD -->
<app-entity-list
  [title]="'Users'"
  [columns]="columns"
  [data]="users"
  [loading]="loading"
  (rowClick)="onRowClick($event)">
</app-entity-list>

<!-- NEW -->
<app-grid-view
  [title]="'Users'"
  [items]="users"
  [columns]="gridColumns"
  [loading]="loading"
  [displayMode]="displayMode"
  [cardColumns]="3"
  (itemClick)="onItemClick($event)"
  (actionClick)="onActionClick($event)">
</app-grid-view>
```

### Step 5: Update Event Handlers

```typescript
// OLD event handler
onRowClick(event: { entityType: string; item: any }) {
  this.router.navigate(['/users', event.item.id]);
}

// NEW event handler
onItemClick(item: GridItem) {
  this.router.navigate(['/users', item.id]);
}

onActionClick(event: { action: string; item: GridItem }) {
  switch (event.action) {
    case 'view':
      this.router.navigate(['/users', event.item.id]);
      break;
    case 'edit':
      this.openEditDialog(event.item);
      break;
    case 'more':
      this.showContextMenu(event.item);
      break;
  }
}
```

## Component Mapping Reference

| Feature | EntityListComponent | GridViewComponent |
|---------|-------------------|-------------------|
| Display Mode Toggle | ✅ Table/Grid | ✅ Table/Grid |
| Responsive | ✅ | ✅ Enhanced |
| Selection | ✅ Bulk actions | ✅ Checkboxes |
| Pagination | ✅ Built-in | ⚠️ Parent-managed |
| Filtering | ✅ Sidebar | ⚠️ Parent-managed |
| Search | ✅ Built-in | ⚠️ Parent-managed |
| Sorting | ✅ Built-in | ⚠️ Column sortable flag |
| Custom Actions | ✅ | ✅ view/edit/more |
| Preferences (DB) | ✅ | ⚠️ Parent-managed |
| User Settings | ✅ Integrated | ⚠️ Parent component |
| Avatar Display | ⚠️ Limited | ✅ Full support |
| Data Type Formatting | ✅ | ✅ Enhanced |

⚠️ = Parent component responsibility

## Entity-Specific Integration Examples

### Example 1: User Management

```typescript
import { Component, OnInit } from '@angular/core';
import { GridViewComponent, GridColumn, GridItem } from '../grid-view/grid-view.component';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [GridViewComponent],
  template: `
    <app-grid-view
      [title]="'User Management'"
      [items]="users"
      [columns]="columns"
      [loading]="loading"
      [displayMode]="'cards'"
      [cardColumns]="3"
      (itemClick)="viewUser($event)"
      (actionClick)="handleUserAction($event)">
    </app-grid-view>
  `
})
export class UserManagementComponent implements OnInit {
  users: GridItem[] = [];
  loading = false;

  columns: GridColumn[] = [
    {
      key: 'fullName',
      label: 'User',
      type: 'avatar',
      sortable: true
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

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.loadUsers();
  }

  private loadUsers() {
    this.loading = true;
    this.userService.getAll().subscribe({
      next: (data) => {
        this.users = data.map(user => ({
          ...user,
          id: user.userId || user.id
        }));
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  viewUser(user: GridItem) {
    // Navigate to user detail
  }

  handleUserAction(event: { action: string; item: GridItem }) {
    switch (event.action) {
      case 'view':
        this.viewUser(event.item);
        break;
      case 'edit':
        // Open edit dialog
        break;
      case 'more':
        // Show menu
        break;
    }
  }
}
```

### Example 2: Student List

```typescript
@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [GridViewComponent],
  template: `
    <app-grid-view
      [title]="'Students'"
      [items]="students"
      [columns]="studentColumns"
      [loading]="isLoading"
      [displayMode]="displayMode"
      [cardColumns]="4"
      [showCheckbox]="true"
      (itemClick)="onStudentClick($event)"
      (selectionChange)="onSelectionChange($event)">
    </app-grid-view>
  `
})
export class StudentListComponent implements OnInit {
  students: GridItem[] = [];
  isLoading = false;
  displayMode: 'cards' | 'list' = 'cards';

  studentColumns: GridColumn[] = [
    {
      key: 'studentName',
      label: 'Name',
      type: 'avatar'
    },
    {
      key: 'rollNumber',
      label: 'Roll No',
      type: 'text'
    },
    {
      key: 'email',
      label: 'Email',
      type: 'email'
    },
    {
      key: 'gpa',
      label: 'GPA',
      type: 'percentage'
    }
  ];

  constructor(private studentService: StudentService) {}

  ngOnInit() {
    this.loadStudents();
  }

  private loadStudents() {
    this.isLoading = true;
    this.studentService.getStudents().subscribe({
      next: (data) => {
        this.students = data;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  onStudentClick(student: GridItem) {
    // Navigate to student profile
  }

  onSelectionChange(selected: Set<string | number>) {
    console.log('Selected students:', Array.from(selected));
  }
}
```

### Example 3: Product Inventory

```typescript
@Component({
  selector: 'app-product-inventory',
  standalone: true,
  imports: [GridViewComponent],
  template: `
    <app-grid-view
      [title]="'Products'"
      [items]="products"
      [columns]="productColumns"
      [loading]="loading"
      [displayMode]="'cards'"
      [cardColumns]="4"
      (itemClick)="viewProduct($event)"
      (actionClick)="handleAction($event)">
    </app-grid-view>
  `
})
export class ProductInventoryComponent implements OnInit {
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
      key: 'stock',
      label: 'In Stock',
      type: 'badge'
    },
    {
      key: 'discount',
      label: 'Discount',
      type: 'percentage'
    }
  ];

  constructor(private productService: ProductService) {}

  ngOnInit() {
    this.loadProducts();
  }

  private loadProducts() {
    this.loading = true;
    this.productService.getAll().subscribe({
      next: (data) => {
        this.products = data.map(p => ({
          ...p,
          id: p.productId || p.id
        }));
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  viewProduct(product: GridItem) {
    // Show product details
  }

  handleAction(event: { action: string; item: GridItem }) {
    // Handle actions
  }
}
```

## Migration Checklist

- [ ] Import GridViewComponent
- [ ] Convert EntityColumn[] to GridColumn[]
- [ ] Ensure all items have `id` property
- [ ] Update template HTML
- [ ] Update event handlers
- [ ] Test responsive layout
- [ ] Test card/list view toggle
- [ ] Test selection checkboxes
- [ ] Test action buttons
- [ ] Update any CSS customizations
- [ ] Test on mobile/tablet
- [ ] Remove old EntityListComponent reference

## Features to Migrate Separately

Some features from EntityListComponent need to be handled at the parent component level:

### Pagination
Parent component should manage pagination:
```typescript
@Component({
  template: `
    <app-grid-view
      [items]="currentPageItems"
      [columns]="columns"
      (itemClick)="onItemClick($event)">
    </app-grid-view>
    
    <div class="pagination">
      <button (click)="goToPage(page - 1)">Previous</button>
      <span>Page {{currentPage}} of {{totalPages}}</span>
      <button (click)="goToPage(page + 1)">Next</button>
    </div>
  `
})
export class MyListComponent {
  currentPage = 1;
  itemsPerPage = 50;
  totalItems = 0;
  
  get totalPages() {
    return Math.ceil(this.totalItems / this.itemsPerPage);
  }
  
  get currentPageItems() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.allItems.slice(start, start + this.itemsPerPage);
  }
}
```

### Filtering
Parent component should manage filters:
```typescript
@Component({
  template: `
    <div class="filters">
      <input [(ngModel)]="searchTerm" placeholder="Search...">
      <select [(ngModel)]="statusFilter">
        <option value="">All</option>
        <option value="active">Active</option>
        <option value="inactive">Inactive</option>
      </select>
    </div>
    
    <app-grid-view
      [items]="filteredItems"
      [columns]="columns">
    </app-grid-view>
  `
})
export class MyListComponent implements OnInit {
  searchTerm = '';
  statusFilter = '';
  allItems: GridItem[] = [];

  get filteredItems() {
    return this.allItems.filter(item => {
      const matchSearch = !this.searchTerm || 
        item.name?.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchStatus = !this.statusFilter || 
        item.status === this.statusFilter;
      return matchSearch && matchStatus;
    });
  }
}
```

### User Preferences
Consider handling view preferences at parent or service level:
```typescript
export class ViewPreferencesService {
  getPreferences(userId: number, org: number) {
    return this.http.get(`/api/preferences/${userId}/${org}`);
  }
  
  saveDisplayMode(userId: number, org: number, mode: 'cards' | 'list') {
    return this.http.put(`/api/preferences/${userId}/${org}`, { displayMode: mode });
  }
}

// In component
constructor(private prefs: ViewPreferencesService) {}

ngOnInit() {
  this.prefs.getPreferences(this.userId, this.orgId).subscribe(prefs => {
    this.displayMode = prefs.displayMode;
  });
}

onDisplayModeChange(mode: 'cards' | 'list') {
  this.displayMode = mode;
  this.prefs.saveDisplayMode(this.userId, this.orgId, mode).subscribe();
}
```

## Benefits of Migration

✅ **Cleaner Code** - Less logic in component
✅ **Better UX** - Enhanced responsive design  
✅ **Flexibility** - Customize filter/search in parent
✅ **Reusability** - Use same grid for different entities
✅ **Performance** - OnPush change detection
✅ **Maintainability** - Centralized grid logic

---

For detailed API documentation, see [README.md](./README.md)
For usage examples, see [USAGE_GUIDE.md](./USAGE_GUIDE.md)
