# Quick Start Guide - Dynamic Entity Relationship System

## Prerequisites
- Spring Boot application running on port 8081
- MySQL 9.x database with ERP schema
- Sample data loaded (students, guardian, medical records)

## Step-by-Step Testing Guide

### 1. Start the Application
```bash
cd /Users/kirubha-2911/Documents/GitHub/erp
./mvnw spring-boot:run
```

Wait for the message: `Started ErpApplication in X.XXX seconds`

### 2. Verify Database Tables Created
```bash
mysql -u root -p -P 3307 -h localhost erp_database

# Check erp_entities table
DESCRIBE erp_entities;

# Check erp_entity_relation table
DESCRIBE erp_entity_relation;
```

### 3. Insert Sample Relationship Data
```bash
mysql -u root -p -P 3307 -h localhost erp_database < src/main/resources/data/sample-entity-relations.sql
```

### 4. Test API Endpoints

#### Test 1: Get All Relationships
```bash
curl -X GET http://localhost:8081/api/entity-relations | jq
```

Expected: List of all defined relationships

#### Test 2: Get Child Relationships for Students
```bash
curl -X GET http://localhost:8081/api/entity-relations/parent/students | jq
```

Expected: Guardian, Medical, Attendance, Grades relationships

#### Test 3: Create New Relationship
```bash
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
  }' | jq
```

Expected: 201 Created with relationship object

#### Test 4: Get Dynamic SQL Query
```bash
curl -X GET http://localhost:8081/api/entity-relations/query/sql/student_guardian_info | jq
```

Expected: 
```json
{
  "sql": "SELECT c.*, p.* FROM student_guardian_info c INNER JOIN students p ON c.student_id = p.student_id"
}
```

#### Test 5: Execute Dynamic Query - Guardian with Student
```bash
curl -X GET "http://localhost:8081/api/entity-relations/query/child-with-parent?childTable=student_guardian_info" | jq
```

Expected: Guardian records joined with Student information

#### Test 6: Execute Dynamic Query - Medical with Student
```bash
curl -X GET "http://localhost:8081/api/entity-relations/query/child-with-parent?childTable=student_medical_info" | jq
```

Expected: Medical records joined with Student information

#### Test 7: Get Student with All Children
```bash
# Replace {studentId} with actual student ID
curl -X GET http://localhost:8081/api/entity-relations/student/1/with-children | jq
```

Expected: Student data with guardian, medical, attendance, and grades

#### Test 8: Get Specific Guardian with Student
```bash
# Replace {guardianId} with actual guardian ID
curl -X GET http://localhost:8081/api/entity-relations/guardian/1/with-student | jq
```

Expected: Guardian record with related Student data

#### Test 9: Get Specific Medical with Student
```bash
# Replace {medicalId} with actual medical ID
curl -X GET http://localhost:8081/api/entity-relations/medical/1/with-student | jq
```

Expected: Medical record with related Student data

#### Test 10: Update Relationship
```bash
# Replace {relationId} with actual relationship ID
curl -X PUT http://localhost:8081/api/entity-relations/1 \
  -H "Content-Type: application/json" \
  -d '{
    "parentTableName": "students",
    "parentPkid": "student_id",
    "parentDisplayColumn": "last_name",
    "childTableName": "student_guardian_info",
    "childPkid": "student_guardian_info_id",
    "childDisplayColumn": "mother_name",
    "foreignKeyColumn": "student_id",
    "description": "Updated relationship description"
  }' | jq
```

Expected: 200 OK with updated relationship

#### Test 11: Delete Relationship
```bash
# Replace {relationId} with actual relationship ID
curl -X DELETE http://localhost:8081/api/entity-relations/99
```

Expected: 204 No Content

### 5. Verify Data in Database

#### Check Relationships
```sql
SELECT * FROM erp_entity_relation;
```

#### Check ERP Entities with Metadata
```sql
SELECT 
    id, 
    singular_name, 
    table_name, 
    pkid, 
    display_column, 
    has_rel_table 
FROM erp_entities 
WHERE table_name IS NOT NULL;
```

#### Test Generated Query Manually
```sql
-- Guardian with Student
SELECT 
    c.student_guardian_info_id AS child_id,
    c.father_name AS child_display,
    p.student_id AS parent_id,
    p.first_name AS parent_display,
    c.*,
    p.*
FROM student_guardian_info c
INNER JOIN students p ON c.student_id = p.student_id
LIMIT 5;
```

### 6. Using Postman

#### Import Collection
1. Create new Postman collection: "ERP Entity Relations"
2. Add requests for all 13 endpoints
3. Set base URL: `http://localhost:8081/api/entity-relations`

#### Sample Requests

**Create Relationship:**
- Method: POST
- URL: `{{baseUrl}}`
- Body: JSON (see Test 3 above)

**Get All:**
- Method: GET
- URL: `{{baseUrl}}`

**Dynamic Query:**
- Method: GET
- URL: `{{baseUrl}}/query/child-with-parent?childTable=student_guardian_info`

### 7. Common Issues & Troubleshooting

#### Issue: 404 Not Found
**Solution:** Check relationship exists in database
```sql
SELECT * FROM erp_entity_relation WHERE c_table_name = 'student_guardian_info';
```

#### Issue: Empty Results
**Solution:** Verify FK data exists
```sql
-- Check if guardian records exist for students
SELECT * FROM student_guardian_info LIMIT 5;

-- Check if students exist
SELECT * FROM students LIMIT 5;
```

#### Issue: SQL Error in Dynamic Query
**Solution:** Check column names in relationship metadata
```sql
SELECT 
    p_table_name,
    p_pkid,
    c_table_name,
    c_pkid,
    fk_column 
FROM erp_entity_relation;
```

#### Issue: Application Not Starting
**Solution:** Check for compilation errors
```bash
./mvnw clean compile
```

### 8. Performance Testing

#### Test with Large Dataset
```bash
# Get all relationships (should be fast with indexes)
time curl -X GET http://localhost:8081/api/entity-relations

# Dynamic query with large result set
time curl -X GET "http://localhost:8081/api/entity-relations/query/child-with-parent?childTable=attendance"
```

#### Check Query Execution Time
Enable SQL logging in `application.properties`:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 9. Integration with Frontend

#### Angular Service Example
```typescript
export class EntityRelationService {
  private apiUrl = 'http://localhost:8081/api/entity-relations';

  getChildRelationships(parentTable: string) {
    return this.http.get(`${this.apiUrl}/parent/${parentTable}`);
  }

  fetchChildWithParent(childTable: string, childId?: number) {
    let url = `${this.apiUrl}/query/child-with-parent?childTable=${childTable}`;
    if (childId) url += `&childId=${childId}`;
    return this.http.get(url);
  }

  fetchStudentWithChildren(studentId: number) {
    return this.http.get(`${this.apiUrl}/student/${studentId}/with-children`);
  }
}
```

### 10. Success Criteria

✅ All API endpoints return expected responses  
✅ Dynamic queries generate correct SQL  
✅ Guardian and Medical data joined with Student info  
✅ Student can be fetched with all children  
✅ Relationships can be created/updated/deleted via API  
✅ No compilation or runtime errors  
✅ Database schema created correctly  
✅ Sample data loaded successfully  

## Next Steps

1. **Load Sample Data**: Ensure students, guardian, and medical records exist
2. **Run All Tests**: Execute all curl commands above
3. **Verify Results**: Check that joined data is correct
4. **Performance Test**: Test with larger datasets
5. **Security**: Add authentication/authorization
6. **Monitoring**: Set up logging and metrics
7. **Documentation**: Share API docs with frontend team

## Quick Reference

### Base URL
```
http://localhost:8081/api/entity-relations
```

### Key Endpoints
- `GET /` - Get all relationships
- `POST /` - Create relationship
- `GET /parent/{table}` - Get child relationships
- `GET /query/child-with-parent?childTable={name}` - Dynamic join
- `GET /student/{id}/with-children` - Student with all data

### Sample Tables
- **Parent**: students
- **Children**: student_guardian_info, student_medical_info, attendance, grades

### Sample PKIDs
- students: student_id
- student_guardian_info: student_guardian_info_id
- student_medical_info: student_medical_info_id

## Support

For issues or questions:
1. Check application logs: `tail -f logs/spring.log`
2. Verify database connectivity
3. Review API documentation in `/docs/API_ENTITY_RELATIONS.md`
4. Check example outputs in `/docs/ENTITY_RELATIONS_EXAMPLES.md`
