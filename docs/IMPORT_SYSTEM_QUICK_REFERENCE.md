# Import System - Quick Reference Guide

## File Structure

```
src/app/
├── models/
│   └── import.model.ts              (Import data models)
├── services/
│   └── import.service.ts            (Import service)
└── components/
    └── import-wizard/
        ├── import-wizard.component.ts       (Main wizard container)
        ├── import-step-1/
        │   └── import-step-1.component.ts   (File upload & settings)
        ├── import-step-2/
        │   └── import-step-2.component.ts   (Field mapping)
        ├── import-step-3/
        │   └── import-step-3.component.ts   (Confirmation)
        └── import-step-4/
            └── import-step-4.component.ts   (Summary & results)
```

## Key Interfaces

### ImportSession
```typescript
interface ImportSession {
  id: string;
  fileName: string;
  fileFormat: string;
  totalRecords: number;
  uploadedAt: Date;
  importType: 'personal' | 'organization';
  enableManualApproval: boolean;
  duplicateAction: 'skip' | 'overwrite' | 'clone';
  skipEmptyFields: boolean;
  findDuplicatesBy: string;
  fieldMappings: FieldMapping[];
  unmappedColumns: string[];
  importedRecords?: ImportResult[];
  status: 'in-progress' | 'completed' | 'failed';
  statistics?: ImportStatistics;
}
```

### ImportSettings
```typescript
interface ImportSettings {
  importType: 'personal' | 'organization';
  enableManualApproval: boolean;
  duplicateAction: 'skip' | 'overwrite' | 'clone';
  skipEmptyFields: boolean;
  findDuplicatesBy: string;
}
```

### FieldMapping
```typescript
interface FieldMapping {
  sourceColumn: string;
  sourceIndex: number;
  targetField: string;
  targetFieldLabel: string;
  isRequired: boolean;
  dataType: string;
}
```

## Step-by-Step Workflow

### Step 1: File Upload & Configuration
1. User selects file (.xlsx, .xls, .csv, or .vcf)
2. File is validated for format and size
3. User selects import type (personal/organization)
4. User configures duplicate handling and other options
5. Click "Next" to proceed to Step 2

**Output**: ImportSession with file info and settings

### Step 2: Field Mapping
1. Display available columns from the file
2. Allow user to map columns to entity fields
3. Option to auto-detect mappings
4. Validate that all required fields are mapped
5. Click "Next" to proceed to Step 3

**Output**: Updated ImportSession with field mappings

### Step 3: Confirmation
1. Display list of unmapped columns
2. Show import summary (file, records, type, duplicate action)
3. Allow user to go back to Step 2 or confirm import
4. Click "Import" to start the actual import process
5. Proceed to Step 4

**Output**: Import begins processing

### Step 4: Summary & Results
1. Display import progress (real-time)
2. Once complete, show statistics:
   - Total records
   - Added records
   - Updated records
   - Skipped records
   - Failed records
   - Success rate
3. Display table with first 10 imported records
4. Option to undo import
5. Click "Done" to complete wizard

## ImportService Methods

### File Management
```typescript
uploadFile(file, entityType)           // Upload and preview file
detectFileFormat(fileName)             // Detect file format
isValidFileSize(bytes)                 // Validate file size
isSupportedFormat(format)              // Check if format is supported
formatFileSize(bytes)                  // Format size for display
```

### Session Management
```typescript
createImportSession(file, entityType, settings)  // Create import session
getSession(sessionId)                           // Get session details
setCurrentSession(session)                      // Set current session
getCurrentSession()                             // Get current session
cancelSession(sessionId)                        // Cancel import session
```

### Field Mapping
```typescript
getFieldMappingTemplate(entityType)        // Get mapping template
saveFieldMappings(sessionId, mappings)     // Save field mappings
autoDetectMappings(sessionId)              // Auto-detect mappings
validateMappings(sessionId)                // Validate mappings
```

### Import Operations
```typescript
startImport(sessionId)                     // Start import with progress
getImportSummary(sessionId)                // Get import results
undoImport(sessionId)                      // Undo import
getImportHistory(entityType, limit)        // Get past imports
```

### State Management
```typescript
currentStep$: Observable<number>           // Current wizard step
currentSession$: Observable<ImportSession> // Current session
importProgress$: Observable<number>        // Import progress

setCurrentStep(step)                       // Set current step
updateProgress(progress)                   // Update progress
```

## Component Props

### ImportWizardComponent
```typescript
// Inputs
@Input() entityType: string = 'students';

// The component manages navigation between steps
// and passes data through the import service
```

### ImportStep1Component
```typescript
// Inputs
@Input() entityType: string;

// Outputs
@Output() nextStep: EventEmitter<ImportSession>;
```

### ImportStep2Component
```typescript
// Inputs
@Input() session: ImportSession | null;

// Outputs
@Output() previousStep: EventEmitter<number>;
@Output() nextStep: EventEmitter<ImportSession>;
```

### ImportStep3Component
```typescript
// Inputs
@Input() session: ImportSession | null;

// Outputs
@Output() previousStep: EventEmitter<number>;
@Output() importStart: EventEmitter<ImportSession>;
```

### ImportStep4Component
```typescript
// Inputs
@Input() session: ImportSession | null;

// Outputs
@Output() done: EventEmitter<void>;
```

## Code Examples

### Use in Route
```typescript
const routes: Routes = [
  {
    path: 'import',
    component: ImportWizardComponent,
    data: { entityType: 'students' }
  }
];
```

### Use in Component Template
```html
<button class="btn btn-primary" (click)="openImport()">
  Import Data
</button>

<app-import-wizard *ngIf="showImport"></app-import-wizard>
```

### Use ImportService Directly
```typescript
import { ImportService } from './services/import.service';

constructor(private importService: ImportService) {}

uploadFile(file: File) {
  this.importService.uploadFile(file, 'students').subscribe(preview => {
    console.log('File preview:', preview);
  });
}

startImporting(sessionId: string) {
  this.importService.startImport(sessionId).subscribe(event => {
    if (event.type === HttpEventType.UploadProgress) {
      console.log(event.loaded / event.total * 100 + '%');
    }
  });
}
```

## Supported Entity Types

- **students**: Student records with fields like fullName, studentId, email, phone, grade, status
- **candidates**: Candidate records (customize as needed)
- **contacts**: Contact records
- **users**: User records

To add a new entity type:
1. Create field mapping template on backend
2. Update EntityType configuration
3. Create entity-specific validators if needed

## Common Configurations

### Enable Manual Approval
```typescript
settings.enableManualApproval = true;
```

### Use Email for Duplicate Detection
```typescript
settings.findDuplicatesBy = 'email';
```

### Skip Empty Fields
```typescript
settings.skipEmptyFields = true;
```

### Organization-level Import
```typescript
settings.importType = 'organization';
```

## Error Messages

| Error | Cause | Solution |
|-------|-------|----------|
| "File format not supported" | Wrong file extension | Use .xlsx, .xls, .csv, or .vcf |
| "File size exceeds 50 MB" | File too large | Split into smaller files |
| "Missing required field mapping" | Required field not mapped | Go back and map required fields |
| "Invalid duplicate detection field" | Field doesn't exist | Choose valid field from dropdown |
| "Email field is required" | Data validation failed | Check data and re-import |

## Performance Tips

1. **Batch Size**: Configure batch size on backend (default 1000)
2. **Large Files**: Use CSV format for very large files (100k+ records)
3. **Network**: Import on stable network connection
4. **Validation**: Pre-validate data before importing
5. **Timing**: Schedule imports during off-peak hours for large batches

## Security Best Practices

1. Always validate file type on backend
2. Scan files for malware before import
3. Limit file size to prevent resource exhaustion
4. Log all import operations
5. Require authentication for import operations
6. Validate all imported data against schema
7. Implement rate limiting on import endpoints

## Testing Checklist

- [ ] File upload with various formats
- [ ] File size validation
- [ ] Field mapping auto-detection
- [ ] Manual field mapping
- [ ] Required field validation
- [ ] Duplicate handling (skip, overwrite, clone)
- [ ] Import progress tracking
- [ ] Error handling and display
- [ ] Undo import functionality
- [ ] Statistics calculation
- [ ] Empty file handling
- [ ] Large file handling
- [ ] Network failure handling
- [ ] Permission validation
- [ ] Audit logging

## Related Documentation

- See [IMPORT_SYSTEM_GUIDE.md](./IMPORT_SYSTEM_GUIDE.md) for comprehensive guide
- See [import.model.ts](../src/app/models/import.model.ts) for detailed interfaces
- See [import.service.ts](../src/app/services/import.service.ts) for service implementation
