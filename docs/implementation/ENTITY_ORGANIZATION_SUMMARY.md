# Entity Organization and Initialization Summary

## Overview
All ERP entities are now properly organized with entity-specific folders, field definition files, and dedicated initializers with proper execution order.

## Entity Organization Structure

### Data Directory Layout
```
src/main/resources/data/
├── address/
│   ├── address_fields.xml          (13 fields)
│   └── sample-addresses.xml        (8352+ records)
├── assignment/
│   └── assignment_fields.xml       (fields)
├── attendance/
│   └── attendance_fields.xml       (fields)
├── course/
│   └── course_fields.xml           (fields)
├── exam/
│   └── exam_fields.xml             (fields)
├── grade/
│   ├── grade_fields.xml            (20 fields)
│   └── grades.xml                  (grade records)
├── health/
│   └── health_fields.xml           (fields)
├── organisation/
│   └── sample-organisations.xml    (organization records)
├── permission/
│   └── sample-permissions.xml      (permission records)
├── role/
│   └── sample-roles.xml            (role records)
├── staff/
│   ├── sample-staff.xml            (staff records)
│   └── staff_fields.xml            (fields)
├── student/
│   ├── student_fields.xml          (fields)
│   ├── grade_fields.xml            (duplicate ref)
│   ├── student_kindergarten.xml    (K grade data)
│   └── student_grade_1-12.xml      (1st-12th grade data)
├── subject/
│   ├── subject_fields.xml          (12 fields)
│   └── subjects.xml                (subject records)
├── timetable/
│   ├── timetable_fields.xml        (18 fields)
│   └── timetables.xml              (timetable records)
└── user/
    ├── sample-users.xml            (user records)
    ├── user_fields.xml             (fields)
    └── parent_fields.xml           (parent entity fields)
```

## Initializer Execution Order

Initializers are executed in the following sequence based on @Order annotations:

| Order | Initializer | Entity Types | Purpose |
|-------|-----------|-----------|---------|
| @Order(0) | **SampleDataInitializer** | Permission, Role, Organization, Staff, User | Loads core system entities (base dependencies) |
| @Order(6) | **StudentDataInitializer** | Student | Loads student data from 13 grade XML files (K-12) |
| @Order(7) | **GradeDataInitializer** | Grade | Loads academic grades and marks |
| @Order(8) | **SubjectDataInitializer** | Subject | Loads available subjects/courses |
| @Order(9) | **TimetableDataInitializer** | Timetable | Loads class schedules and timetables |
| @Order(10) | **AddressDataInitializer** | Address | Loads addresses for all entity types |

## Field Definition Files

Each entity now has a comprehensive field definition XML file following the standard format:

### Common Field Attributes
- **fieldName**: Property name in Java entity
- **fieldLabel**: Display label for UI
- **fieldType**: Data type (STRING, INTEGER, DECIMAL, DATE, DATETIME, BOOLEAN, ENUM, TEXT, etc.)
- **fieldCategory**: Grouping (System, Personal, Contact, Academic, Status, etc.)
- **displayOrder**: UI display sequence
- **defaultWidth**: Default column width in pixels
- **isRequired**: Whether field is mandatory
- **isSearchable**: Whether field can be searched
- **isSortable**: Whether field can be sorted
- **fieldDescription**: Human-readable description

### Entity Field Counts
- **Address**: 13 fields (entity reference, address components, type, status)
- **Grade**: 20 fields (student, course, marks, exam, academic info)
- **Subject**: 12 fields (code, name, category, credits, difficulty, status)
- **Timetable**: 18 fields (schedule, location, subject, teacher, lab info)
- **Student**: Fields inherited with additional academic attributes
- **Staff**: Fields with experience and specialization
- **User**: Fields with authentication and authorization attributes
- **Permission**: Fields for permission management
- **Role**: Fields for role definition
- **Organization**: Fields for org structure
- **Other Entities**: Assignment, Attendance, Course, Exam, Health (fields present)

## Initialization Features

### SampleDataInitializer (@Order(0))
- **Entities**: Permission → Role → Organization → Staff → User (dependency order)
- **Features**:
  - Idempotent loading (checks if data exists)
  - Dependency-aware ordering
  - Comprehensive error handling
  - Detailed logging with summary reports
  - Atomic transaction handling

### Specialized Initializers (@Order 6-10)
- **StudentDataInitializer**: Loads K-12 student data (13 XML files)
- **GradeDataInitializer**: Loads grade records with duplicate checking
- **SubjectDataInitializer**: Loads subjects with validation
- **TimetableDataInitializer**: Loads timetable entries
- **AddressDataInitializer**: Loads polymorphic address records

## Data Files Organized

All data files are now properly organized:
- ✅ grades.xml → grade/grades.xml
- ✅ subjects.xml → subject/subjects.xml
- ✅ timetables.xml → timetable/timetables.xml
- ✅ sample-addresses.xml → address/sample-addresses.xml
- ✅ Student grades: student_grade_1.xml through student_grade_12.xml
- ✅ Student kindergarten: student_kindergarten.xml

## Key Benefits

1. **Clear Organization**: Each entity has its own folder with data and metadata
2. **Complete Field Definitions**: All entities have documented field metadata for UI generation
3. **Proper Initialization Order**: Dependencies respected through @Order annotations
4. **Idempotent Loading**: Safe to restart without duplicate data
5. **Comprehensive Logging**: Full audit trail of what was loaded
6. **Error Handling**: Robust exception handling and recovery
7. **Scalability**: Easy to add new entities following the same pattern

## Migration Path

To add a new entity:
1. Create folder: `data/entityname/`
2. Create field file: `data/entityname/entityname_fields.xml` (13-20 fields)
3. Create data file: `data/entityname/sample-entityname.xml`
4. Create initializer: `src/main/java/krs/erp/initializer/EntityNameDataInitializer.java`
5. Set appropriate @Order value (after dependencies)
6. Run tests to verify loading

## Compilation Status

✅ **BUILD SUCCESS** - All 117 source files compile without errors
- Proper imports and dependencies resolved
- All file paths updated to new structure
- Initializer execution order properly configured
