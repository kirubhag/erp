# Address Refactoring Documentation

## Overview
Refactored address information from being embedded in entity tables (Student, Parent, Staff) to a separate normalized `addresses` table with foreign key relationships.

## Database Schema

### addresses Table
```sql
CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,        -- STUDENT, PARENT, STAFF, ORGANIZATION
    entity_id BIGINT NOT NULL,                -- Foreign key to the entity
    address_line1 VARCHAR(100),
    address_line2 VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(50),
    postal_code VARCHAR(20),
    country VARCHAR(50),
    is_primary BOOLEAN DEFAULT TRUE NOT NULL,
    address_type VARCHAR(20) DEFAULT 'RESIDENTIAL',  -- RESIDENTIAL, MAILING, WORK, OTHER
    created_time DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    modified_time DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),                  -- Audit: who created this address
    modified_by VARCHAR(100),                 -- Audit: who last modified this address
    owner_id BIGINT,                          -- From BaseEntity
    is_active INT DEFAULT 1,                  -- From BaseEntity: 1=Active, 0=Inactive, -1=Deleted
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_primary (entity_type, entity_id, is_primary)
);
```

### Entity Tables Updated
- **students**: Removed address columns, added `address_id BIGINT` FK to addresses table
- **parents**: Removed address columns, added `address_id BIGINT` FK to addresses table  
- **staff**: Removed address columns, added `address_id BIGINT` FK to addresses table

## Java Model Changes

### New Entity: Address.java
- Location: `src/main/java/krs/erp/model/Address.java`
- Uses polymorphic association pattern with `entityType` and `entityId`
- Supports multiple addresses per entity via `isPrimary` flag
- Enums: `EntityType` (STUDENT, PARENT, STAFF, ORGANIZATION, OTHER)
- Enums: `AddressType` (RESIDENTIAL, MAILING, WORK, OTHER)

### Updated Entities

#### Student.java
**Removed fields:**
- `addressLine1`, `addressLine2`, `city`, `state`, `postalCode`, `country`

**Added:**
```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "address_id")
private Address address;
```

**Updated method:**
```java
public String getFullAddress() {
    if (this.address != null) {
        return this.address.getFullAddress();
    }
    return "";
}
```

#### Parent.java
**Removed fields:**
- `addressLine1`, `addressLine2`, `city`, `state`, `postalCode`, `country`

**Added:**
```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "address_id")
private Address address;
```

#### Staff.java
**Removed fields:**
- `addressLine1`, `addressLine2`, `city`, `state`, `postalCode`, `country`

**Added:**
```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "address_id")
private Address address;
```

### New Repository: AddressRepository.java
- Location: `src/main/java/krs/erp/repository/AddressRepository.java`
- Methods:
  - `findByEntityTypeAndEntityId()` - Get all addresses for an entity
  - `findPrimaryAddress()` - Get primary address for an entity
  - `findByEntityAndAddressType()` - Filter by address type
  - `deleteByEntityTypeAndEntityId()` - Delete all addresses for entity
  - `existsByEntityTypeAndEntityId()` - Check if entity has addresses

## Migration Script
- File: `V003__Extract_Address_To_Separate_Table.sql`
- Creates addresses table with all BaseEntity audit columns (`created_by`, `modified_by`, `created_time`, `modified_time`, `owner_id`, `is_active`)
- Migrates existing address data from students, parents, staff tables (preserving all audit fields)
- **Safe Migration Order:**
  1. Creates addresses table
  2. Copies all address data to addresses table (including audit fields)
  3. Adds `address_id` FK column to entity tables
  4. Updates `address_id` with the migrated address IDs
  5. **Only then** drops old address columns from entity tables
- This ensures no data loss and maintains referential integrity throughout the migration

### Migration Flow Diagram
```
BEFORE MIGRATION:
┌─────────────────────────────────────────┐
│ students                                │
│ ├── id                                  │
│ ├── first_name, last_name              │
│ ├── address_line1   ← Embedded         │
│ ├── address_line2   ← in entity        │
│ ├── city, state     ← table            │
│ ├── postal_code     ←                  │
│ ├── country         ←                  │
│ ├── created_by, modified_by            │
│ └── created_time, modified_time        │
└─────────────────────────────────────────┘

AFTER MIGRATION:
┌────────────────────────┐         ┌─────────────────────────────────┐
│ students               │         │ addresses                       │
│ ├── id                 │         │ ├── id                          │
│ ├── first_name         │         │ ├── entity_type = 'STUDENT'    │
│ ├── last_name          │         │ ├── entity_id → students.id    │
│ ├── address_id ────────┼────────>│ ├── address_line1              │
│ ├── created_by         │         │ ├── address_line2              │
│ ├── modified_by        │         │ ├── city, state                │
│ ├── created_time       │         │ ├── postal_code, country       │
│ └── modified_time      │         │ ├── is_primary, address_type   │
└────────────────────────┘         │ ├── created_by ← Preserved     │
                                    │ ├── modified_by ← from entity  │
                                    │ ├── created_time ← audit fields│
                                    │ ├── modified_time ←            │
                                    │ ├── owner_id                   │
                                    │ └── is_active                  │
                                    └─────────────────────────────────┘
```

## Frontend Considerations

### Template Field Mapping
Frontend templates currently use nested `address` object with different field names:

| Frontend Field | Backend Field |
|---------------|---------------|
| `item.address.street` | `address.addressLine1` |
| `item.address.city` | `address.city` |
| `item.address.state` | `address.state` |
| `item.address.zipCode` | `address.postalCode` |

### Files Needing Updates
1. **student-detail.html** - Uses `item.address.street`, needs mapping to `addressLine1`
2. **student-edit.html** - Uses `item.address.street`, needs mapping  
3. **student-form-modal.html** - Uses `studentForm.address.street`, needs mapping

### Recommended Approach
Create a DTO or service layer method to map between frontend and backend field names:

```java
// Option 1: Add transient getters to Address entity
@Transient
public String getStreet() {
    return addressLine1;
}

@Transient
public void setStreet(String street) {
    this.addressLine1 = street;
}

@Transient
public String getZipCode() {
    return postalCode;
}

@Transient
public void setZipCode(String zipCode) {
    this.postalCode = zipCode;
}
```

## Service Layer Updates Needed

### StudentService
When creating/updating students with addresses:
```java
public Student createStudent(StudentDTO dto) {
    Student student = new Student();
    // ... set student fields
    
    if (dto.getAddress() != null) {
        Address address = new Address(Address.EntityType.STUDENT, null);
        address.setAddressLine1(dto.getAddress().getStreet());
        address.setCity(dto.getAddress().getCity());
        address.setState(dto.getAddress().getState());
        address.setPostalCode(dto.getAddress().getZipCode());
        address = addressRepository.save(address);
        
        student.setAddress(address);
        student = studentRepository.save(student);
        
        // Update address with saved student ID
        address.setEntityId(student.getId());
        addressRepository.save(address);
    }
    
    return student;
}
```

### ParentService
Similar pattern for creating/updating parents with addresses

### StaffService
Similar pattern for creating/updating staff with addresses

## Testing Updates Needed

### Unit Tests
Test files need updates to use new Address relationship:

**Before:**
```java
student.setAddressLine1("123 Main St");
student.setCity("Springfield");
```

**After:**
```java
Address address = new Address(Address.EntityType.STUDENT, student.getId());
address.setAddressLine1("123 Main St");
address.setCity("Springfield");
address = addressRepository.save(address);
student.setAddress(address);
```

### Files to Update
- `StudentTest.java`
- `ParentTest.java`
- `StaffTest.java`

## Benefits of This Refactoring

1. **Database Normalization**: Eliminates redundant address columns across multiple tables
2. **Reusability**: Address logic centralized in one place
3. **Flexibility**: Supports multiple addresses per entity
4. **Maintainability**: Changes to address structure only need updates in one table
5. **Extensibility**: Easy to add new address types or new entities that need addresses
6. **Data Integrity**: Foreign key constraints ensure referential integrity

## Future Enhancements

1. Add support for multiple addresses per entity (currently using `isPrimary` flag)
2. Add address validation service (validate postal codes, state/country combinations)
3. Add geocoding integration for address coordinates
4. Add address history/audit trail
5. Support for international address formats
6. Address autocomplete/suggestion API integration

## Rollback Plan

If rollback is needed, run the following SQL (backup data first!):

```sql
-- Add address columns back to entity tables
ALTER TABLE students ADD COLUMN address_line1 VARCHAR(100), 
    ADD COLUMN address_line2 VARCHAR(100),
    ADD COLUMN city VARCHAR(50),
    ADD COLUMN state VARCHAR(50),
    ADD COLUMN postal_code VARCHAR(20),
    ADD COLUMN country VARCHAR(50);

-- Restore data from addresses table
UPDATE students s
INNER JOIN addresses a ON s.address_id = a.id
SET s.address_line1 = a.address_line1,
    s.address_line2 = a.address_line2,
    s.city = a.city,
    s.state = a.state,
    s.postal_code = a.postal_code,
    s.country = a.country;

-- Remove FK and drop addresses table
ALTER TABLE students DROP FOREIGN KEY fk_student_address;
ALTER TABLE students DROP COLUMN address_id;
-- Repeat for parents and staff...
DROP TABLE addresses;
```

## Notes

- Migration preserves all existing address data
- Empty/null addresses are not migrated to the addresses table
- Address foreign keys are nullable (entities can exist without addresses)
- The `getFullAddress()` helper method behavior remains unchanged from consumer perspective
