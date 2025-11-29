# Dynamic Entity Relationship Management System

## 🎯 Overview

A complete backend feature for dynamically managing relationships between ERP entities without hard-coding. Built with Spring Boot 3.x and MySQL 9.x, this system uses metadata-driven architecture to automatically build JOIN queries based on stored relationship information.

## 🌟 Key Features

- ✅ **Metadata-Driven**: All relationships stored in database, not code
- ✅ **Dynamic Query Building**: Automatic SQL JOIN generation
- ✅ **Generic & Specific APIs**: Flexible endpoints for different use cases
- ✅ **CRUD Operations**: Complete relationship management
- ✅ **Zero Hard-Coding**: Add new relationships via API without code changes
- ✅ **Audit Trail**: Track who created/modified relationships
- ✅ **Performance Optimized**: Indexed columns for fast lookups

## 📋 Business Requirements Implemented

### ✅ Updated `erp_entities` Table Structure
```sql
- table_name: Physical database table name (e.g., "students")
- pkid: Primary key column name (e.g., "student_id")
- display_column: Human-readable column (e.g., "first_name")
- has_rel_table: Boolean indicating child relationships exist
```

### ✅ Created `erp_entity_relation` Table
```sql
- p_table_name: Parent table name
- p_pkid: Parent primary key column
- p_display_column: Parent display column
- c_table_name: Child table name
- c_pkid: Child primary key column
- c_display_column: Child display column
- fk_column: Foreign key column in child table
- description: Relationship description
- Audit fields: created_by, modified_by, created_time, modified_time
```

### ✅ Relationship Examples
- **Students → Guardian**: student_guardian_info table
- **Students → Medical**: student_medical_info table
- **Students → Attendance**: attendance table
- **Students → Grades**: grades table

### ✅ Dynamic Query Features
- Fetch Guardian with Student information automatically
- Fetch Medical with Student information automatically
- Fetch Student with all Guardian, Medical, Attendance, Grades records
- Generic queries work with any parent-child relationship

## 🏗️ Architecture

### Entity Layer
```
ErpEntity.java              - Updated with metadata fields
ErpEntityRelation.java      - New entity for relationships
```

### Repository Layer
```
ErpEntityRepository.java           - Enhanced with table name queries
ErpEntityRelationRepository.java   - New repository for relationships
```

### Service Layer
```
ErpEntityRelationService.java      - Dynamic query building & execution
```

### Controller Layer
```
ErpEntityRelationController.java   - 13 REST endpoints
```

## 🔌 API Endpoints

### Base URL
```
http://localhost:8081/api/entity-relations
```

### CRUD Operations
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/entity-relations` | Create relationship |
| PUT | `/api/entity-relations/{id}` | Update relationship |
| DELETE | `/api/entity-relations/{id}` | Delete relationship |
| GET | `/api/entity-relations/{id}` | Get by ID |
| GET | `/api/entity-relations` | Get all relationships |

### Query Operations
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/entity-relations/parent/{table}` | Get child relationships |
| GET | `/api/entity-relations/child/{table}` | Get parent relationships |
| GET | `/api/entity-relations/query/child-with-parent` | Dynamic JOIN child→parent |
| GET | `/api/entity-relations/query/parent-with-children` | Dynamic JOIN parent→children |
| GET | `/api/entity-relations/query/sql/{childTable}` | Get generated SQL |

### Specific Operations
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/entity-relations/guardian/{id}/with-student` | Guardian with Student |
| GET | `/api/entity-relations/medical/{id}/with-student` | Medical with Student |
| GET | `/api/entity-relations/student/{id}/with-children` | Student with all children |

## 🚀 Quick Start

### 1. Database Setup
The schema will be automatically created when the application starts. Tables created:
- `erp_entities` (updated with new columns)
- `erp_entity_relation` (new table)

### 2. Load Sample Data
```sql
mysql -u root -p -P 3307 -h localhost erp_database < src/main/resources/data/sample-entity-relations.sql
```

### 3. Start Application
```bash
./mvnw spring-boot:run
```

### 4. Test API
```bash
# Get all relationships
curl http://localhost:8081/api/entity-relations | jq

# Create new relationship
curl -X POST http://localhost:8081/api/entity-relations \
  -H "Content-Type: application/json" \
  -d '{
    "parentTableName": "students",
    "parentPkid": "student_id",
    "parentDisplayColumn": "first_name",
    "childTableName": "student_guardian_info",
    "childPkid": "student_guardian_info_id",
    "childDisplayColumn": "father_name",
    "foreignKeyColumn": "student_id"
  }' | jq

# Fetch Guardian with Student
curl "http://localhost:8081/api/entity-relations/query/child-with-parent?childTable=student_guardian_info" | jq

# Fetch Student with all children
curl http://localhost:8081/api/entity-relations/student/1/with-children | jq
```

## 📊 Example Responses

### Get All Relationships
```json
[
  {
    "id": 1,
    "parentTableName": "students",
    "parentPkid": "student_id",
    "childTableName": "student_guardian_info",
    "foreignKeyColumn": "student_id"
  }
]
```

### Guardian with Student (Dynamic JOIN)
```json
[
  {
    "child_id": 1,
    "child_display": "Michael Johnson",
    "parent_id": 101,
    "parent_display": "Emma",
    "father_name": "Michael Johnson",
    "father_occupation": "Software Engineer",
    "student_first_name": "Emma",
    "student_last_name": "Johnson",
    "student_email": "emma.johnson@school.edu"
  }
]
```

### Student with All Children
```json
{
  "parentTable": "students",
  "parentId": 101,
  "student_guardian_info": [...],
  "student_medical_info": [...],
  "attendance": [...],
  "grades": [...]
}
```

## 🔍 How Dynamic Queries Work

### 1. Store Relationship Metadata
```json
{
  "parentTableName": "students",
  "parentPkid": "student_id",
  "childTableName": "student_guardian_info",
  "foreignKeyColumn": "student_id"
}
```

### 2. Service Builds SQL Dynamically
```sql
SELECT c.*, p.* 
FROM student_guardian_info c 
INNER JOIN students p 
ON c.student_id = p.student_id
```

### 3. Execute and Return Results
Service uses EntityManager to execute native query and returns results as Maps.

## 📁 File Structure

```
src/main/java/krs/erp/
├── model/
│   ├── ErpEntity.java (updated)
│   └── ErpEntityRelation.java (new)
├── repository/
│   ├── ErpEntityRepository.java (updated)
│   └── ErpEntityRelationRepository.java (new)
├── service/
│   └── ErpEntityRelationService.java (new)
└── controller/
    └── ErpEntityRelationController.java (new)

src/main/resources/
├── schema.sql (updated)
└── data/
    ├── erp-entities.xml (updated)
    ├── erp-entity-relations.xml (new)
    └── sample-entity-relations.sql (new)

docs/
├── API_ENTITY_RELATIONS.md (new)
├── ENTITY_RELATIONS_EXAMPLES.md (new)
├── ENTITY_RELATIONS_IMPLEMENTATION_SUMMARY.md (new)
└── QUICK_START_ENTITY_RELATIONS.md (new)
```

## 🎓 Use Cases

### Use Case 1: Fetch Guardian Information with Student Details
```bash
GET /api/entity-relations/guardian/1/with-student
```
**Returns**: Guardian data automatically joined with related Student information

### Use Case 2: Fetch Medical Records with Student Details
```bash
GET /api/entity-relations/medical/1/with-student
```
**Returns**: Medical data automatically joined with related Student information

### Use Case 3: Fetch Student with All Related Data
```bash
GET /api/entity-relations/student/101/with-children
```
**Returns**: Complete student profile with guardian, medical, attendance, and grades

### Use Case 4: Add New Entity Relationship
```bash
POST /api/entity-relations
```
**Result**: New relationship added without any code changes

## 🔧 Configuration

### Database Configuration
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/erp_database
spring.jpa.hibernate.ddl-auto=update
```

### Enable SQL Logging (Development)
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## ✅ Testing Checklist

- [x] Compilation successful
- [x] Schema created correctly
- [x] Sample data loaded
- [ ] API endpoints tested
- [ ] Guardian with Student query verified
- [ ] Medical with Student query verified
- [ ] Student with children query verified
- [ ] Create/Update/Delete operations tested
- [ ] Performance tested with large datasets

## 📚 Documentation

- **API Reference**: [API_ENTITY_RELATIONS.md](./API_ENTITY_RELATIONS.md)
- **Examples**: [ENTITY_RELATIONS_EXAMPLES.md](./ENTITY_RELATIONS_EXAMPLES.md)
- **Implementation**: [ENTITY_RELATIONS_IMPLEMENTATION_SUMMARY.md](./ENTITY_RELATIONS_IMPLEMENTATION_SUMMARY.md)
- **Quick Start**: [QUICK_START_ENTITY_RELATIONS.md](./QUICK_START_ENTITY_RELATIONS.md)

## 🎯 Benefits

1. **No Code Changes**: Add relationships via API, not code
2. **Flexible**: Works with any parent-child table structure
3. **Maintainable**: Central relationship management
4. **Scalable**: Indexed for performance with large datasets
5. **Auditable**: Track who created/modified relationships
6. **Transparent**: View generated SQL for debugging

## 🔮 Future Enhancements

- [ ] Support for many-to-many relationships
- [ ] Query optimization and caching
- [ ] WHERE clause and filtering support
- [ ] Pagination for large result sets
- [ ] GraphQL API support
- [ ] Relationship validation
- [ ] Performance monitoring
- [ ] Frontend UI for relationship management

## 🐛 Troubleshooting

### Issue: 404 Not Found
**Solution**: Check relationship exists in database
```sql
SELECT * FROM erp_entity_relation WHERE c_table_name = 'your_table';
```

### Issue: Empty Results
**Solution**: Verify data exists in both parent and child tables
```sql
SELECT COUNT(*) FROM student_guardian_info;
SELECT COUNT(*) FROM students;
```

### Issue: SQL Error
**Solution**: Verify column names in relationship metadata match actual table structure

## 📞 Support

For questions or issues:
1. Check documentation in `/docs` folder
2. Review API examples
3. Verify database schema
4. Check application logs

## 📄 License

Copyright © 2025 KRS ERP System. All rights reserved.

---

**Version**: 1.0.0  
**Last Updated**: 2025-11-29  
**Build Status**: ✅ Successful  
**Test Status**: ⏳ Pending Integration Tests
