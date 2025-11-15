# Organization Registration and Sample Data Population System

## Overview

This document describes the new organization registration and sample data population system implemented in the ERP application. The system replaces automatic organization data population with a user-initiated registration process, providing comprehensive tracking of all data imports via the ImportHistory mechanism.

## Architecture

### 1. **ImportHistory Model & Tables**

The `ImportHistory` entity tracks all data population events with the following information:

```
Entity: ImportHistory
Table: import_history (25th table in schema)

Fields:
- id: BIGINT (Primary Key)
- entity_name: VARCHAR(100) - Name of the entity being imported (e.g., STUDENTS, PARENTS, PERMISSIONS)
- import_type: ENUM - Type of import (SAMPLE_DATA, MANUAL_IMPORT, REGISTRATION, MIGRATION, SYNC)
- record_count: INT - Number of records imported
- source: VARCHAR(255) - Source of data (e.g., sample-students.xml, CSV upload)
- imported_by: VARCHAR(100) - User who initiated import (SYSTEM for auto-initialization)
- import_start_time: DATETIME - When import started
- import_end_time: DATETIME - When import completed
- import_status: ENUM - Status (PENDING, SUCCESS, FAILED, PARTIAL)
- error_message: VARCHAR(1000) - Error details if import failed
- notes: VARCHAR(500) - Additional notes
- created_at: DATETIME - Record creation timestamp
- is_active: BOOLEAN - Soft delete flag

Indexes:
- entity_name, import_type, import_status, created_at (for fast queries)
```

### 2. **Database Schema Changes**

**Phase 13 Updated:** Now contains both RecycleBin and ImportHistory tables

```sql
-- New table added to schema.sql
CREATE TABLE IF NOT EXISTS import_history (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_name VARCHAR(100) NOT NULL,
    import_type VARCHAR(50) NOT NULL,
    record_count INT NOT NULL DEFAULT 0,
    source VARCHAR(255),
    imported_by VARCHAR(100) NOT NULL,
    import_start_time DATETIME NOT NULL,
    import_end_time DATETIME,
    import_status VARCHAR(20) NOT NULL,
    error_message VARCHAR(1000),
    notes VARCHAR(500),
    created_at DATETIME NOT NULL,
    is_active BOOLEAN DEFAULT true,
    INDEX idx_entity_name (entity_name),
    INDEX idx_import_type (import_type),
    INDEX idx_import_status (import_status),
    INDEX idx_created_at (created_at),
    INDEX idx_is_active (is_active)
);
```

### 3. **Backend Components**

#### 3.1 ImportHistoryRepository
Location: `src/main/java/krs/erp/repository/ImportHistoryRepository.java`

Key Methods:
```java
// Check if entity has been imported
boolean hasEntityBeenImported(String entityName)

// Get all imports for an entity
List<ImportHistory> findByEntityNameOrderByCreatedAtDesc(String entityName)

// Get most recent successful import
Optional<ImportHistory> findFirstByEntityNameAndImportStatusOrderByCreatedAtDesc(
    String entityName, ImportStatus.SUCCESS)

// Get all imported entities
List<String> getImportedEntities()

// Get total records imported
Integer getTotalRecordsImportedForEntity(String entityName)

// Paginated queries
Page<ImportHistory> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable)
```

#### 3.2 ImportHistoryService
Location: `src/main/java/krs/erp/service/ImportHistoryService.java`

Key Methods:
```java
// Create and manage import history
ImportHistory createImportHistory(String entityName, ImportType, String importedBy, String source)
void markImportAsSuccessful(ImportHistory history, int recordCount)
void markImportAsFailed(ImportHistory history, String errorMessage)

// Check import status
boolean hasEntityBeenImported(String entityName)
boolean hasSampleDataBeenLoaded()

// Retrieve history
List<ImportHistory> getImportsForEntity(String entityName)
Optional<ImportHistory> getMostRecentSuccessfulImport(String entityName)
List<String> getImportedEntities()
```

#### 3.3 ImportHistoryController
Location: `src/main/java/krs/erp/controller/ImportHistoryController.java`

REST API Endpoints:
```
GET  /api/v1/import-history                          - All import history (paginated)
GET  /api/v1/import-history/entity/{entityName}      - History for specific entity
GET  /api/v1/import-history/check/{entityName}       - Check if entity imported
GET  /api/v1/import-history/entities                 - List of all imported entities
GET  /api/v1/import-history/recent                   - Recent 10 imports
GET  /api/v1/import-history/{id}                     - Get specific import record
GET  /api/v1/import-history/summary/sample-data      - Summary of sample data imports
```

### 4. **Organization Registration**

#### 4.1 OrganizationRegistrationRequest DTO
Location: `src/main/java/krs/erp/dto/OrganizationRegistrationRequest.java`

Contains all organization registration fields:
- Basic Info: name, type, code, description
- Contact: email, phone, fax, website
- Address: street, city, state, postal code, country
- Registration: registration_number, tax_id, established_year, accreditation
- **loadSampleData**: Boolean flag for optional sample data population

#### 4.2 Organization Registration Endpoint
Location: `src/main/java/krs/erp/controller/OrganizationController.java`

```
POST /api/organizations/register
Headers: Content-Type: application/json
Body: OrganizationRegistrationRequest

Response: 201 Created with Organization entity
{
    "id": 1,
    "name": "Example School",
    "type": "School",
    "code": "ES001",
    ...
}
```

### 5. **Sample Data Initialization Changes**

#### Before (Old System):
```
SampleDataInitializer (@Order(0))
├── loadPermissions()      ✓
├── loadRoles()            ✓
├── loadOrganizations()    ✓ AUTO-LOAD (NOW DISABLED)
├── loadStaff()            ✓
└── loadUsers()            ✓
```

#### After (New System):
```
SampleDataInitializer (@Order(0))
├── loadPermissions()      ✓
├── loadRoles()            ✓
├── loadOrganizations()    ✗ DISABLED - User must register manually
├── loadStaff()            ✓
└── loadUsers()            ✓

Optional on Dashboard:
└── Sample Data Population Modal (triggered by user choice)
    ├── Students         (checked via ImportHistory)
    ├── Grades          (checked via ImportHistory)
    ├── Subjects        (checked via ImportHistory)
    ├── Timetables      (checked via ImportHistory)
    ├── Parents         (checked via ImportHistory)
    └── Addresses       (checked via ImportHistory)
```

## Usage Flow

### 1. **First-Time Setup**

```
User Visits Application
    ↓
Home Page Shows Organization Registration Form
    ↓
User Fills Registration Form
    ↓
POST /api/organizations/register
    ↓
Organization Created Successfully
    ↓
Redirect to Dashboard
    ↓
Dashboard Shows: "Sample Data Population Available"
    ↓
User Selects: "Load Sample Data"
    ↓
System Checks ImportHistory for each entity
    ↓
Load Only Missing Data
    ↓
Update ImportHistory for each entity
    ↓
Display: "X records loaded successfully"
```

### 2. **Import History Tracking**

Each data load creates an ImportHistory record:

```json
{
    "id": 1,
    "entityName": "STUDENTS",
    "importType": "SAMPLE_DATA",
    "recordCount": 369,
    "source": "sample-students.xml",
    "importedBy": "SYSTEM",
    "importStartTime": "2025-11-15T21:41:00",
    "importEndTime": "2025-11-15T21:41:30",
    "importStatus": "SUCCESS",
    "errorMessage": null,
    "createdAt": "2025-11-15T21:41:00"
}
```

### 3. **Duplicate Prevention**

Before loading any sample data:

```javascript
// Frontend
const isImported = await fetch('/api/v1/import-history/check/STUDENTS')
    .then(r => r.json());

if (isImported.isImported) {
    console.log('Data already loaded');
    return;
} else {
    console.log('Loading data...');
    // Proceed with loading
}
```

## REST API Examples

### Check if Students have been imported
```bash
curl http://localhost:8081/api/v1/import-history/check/STUDENTS

Response:
{
    "entityName": "STUDENTS",
    "isImported": true,
    "lastImportTime": "2025-11-15T21:41:30",
    "recordCount": 369
}
```

### Get all imported entities
```bash
curl http://localhost:8081/api/v1/import-history/entities

Response:
[
    "PERMISSIONS",
    "ROLES",
    "STAFF",
    "USERS",
    "STUDENTS",
    "GRADES",
    "SUBJECTS",
    "TIMETABLES",
    "PARENTS",
    "ADDRESSES"
]
```

### Get sample data population summary
```bash
curl http://localhost:8081/api/v1/import-history/summary/sample-data

Response:
{
    "totalImports": 6,
    "imports": [...],
    "hasSampleDataBeenLoaded": true,
    "successfulImports": 6,
    "failedImports": 0
}
```

### Register new organization
```bash
curl -X POST http://localhost:8081/api/organizations/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Example School",
    "type": "School",
    "code": "ES001",
    "email": "info@example.com",
    "phone": "+1234567890",
    "city": "New York",
    "country": "USA",
    "loadSampleData": true
  }'

Response:
{
    "id": 1,
    "name": "Example School",
    "type": "School",
    "code": "ES001",
    "email": "info@example.com",
    "phone": "+1234567890",
    "city": "New York",
    "country": "USA",
    "isActive": true,
    "createdTime": "2025-11-15T21:45:00"
}
```

## Frontend Integration Checklist

- [ ] Create organization registration form on home page
- [ ] Add form validation for all required fields
- [ ] Implement POST to `/api/organizations/register`
- [ ] Handle success response - redirect to dashboard
- [ ] Create sample data population modal on dashboard
- [ ] Add checkbox for each entity with import status
- [ ] Check `/api/v1/import-history/check/{entityName}` before allowing load
- [ ] Implement progress bar for data loading
- [ ] Display ImportHistory records in audit trail
- [ ] Add error handling and retry mechanism

## Database Statistics

**Total Tables:** 25
- 23 Entity tables
- 1 RecycleBin table
- 1 ImportHistory table (NEW)

**Current Sample Data (after initial load):**
- Permissions: 22
- Roles: 10
- Staff: 8
- Users: 11
- Students: 369
- Grades: 34
- Subjects: 50
- Timetables: 32
- Parents: 20
- Addresses: 596
- **Total: 1,152 records**

**Organizations:** 0 (User must register - no auto-load)

## Key Benefits

1. **User Control**: Organizations register themselves instead of auto-loading sample data
2. **Audit Trail**: Complete history of all data imports with timestamps and status
3. **Duplicate Prevention**: Query ImportHistory before loading to avoid re-loading
4. **Partial Loads**: Support for loading only missing entities
5. **Error Tracking**: Capture and store error messages for failed imports
6. **Flexible Import Types**: Support for multiple import mechanisms (SAMPLE_DATA, MANUAL_IMPORT, REGISTRATION, MIGRATION, SYNC)
7. **Performance**: Indexed queries on entity_name, import_type, and import_status

## Schema Evolution

The schema now follows a clean 13-phase approach with FK dependencies properly ordered:

```
Phase 1:  Base Tables (organizations, permissions, roles, addresses)
Phase 2:  Users (iam_users, user_roles)
Phase 3:  Students
Phase 4:  Staff
Phase 5:  Parents (parent_student_relations)
Phase 6:  Academic (subjects, grades, timetables)
Phase 7:  Attendance
Phase 8:  Health Records
Phase 9:  ERP Fields
Phase 10: Email Templates & Logs
Phase 11: ERP Entities
Phase 12: Custom Views
Phase 13: Recycle Bin + Import History
```

## Troubleshooting

### Issue: ImportHistory table not created
**Solution:** Reload schema.sql manually:
```bash
mysql -h localhost -P 3307 -u root erp_database < schema.sql
```

### Issue: Sample data loads multiple times
**Solution:** Check ImportHistory before loading:
```java
if (importHistoryRepository.hasEntityBeenImported("STUDENTS")) {
    logger.info("Students already loaded, skipping");
    return;
}
```

### Issue: Organization registration returns conflict
**Solution:** Check if organization code already exists:
```bash
curl http://localhost:8081/api/organizations/code/ES001
```

## Future Enhancements

1. **Async Loading**: Move sample data loading to async tasks
2. **Batch Import**: Support bulk CSV/Excel imports with history tracking
3. **Import Rollback**: Ability to rollback failed imports
4. **Selective Loading**: Choose which entities to load
5. **Progress Tracking**: Real-time progress updates during import
6. **Data Validation**: Comprehensive validation before import
7. **Import Scheduling**: Schedule imports for off-peak hours
8. **Dashboard Analytics**: Visualize import history and statistics
