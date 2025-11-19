# Import System - Backend Team Handoff Summary

## Status: Ready for Backend Implementation

**Date**: November 19, 2025  
**Frontend Status**: ✅ **100% COMPLETE** - Production ready  
**Backend Status**: ⏳ **Ready for start** - All specifications documented

---

## Quick Start for Backend Team

### 1. Read These Documents (in order)

1. **`IMPORT_SYSTEM_GUIDE.md`** - Complete system architecture and design
2. **`IMPORT_BACKEND_IMPLEMENTATION.md`** - Backend implementation guide with code examples
3. **`IMPORT_SYSTEM_QUICK_REFERENCE.md`** - API contracts and quick reference

### 2. Frontend Files (Reference Only)

Located in `src/main/resources/static/angular/src/app/features/import/`:

- `import.model.ts` - All TypeScript interfaces (use for API response shapes)
- `import.service.ts` - All API endpoints called by frontend (defines contract)
- `import-wizard.component.ts` - Main wizard container
- `import-step-1.component.ts` - File upload & settings
- `import-step-2.component.ts` - Field mapping
- `import-step-3.component.ts` - Confirmation
- `import-step-4.component.ts` - Summary with real-time polling

### 3. What You Need to Build

Backend team needs to implement **11 REST API endpoints**:

```
1. POST   /api/import/upload-preview              → File validation & preview
2. POST   /api/import/sessions                    → Create import session
3. GET    /api/import/mapping-templates/{type}   → Get entity fields
4. PUT    /api/import/sessions/{id}/mappings     → Save field mappings
5. POST   /api/import/sessions/{id}/validate-mappings
6. POST   /api/import/sessions/{id}/auto-detect-mappings
7. POST   /api/import/sessions/{id}/import        → Start import
8. GET    /api/import/sessions/{id}/summary      → Get results (polling)
9. POST   /api/import/sessions/{id}/undo          → Reverse import
10. GET   /api/import/history/{entityType}       → Past imports
11. POST  /api/import/sessions/{id}/cancel       → Cancel in-progress
```

All endpoints specified with:
- ✅ Exact request/response JSON examples
- ✅ Java implementation code samples
- ✅ Required parameters and validation
- ✅ Error handling patterns
- ✅ Database entity examples
- ✅ Security requirements

---

## Frontend Implementation Details

### Component Structure

```
src/main/resources/static/angular/src/app/features/import/
├── components/
│   ├── import-wizard/
│   │   ├── import-wizard.component.ts        (120 lines - main container)
│   │   ├── import-wizard.component.html
│   │   └── import-wizard.component.css
│   ├── import-step-1/
│   │   ├── import-step-1.component.ts        (280 lines - file upload)
│   │   ├── import-step-1.component.html
│   │   └── import-step-1.component.css
│   ├── import-step-2/
│   │   ├── import-step-2.component.ts        (240 lines - field mapping)
│   │   └── ...
│   ├── import-step-3/
│   │   ├── import-step-3.component.ts        (220 lines - confirmation)
│   │   └── ...
│   └── import-step-4/
│       ├── import-step-4.component.ts        (310 lines - summary)
│       └── ...
├── services/
│   ├── import.service.ts                     (290 lines - API integration)
│   └── import.model.ts                       (150 lines - interfaces)
└── ...
```

### API Consumption Pattern

The frontend service (`import.service.ts`) calls exactly these endpoints:

```typescript
// Example: Upload file
POST /api/import/upload-preview
Request: FormData with file
Response: ImportPreview { totalRows, headerRow, sampleRows, detectedFormat }

// Example: Get results (polling every 2 seconds)
GET /api/import/sessions/{id}/summary
Response: ImportSession { statistics, importedRecords, status }
```

The frontend polls the summary endpoint every 2 seconds until `status === 'completed'`.

---

## Key Backend Requirements

### 1. File Format Support
- ✅ .csv (comma-separated values)
- ✅ .xlsx (Excel 2007+)
- ✅ .xls (Excel 97-2003)
- ✅ .vcf (vCard format)
- Maximum file size: **50 MB**

### 2. Entity Types to Support (configurable)
- `students` - Educational institution students
- `candidates` - Job candidates
- `contacts` - General contacts
- `users` - System users

**Each entity type has different field requirements** (see FieldMappingTemplate)

### 3. Import Settings

```json
{
  "importType": "personal|organization",  // Type of records
  "duplicateAction": "skip|overwrite|clone",  // How to handle duplicates
  "skipEmptyFields": true|false,          // Ignore empty cells
  "enableManualApproval": true|false,     // User review before save
  "findDuplicatesBy": "email|phone|name|id"  // Which field for matching
}
```

### 4. Real-Time Progress Tracking

Frontend polls `GET /api/import/sessions/{id}/summary` every 2 seconds.

Response must include:
- `status`: "pending", "in-progress", "completed", "failed", "cancelled"
- `statistics`: { totalRecords, addedRecords, updatedRecords, skippedRecords, failedRecords, successRate }
- `importedRecords`: Array of ImportResult objects

### 5. Duplicate Handling Strategies

**skip**: If record exists, don't import (log as skipped)  
**overwrite**: If record exists, update with new data  
**clone**: If record exists, create as new record with different ID

### 6. Auto-Detection (Optional but Recommended)

Frontend has button "Auto-Detect Mappings" that calls:
```
POST /api/import/sessions/{id}/auto-detect-mappings
```

Attempts to match CSV columns to entity fields based on:
- Exact name match (fullName → fullName)
- Fuzzy matching (student_name → fullName)
- Common variations (email_address → email)

Suggestions provided in FieldMappingTemplate.

### 7. Validation Rules

**Client-side** (happens first):
- File format validation
- File size check

**Server-side** (on all endpoints):
- User authentication
- Organization authorization
- Field mapping validation
- Data type validation
- Required field checks

---

## Database Schema (Entities Needed)

### ImportSession
- id (UUID, primary key)
- userId (FK to User)
- organizationId (FK to Organization)
- fileName, fileFormat, totalRecords
- importType, duplicateAction, findDuplicatesBy
- status (enum: pending, in-progress, completed, failed, cancelled)
- uploadedAt, importedAt timestamps
- fieldMappings (relationship to FieldMapping table)
- importedRecords (relationship to ImportResult table)

### ImportResult
- id (auto-increment primary key)
- importSessionId (FK)
- rowNumber, recordId
- status (enum: added, updated, skipped, failed)
- data (JSON blob)
- errors (JSON array)

### FieldMapping
- id (auto-increment)
- importSessionId (FK)
- sourceColumn, sourceIndex
- targetField, targetFieldLabel
- isRequired, dataType

### FieldMappingTemplate (per entity type)
- id
- entityType (e.g., "students")
- fieldName, fieldLabel
- required, dataType
- suggestions (JSON array)

---

## Implementation Priority

### Phase 1 (Week 1 - Core Functionality)
1. Create database entities (ImportSession, ImportResult, FieldMapping)
2. Create repositories and DAOs
3. Implement endpoints 1-4 (upload, create session, get template, save mappings)
4. Implement field mapping templates for each entity type
5. Test with frontend file upload and mapping workflow

### Phase 2 (Week 2 - Import Logic)
6. Implement file parsing (XLSX, CSV support first)
7. Implement duplicate detection logic
8. Implement data validation and transformation
9. Implement endpoints 5-7 (validate, auto-detect, start import)
10. Test with actual data imports

### Phase 3 (Week 3 - Polish & Results)
11. Implement batch processing for large files
12. Implement progress tracking and polling
13. Implement endpoints 8-11 (summary, undo, history, cancel)
14. Add audit logging
15. Comprehensive testing

---

## Integration Checklist

- [ ] Database entities created and migrations run
- [ ] All 11 API endpoints implemented
- [ ] File parsing implemented (CSV, XLSX, XLS, VCF)
- [ ] Field mapping templates configured for all entity types
- [ ] Duplicate detection and handling working
- [ ] Auto-detection algorithm implemented
- [ ] Batch processing for large files configured
- [ ] Audit logging added
- [ ] Error handling and validation complete
- [ ] Security checks (auth, authorization) in place
- [ ] Unit tests written (75% coverage target)
- [ ] Integration tests written
- [ ] API documentation updated
- [ ] Tested with frontend on localhost
- [ ] Tested with large files (10k+ records)
- [ ] Performance tested and optimized
- [ ] Deployed to staging
- [ ] Tested on staging environment

---

## Common Integration Points

### 1. Student/Candidate List Page
Add "Import" button to toolbar that opens wizard:
```typescript
// In list component
importData() {
  this.router.navigate(['/import/students']);
}
```

### 2. Refresh After Import
After successful import (onImportDone), refresh the list:
```typescript
// In wizard component
this.router.navigate(['/students']);
this.studentList.refresh();  // Trigger list reload
```

### 3. Import History
Display past imports in a history panel:
```typescript
GET /api/import/history/students?limit=10
```

### 4. Permission Checks
User must have `IMPORT_STUDENTS` permission (example):
```java
@PreAuthorize("hasAuthority('IMPORT_STUDENTS')")
@PostMapping("/sessions")
public ResponseEntity<ImportSessionDTO> createSession(...) { }
```

---

## Testing Strategy

### Unit Tests (Service Layer)
- File format detection
- Field mapping validation
- Duplicate detection logic
- Data transformation

### Integration Tests (API Layer)
- Upload and preview
- Session creation
- Field mapping CRUD
- Auto-detection accuracy
- Bulk import with 1k, 10k, 100k records
- All duplicate strategies (skip, overwrite, clone)
- Error scenarios

### End-to-End Tests
- Complete workflow (upload → map → validate → import → results)
- Undo import reverses all changes
- Large file handling (50 MB limit)
- Network interruption recovery
- Concurrent imports from multiple users

---

## Documentation Links

1. **Full API Specification**: `IMPORT_BACKEND_IMPLEMENTATION.md`
   - All 11 endpoints with code examples
   - Entity diagrams
   - Error handling
   - Security considerations
   - Performance tips

2. **System Architecture**: `IMPORT_SYSTEM_GUIDE.md`
   - High-level design
   - Component structure
   - Data flow diagrams
   - Configuration options

3. **Quick Reference**: `IMPORT_SYSTEM_QUICK_REFERENCE.md`
   - API contract summary
   - Code examples
   - Common errors
   - Testing checklist

---

## Support & Questions

For questions about:
- **Frontend behavior**: Review the component files (import-step-*.component.ts)
- **API contract**: See IMPORT_SYSTEM_GUIDE.md sections "Service Methods"
- **Data models**: See import.model.ts interfaces
- **Implementation details**: See IMPORT_BACKEND_IMPLEMENTATION.md

---

## Next Steps

1. ✅ Backend team reads all 3 documentation files
2. ✅ Backend team sets up database schema
3. ✅ Backend team implements 11 API endpoints
4. ✅ Backend team runs integration tests with frontend
5. ✅ Deploy to staging and test end-to-end

---

**Status**: Ready for handoff to backend team ✅  
**Frontend Completion**: 100% - All components, service, and models complete  
**Documentation**: 100% - Comprehensive guides and API specs provided  
**Backend Readiness**: All specifications and examples provided, ready to start implementation
