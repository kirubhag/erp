# Dynamic Entity Relationship System - Example Outputs

## Example 1: Fetching Guardian Data with Student Information

### Request
```http
GET /api/entity-relations/guardian/1/with-student
```

### Dynamic SQL Generated
```sql
SELECT 
    c.student_guardian_info_id AS child_id,
    c.father_name AS child_display,
    p.student_id AS parent_id,
    p.first_name AS parent_display,
    c.*,
    p.*
FROM student_guardian_info c
INNER JOIN students p ON c.student_id = p.student_id
WHERE c.student_guardian_info_id = 1;
```

### Response (200 OK)
```json
[
  {
    "child_id": 1,
    "child_display": "Michael Johnson",
    "parent_id": 101,
    "parent_display": "Emma",
    "guardian_id": 1,
    "student_id": 101,
    "father_name": "Michael Johnson",
    "father_occupation": "Software Engineer",
    "father_phone": "+1-555-0101",
    "father_email": "michael.johnson@email.com",
    "mother_name": "Sarah Johnson",
    "mother_occupation": "Teacher",
    "mother_phone": "+1-555-0102",
    "mother_email": "sarah.johnson@email.com",
    "guardian_name": null,
    "guardian_relation": null,
    "guardian_phone": null,
    "guardian_email": null,
    "student_first_name": "Emma",
    "student_last_name": "Johnson",
    "student_email": "emma.johnson@school.edu",
    "student_phone": "+1-555-0103",
    "student_identifier": "STU2024001",
    "date_of_birth": "2010-05-15",
    "grade_level": "Grade 8"
  }
]
```

---

## Example 2: Fetching Medical Data with Student Information

### Request
```http
GET /api/entity-relations/medical/1/with-student
```

### Dynamic SQL Generated
```sql
SELECT 
    c.student_medical_info_id AS child_id,
    c.doctor_name AS child_display,
    p.student_id AS parent_id,
    p.first_name AS parent_display,
    c.*,
    p.*
FROM student_medical_info c
INNER JOIN students p ON c.student_id = p.student_id
WHERE c.student_medical_info_id = 1;
```

### Response (200 OK)
```json
[
  {
    "child_id": 1,
    "child_display": "Dr. Robert Smith",
    "parent_id": 101,
    "parent_display": "Emma",
    "medical_info_id": 1,
    "student_id": 101,
    "allergies": "Peanuts, Shellfish",
    "medical_conditions": "Asthma",
    "medications": "Albuterol inhaler as needed",
    "doctor_name": "Dr. Robert Smith",
    "doctor_phone": "+1-555-0200",
    "hospital_preference": "City General Hospital",
    "insurance_provider": "Blue Cross Blue Shield",
    "insurance_policy_number": "BCB123456789",
    "student_first_name": "Emma",
    "student_last_name": "Johnson",
    "student_email": "emma.johnson@school.edu",
    "student_identifier": "STU2024001",
    "grade_level": "Grade 8"
  }
]
```

---

## Example 3: Fetching Student with All Child Records

### Request
```http
GET /api/entity-relations/student/101/with-children
```

### Response (200 OK)
```json
{
  "parentTable": "students",
  "parentId": 101,
  "student_guardian_info": [
    {
      "column_0": 1,
      "column_1": 101,
      "column_2": "Michael Johnson",
      "column_3": "Software Engineer",
      "column_4": "+1-555-0101",
      "column_5": "michael.johnson@email.com",
      "column_6": "Sarah Johnson",
      "column_7": "Teacher",
      "column_8": "+1-555-0102",
      "column_9": "sarah.johnson@email.com",
      "column_10": null,
      "column_11": null,
      "column_12": null,
      "column_13": null
    }
  ],
  "student_medical_info": [
    {
      "column_0": 1,
      "column_1": 101,
      "column_2": "Peanuts, Shellfish",
      "column_3": "Asthma",
      "column_4": "Albuterol inhaler as needed",
      "column_5": "Dr. Robert Smith",
      "column_6": "+1-555-0200",
      "column_7": "City General Hospital",
      "column_8": "Blue Cross Blue Shield",
      "column_9": "BCB123456789"
    }
  ],
  "attendance": [
    {
      "column_0": 501,
      "column_1": "2025-11-25",
      "column_2": "08:30:00",
      "column_3": "15:30:00",
      "column_4": "PRESENT",
      "column_5": 101
    },
    {
      "column_0": 502,
      "column_1": "2025-11-26",
      "column_2": "08:35:00",
      "column_3": "15:28:00",
      "column_4": "PRESENT",
      "column_5": 101
    }
  ],
  "grades": [
    {
      "column_0": 301,
      "column_1": 101,
      "column_2": "Emma Johnson",
      "column_3": "Grade 8",
      "column_4": "MATH101",
      "column_5": "Mathematics",
      "column_6": "MIDTERM",
      "column_7": 92.5,
      "column_8": 100.0,
      "column_9": 92.5,
      "column_10": "A",
      "column_11": 4.0,
      "column_12": "2025-10-15",
      "column_13": "Fall 2025"
    },
    {
      "column_0": 302,
      "column_1": 101,
      "column_2": "Emma Johnson",
      "column_3": "Grade 8",
      "column_4": "ENG101",
      "column_5": "English Literature",
      "column_6": "MIDTERM",
      "column_7": 88.0,
      "column_8": 100.0,
      "column_9": 88.0,
      "column_10": "B+",
      "column_11": 3.3,
      "column_12": "2025-10-16",
      "column_13": "Fall 2025"
    }
  ]
}
```

---

## Example 4: Getting All Relationships for a Parent Table

### Request
```http
GET /api/entity-relations/parent/students
```

### Response (200 OK)
```json
[
  {
    "id": 1,
    "parentTableName": "students",
    "parentPkid": "student_id",
    "parentDisplayColumn": "first_name",
    "childTableName": "student_guardian_info",
    "childPkid": "student_guardian_info_id",
    "childDisplayColumn": "father_name",
    "foreignKeyColumn": "student_id",
    "description": "Relationship between Student and Guardian information tables",
    "createdBy": "system",
    "createdTime": "2025-11-29T10:00:00",
    "isActive": 1
  },
  {
    "id": 2,
    "parentTableName": "students",
    "parentPkid": "student_id",
    "parentDisplayColumn": "first_name",
    "childTableName": "student_medical_info",
    "childPkid": "student_medical_info_id",
    "childDisplayColumn": "doctor_name",
    "foreignKeyColumn": "student_id",
    "description": "Relationship between Student and Medical information tables",
    "createdBy": "system",
    "createdTime": "2025-11-29T10:00:00",
    "isActive": 1
  },
  {
    "id": 3,
    "parentTableName": "students",
    "parentPkid": "student_id",
    "parentDisplayColumn": "first_name",
    "childTableName": "attendance",
    "childPkid": "attendance_id",
    "childDisplayColumn": "attendance_date",
    "foreignKeyColumn": "student_id",
    "description": "Relationship between Student and Attendance records",
    "createdBy": "system",
    "createdTime": "2025-11-29T10:00:00",
    "isActive": 1
  },
  {
    "id": 4,
    "parentTableName": "students",
    "parentPkid": "student_id",
    "parentDisplayColumn": "first_name",
    "childTableName": "grades",
    "childPkid": "grade_id",
    "childDisplayColumn": "course_name",
    "foreignKeyColumn": "student_id",
    "description": "Relationship between Student and Grade records",
    "createdBy": "system",
    "createdTime": "2025-11-29T10:00:00",
    "isActive": 1
  }
]
```

---

## Example 5: Dynamic Query Building

### Request
```http
GET /api/entity-relations/query/sql/student_guardian_info
```

### Response (200 OK)
```json
{
  "sql": "SELECT c.*, p.* FROM student_guardian_info c INNER JOIN students p ON c.student_id = p.student_id"
}
```

---

## Example 6: Generic Child with Parent Query

### Request
```http
GET /api/entity-relations/query/child-with-parent?childTable=student_guardian_info
```

### Response (200 OK)
```json
[
  {
    "child_id": 1,
    "child_display": "Michael Johnson",
    "parent_id": 101,
    "parent_display": "Emma",
    "column_0": 1,
    "column_1": 101,
    "column_2": "Michael Johnson",
    "column_3": "Software Engineer"
  },
  {
    "child_id": 2,
    "child_display": "David Smith",
    "parent_id": 102,
    "parent_display": "Oliver",
    "column_0": 2,
    "column_1": 102,
    "column_2": "David Smith",
    "column_3": "Doctor"
  }
]
```

---

## Example 7: Filtered Child with Parent Query

### Request
```http
GET /api/entity-relations/query/child-with-parent?childTable=student_medical_info&childId=2
```

### Response (200 OK)
```json
[
  {
    "child_id": 2,
    "child_display": "Dr. Emily Brown",
    "parent_id": 102,
    "parent_display": "Oliver",
    "column_0": 2,
    "column_1": 102,
    "column_2": "None",
    "column_3": "None",
    "column_4": "None",
    "column_5": "Dr. Emily Brown",
    "column_6": "+1-555-0201",
    "column_7": "University Hospital",
    "column_8": "Aetna",
    "column_9": "AET987654321"
  }
]
```

---

## Example 8: Creating a New Relationship

### Request
```http
POST /api/entity-relations
Content-Type: application/json

{
  "parentTableName": "students",
  "parentPkid": "student_id",
  "parentDisplayColumn": "first_name",
  "childTableName": "student_projects",
  "childPkid": "project_id",
  "childDisplayColumn": "project_name",
  "foreignKeyColumn": "student_id",
  "description": "Relationship between Student and Projects"
}
```

### Response (201 Created)
```json
{
  "id": 5,
  "parentTableName": "students",
  "parentPkid": "student_id",
  "parentDisplayColumn": "first_name",
  "childTableName": "student_projects",
  "childPkid": "project_id",
  "childDisplayColumn": "project_name",
  "foreignKeyColumn": "student_id",
  "description": "Relationship between Student and Projects",
  "createdBy": "admin",
  "createdTime": "2025-11-29T14:30:00",
  "modifiedBy": null,
  "modifiedTime": null,
  "ownerId": null,
  "isActive": 1
}
```

---

## Summary

The dynamic entity relationship system provides:

1. **Metadata-Driven Architecture**: All relationships stored in database
2. **Dynamic Query Building**: Automatic JOIN query generation
3. **No Code Changes**: Add relationships via API without modifying code
4. **Flexible Queries**: Generic and specific endpoints for different use cases
5. **Comprehensive Data**: Fetch parent with all children or child with parent
6. **SQL Transparency**: View generated SQL for debugging

This system eliminates hard-coded relationships and provides a scalable solution for managing entity relationships across the ERP system.
