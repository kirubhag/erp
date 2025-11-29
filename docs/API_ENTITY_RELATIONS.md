# ERP Entity Relationship Management API Documentation

## Overview
This API provides dynamic relationship management between ERP entities, allowing automatic joining of parent and child tables without hard-coding relationships.

## Base URL
```
http://localhost:8081/api/entity-relations
```

## Endpoints

### 1. Create Entity Relationship
**POST** `/api/entity-relations`

Creates a new relationship between parent and child entities.

**Request Body:**
```json
{
  "parentTableName": "students",
  "parentPkid": "student_id",
  "parentDisplayColumn": "first_name",
  "childTableName": "student_guardian_info",
  "childPkid": "student_guardian_info_id",
  "childDisplayColumn": "father_name",
  "foreignKeyColumn": "student_id",
  "description": "Relationship between Student and Guardian information"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "parentTableName": "students",
  "parentPkid": "student_id",
  "parentDisplayColumn": "first_name",
  "childTableName": "student_guardian_info",
  "childPkid": "student_guardian_info_id",
  "childDisplayColumn": "father_name",
  "foreignKeyColumn": "student_id",
  "description": "Relationship between Student and Guardian information",
  "createdBy": "admin",
  "createdTime": "2025-11-29T10:00:00",
  "isActive": 1
}
```

---

### 2. Update Entity Relationship
**PUT** `/api/entity-relations/{id}`

Updates an existing relationship.

**Path Parameters:**
- `id` - Relationship ID

**Request Body:** Same as Create

**Response:** `200 OK` with updated relationship object

---

### 3. Delete Entity Relationship
**DELETE** `/api/entity-relations/{id}`

Deletes a relationship.

**Path Parameters:**
- `id` - Relationship ID

**Response:** `204 No Content`

---

### 4. Get Relationship by ID
**GET** `/api/entity-relations/{id}`

Retrieves a specific relationship.

**Response:** `200 OK`
```json
{
  "id": 1,
  "parentTableName": "students",
  "parentPkid": "student_id",
  "childTableName": "student_guardian_info",
  "foreignKeyColumn": "student_id"
}
```

---

### 5. Get All Relationships
**GET** `/api/entity-relations`

Retrieves all entity relationships.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "parentTableName": "students",
    "childTableName": "student_guardian_info",
    "foreignKeyColumn": "student_id"
  },
  {
    "id": 2,
    "parentTableName": "students",
    "childTableName": "student_medical_info",
    "foreignKeyColumn": "student_id"
  }
]
```

---

### 6. Get Child Relationships for Parent Table
**GET** `/api/entity-relations/parent/{tableName}`

Retrieves all child relationships for a parent table.

**Path Parameters:**
- `tableName` - Parent table name (e.g., "students")

**Example:**
```
GET /api/entity-relations/parent/students
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "parentTableName": "students",
    "childTableName": "student_guardian_info",
    "foreignKeyColumn": "student_id"
  },
  {
    "id": 2,
    "parentTableName": "students",
    "childTableName": "student_medical_info",
    "foreignKeyColumn": "student_id"
  }
]
```

---

### 7. Get Parent Relationships for Child Table
**GET** `/api/entity-relations/child/{tableName}`

Retrieves all parent relationships for a child table.

**Path Parameters:**
- `tableName` - Child table name (e.g., "student_guardian_info")

**Example:**
```
GET /api/entity-relations/child/student_guardian_info
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "parentTableName": "students",
    "childTableName": "student_guardian_info",
    "foreignKeyColumn": "student_id"
  }
]
```

---

### 8. Fetch Child Data with Parent (Dynamic Join)
**GET** `/api/entity-relations/query/child-with-parent`

Dynamically joins child table with parent table based on relationship metadata.

**Query Parameters:**
- `childTable` - Child table name (required)
- `childId` - Child record ID (optional, filters results)

**Example:**
```
GET /api/entity-relations/query/child-with-parent?childTable=student_guardian_info&childId=1
```

**Response:** `200 OK`
```json
[
  {
    "child_id": 1,
    "child_display": "John Smith Sr.",
    "parent_id": 100,
    "parent_display": "John Smith Jr.",
    "column_0": 1,
    "column_1": "John Smith Sr.",
    "column_2": "Engineer",
    "column_3": "+1234567890",
    "column_4": 100
  }
]
```

---

### 9. Fetch Parent Data with All Children (Dynamic Join)
**GET** `/api/entity-relations/query/parent-with-children`

Fetches parent record with all related child records.

**Query Parameters:**
- `parentTable` - Parent table name (required)
- `parentId` - Parent record ID (required)

**Example:**
```
GET /api/entity-relations/query/parent-with-children?parentTable=students&parentId=100
```

**Response:** `200 OK`
```json
{
  "parentTable": "students",
  "parentId": 100,
  "student_guardian_info": [
    {
      "column_0": 1,
      "column_1": "John Smith Sr.",
      "column_2": "Engineer"
    }
  ],
  "student_medical_info": [
    {
      "column_0": 1,
      "column_1": "Dr. Jane Doe",
      "column_2": "Peanut allergy"
    }
  ]
}
```

---

### 10. Get Dynamic SQL Query
**GET** `/api/entity-relations/query/sql/{childTable}`

Returns the SQL query that would be executed for joining child with parent.

**Path Parameters:**
- `childTable` - Child table name

**Example:**
```
GET /api/entity-relations/query/sql/student_guardian_info
```

**Response:** `200 OK`
```json
{
  "sql": "SELECT c.*, p.* FROM student_guardian_info c INNER JOIN students p ON c.student_id = p.student_id"
}
```

---

### 11. Fetch Guardian with Student (Specific)
**GET** `/api/entity-relations/guardian/{id}/with-student`

Specific endpoint to fetch Guardian information with related Student data.

**Path Parameters:**
- `id` - Guardian record ID

**Example:**
```
GET /api/entity-relations/guardian/1/with-student
```

**Response:** `200 OK` with joined data

---

### 12. Fetch Medical with Student (Specific)
**GET** `/api/entity-relations/medical/{id}/with-student`

Specific endpoint to fetch Medical information with related Student data.

**Path Parameters:**
- `id` - Medical record ID

**Example:**
```
GET /api/entity-relations/medical/1/with-student
```

**Response:** `200 OK` with joined data

---

### 13. Fetch Student with All Children (Specific)
**GET** `/api/entity-relations/student/{id}/with-children`

Specific endpoint to fetch Student with all Guardian and Medical records.

**Path Parameters:**
- `id` - Student ID

**Example:**
```
GET /api/entity-relations/student/100/with-children
```

**Response:** `200 OK`
```json
{
  "parentTable": "students",
  "parentId": 100,
  "student_guardian_info": [...],
  "student_medical_info": [...]
}
```

---

## Example Usage Scenarios

### Scenario 1: Setting up Student → Guardian Relationship
```bash
# Step 1: Create relationship metadata
curl -X POST http://localhost:8081/api/entity-relations \
  -H "Content-Type: application/json" \
  -d '{
    "parentTableName": "students",
    "parentPkid": "student_id",
    "parentDisplayColumn": "first_name",
    "childTableName": "student_guardian_info",
    "childPkid": "student_guardian_info_id",
    "childDisplayColumn": "father_name",
    "foreignKeyColumn": "student_id",
    "description": "Student to Guardian relationship"
  }'

# Step 2: Query Guardian data with Student info
curl http://localhost:8081/api/entity-relations/query/child-with-parent?childTable=student_guardian_info&childId=1
```

### Scenario 2: Fetching Student with All Related Data
```bash
# Get all relationships for students table
curl http://localhost:8081/api/entity-relations/parent/students

# Fetch student with all guardian and medical records
curl http://localhost:8081/api/entity-relations/student/100/with-children
```

### Scenario 3: Dynamic Query Building
```bash
# Get the SQL that will be executed
curl http://localhost:8081/api/entity-relations/query/sql/student_guardian_info

# Execute the query
curl http://localhost:8081/api/entity-relations/query/child-with-parent?childTable=student_guardian_info
```

---

## Error Responses

### 404 Not Found
```json
{
  "timestamp": "2025-11-29T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "No relationship found for child table: invalid_table"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2025-11-29T10:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Error executing dynamic query"
}
```

---

## Database Schema

### erp_entities Table (Updated)
```sql
CREATE TABLE erp_entities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    singular_name VARCHAR(100) NOT NULL UNIQUE,
    plural_name VARCHAR(100) NOT NULL,
    table_name VARCHAR(100),           -- NEW
    pkid VARCHAR(100),                 -- NEW
    display_column VARCHAR(100),       -- NEW
    has_rel_table BOOLEAN DEFAULT false, -- NEW
    is_active BOOLEAN DEFAULT true,
    ...
);
```

### erp_entity_relation Table (New)
```sql
CREATE TABLE erp_entity_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    p_table_name VARCHAR(100) NOT NULL,
    p_pkid VARCHAR(100) NOT NULL,
    p_display_column VARCHAR(100),
    c_table_name VARCHAR(100) NOT NULL,
    c_pkid VARCHAR(100) NOT NULL,
    c_display_column VARCHAR(100),
    fk_column VARCHAR(100) NOT NULL,
    description TEXT,
    created_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    is_active INT DEFAULT 1
);
```

---

## Benefits

1. **No Hard-Coding**: Relationships are defined in metadata, not code
2. **Dynamic Queries**: Automatically build JOIN queries based on relationships
3. **Flexible**: Easy to add new relationships without code changes
4. **Maintainable**: Central location for relationship management
5. **Scalable**: Works with any parent-child table relationship

---

## Next Steps

1. Test API endpoints with sample data
2. Implement frontend integration
3. Add authentication/authorization
4. Implement caching for frequently accessed relationships
5. Add support for many-to-many relationships
6. Implement relationship validation
