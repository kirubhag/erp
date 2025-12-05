# UI Type System - Quick Start Guide

## Overview

The UI Type System enables dynamic field rendering based on field configuration stored in the database. This guide shows you how to integrate it into your forms.

## Step 1: Import the Dynamic Field Renderer

```typescript
import { DynamicFieldRendererComponent } from '../dynamic-field-renderer/dynamic-field-renderer.component';

@Component({
  standalone: true,
  imports: [CommonModule, DynamicFieldRendererComponent],
  // ...
})
```

## Step 2: Load Field Configuration

```typescript
export class YourFormComponent implements OnInit {
  fields: ErpField[] = [];
  formData: any = {};
  
  constructor(private fieldService: FieldService) {}
  
  ngOnInit() {
    // Load fields for your entity type
    this.fieldService.getFieldsByEntityType('STUDENT').subscribe(fields => {
      this.fields = fields.filter(f => f.showInForm);
    });
  }
}
```

## Step 3: Render Fields in Template

```html
<form (ngSubmit)="onSubmit()">
  <div *ngFor="let field of fields">
    <app-dynamic-field-renderer
      [field]="field"
      [value]="formData[field.fieldName]"
      [mode]="'create'"
      (valueChange)="onFieldChange(field.fieldName, $event)">
    </app-dynamic-field-renderer>
  </div>
  <button type="submit">Submit</button>
</form>
```

## Step 4: Handle Value Changes

```typescript
onFieldChange(fieldName: string, value: any) {
  this.formData[fieldName] = value;
}

onSubmit() {
  // Submit formData to your API
  this.http.post('/api/students', this.formData).subscribe(/*...*/);
}
```

## Field Modes

The dynamic field renderer supports three modes:

- **`create`** - For creating new records (all fields editable)
- **`edit`** - For editing existing records (all fields editable)
- **`view`** - For viewing records (read-only, formatted display)

## Supported Field Types

| UI Type ID | Field Type | Description |
|------------|------------|-------------|
| 100 | Single Line Text | Standard text input |
| 101 | Multi Line Text | Textarea |
| 102 | Email | Email input with validation |
| 103 | Phone | Phone number input |
| 104 | Picklist | Dropdown select |
| 106 | Date | Date picker |
| 107 | DateTime | Date and time picker |
| 108 | Number | Integer input |
| 110 | Currency | Currency input |
| 111 | Decimal | Decimal number input |
| 112 | Percent | Percentage (0-100) |
| 114 | Checkbox | Boolean checkbox |

## Complete Example

See [student-form-example.component.ts](file:///Users/kirubha-2911/Documents/GitHub/erp/src/main/resources/static/angular/src/app/components/student-form-example/student-form-example.component.ts) for a full working example with:
- Field grouping by sections
- Validation
- Responsive layout
- Loading and error states

## Next Steps

1. **Run the application** - The migration will run automatically on startup
2. **Test the dynamic renderer** - Navigate to any form using the component
3. **Customize styling** - Modify `dynamic-field-renderer.component.css`
4. **Add more field types** - Extend for file uploads, rich text, etc.

## API Endpoints

- `GET /api/fields/{entityType}` - Get all fields for an entity
- `GET /api/fields/{entityType}/grouped` - Get fields grouped by section
- `GET /api/fields/ui-types` - Get all UI field type metadata
- `POST /api/fields/validate-value` - Validate a field value

## Troubleshooting

**Fields not rendering?**
- Check that `ui_type` is populated in the database
- Verify field `showInForm` is true
- Check browser console for errors

**Validation not working?**
- Ensure `isRequired` and `validationPattern` are set correctly
- Check UIFieldTypeService is imported

**Styling issues?**
- Verify CSS file is imported in component
- Check for Bootstrap conflicts
