# ERP Data Loading Architecture

## Overview

This document explains the data initialization architecture for the ERP system, clarifying the separation between schema creation and data population.

## Architecture Principles

### Single Source of Truth
All seed data is maintained in **XML files only** to ensure consistency and avoid duplication. SQL schema files contain **only table definitions**, not data.

## Data Loading Flow

```
Application Startup
    ↓
[1] Schema Creation (SQL)
    ├── master_schema.sql → Creates IAM_MasterDB tables
    └── tenant_schema.sql → Creates tenant database tables (via JPA/Hibernate)
    ↓
[2] MasterDbSystemDataInitializer (@Order 1)
    ├── Loads data from XML → IAM_MasterDB
    ├── Uses INSERT ... ON DUPLICATE KEY UPDATE
    └── Populates:
        ├── erp_entities (from erp-entities.xml)
        ├── erp_sections (from **/*_sections.xml)
        ├── erp_fields (from **/*_fields.xml)
        ├── permissions (from sample-permissions.xml)
        ├── roles (from sample-roles.xml)
        └── role_permissions (from sample-roles.xml)
    ↓
[3] TenantProvisioningService
    └── Copies system data from IAM_MasterDB → Tenant DB
        ├── Uses INSERT IGNORE (preserves existing)
        └── Entities, Sections, Fields replicated
```

## File Organization

### SQL Schema Files (Table Creation Only)
- `src/main/resources/master_schema.sql`
  - Creates tables in IAM_MasterDB
  - **No INSERT statements**
  - Contains only CREATE TABLE definitions

- `src/main/resources/scripts/tenant_schema.sql`
  - Creates tables in tenant databases
  - **No INSERT statements**
  - Contains only CREATE TABLE definitions

### XML Data Files (Data Population)
```
src/main/resources/data/
├── erp-entities.xml                    # Entity definitions
├── room/
│   ├── room_sections.xml              # Room sections metadata
│   ├── room_fields.xml                # Room fields metadata
│   └── rooms.xml                       # Sample room data
├── timetable/
│   ├── timetable_sections.xml         # Timetable sections metadata
│   ├── timetable_fields.xml           # Timetable fields metadata
│   └── timetables.xml                  # Sample timetable data
├── student/
│   ├── student_sections.xml
│   ├── student_fields.xml
│   └── sample-students.xml
├── staff/
│   ├── staff_sections.xml
│   ├── staff_fields.xml
│   └── sample-staff.xml
├── attendance/
│   ├── attendance_sections.xml
│   ├── attendance_fields.xml
│   └── attendance.xml
├── grade/
│   ├── grade_sections.xml
│   ├── grade_fields.xml
│   └── grades.xml
├── parent/
│   ├── parent_sections.xml
│   ├── parent_fields.xml
│   └── sample-parents.xml
├── health/
│   ├── health_sections.xml
│   └── health_fields.xml
├── role/
│   └── sample-roles.xml
├── permission/
│   └── sample-permissions.xml
└── organisation/
    └── sample-organisations.xml
```

## Data Loading Components

### 1. MasterDbSystemDataInitializer
**Purpose**: Load system-wide configuration into IAM_MasterDB

**Order**: `@Order(1)` - Runs first

**Loads**:
- ERP Entities (erp-entities.xml)
- ERP Sections (**/*_sections.xml)
- ERP Fields (**/*_fields.xml)
- System Permissions
- System Roles
- Role-Permission mappings

**Database**: IAM_MasterDB (shared across all tenants)

**Strategy**: `INSERT ... ON DUPLICATE KEY UPDATE` (idempotent)

### 2. TenantProvisioningService
**Purpose**: Copy system data to new tenant databases

**Triggered**: When new tenant is created

**Copies from IAM_MasterDB**:
- erp_entities
- erp_sections
- erp_fields
- erp_entity_relations

**Database**: Tenant-specific database

**Strategy**: `INSERT IGNORE` (preserves existing tenant customizations)

### 3. SampleDataController
**Purpose**: Load sample data on-demand per tenant

**Endpoint**: `POST /api/v1/sample-data/populate`

**Loads**: Entity-specific sample data (students, staff, attendance, etc.)

**Database**: Tenant database (current context)

**Strategy**: User-initiated via UI

## Why XML Instead of SQL?

### Advantages of XML Data Files

1. **Single Source of Truth**
   - One place to update entity definitions
   - No risk of SQL and code being out of sync

2. **Update Mechanism**
   - `ON DUPLICATE KEY UPDATE` allows hot-fixes
   - Schema changes don't require database recreation

3. **Validation**
   - Java code validates XML before insertion
   - Type-safe mappings to Java entities

4. **Version Control**
   - Clear diffs in XML show what changed
   - Merge conflicts easier to resolve

5. **Tenant Customization**
   - Master data in IAM_MasterDB
   - Tenants can override via `INSERT IGNORE`
   - Customizations preserved on updates

### Problems with SQL INSERT Statements

❌ **Duplication**: Same data in SQL and XML  
❌ **Inconsistency**: SQL and XML could diverge  
❌ **Hard to Update**: Requires ALTER scripts  
❌ **No Type Safety**: SQL strings prone to errors  
❌ **Merge Conflicts**: Variable IDs cause conflicts  

## Common Issues Resolved

### Issue: Wrong Icons in Tenant Database
**Root Cause**: SQL schema had hardcoded INSERT with Material Icons  
**Solution**: Removed INSERT from SQL, data now flows from XML → Master → Tenant  
**Prevention**: All data in XML only, SQL contains only CREATE TABLE

### Issue: Entity Not Showing in Navbar
**Root Cause**: Entity defined in SQL but not in erp-entities.xml  
**Solution**: All entities must be in erp-entities.xml  
**Prevention**: MasterDbSystemDataInitializer loads from XML only

### Issue: Section IDs Mismatch
**Root Cause**: SQL INSERT used hardcoded IDs, XML auto-incremented differently  
**Solution**: SQL removed, XML loading uses ON DUPLICATE KEY UPDATE  
**Prevention**: No manual ID assignment in XML or SQL

## Best Practices

### Adding a New Entity

1. **Add to erp-entities.xml**
   ```xml
   <entity>
       <id>16</id>
       <singular_name>Library Book</singular_name>
       <plural_name>Library Books</plural_name>
       <system_name>LIBRARY_BOOK</system_name>
       <icon>fas fa-book</icon>
       <route>#!/library-books</route>
       <table_name>library_books</table_name>
       <pkid>book_id</pkid>
       <display_column>title</display_column>
   </entity>
   ```

2. **Create sections XML**: `data/library/library_sections.xml`

3. **Create fields XML**: `data/library/library_fields.xml`

4. **Create sample data XML** (optional): `data/library/library_books.xml`

5. **Add table to schema**: `master_schema.sql` (CREATE TABLE only)

6. **Restart application**: MasterDbSystemDataInitializer loads automatically

### Updating Entity Metadata

1. **Update XML file** (e.g., change icon in erp-entities.xml)
2. **Restart application** (or trigger reload endpoint)
3. **ON DUPLICATE KEY UPDATE** refreshes master DB
4. **Tenants get update** on next provisioning or manual refresh

### DO NOT

❌ Add INSERT statements to master_schema.sql  
❌ Add INSERT statements to tenant_schema.sql  
❌ Hardcode data in Java @PostConstruct methods  
❌ Duplicate entity definitions across files  
❌ Use fixed IDs for auto-increment fields  

### ALWAYS

✅ Define entities in erp-entities.xml  
✅ Create *_sections.xml for each entity  
✅ Create *_fields.xml for each entity  
✅ Let MasterDbSystemDataInitializer load data  
✅ Use ON DUPLICATE KEY UPDATE for updates  

## Debugging Data Issues

### Check Master Database
```sql
-- Connect to master DB
mysql -h localhost -P 3307 -u root IAM_MasterDB

-- Check entities
SELECT singular_name, system_name, icon FROM erp_entities;

-- Check sections for entity
SELECT section_name, section_label FROM erp_sections WHERE entity_type = 'ROOM';

-- Check fields for entity
SELECT field_name, field_label, section_id FROM erp_fields WHERE entity_type = 'ROOM';
```

### Check Tenant Database
```sql
-- Connect to tenant DB
mysql -h localhost -P 3307 -u root erpdb27aab20c

-- Check entities (system_name may differ in tenants)
SELECT singular_name, system_name, icon FROM erp_entities;

-- Verify data was copied from master
SELECT COUNT(*) FROM erp_sections;
SELECT COUNT(*) FROM erp_fields;
```

### Check Initialization Logs
```bash
# Look for MasterDbSystemDataInitializer logs
grep "MasterDbSystemDataInitializer" logs/spring-boot-app.log

# Check if XML files were loaded
grep "Loading ERP entities" logs/spring-boot-app.log
grep "Loaded .* ERP entities" logs/spring-boot-app.log
```

### Verify XML Files Are Found
```bash
# List all section XML files
find src/main/resources/data -name "*_sections.xml"

# List all field XML files
find src/main/resources/data -name "*_fields.xml"

# Check erp-entities.xml
cat src/main/resources/data/erp-entities.xml | grep system_name
```

## Migration Notes

### Previous Architecture (❌ Deprecated)
- SQL files contained both CREATE TABLE and INSERT statements
- Data duplicated between SQL and XML
- Inconsistencies between master_schema.sql and tenant_schema.sql
- Material Icons in SQL, FontAwesome in XML

### Current Architecture (✅ Active)
- SQL files contain **only CREATE TABLE** statements
- XML files are the **single source of truth** for data
- MasterDbSystemDataInitializer loads all data from XML
- TenantProvisioningService copies from master to tenant
- Consistent icons, metadata, and entity definitions

## Future Enhancements

1. **Hot Reload Endpoint**
   - Allow reloading XML without restart
   - Useful for production metadata updates

2. **XML Validation**
   - XSD schema for XML files
   - Pre-startup validation

3. **Migration Scripts**
   - Automatic schema versioning
   - Rollback support for XML changes

4. **Tenant Customization UI**
   - Allow tenants to override master metadata
   - Preserve customizations during updates

5. **Audit Trail**
   - Track XML changes over time
   - Metadata version history

## Summary

| Aspect | Solution |
|--------|----------|
| **Schema Definition** | SQL files (CREATE TABLE only) |
| **Data Population** | XML files loaded by Java |
| **Master Database** | MasterDbSystemDataInitializer |
| **Tenant Database** | TenantProvisioningService (copies from master) |
| **Update Strategy** | ON DUPLICATE KEY UPDATE (master), INSERT IGNORE (tenant) |
| **Sample Data** | SampleDataController (on-demand) |
| **Single Source** | XML files in src/main/resources/data/ |

**Key Principle**: SQL creates structure, XML populates data. Never mix the two.
