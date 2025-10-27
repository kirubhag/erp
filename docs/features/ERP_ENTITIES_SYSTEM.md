# ERP Entities System - Implementation Documentation

## Overview
The ERP Entities system manages the visibility and access control of menu items in the School ERP application. It determines which entities (modules) are accessible to users based on their roles.

## Database Schema

### 1. `erp_entities` Table
Stores the list of entities/modules available in the ERP system.

**Columns:**
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT) - Unique identifier
- `singular_name` (VARCHAR(100), UNIQUE, NOT NULL) - Singular form of entity name (e.g., "Student")
- `plural_name` (VARCHAR(100), NOT NULL) - Plural form of entity name (e.g., "Students")
- `description` (TEXT) - Description of the entity's purpose
- `is_active` (BOOLEAN, DEFAULT TRUE) - Whether the entity is currently active
- `created_date` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP) - When the entity was created
- `last_modified_date` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE) - When the entity was last modified
- `created_by` (VARCHAR(100)) - User who created the entity
- `last_modified_by` (VARCHAR(100)) - User who last modified the entity

**Indexes:**
- `idx_singular_name` on `singular_name`
- `idx_is_active` on `is_active`

**Initial Data:**
- Student - Student management entity
- Staff - Staff/employee management entity
- Attendance - Attendance tracking entity

### 2. `erp_entities_role_relation` Table
Maps entities to user roles, controlling which roles can access which entities.

**Columns:**
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT) - Unique identifier
- `entity_id` (BIGINT, NOT NULL, FOREIGN KEY) - References `erp_entities.id`
- `role_id` (BIGINT, NOT NULL, FOREIGN KEY) - References `roles.id`
- `created_date` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP) - When the relation was created
- `last_modified_date` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE) - When the relation was last modified
- `created_by` (VARCHAR(100)) - User who created the relation
- `last_modified_by` (VARCHAR(100)) - User who last modified the relation

**Constraints:**
- `unique_entity_role` - Unique constraint on (`entity_id`, `role_id`)
- Foreign key on `entity_id` references `erp_entities(id)` ON DELETE CASCADE
- Foreign key on `role_id` references `roles(id)` ON DELETE CASCADE

**Indexes:**
- `idx_entity_id` on `entity_id`
- `idx_role_id` on `role_id`

## Java Implementation

### Entity Classes

#### `ErpEntity.java`
Located at: `/src/main/java/krs/erp/model/ErpEntity.java`

Main entity class representing an ERP module/entity. Includes:
- All database field mappings
- One-to-many relationship with `ErpEntityRoleRelation`
- `@JsonIgnore` on `roleRelations` to prevent lazy loading issues
- Helper methods `addRoleRelation()` and `removeRoleRelation()`
- Automatic timestamp management via `@PrePersist` and `@PreUpdate`

#### `ErpEntityRoleRelation.java`
Located at: `/src/main/java/krs/erp/model/ErpEntityRoleRelation.java`

Junction entity representing the many-to-many relationship between entities and roles. Includes:
- Many-to-one relationships with both `ErpEntity` and `Role`
- Constructors for easy instantiation
- Proper `equals()` and `hashCode()` implementations

### Repository Interfaces

#### `ErpEntityRepository.java`
Located at: `/src/main/java/krs/erp/repository/ErpEntityRepository.java`

Data access methods:
- `findBySingularName(String)` - Find entity by singular name
- `findByIsActiveTrue()` - Find all active entities
- `findActiveEntitiesByRoleId(Long)` - Find entities accessible by a role
- `findActiveEntitiesByRoleIds(List<Long>)` - Find entities accessible by multiple roles
- `isEntityAccessibleByRole(Long, Long)` - Check if entity is accessible by role

#### `ErpEntityRoleRelationRepository.java`
Located at: `/src/main/java/krs/erp/repository/ErpEntityRoleRelationRepository.java`

Data access methods for managing entity-role mappings:
- `findByErpEntityId(Long)` - Find all role relations for an entity
- `findByRoleId(Long)` - Find all entity relations for a role
- `findByErpEntityIdAndRoleId(Long, Long)` - Find specific relation
- `deleteByErpEntityId(Long)` - Delete all relations for an entity
- `deleteByRoleId(Long)` - Delete all relations for a role
- `existsByErpEntityIdAndRoleId(Long, Long)` - Check if relation exists
- `countByErpEntityId(Long)` - Count relations for an entity
- `countByRoleId(Long)` - Count relations for a role

### Service Layer

#### `ErpEntityService.java`
Located at: `/src/main/java/krs/erp/service/ErpEntityService.java`

Business logic methods:
- **Entity Management:**
  - `getAllEntities()` - Get all entities
  - `getActiveEntities()` - Get only active entities
  - `getEntityById(Long)` - Get entity by ID
  - `getEntityBySingularName(String)` - Get entity by name
  - `createEntity(ErpEntity)` - Create new entity
  - `updateEntity(Long, ErpEntity)` - Update existing entity
  - `deleteEntity(Long)` - Delete entity

- **Role-Based Access:**
  - `getEntitiesByRole(Long)` - Get entities for a role
  - `getEntitiesByRoles(List<Long>)` - Get entities for multiple roles
  - `isEntityAccessibleByRole(Long, Long)` - Check accessibility

- **Role Mapping:**
  - `addRoleToEntity(Long, Role)` - Grant role access to entity
  - `removeRoleFromEntity(Long, Long)` - Revoke role access
  - `getEntityRoleRelations(Long)` - Get all role relations for entity
  - `getRoleEntityRelations(Long)` - Get all entity relations for role

### REST Controller

#### `ErpEntityController.java`
Located at: `/src/main/java/krs/erp/controller/ErpEntityController.java`

REST API Endpoints:

**Entity CRUD:**
- `GET /api/erp-entities` - Get all entities (supports `?activeOnly=true`)
- `GET /api/erp-entities/{id}` - Get entity by ID
- `GET /api/erp-entities/by-name/{name}` - Get entity by singular name
- `POST /api/erp-entities` - Create new entity
- `PUT /api/erp-entities/{id}` - Update entity
- `DELETE /api/erp-entities/{id}` - Delete entity

**Role-Based Queries:**
- `GET /api/erp-entities/by-role/{roleId}` - Get entities for a specific role
- `POST /api/erp-entities/by-roles` - Get entities for multiple roles (body: array of role IDs)
- `GET /api/erp-entities/{entityId}/accessible/{roleId}` - Check if entity is accessible by role

**Role Mapping Management:**
- `POST /api/erp-entities/{entityId}/roles` - Add role to entity (body: Role object)
- `DELETE /api/erp-entities/{entityId}/roles/{roleId}` - Remove role from entity
- `GET /api/erp-entities/{entityId}/roles` - Get all roles for an entity
- `GET /api/erp-entities/roles/{roleId}/entities` - Get all entities for a role

## Migration File

**File:** `V004__Create_ERP_Entities_And_Role_Mapping.sql`
**Location:** `/src/main/resources/db/migration/`

This Flyway migration:
1. Creates the `erp_entities` table with indexes
2. Creates the `erp_entities_role_relation` table with foreign keys
3. Inserts initial data for Student, Staff, and Attendance entities

## API Testing Examples

### Get All Entities
```bash
curl http://localhost:8081/api/erp-entities
```

### Get Active Entities Only
```bash
curl "http://localhost:8081/api/erp-entities?activeOnly=true"
```

### Get Entity by Name
```bash
curl http://localhost:8081/api/erp-entities/by-name/Student
```

### Get Entity by ID
```bash
curl http://localhost:8081/api/erp-entities/1
```

### Create New Entity
```bash
curl -X POST http://localhost:8081/api/erp-entities \
  -H "Content-Type: application/json" \
  -d '{
    "singularName": "Course",
    "pluralName": "Courses",
    "description": "Course management entity",
    "isActive": true
  }'
```

### Update Entity
```bash
curl -X PUT http://localhost:8081/api/erp-entities/1 \
  -H "Content-Type: application/json" \
  -d '{
    "singularName": "Student",
    "pluralName": "Students",
    "description": "Updated description",
    "isActive": true
  }'
```

### Get Entities by Role
```bash
curl http://localhost:8081/api/erp-entities/by-role/1
```

### Check Entity Accessibility
```bash
curl http://localhost:8081/api/erp-entities/1/accessible/1
```

## Usage Scenarios

### 1. Menu Visibility Control
The system can be used to dynamically generate the top menu bar based on the user's role:
1. Get the user's role IDs
2. Call `/api/erp-entities/by-roles` with those role IDs
3. Display menu items for the returned entities

### 2. Access Control
Before allowing access to a module:
1. Identify the entity (e.g., "Student")
2. Get the user's role ID
3. Call `/api/erp-entities/{entityId}/accessible/{roleId}`
4. Grant or deny access based on the response

### 3. Role Management
When configuring a role's permissions:
1. Get all available entities via `/api/erp-entities?activeOnly=true`
2. Display them with checkboxes
3. Use `POST /api/erp-entities/{entityId}/roles` to grant access
4. Use `DELETE /api/erp-entities/{entityId}/roles/{roleId}` to revoke access

## Database Verification

```sql
-- View all entities
SELECT * FROM erp_entities;

-- View entity-role mappings
SELECT 
    e.singular_name,
    e.plural_name,
    r.name as role_name
FROM erp_entities e
JOIN erp_entities_role_relation rel ON e.id = rel.entity_id
JOIN roles r ON rel.role_id = r.id;

-- Check which entities are accessible by a specific role
SELECT e.* 
FROM erp_entities e
JOIN erp_entities_role_relation rel ON e.id = rel.entity_id
WHERE rel.role_id = 1 AND e.is_active = true;
```

## Future Enhancements

Potential improvements to consider:
1. Add display order field for menu sorting
2. Add icon/image URL for entity representation
3. Add entity categories/groups
4. Add permission levels (read, write, delete) per entity-role mapping
5. Add entity-specific configuration options
6. Implement entity activation/deactivation workflows
7. Add audit logging for entity-role mapping changes
8. Create admin UI for managing entities and role mappings

## Notes

- The system uses the existing `roles` table rather than creating a separate `profiles` table
- Changed from `erp_entities_profile_relation` to `erp_entities_role_relation` to align with existing schema
- `@JsonIgnore` annotation prevents lazy loading serialization issues in REST responses
- All timestamps are managed automatically via JPA lifecycle callbacks
- Cascade delete ensures orphan cleanup when entities or roles are removed
