# Generic Custom View System

## Overview

The ERP system has been refactored to use a generic custom view system instead of entity-specific implementations. This allows for a single, reusable system to manage custom views across all entity types.

## Architecture

### Core Components

1. **CustomView Entity** (`krs.erp.model.CustomView`)
   - Generic entity that can handle custom views for any entity type
   - Uses `EntityType` enum to specify which entity the view is for
   - Extends `BaseEntity` for audit fields and common functionality

2. **EntityType Enum** (`krs.erp.enums.EntityType`)
   - Defines all available entity types: STUDENT, PARENT, ATTENDANCE, HEALTH, etc.
   - Provides utility methods for validation and display

3. **CustomViewRepository** (`krs.erp.repository.CustomViewRepository`)
   - JPA repository with entity-type-specific query methods
   - Supports filtering by entity type, user access, defaults, etc.

4. **CustomViewService** (`krs.erp.service.CustomViewService`)
   - Business logic for managing custom views
   - Handles CRUD operations, access control, default view management

5. **CustomViewController** (`krs.erp.controller.CustomViewController`)
   - REST API endpoints for custom view management
   - Supports all entity types through single set of endpoints

## API Endpoints

### Base URL: `/api/custom-views`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/?entityType={type}&userId={id}` | Get views for entity type |
| GET | `/{id}` | Get specific view by ID |
| GET | `/default?entityType={type}` | Get default view for entity type |
| GET | `/user/{userId}?entityType={type}` | Get user's views for entity type |
| POST | `/?userId={id}` | Create new custom view |
| PUT | `/{id}?userId={id}` | Update existing view |
| DELETE | `/{id}?userId={id}` | Delete custom view |
| POST | `/{id}/clone?newViewName={name}&userId={id}` | Clone existing view |
| GET | `/recent?entityType={type}&limit={n}` | Get recent views |
| GET | `/count?entityType={type}` | Get view count |
| GET | `/entity-types` | Get all available entity types |

## Entity Types

The following entity types are supported:

- `STUDENT` - Student management views
- `PARENT` - Parent/guardian views  
- `ATTENDANCE` - Attendance tracking views
- `HEALTH` - Health record views
- `ACADEMIC` - Academic record views
- `BEHAVIOR` - Behavioral record views
- `EXTRACURRICULAR` - Activity and club views
- `STAFF` - Staff management views
- `TEACHER` - Teacher-specific views
- `COURSE` - Course management views
- `GRADE` - Grading and assessment views
- `SCHEDULE` - Scheduling views
- `FINANCIAL` - Financial record views
- `COMMUNICATION` - Communication log views
- `FACILITY` - Facility management views

## Migration from StudentCustomView

### Migration Process

1. **Run Migration**
   ```
   POST /api/admin/custom-view-migration/migrate
   ```
   This will copy all `StudentCustomView` records to the new `CustomView` table with `entityType = STUDENT`.

2. **Verify Migration**
   ```
   GET /api/admin/custom-view-migration/verify
   ```
   This checks that all data was migrated correctly and counts match.

3. **Cleanup Old Data** (Optional)
   ```
   DELETE /api/admin/custom-view-migration/cleanup
   ```
   **WARNING**: This permanently deletes the old `StudentCustomView` data.

### Migration Service

The `CustomViewMigrationService` provides:
- Automatic migration of existing data
- Data integrity verification
- Safe cleanup of old data
- Comprehensive logging and error handling

## Usage Examples

### Frontend Integration

```javascript
// Get all student custom views for a user
fetch('/api/custom-views?entityType=STUDENT&userId=123')
  .then(response => response.json())
  .then(views => {
    // Handle student custom views
  });

// Get default attendance view
fetch('/api/custom-views/default?entityType=ATTENDANCE')
  .then(response => response.json())
  .then(defaultView => {
    // Handle default attendance view
  });

// Create new parent custom view
const newView = {
  viewName: 'Emergency Contacts',
  description: 'Shows parent emergency contact information',
  entityType: 'PARENT',
  selectedFields: ['fullName', 'phone', 'email', 'address'],
  isDefault: false,
  isPublic: true
};

fetch('/api/custom-views?userId=123', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(newView)
})
.then(response => response.json())
.then(createdView => {
  // Handle created view
});
```

### Service Usage

```java
@Autowired
private CustomViewService customViewService;

// Get all student views accessible to user
List<CustomView> studentViews = customViewService.getAccessibleViews(EntityType.STUDENT, userId);

// Create new attendance view
CustomView attendanceView = new CustomView();
attendanceView.setViewName("Daily Attendance");
attendanceView.setEntityType(EntityType.ATTENDANCE);
attendanceView.setSelectedFields(Arrays.asList("studentName", "date", "status"));
CustomView savedView = customViewService.createView(attendanceView, userId);
```

## Benefits

1. **Code Reusability** - Single implementation works for all entity types
2. **Maintainability** - One set of services, controllers, and repositories to maintain
3. **Consistency** - Same API patterns across all entity types
4. **Extensibility** - Easy to add new entity types by updating the enum
5. **Type Safety** - Enum-based entity types prevent invalid configurations

## Database Schema

### custom_views table
```sql
CREATE TABLE custom_views (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    view_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    entity_type VARCHAR(50) NOT NULL,
    selected_fields TEXT,
    is_default BOOLEAN DEFAULT FALSE,
    is_public BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_custom_views_entity_type ON custom_views(entity_type);
CREATE INDEX idx_custom_views_created_by ON custom_views(created_by);
CREATE UNIQUE INDEX idx_custom_views_name_entity ON custom_views(view_name, entity_type);
```