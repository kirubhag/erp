# Grid View Component

A reusable, responsive grid/list view component for displaying entities throughout the ERP application.

## Features

✨ **Core Features:**
- 📱 **Fully Responsive** - Automatically adapts to all screen sizes
- 🎨 **Dual Display Modes** - Switch between card grid and table list views
- ✅ **Selection Support** - Checkbox selection for bulk operations
- 🎯 **Smart Avatars** - Auto-generated avatar circles with initials
- 📊 **Multiple Data Types** - Support for text, email, badges, dates, currency, percentages
- 🔄 **Loading & Empty States** - Built-in visual feedback
- ⚙️ **Highly Configurable** - Customize columns, actions, and display options
- 🎭 **Type Safe** - Fully typed with TypeScript interfaces
- ♿ **Accessible** - WCAG compliant with proper ARIA labels

## Installation

The component is standalone and ready to use. Simply import it into your module or component:

```typescript
import { GridViewComponent } from './components/grid-view/grid-view.component';

@Component({
  imports: [GridViewComponent]
})
export class MyComponent {
  // ...
}
```

## Basic Usage

### 1. Define Columns

```typescript
const columns: GridColumn[] = [
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
    key: 'status',
    label: 'Status',
    type: 'badge'
  },
  {
    key: 'joinDate',
    label: 'Joined',
    type: 'date'
  }
];
```

### 2. Prepare Data

```typescript
const items: GridItem[] = [
  {
    id: 1,
    name: 'John Doe',
    email: 'john@example.com',
    status: 'Active',
    joinDate: '2023-01-15'
  },
  // ... more items
];
```

### 3. Add to Template

```html
<app-grid-view
  [title]="'Users'"
  [items]="users"
  [columns]="columns"
  [loading]="isLoading"
  [displayMode]="'cards'"
  [cardColumns]="3"
  [showCheckbox]="true"
  [showAvatar]="true"
  [showActions]="true"
  (itemClick)="onItemClick($event)"
  (actionClick)="onActionClick($event)"
  (selectionChange)="onSelectionChange($event)">
</app-grid-view>
```

### 4. Handle Events

```typescript
export class MyComponent {
  onItemClick(item: GridItem) {
    // Navigate to detail view
    this.router.navigate(['/users', item.id]);
  }

  onActionClick(event: { action: string; item: GridItem }) {
    switch (event.action) {
      case 'view':
        // Show detail view
        break;
      case 'edit':
        // Show edit dialog
        break;
      case 'more':
        // Show context menu
        break;
    }
  }

  onSelectionChange(selectedItems: Set<string | number>) {
    console.log('Selected:', Array.from(selectedItems));
  }
}
```

## Column Types

| Type | Description | Example |
|------|-------------|---------|
| `text` | Plain text | `John Doe` |
| `email` | Email link | `john@example.com` |
| `avatar` | Avatar with initials + text | Avatar circle with "JD" + "John Doe" |
| `badge` | Status badge | Green badge "Active" |
| `date` | Formatted date | `Jan 15, 2023` |
| `currency` | Currency format | `$1,299.99` |
| `percentage` | Percentage format | `95.50%` |
| `custom` | Custom formatter function | User-defined |

## Component Inputs

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `title` | string | 'Grid View' | Header title |
| `items` | GridItem[] | [] | Data items to display |
| `columns` | GridColumn[] | [] | Column configuration |
| `loading` | boolean | false | Show loading spinner |
| `displayMode` | 'cards' \| 'list' | 'cards' | Initial display mode |
| `cardColumns` | number | 3 | Number of card columns (1-5) |
| `showCheckbox` | boolean | true | Show selection checkboxes |
| `showAvatar` | boolean | true | Show avatar circles |
| `showActions` | boolean | true | Show action buttons |
| `emptyMessage` | string | 'No items found' | Empty state message |
| `errorMessage` | string | '' | Error message to display |
| `selectedItems` | Set | new Set() | Pre-selected items |
| `enableSelection` | boolean | true | Enable selection |
| `hoverEffect` | boolean | true | Enable card hover effect |

## Component Outputs

| Event | Payload | Description |
|-------|---------|-------------|
| `itemClick` | GridItem | User clicked an item/card |
| `selectionChange` | Set<string \| number> | Selection changed |
| `actionClick` | {action: string, item: GridItem} | Action button clicked |

## Responsive Behavior

### Desktop (1200px+)
- 3-4 card columns (configurable)
- 1rem padding
- Full-featured table
- Hover effects enabled

### Tablet (768px-1200px)
- 2 card columns
- Reduced padding (0.75rem)
- Compact table view
- Touch-friendly spacing

### Mobile (480px-768px)
- 1 card column
- Minimal padding (0.5rem)
- List view preferred
- Simplified layout

### Very Small (<480px)
- Single column only
- List view forced
- Minimal spacing
- Mobile-optimized

## Custom Column Formatting

Add custom formatting logic with the `formatter` function:

```typescript
const columns: GridColumn[] = [
  {
    key: 'salary',
    label: 'Annual Salary',
    type: 'currency',
    formatter: (value, item) => {
      return `$${(value / 1000).toFixed(0)}K`;
    }
  },
  {
    key: 'completedTasks',
    label: 'Tasks',
    type: 'text',
    formatter: (value, item) => {
      return `${value} / ${item.totalTasks}`;
    }
  }
];
```

## Complete Example: Student Management

```typescript
import { Component, OnInit } from '@angular/core';
import { GridViewComponent, GridColumn, GridItem } from '../grid-view/grid-view.component';

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [GridViewComponent],
  template: `
    <app-grid-view
      [title]="'Student Directory'"
      [items]="students"
      [columns]="columns"
      [loading]="loading"
      [cardColumns]="3"
      [emptyMessage]="'No students found'"
      (itemClick)="viewStudent($event)"
      (actionClick)="handleAction($event)">
    </app-grid-view>
  `
})
export class StudentListComponent implements OnInit {
  students: GridItem[] = [];
  loading = false;

  columns: GridColumn[] = [
    {
      key: 'name',
      label: 'Name',
      type: 'avatar',
      sortable: true
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
    },
    {
      key: 'status',
      label: 'Status',
      type: 'badge'
    }
  ];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadStudents();
  }

  private loadStudents() {
    this.loading = true;
    this.api.getStudents().subscribe({
      next: (data) => {
        this.students = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load students:', err);
        this.loading = false;
      }
    });
  }

  viewStudent(student: GridItem) {
    // Navigate to student detail page
  }

  handleAction(event: { action: string; item: GridItem }) {
    switch (event.action) {
      case 'view':
        this.viewStudent(event.item);
        break;
      case 'edit':
        // Show edit modal
        break;
      case 'more':
        // Show context menu
        break;
    }
  }
}
```

## Styling

The component uses CSS custom properties for theming:

```css
:root {
  --app-primary: #0891B2;
  --app-primary-dark: #0369A1;
  --border-color: #e0e0e0;
}
```

To customize colors globally, override these variables in your `styles.css`.

## Accessibility

- ✅ Keyboard navigation support
- ✅ ARIA labels on all interactive elements
- ✅ Color contrast WCAG AA compliant
- ✅ Focus indicators visible
- ✅ Semantic HTML structure

## Performance

- 🚀 OnPush change detection strategy
- 📦 Lazy-loaded component
- 🔄 TrackBy function for ngFor optimization
- 💾 Minimal re-renders

## Browser Support

- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)
- Mobile browsers (iOS Safari, Chrome Android)

## License

Part of the ERP system
