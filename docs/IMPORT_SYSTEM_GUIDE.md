# Comprehensive Entity Import System

## Overview

The Entity Import System is a multi-step wizard component that allows users to import data from spreadsheets (.xlsx, .xls, .csv, .vcf) into the ERP system. It provides a seamless workflow with validation, field mapping, and detailed result reporting.

## Features

### 1. **Multi-Step Wizard Interface**
- **Step 1**: File upload and import configuration
- **Step 2**: Field mapping and auto-detection
- **Step 3**: Confirmation with unmapped columns review
- **Step 4**: Import results and summary

### 2. **File Management**
- Support for .xlsx, .xls, .csv, and .vcf formats
- File size validation (max 50 MB)
- Automatic file format detection
- Preview of file contents

### 3. **Flexible Import Settings**
- **Import Type**: Personal or Organization-level imports
- **Duplicate Handling**: Skip, Overwrite, or Clone duplicates
- **Manual Approval**: Enable/disable approval workflow
- **Field Skipping**: Option to skip empty fields
- **Duplicate Detection**: Configure field for duplicate detection (Email, Phone, Name, ID)

### 4. **Field Mapping**
- Auto-detect mappings based on column headers
- Manual field mapping interface
- Required field validation
- Support for multiple entity types

### 5. **Comprehensive Validation**
- File format validation
- File size validation
- Required field mapping validation
- Data type validation during import

### 6. **Detailed Results**
- Import statistics (Added, Updated, Skipped, Failed)
- Success rate calculation
- Individual record details
- Error reporting
- Undo import capability

## Architecture

### Component Structure

```
ImportWizardComponent (Main Container)
├── ImportStep1Component (File Upload)
├── ImportStep2Component (Field Mapping)
├── ImportStep3Component (Confirmation)
└── ImportStep4Component (Summary)
```

### Models

#### ImportSession
Main session object tracking the entire import workflow:
```typescript
{
  id: string;
  fileName: string;
  fileFormat: string;
  totalRecords: number;
  importType: 'personal' | 'organization';
  fieldMappings: FieldMapping[];
  unmappedColumns: string[];
  status: 'in-progress' | 'completed' | 'failed';
  statistics?: ImportStatistics;
}
```

#### FieldMapping
Maps source columns to target entity fields:
```typescript
{
  sourceColumn: string;
  sourceIndex: number;
  targetField: string;
  targetFieldLabel: string;
  isRequired: boolean;
  dataType: string;
}
```

#### ImportStatistics
Tracks import results:
```typescript
{
  totalRecords: number;
  addedRecords: number;
  updatedRecords: number;
  skippedRecords: number;
  failedRecords: number;
  successRate: number;
}
```

### Service: ImportService

The `ImportService` manages all import-related operations:

#### Key Methods

**File Operations**
```typescript
uploadFile(file: File, entityType: string): Observable<ImportPreview>
createImportSession(file: File, entityType: string, settings: ImportSettings): Observable<ImportSession>
detectFileFormat(fileName: string): 'xlsx' | 'xls' | 'csv' | 'vcf'
isValidFileSize(sizeInBytes: number): boolean
isSupportedFormat(format: string): boolean
```

**Mapping Operations**
```typescript
getFieldMappingTemplate(entityType: string): Observable<FieldMappingTemplate>
saveFieldMappings(sessionId: string, mappings: FieldMapping[]): Observable<ImportSession>
autoDetectMappings(sessionId: string): Observable<FieldMapping[]>
validateMappings(sessionId: string): Observable<{ valid: boolean; errors?: string[] }>
```

**Import Operations**
```typescript
startImport(sessionId: string): Observable<HttpEvent<any>>
getImportSummary(sessionId: string): Observable<ImportSession>
undoImport(sessionId: string): Observable<any>
getImportHistory(entityType: string, limit?: number): Observable<ImportSession[]>
```

**Session Management**
```typescript
setCurrentSession(session: ImportSession): void
getCurrentSession(): ImportSession | null
cancelSession(sessionId: string): Observable<any>
```

## Usage

### Basic Integration

Add the import wizard to your routing:

```typescript
const routes: Routes = [
  {
    path: 'students/import',
    component: ImportWizardComponent,
    data: { entityType: 'students' }
  }
];
```

Or use it directly in a component:

```typescript
import { ImportWizardComponent } from './components/import-wizard/import-wizard.component';

@Component({
  selector: 'app-student-management',
  imports: [CommonModule, ImportWizardComponent],
  template: `
    <div class="container">
      <button class="btn btn-primary" (click)="openImportWizard()">
        <i class="fas fa-upload me-2"></i>Import Students
      </button>
      <app-import-wizard *ngIf="showWizard"></app-import-wizard>
    </div>
  `
})
export class StudentManagementComponent {
  showWizard = false;

  openImportWizard() {
    this.showWizard = true;
  }
}
```

### Customizing for Different Entity Types

The import system supports multiple entity types. Configure entity-specific templates:

```typescript
// In your backend service, create field mapping templates for each entity
POST /api/import/mapping-templates
{
  "entityType": "students",
  "fields": [
    {
      "key": "fullName",
      "label": "Full Name",
      "required": true,
      "dataType": "string",
      "suggestions": ["name", "student_name", "full_name"]
    },
    {
      "key": "studentId",
      "label": "Student ID",
      "required": true,
      "dataType": "string",
      "suggestions": ["id", "student_id", "reg_number"]
    }
  ]
}
```

## Backend API Requirements

### Endpoints to Implement

#### 1. Upload and Preview
```
POST /api/import/upload-preview
Content-Type: multipart/form-data

Request:
- file: File
- entityType: string

Response:
{
  "totalRows": 500,
  "headerRow": ["name", "email", "phone", ...],
  "sampleRows": [...],
  "detectedFormat": "csv"
}
```

#### 2. Create Session
```
POST /api/import/sessions
Content-Type: multipart/form-data

Request:
- file: File
- entityType: string
- settings: ImportSettings JSON

Response:
{
  "id": "session_123",
  "fileName": "students.csv",
  "totalRecords": 500,
  "fieldMappings": [...],
  "unmappedColumns": [...]
}
```

#### 3. Field Mapping Template
```
GET /api/import/mapping-templates/:entityType

Response:
{
  "entityType": "students",
  "fields": [...]
}
```

#### 4. Save Field Mappings
```
PUT /api/import/sessions/:sessionId/mappings
Content-Type: application/json

Request:
{
  "mappings": [
    {
      "sourceColumn": "name",
      "targetField": "fullName",
      "targetFieldLabel": "Full Name",
      "isRequired": true,
      "dataType": "string"
    }
  ]
}

Response:
{ updated session }
```

#### 5. Validate Mappings
```
POST /api/import/sessions/:sessionId/validate-mappings

Response:
{
  "valid": true,
  "errors": []
}
```

#### 6. Auto-Detect Mappings
```
POST /api/import/sessions/:sessionId/auto-detect-mappings

Response:
[
  { mapping objects }
]
```

#### 7. Start Import
```
POST /api/import/sessions/:sessionId/import
Content-Type: application/json

Response (with progress events):
HttpEvent<ImportProgress>
```

#### 8. Import Summary
```
GET /api/import/sessions/:sessionId/summary

Response:
{
  "id": "session_123",
  "status": "completed",
  "statistics": {
    "totalRecords": 500,
    "addedRecords": 450,
    "updatedRecords": 40,
    "skippedRecords": 10,
    "failedRecords": 0,
    "successRate": 100
  },
  "importedRecords": [...]
}
```

#### 9. Undo Import
```
POST /api/import/sessions/:sessionId/undo

Response:
{ success: true }
```

## Configuration

### Supported File Formats

| Format | Extension | Max Records | Max Size |
|--------|-----------|-------------|----------|
| Excel | .xlsx | 1500 | 50 MB |
| Excel 97-2003 | .xls | 1500 | 50 MB |
| CSV | .csv | 20000 | 50 MB |
| vCard | .vcf | 1500 | 50 MB |

### Duplicate Detection Options

- **Email**: Find duplicates by email field
- **Phone**: Find duplicates by phone field
- **Name**: Find duplicates by name field
- **ID**: Find duplicates by ID field

### Duplicate Handling Strategies

| Strategy | Behavior |
|----------|----------|
| Skip | Ignore duplicate records |
| Overwrite | Replace existing records with imported data |
| Clone | Create new records with duplicate data |

## Advanced Features

### Progress Tracking

Monitor import progress with HttpClient progress events:

```typescript
this.importService.startImport(sessionId).subscribe(event => {
  if (event.type === HttpEventType.UploadProgress) {
    const percentDone = Math.round((100 * event.loaded) / event.total);
    console.log('Progress: ' + percentDone + '%');
  } else if (event.type === HttpEventType.Response) {
    console.log('Import complete');
  }
});
```

### Import History

Retrieve past import sessions:

```typescript
this.importService.getImportHistory('students', 10).subscribe(history => {
  console.log('Recent imports:', history);
});
```

### Custom Validators

Extend the import system with custom validators:

```typescript
export class CustomImportValidator {
  validateStudentEmail(email: string): { valid: boolean; error?: string } {
    if (!email.includes('@')) {
      return { valid: false, error: 'Invalid email format' };
    }
    return { valid: true };
  }

  validateStudentId(id: string): { valid: boolean; error?: string } {
    if (id.length < 5) {
      return { valid: false, error: 'Student ID must be at least 5 characters' };
    }
    return { valid: true };
  }
}
```

## Error Handling

The import system provides detailed error reporting:

```typescript
{
  "rowNumber": 42,
  "status": "failed",
  "errors": [
    "Email field is required",
    "Invalid phone format",
    "Duplicate student ID"
  ]
}
```

## Security Considerations

1. **File Validation**: All files are validated on upload
2. **Size Limits**: Maximum 50 MB per file
3. **Format Validation**: Only supported formats accepted
4. **Data Validation**: All imported data is validated against entity schema
5. **Session Security**: Sessions are tied to authenticated users
6. **Audit Trail**: All imports are logged for audit purposes

## Performance

- **Batch Processing**: Records processed in batches to prevent timeout
- **Progress Reporting**: Real-time progress updates via polling
- **Database Optimization**: Bulk insert operations for better performance
- **Memory Management**: Streaming for large files

## Troubleshooting

### Common Issues

**Issue**: "File size exceeds 50 MB limit"
- **Solution**: Split your file into smaller batches or compress the data

**Issue**: "Column not recognized"
- **Solution**: Ensure first row contains column headers matching field names

**Issue**: "Required field mapping is missing"
- **Solution**: Go back to step 2 and map all required fields (marked with *)

**Issue**: "Import failed for row X"
- **Solution**: Check the error message for that row and correct the data

## Testing

Example test cases:

```typescript
describe('ImportWizardComponent', () => {
  it('should create import session', () => {
    // Test file upload and session creation
  });

  it('should auto-detect field mappings', () => {
    // Test auto-detection with various column names
  });

  it('should validate required fields', () => {
    // Test validation of required fields
  });

  it('should handle duplicate records', () => {
    // Test all duplicate handling strategies
  });

  it('should generate correct statistics', () => {
    // Test statistics calculation
  });

  it('should support undo operation', () => {
    // Test undo functionality
  });
});
```

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## License

Part of the ERP System

## Support

For issues or feature requests, contact the development team.
