# Entity Fields Dynamic Sidebar Implementation

## Summary
Converted the static entity list sidebar to a dynamic, reusable component that loads entity fields from the `erp_fields` database table based on the selected entity type.

## Changes Made

### 1. New Component Created: `EntityFieldsSidebarComponent`
**Location:** `src/main/resources/static/angular/src/app/components/entity-fields-sidebar/`

**Files:**
- `entity-fields-sidebar.component.ts` - Component logic
- `entity-fields-sidebar.component.html` - Template
- `entity-fields-sidebar.component.css` - Styles

**Key Features:**
- Standalone Angular component (Angular 17+)
- Loads entity fields dynamically from `/api/fields/{entityType}` endpoint
- Automatically refreshes when entity type changes (`OnChanges`)
- Displays loading, error, and empty states
- Shows selected filter count with theme-colored badge
- Emits `fieldFilterChange` event when filters are selected/deselected
- "Clear All Filters" button to reset selections
- Responsive design (hidden on mobile)

**Interfaces:**
```typescript
export interface EntityField {
  id: number;
  fieldName: string;
  fieldLabel: string;
  fieldType: string;
  isRequired: boolean;
  isActive: number;
  displayOrder: number;
  isSearchable?: boolean;
  isSortable?: boolean;
  fieldCategory?: string;
}

export interface FieldFilter {
  fieldName: string;
  fieldLabel: string;
  selected: boolean;
}
```

### 2. Updated: `EntityListComponent`

**File:** `src/main/resources/static/angular/src/app/components/entity-list/entity-list.component.ts`

**Changes:**
- Added import: `EntityFieldsSidebarComponent, FieldFilter`
- Updated `imports` array to include `EntityFieldsSidebarComponent`
- Added new method: `onFieldFilterChange(selectedFilters: FieldFilter[])`
  - Handles filter changes from the dynamic sidebar
  - Updates `entityFieldFilters` for backward compatibility
  - Emits `filterChange` event with selected field names

**File:** `src/main/resources/static/angular/src/app/components/entity-list/entity-list.component.html`

**Changes:**
- Replaced static sidebar HTML with new `<app-entity-fields-sidebar>` component
- Added conditional rendering:
  - If `entityType` is provided → Use dynamic sidebar
  - If no `entityType` → Fallback to legacy static sidebar
- Bindings:
  ```html
  <app-entity-fields-sidebar 
    *ngIf="sidebarVisible && entityType"
    [entityType]="entityType"
    [visible]="sidebarVisible"
    (fieldFilterChange)="onFieldFilterChange($event)">
  </app-entity-fields-sidebar>
  ```

### 3. Backend API Endpoint (Already Exists)

**Endpoint:** `GET /api/fields/{entityType}`

**Controller:** `ErpFieldController` (already implemented)

**Features:**
- Returns all active fields for the specified entity type
- Sorted by `displayOrder` and `fieldLabel`
- Includes field metadata: name, label, type, category, searchable, sortable flags

**Example Response:**
```json
[
  {
    "id": 1,
    "fieldName": "firstName",
    "fieldLabel": "First Name",
    "fieldType": "STRING",
    "isRequired": true,
    "isActive": 1,
    "displayOrder": 1,
    "isSearchable": true,
    "isSortable": true,
    "fieldCategory": "PERSONAL"
  },
  ...
]
```

## How It Works

### 1. Component Initialization
When the entity list page loads with an `entityType` (e.g., "students"):
1. `EntityFieldsSidebarComponent` receives `entityType` as input
2. Component calls `loadEntityFields()` in `ngOnInit()`
3. Makes HTTP GET request to `/api/fields/students`
4. Filters active fields (`isActive === 1`)
5. Sorts by `displayOrder`
6. Creates `FieldFilter` array with all fields (initially unselected)

### 2. User Interaction
When user selects/deselects checkboxes:
1. `onFieldFilterChange(fieldName)` is called
2. Toggle filter's `selected` state
3. Emit `fieldFilterChange` event with array of selected filters

### 3. Parent Component Handling
Entity list component receives filter changes:
1. `onFieldFilterChange(selectedFilters)` is called
2. Updates `entityFieldFilters` object for legacy code compatibility
3. Emits `filterChange` event to parent component
4. Parent component (e.g., StudentListComponent) applies filters to data

### 4. Entity Type Changes
If entity type changes dynamically:
1. `ngOnChanges()` detects `entityType` change
2. Calls `loadEntityFields()` to fetch new fields
3. Resets filter selections
4. Updates UI with new field list

## Usage Example

### In a List Component (e.g., StudentListComponent):

```typescript
@Component({
  template: `
    <app-entity-list
      entityType="students"
      [columns]="columns"
      [data]="students"
      (filterChange)="onFilterChange($event)">
    </app-entity-list>
  `
})
export class StudentListComponent {
  onFilterChange(filters: { [key: string]: any }) {
    console.log('Entity field filters:', filters.entityFields);
    // filters.entityFields contains array of selected field names
    // e.g., ['firstName', 'email', 'grade']
    
    // Apply filters to your data loading logic
  }
}
```

## Benefits

1. **Dynamic Content**: Fields loaded from database, not hardcoded
2. **Reusable**: Single component works for all entity types
3. **Maintainable**: Field changes in database automatically reflected
4. **Consistent**: Same styling and behavior across all entity lists
5. **Scalable**: Easy to add new entity types without code changes
6. **Type-Safe**: TypeScript interfaces for type checking
7. **User Experience**: Loading states, error handling, empty states

## Migration Path

### Existing Lists Using Static Columns/Filters
- No changes required immediately
- Legacy sidebar still available as fallback
- Migrate by adding `entityType` input to entity-list
- Example: `entityType="students"`, `entityType="staff"`, etc.

### New Entity Lists
- Always provide `entityType` to use dynamic sidebar
- Ensure entity has fields defined in `erp_fields` table
- No need to define columns array for sidebar filters

## Database Requirements

For each entity that needs dynamic fields:
1. Entity must have an `EntityType` enum value (e.g., `STUDENTS`, `STAFF`)
2. Fields must be defined in `erp_fields` table
3. Fields must have `isActive = 1` to be visible
4. Set appropriate `displayOrder` for field ordering
5. Mark fields as `isSearchable` or `isSortable` as needed

## Testing

### Manual Testing Steps:
1. Navigate to Students list (`/entity-detail/students`)
2. Verify sidebar loads fields from database
3. Check loading state appears briefly
4. Verify fields are sorted correctly
5. Select/deselect checkboxes
6. Verify filter count badge updates
7. Click "Clear All Filters" button
8. Check console for filter change events
9. Test with different entity types

### Expected Results:
- ✅ Sidebar shows student-specific fields
- ✅ Fields loaded from `erp_fields` table for `STUDENTS` entity
- ✅ Checkbox selections emit filter events
- ✅ Filter count badge shows correct number
- ✅ Clear button resets all selections
- ✅ Theme colors applied correctly

## Future Enhancements

Potential improvements for future iterations:
1. Add field search/filter within sidebar
2. Group fields by category (PERSONAL, CONTACT, ACADEMIC)
3. Remember user's filter preferences (localStorage)
4. Add "Select All" / "Deselect All" buttons
5. Show field types with icons
6. Add tooltips with field descriptions
7. Support advanced filter types (date range, number range)
8. Collapsible field groups
9. Drag-and-drop field reordering
10. Save custom field views/presets

## Notes

- The sidebar uses the existing `EntityType` enum from backend
- Entity type values are case-insensitive (backend handles conversion)
- The component respects the global loading bar (no local loading needed)
- Styling uses CSS variables for theme consistency
- Component is mobile-responsive (hidden on small screens)
- Backward compatible with existing entity-list implementations
