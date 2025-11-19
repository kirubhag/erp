# Grid View Component - Quick Reference

## Import
```typescript
import { GridViewComponent, GridColumn, GridItem } from './components/grid-view/grid-view.component';
```

## Minimal Example
```html
<app-grid-view
  [items]="users"
  [columns]="columns"
  (itemClick)="onUserClick($event)">
</app-grid-view>
```

## Column Types

| Type | Display | Example |
|------|---------|---------|
| `avatar` | Avatar + Name | ![JD] John Doe |
| `text` | Plain text | John Doe |
| `email` | Email link | john@example.com |
| `badge` | Status badge | 🟢 Active |
| `date` | Formatted date | Jan 15, 2023 |
| `currency` | $ Format | $1,299.99 |
| `percentage` | % Format | 95.50% |

## Column Definition
```typescript
{
  key: 'fieldName',           // Property name in item
  label: 'Display Name',      // Column header
  type: 'avatar',             // Data type
  sortable: true,             // Optional
  width: '200px',             // Optional
  formatter: (v, item) => v   // Optional custom formatting
}
```

## Input Properties
```typescript
[title]="'My List'"                    // Header title
[items]="dataArray"                    // Data to display
[columns]="columnConfig"               // Column definitions
[loading]="isLoading"                  // Show spinner
[displayMode]="'cards'"                // 'cards' or 'list'
[cardColumns]="3"                      // 1-5 columns on desktop
[showCheckbox]="true"                  // Selection boxes
[showAvatar]="true"                    // Avatar circles
[showActions]="true"                   // Action buttons
[emptyMessage]="'No items'"            // Empty state text
[selectedItems]="new Set()"            // Pre-selected items
[enableSelection]="true"               // Enable checkboxes
[hoverEffect]="true"                   // Card hover effects
```

## Output Events
```typescript
(itemClick)="onItemClick($event)"           // GridItem
(selectionChange)="onSelectionChange($event)" // Set<id>
(actionClick)="onActionClick($event)"       // {action, item}
```

## Action Events
The `actionClick` event includes:
- `action`: 'view' | 'edit' | 'more'
- `item`: The selected GridItem

```typescript
onActionClick(event: { action: string; item: GridItem }) {
  switch (event.action) {
    case 'view':
      this.router.navigate(['/detail', event.item.id]);
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

## Complete Component Template
```typescript
@Component({
  selector: 'app-my-list',
  standalone: true,
  imports: [GridViewComponent],
  template: `
    <app-grid-view
      [title]="'Users'"
      [items]="users"
      [columns]="columns"
      [loading]="loading"
      [displayMode]="displayMode"
      [cardColumns]="3"
      [showCheckbox]="true"
      (itemClick)="onItemClick($event)"
      (actionClick)="onActionClick($event)"
      (selectionChange)="onSelectionChange($event)">
    </app-grid-view>
  `
})
export class MyListComponent implements OnInit {
  users: GridItem[] = [];
  loading = false;
  displayMode: 'cards' | 'list' = 'cards';
  selectedItems: Set<string | number> = new Set();

  columns: GridColumn[] = [
    { key: 'name', label: 'Name', type: 'avatar', sortable: true },
    { key: 'email', label: 'Email', type: 'email' },
    { key: 'status', label: 'Status', type: 'badge' }
  ];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.api.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  onItemClick(item: GridItem) {
    // Handle item click
  }

  onActionClick(event: { action: string; item: GridItem }) {
    // Handle action
  }

  onSelectionChange(selected: Set<string | number>) {
    this.selectedItems = selected;
  }
}
```

## Custom Column Formatter
```typescript
{
  key: 'salary',
  label: 'Salary',
  type: 'currency',
  formatter: (value, item) => {
    return `$${(value / 1000).toFixed(0)}K`;
  }
}
```

## Responsive Breakpoints
- **Desktop (1200px+)**: 3-4 columns, full features
- **Tablet (768-1200px)**: 2 columns, compact
- **Mobile (480-768px)**: 1 column, list view preferred
- **Small (<480px)**: List view forced, minimal spacing

## Styling with CSS Variables
```css
:root {
  --app-primary: #0891B2;           /* Main color */
  --app-primary-dark: #0369A1;      /* Dark variant */
  --border-color: #e0e0e0;          /* Borders */
  --grid-gap: 1rem;                 /* Card spacing */
}
```

## Display Modes
```typescript
// Cards view (default)
[displayMode]="'cards'"   // Responsive grid

// List view (table)
[displayMode]="'list'"    // Responsive table
```

## Selection Example
```typescript
// Select specific items
const selected = new Set([1, 2, 3]);
this.selectedItems = selected;

// Get selected items
const selectedIds = Array.from(this.selectedItems);
```

## Data Interface
```typescript
interface GridItem {
  id: string | number;  // Required
  [key: string]: any;   // Other properties
}

interface GridColumn {
  key: string;
  label: string;
  type: 'text' | 'email' | 'badge' | 'date' | 'avatar' | 'currency' | 'percentage';
  sortable?: boolean;
  width?: string;
  formatter?: (value: any, item?: any) => string;
}
```

## Common Patterns

### Loading Data
```typescript
ngOnInit() {
  this.loading = true;
  this.service.getItems().subscribe({
    next: (data) => {
      this.items = data.map(item => ({
        ...item,
        id: item.id || item.itemId  // Ensure id exists
      }));
      this.loading = false;
    },
    error: () => this.loading = false
  });
}
```

### Filtering
```typescript
get filteredItems() {
  return this.items.filter(item => 
    item.name.toLowerCase().includes(this.searchTerm.toLowerCase())
  );
}

<app-grid-view [items]="filteredItems" ...></app-grid-view>
```

### Pagination
```typescript
get currentPageItems() {
  const start = (this.page - 1) * this.pageSize;
  return this.items.slice(start, start + this.pageSize);
}

<app-grid-view [items]="currentPageItems" ...></app-grid-view>
```

### Navigation
```typescript
onItemClick(item: GridItem) {
  this.router.navigate(['/detail', item.id]);
}
```

### Bulk Actions
```typescript
onSelectionChange(selected: Set<string | number>) {
  this.selectedItems = selected;
  // Enable bulk action buttons
  this.canBulkDelete = selected.size > 0;
}

deleteSelected() {
  const ids = Array.from(this.selectedItems);
  this.service.deleteMultiple(ids).subscribe(() => {
    this.loadData();
  });
}
```

## Testing
```typescript
// Create component
const component = TestBed.createComponent(GridViewComponent).componentInstance;

// Set inputs
component.items = mockData;
component.columns = mockColumns;

// Trigger events
component.toggleItem(item);
component.itemClick.emit(item);

// Check outputs
expect(component.selectedItems.has(item.id)).toBe(true);
```

## Performance Tips
1. Use `trackBy` function in parent ngFor loops
2. Implement OnPush change detection where possible
3. Lazy load component if not immediately needed
4. Pre-process data before passing to component
5. Use pagination for large datasets
6. Implement virtual scrolling for 1000+ items

## Accessibility
- Component is WCAG AA compliant
- All interactive elements are keyboard accessible
- Color contrast ratios meet standards
- ARIA labels provided
- Screen reader friendly

## Browser Support
- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)
- Mobile browsers (iOS Safari, Chrome Android)

## File Location
```
/src/main/resources/static/angular/src/app/components/grid-view/
```

## Related Files
- `README.md` - Full documentation
- `USAGE_GUIDE.md` - Detailed examples
- `INTEGRATION_GUIDE.md` - Migration guide
- `grid-view.component.spec.ts` - Unit tests

---

**For detailed documentation, see README.md in the grid-view component folder**
