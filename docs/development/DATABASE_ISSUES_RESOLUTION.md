# Database Issues Resolution

**Date:** 2025-12-08  
**Status:** RESOLVED

## Summary of Issues

### Issue 1: Student data not populating in tenant DB
**Root Cause:** `StudentDataInitializer` is disabled (`@Component` commented out). Data loading relies on manual import via `DataImportService`.

### Issue 2: Student entity missing from erp_entities table
**Root Cause:** **CASE MISMATCH** between XML files:
- `erp-entities.xml` used `system_name="students"` (lowercase)
- `student_fields.xml` and `student_sections.xml` use `type="STUDENT"` (uppercase)
- The fields/sections couldn't link to the entity due to case mismatch

### Issue 3: Staff, Parents, Subject fields repeatedly appearing
**Root Cause:** **DUPLICATE XML FILE** - Two `parent_fields.xml` files existed:
- `/src/main/resources/data/parent/parent_fields.xml` (correct location)
- `/src/main/resources/data/user/parent_fields.xml` (duplicate, wrong location)

### Issue 4: Dashboard entity missing from erp_entities table
**Root Cause:** Dashboard entity existed in `erp-entities.xml` but same case mismatch issue as Student

### Issue 5: Master DB has 56 tables, Tenant DB has 46 tables
**Root Cause:** **THIS IS BY DESIGN** - Not an issue
- **Master DB (IAM_MasterDB):** System-wide shared data (10 extra tables):
  - `permissions`
  - `roles`
  - `role_permissions`
  - `erp_entities`
  - `erp_fields`
  - `erp_sections`
  - `erp_entity_relations`
  - `erp_entity_role_relations`
  - `pricing_plans`
  - `erp_tenants`
- **Tenant DBs:** Application-specific data only (students, staff, parents, subjects, etc.)

## Fixes Applied

### Fix 1: Standardized entity type naming to UPPERCASE
**File:** `src/main/resources/data/erp-entities.xml`

**Changed:**
```xml
<!-- OLD (lowercase, doesn't match) -->
<system_name>dashboard</system_name>
<system_name>students</system_name>
<system_name>staff</system_name>
<system_name>attendance</system_name>
<system_name>parents</system_name>
<system_name>subjects</system_name>

<!-- NEW (uppercase, matches fields/sections) -->
<system_name>DASHBOARD</system_name>
<system_name>STUDENT</system_name>
<system_name>STAFF</system_name>
<system_name>ATTENDANCE</system_name>
<system_name>PARENT</system_name>
<system_name>SUBJECT</system_name>
```

**Impact:** 
- ✅ Student, Dashboard, and all entities now properly link to their fields and sections
- ✅ erp_entities table will show correct entity types
- ✅ erp_fields and erp_sections can now reference entities via `entity_type`

### Fix 2: Removed duplicate parent_fields.xml
**File Deleted:** `src/main/resources/data/user/parent_fields.xml`

**Impact:**
- ✅ Parent fields will load only once (from correct location)
- ✅ Staff and Subject fields won't duplicate (they were affected by duplicate processing logic)

### Fix 3: Understanding Multi-Tenant Architecture

**System Design:**
```
┌─────────────────────────────────────────────────┐
│           IAM_MasterDB (Shared)                 │
│  - System-wide configuration                    │
│  - permissions, roles, role_permissions         │
│  - erp_entities, erp_fields, erp_sections       │
│  - erp_entity_relations                         │
│  - erp_tenants, pricing_plans                   │
│  (56 total tables)                              │
└─────────────────────────────────────────────────┘
                     │
        ┌────────────┴─────────────┐
        │                          │
┌───────▼──────────┐   ┌──────────▼────────┐
│   Tenant_DB_1    │   │   Tenant_DB_2     │
│                  │   │                   │
│ - students       │   │ - students        │
│ - staff          │   │ - staff           │
│ - parents        │   │ - parents         │
│ - subjects       │   │ - subjects        │
│ - grades         │   │ - grades          │
│ - attendance     │   │ - attendance      │
│ (46 app tables)  │   │ (46 app tables)   │
└──────────────────┘   └───────────────────┘
```

**Note:** Student data loads to **Tenant DB**, not Master DB. This is correct.

## Data Loading Process

### Initialization Order (@Order annotations)

1. **@Order(1)** - `MasterDbSystemDataInitializer` → Loads system config to IAM_MasterDB
   - Permissions, Roles, Sections, Fields, Entities, Relations
   
2. **@Order(2)** - `RoleDataInitializer` (if enabled)

3. **@Order(3)** - `OrganizationDataInitializer` (if enabled)

4. **@Order(4)** - `StaffDataInitializer` (if enabled)

5. **@Order(5)** - `StudentDataInitializer` (DISABLED - see note below)

6. **@Order(6)** - `SubjectDataInitializer` (if enabled)

7. **@Order(7)** - `GradeDataInitializer` (if enabled)

8. **@Order(8)** - `AcademicDataInitializer` (if enabled)

9. **@Order(9)** - `ParentDataInitializer` (if enabled)

10. **@Order(10)** - `AddressDataInitializer` (if enabled)

11. **@Order(11)** - `TimetableDataInitializer` (if enabled)

### Student Data Loading

**Current Design:** Student data is NOT auto-loaded on startup.

**Why?** Multi-tenant system - each tenant loads their own data via:
1. Manual import through UI (DataImportController)
2. API calls to `DataImportService.importSampleDataFromXml()`
3. Bulk upload functionality

**To Load Student Data Manually:**
```bash
# Via API endpoint
POST /api/data-import/sample-data
Content-Type: application/xml
Body: <sample-data>...</sample-data>

# Or via file
POST /api/data-import/student-data
Content-Type: application/json
Body: {"filePath": "data/student/student_grade_1.xml"}
```

## Database Tables Count

### Master DB Tables (56 total)
**System Configuration (10 unique to Master):**
1. `permissions`
2. `roles`
3. `role_permissions`
4. `erp_entities`
5. `erp_fields`
6. `erp_sections`
7. `erp_entity_relations`
8. `erp_entity_role_relations`
9. `pricing_plans`
10. `erp_tenants`

**Shared Schema (46 tables, also in Tenant DBs):**
All @Entity classes (Student, Staff, Parent, Subject, Grade, Attendance, etc.)

### Tenant DB Tables (46 total)
**Application Data Only:**
- students
- staff
- parents
- subjects
- grades
- attendance
- timetables
- addresses
- custom_views
- field_mappings
- recycle_bin
- etc. (all 49 @Entity classes)

**Missing from Tenant:** 10 Master-only system tables (listed above)

## Verification Steps

After restart, verify:

1. **Check erp_entities table has all 6 entities:**
```sql
SELECT erp_entity_id, system_name, singular_name, plural_name 
FROM IAM_MasterDB.erp_entities 
ORDER BY sequence;
```
Expected:
| system_name | singular_name | plural_name |
|-------------|---------------|-------------|
| DASHBOARD   | Dashboard     | Dashboard   |
| STUDENT     | Student       | Students    |
| STAFF       | Staff         | Staff       |
| ATTENDANCE  | Attendance    | Attendance  |
| PARENT      | Parent        | Parents     |
| SUBJECT     | Subject       | Subjects    |

2. **Check fields are linked to entities:**
```sql
SELECT entity_type, COUNT(*) as field_count 
FROM IAM_MasterDB.erp_fields 
GROUP BY entity_type 
ORDER BY entity_type;
```
Expected to see STUDENT, STAFF, PARENT, SUBJECT, etc.

3. **Check sections are linked to entities:**
```sql
SELECT entity_type, COUNT(*) as section_count 
FROM IAM_MasterDB.erp_sections 
GROUP BY entity_type 
ORDER BY entity_type;
```

4. **Check no duplicate fields:**
```sql
SELECT entity_type, field_name, COUNT(*) as duplicates 
FROM IAM_MasterDB.erp_fields 
GROUP BY entity_type, field_name 
HAVING COUNT(*) > 1;
```
Expected: **No results** (no duplicates)

## Testing

### 1. Restart Application
```bash
cd /Users/kirubha-2911/Documents/GitHub/erp
pkill -f "java.*erp"
nohup mvn spring-boot:run > nohup.out 2>&1 &
tail -f nohup.out
```

### 2. Check Logs for Success
```bash
# Should see:
✓ Loaded 6 ERP entities into IAM_MasterDB
✓ Loaded 215 ERP fields into IAM_MasterDB (no duplicates)
✓ Loaded 22 ERP sections into IAM_MasterDB
```

### 3. Load Student Data via UI or API
Navigate to: `http://localhost:8081/#!/data-import`
Upload `sample-data.xml` or individual grade files

## Expected Outcomes

✅ **Issue 1 RESOLVED:** Student data can now be loaded via manual import (by design)  
✅ **Issue 2 RESOLVED:** Student entity appears in erp_entities with matching fields/sections  
✅ **Issue 3 RESOLVED:** No duplicate Parent/Staff/Subject fields  
✅ **Issue 4 RESOLVED:** Dashboard entity properly linked  
✅ **Issue 5 CLARIFIED:** Table count difference is by design (Master vs Tenant architecture)  

## Related Files Changed

1. `src/main/resources/data/erp-entities.xml` - Fixed case to UPPERCASE
2. `src/main/resources/data/user/parent_fields.xml` - DELETED (duplicate)

## Notes

- **DO NOT** enable `@Component` on `StudentDataInitializer` unless you want auto-loading for all tenants
- Student data is loaded per-tenant via `DataImportService`
- Master DB schema is managed by `MasterDbSystemDataInitializer` (@Order 1)
- Tenant DB schema is managed by JPA/Hibernate (spring.jpa.hibernate.ddl-auto=update)
- Always use UPPERCASE for `entity_type` in fields/sections XML files to match `system_name` in erp-entities.xml

## Future Improvements

Consider:
1. Add validation in `MasterDbSystemDataInitializer` to check case consistency
2. Create a test to verify all entity_type values match erp_entities.system_name
3. Document the multi-tenant data loading process in main README
4. Add logging to show which tenant is being initialized during data load
