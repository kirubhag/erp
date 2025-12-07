# IAM_MasterDB System Data Population - Implementation Guide

## Overview

This document describes the implementation of system-wide data population in `IAM_MasterDB` for the multi-tenant ERP system. System configuration data (ERP fields, sections, entities, roles, permissions) is now stored centrally in `IAM_MasterDB` and shared across all tenants, rather than being duplicated in each tenant database.

## Architecture Changes

### Before
- Each tenant database contained its own copy of:
  - ERP field definitions
  - ERP section definitions
  - ERP entity definitions
  - System roles and permissions
- Data duplication across tenants
- Difficult to maintain consistency
- System configuration changes required updates to all tenant databases

### After
- `IAM_MasterDB` stores system-wide configuration:
  - **ERP Metadata**: Fields, sections, entities, entity relations
  - **Access Control**: Roles, permissions, role-permission mappings
  - **Tenant Registry**: Tenant information and user identity
- Each tenant database contains only:
  - Tenant-specific business data (students, staff, parents, etc.)
  - Transactional data (attendance, grades, assignments, etc.)
- Single source of truth for system configuration
- Easy to maintain and update system-wide settings

## Database Schema

### New System Tables in IAM_MasterDB

The following tables have been added to `IAM_MasterDB` (see `scripts/iam_master_system_tables.sql`):

#### 1. **Access Control Tables**
- `permissions` - System permission definitions
- `roles` - System role definitions  
- `role_permissions` - Role-permission mappings

#### 2. **ERP Metadata Tables**
- `erp_sections` - UI section definitions for all entity types
- `erp_fields` - Field definitions for all entity types
- `erp_entities` - Entity metadata (name, icon, route, etc.)
- `erp_entity_relations` - Inter-entity relationships
- `erp_entity_role_relations` - Entity access control by role

#### 3. **Profile Tables** (Optional)
- `user_profiles` - User profile/type definitions
- `profile_roles` - Profile-role mappings

## Implementation Components

### 1. SQL Schema Script

**File**: `scripts/iam_master_system_tables.sql`

Creates all system tables in `IAM_MasterDB`. Run this script to set up the schema:

```sql
mysql -u root -p < scripts/iam_master_system_tables.sql
```

### 2. Java Initializer

**File**: `src/main/java/krs/erp/config/MasterDbSystemDataInitializer.java`

Spring Boot `CommandLineRunner` that:
- Runs on application startup (`@Order(1)` - before tenant initializers)
- Ensures `IAM_MasterDB` exists
- Loads system data from XML files into `IAM_MasterDB`:
  - System permissions from `data/permission/sample-permissions.xml`
  - System roles from `data/role/sample-roles.xml`
  - Role-permission mappings
  - ERP sections from `data/**/*_sections.xml`
  - ERP fields from `data/**/*_fields.xml`
  - ERP entities from `data/erp-entities.xml`
  - ERP entity relations from `data/erp-entity-relations.xml`

**Key Features**:
- Idempotent: Checks if data exists before loading
- Skips loading if data already present
- Logs all operations for debugging
- Uses `masterDataSource` bean to connect to IAM_MasterDB

### 3. Configuration Changes

**File**: `src/main/resources/application.properties`

Updated master datasource to connect to `IAM_MasterDB`:

```properties
# Master Database Configuration - IAM_MasterDB
spring.datasource.url=jdbc:mysql://localhost:3307/IAM_MasterDB?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**Note**: The `masterDataSource` bean in `DataSourceConfig.java` uses these properties.

## Data Flow

### System Data Loading (Application Startup)

```
Application Startup
    ↓
MasterDbSystemDataInitializer (@Order(1))
    ↓
Check IAM_MasterDB exists
    ↓
Load XML files → Insert into IAM_MasterDB tables
    ├── permissions
    ├── roles  
    ├── role_permissions
    ├── erp_sections
    ├── erp_fields
    ├── erp_entities
    └── erp_entity_relations
    ↓
ErpFieldInitializer (disabled for IAM_MasterDB)
    └── (Now only loads into tenant databases if needed)
```

### Tenant Registration Flow

```
New Tenant Registration
    ↓
RegistrationService.registerTenant()
    ↓
1. Create tenant record in IAM_MasterDB.erp_tenants
    ↓
2. Create tenant database (e.g., erp_tenant_acme_123456)
    ↓
3. Initialize tenant schema (tenant_schema.sql)
    ↓
4. Create organization and admin user in tenant DB
    ↓
5. Tenant references system data from IAM_MasterDB
```

## XML Data Sources

The system loads configuration from these XML files:

### Access Control
- `src/main/resources/data/permission/sample-permissions.xml` - System permissions
- `src/main/resources/data/role/sample-roles.xml` - System roles

### ERP Metadata
- `src/main/resources/data/**/*_sections.xml` - Section definitions
- `src/main/resources/data/**/*_fields.xml` - Field definitions
- `src/main/resources/data/erp-entities.xml` - Entity definitions
- `src/main/resources/data/erp-entity-relations.xml` - Entity relationships

**Example Section Definition** (`student_sections.xml`):
```xml
<entityFields type="STUDENT">
    <section>
        <sectionName>personal_info</sectionName>
        <sectionLabel>Personal Information</sectionLabel>
        <layoutType>TWO_COLUMN</layoutType>
        <displayOrder>1</displayOrder>
        <isCollapsible>false</isCollapsible>
        <showInCreate>true</showInCreate>
        <showInEdit>true</showInEdit>
        <showInDetail>true</showInDetail>
    </section>
</entityFields>
```

## Repository and Service Updates

### Repositories

The following repositories now query `IAM_MasterDB` for system data:

- `ErpFieldRepository` - Query `IAM_MasterDB.erp_fields`
- `ErpSectionRepository` - Query `IAM_MasterDB.erp_sections`
- `ErpEntityRepository` - Query `IAM_MasterDB.erp_entities`
- `RoleRepository` - Query `IAM_MasterDB.roles`
- `PermissionRepository` - Query `IAM_MasterDB.permissions`

**Update Required**: Add `@Table(name = "IAM_MasterDB.table_name", catalog = "IAM_MasterDB")` to JPA entities.

### Services

Update XML loader services to use `masterDataSource`:

- `ErpFieldXmlLoaderService` - No longer needed (replaced by MasterDbSystemDataInitializer)
- `ErpSectionXmlLoaderService` - No longer needed  
- `ErpEntityRelationXmlLoaderService` - No longer needed
- `ErpFieldInitializer` - Disable or remove

## Migration Steps

### For Existing Deployments

If you have existing tenant databases with ERP metadata:

1. **Backup existing data**
   ```bash
   mysqldump -u root -p erp_database > backup.sql
   ```

2. **Create IAM_MasterDB and system tables**
   ```bash
   mysql -u root -p < scripts/iam_master_schema.sql
   mysql -u root -p < scripts/iam_master_system_tables.sql
   ```

3. **Register existing tenant**
   ```bash
   mysql -u root -p < scripts/register_existing_tenant.sql
   ```

4. **Update application.properties**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3307/IAM_MasterDB?...
   ```

5. **Start application** - `MasterDbSystemDataInitializer` will populate system data

6. **Verify data**
   ```sql
   USE IAM_MasterDB;
   SELECT COUNT(*) FROM erp_fields;
   SELECT COUNT(*) FROM erp_sections;
   SELECT COUNT(*) FROM erp_entities;
   SELECT COUNT(*) FROM roles;
   SELECT COUNT(*) FROM permissions;
   ```

7. **Remove system data from tenant databases** (optional cleanup)
   ```sql
   USE erp_tenant_xyz;
   -- Keep only tenant-specific data
   -- Remove system configuration that now lives in IAM_MasterDB
   ```

### For New Deployments

1. **Create IAM_MasterDB**
   ```bash
   mysql -u root -p < scripts/iam_master_schema.sql
   mysql -u root -p < scripts/iam_master_system_tables.sql
   ```

2. **Configure application.properties** (already done)

3. **Start application** - System data auto-populates

4. **Register new tenants** via `/api/registration/register` endpoint

## Benefits

### 1. **Centralized Configuration**
- Single source of truth for system metadata
- Easy to update field definitions, roles, permissions across all tenants
- No data duplication

### 2. **Consistency**
- All tenants use same system configuration
- UI behavior consistent across tenants
- Field definitions identical

### 3. **Maintainability**
- Update system data once in IAM_MasterDB
- Changes reflect immediately for all tenants
- No need to migrate each tenant database

### 4. **Performance**
- Reduced storage (no duplicate system data)
- Faster tenant provisioning (no need to copy system data)

### 5. **Security**
- Centralized role and permission management
- Easier to audit access control
- Consistent security model

## Testing

### 1. Verify IAM_MasterDB Population

```sql
USE IAM_MasterDB;

-- Check system tables exist
SHOW TABLES;

-- Verify data loaded
SELECT COUNT(*) as permission_count FROM permissions;
SELECT COUNT(*) as role_count FROM roles;
SELECT COUNT(*) as section_count FROM erp_sections;
SELECT COUNT(*) as field_count FROM erp_fields;
SELECT COUNT(*) as entity_count FROM erp_entities;

-- Sample queries
SELECT * FROM roles WHERE system_role = true;
SELECT * FROM erp_entities WHERE is_active = true ORDER BY sequence;
SELECT entity_type, COUNT(*) as field_count 
FROM erp_fields 
GROUP BY entity_type;
```

### 2. Test Application Startup

```bash
./mvnw spring-boot:run
```

Check logs for:
```
=== Starting IAM_MasterDB System Data Initialization ===
✓ IAM_MasterDB database ensured
Loading system permissions into IAM_MasterDB...
✓ Loaded 22 system permissions into IAM_MasterDB
Loading system roles into IAM_MasterDB...
✓ Loaded 6 system roles into IAM_MasterDB
...
=== IAM_MasterDB System Data Initialization Completed ===
```

### 3. Test Tenant Registration

```bash
curl -X POST http://localhost:8081/api/registration/register \
  -H "Content-Type: application/json" \
  -d '{
    "organizationName": "Test School",
    "adminFirstName": "John",
    "adminLastName": "Doe",
    "adminEmail": "admin@testschool.com",
    "adminPassword": "password123",
    "adminPhone": "1234567890"
  }'
```

Verify:
- Tenant created in `IAM_MasterDB.erp_tenants`
- Tenant database created
- Admin user created in tenant database
- No system data duplicated in tenant database

## Troubleshooting

### Issue: Tables not found in IAM_MasterDB

**Solution**: Run the schema script:
```bash
mysql -u root -p < scripts/iam_master_system_tables.sql
```

### Issue: Data not loading on startup

**Check**:
1. MasterDbSystemDataInitializer is enabled (`@Component`)
2. XML files exist in `src/main/resources/data/`
3. masterDataSource connects to IAM_MasterDB
4. Check application logs for errors

### Issue: Duplicate key errors

**Reason**: Data already exists in IAM_MasterDB

**Solution**: This is expected. The initializer uses `INSERT ... ON DUPLICATE KEY UPDATE` to skip existing data.

### Issue: Section/field lookups failing

**Check**:
1. JPA entities have correct `@Table` annotations with IAM_MasterDB catalog
2. Repositories query the correct database
3. masterDataSource is properly configured

## Next Steps

### Required Updates

1. **Update JPA Entities**
   - Add `@Table(catalog = "IAM_MasterDB")` to system entities:
     - `ErpField.java`
     - `ErpSection.java`
     - `ErpEntity.java`
     - `ErpEntityRelation.java`
     - `Role.java`
     - `Permission.java`

2. **Update Repositories**
   - Ensure repositories use `masterDataSource` for system tables
   - Update native queries to reference `IAM_MasterDB.table_name`

3. **Update Services**
   - Disable or remove old XML loaders:
     - `ErpFieldXmlLoaderService`
     - `ErpSectionXmlLoaderService`
     - `ErpEntityRelationXmlLoaderService`
     - `ErpFieldInitializer`

4. **Update Controllers**
   - Ensure controllers fetch system data from IAM_MasterDB
   - Update API endpoints if needed

### Optional Enhancements

1. **Admin UI for System Data**
   - Create admin interface to manage fields, sections, roles
   - Allow superadmin to update system configuration

2. **Tenant Customization**
   - Allow tenants to extend (not modify) system fields
   - Store tenant-specific customizations in tenant database

3. **Version Control**
   - Add version tracking to system data
   - Support rollback of configuration changes

4. **Caching**
   - Cache system data for performance
   - Invalidate cache when system data updates

## Conclusion

The IAM_MasterDB system data population provides a robust foundation for multi-tenant ERP architecture. System configuration is now centrally managed, consistent across tenants, and easy to maintain.

For questions or issues, refer to the codebase documentation or contact the development team.
