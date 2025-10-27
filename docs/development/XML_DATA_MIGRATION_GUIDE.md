# XML Sample Data Migration Guide

## Overview
This guide explains how to migrate the XML sample data files to work with the new Address entity structure after the address refactoring.

## Current Status

### ✅ Completed Files
1. **sample-addresses.xml** - Created with 11 address entries for sample data
2. **sample-data.xml** - Updated to use `address_id` instead of embedded address fields
   - Staff records (2): Updated with `address_id` (IDs 1-2)
   - Parent records (4): Updated with `address_id` (IDs 3-6)
   - Student records (5): Updated with `address_id` (IDs 7-11)

### 🔄 Pending Files
The following grade-level files still contain embedded address fields:
- `kindergarten.xml`
- `grade_1.xml` through `grade_12.xml`

These files contain 40+ students each with embedded address data that needs migration.

## Migration Strategy

### Option 1: Pre-Migration (Recommended for Production)
Run the V003 migration script first, which will:
1. Create the `addresses` table
2. Automatically migrate address data from existing student records
3. Update `address_id` foreign keys
4. Drop old address columns

After migration, the grade XML files will work if they:
- Remove address fields (`address_line1`, `address_line2`, `city`, `state`, `postal_code`, `country`)
- Leave `address_id` null (addresses will be created on first save via service layer)

### Option 2: Generate Address XMLs for Grade Files
Create separate address XML files for each grade level:

**Example for grade_1.xml:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<dataset>
    <!-- Addresses for Grade 1 Students (IDs 101-139+) -->
    <addresses id="101" entity_type="STUDENT" entity_id="101" 
               address_line1="101 Maple Street" 
               city="Springfield" state="IL" postal_code="62701" country="USA" 
               is_primary="1" address_type="RESIDENTIAL"
               created_time="2024-01-01 00:00:00" modified_time="2024-01-01 00:00:00"
               created_by="admin" modified_by="admin" owner_id="1" is_active="1" />
    <!-- ... more addresses ... -->
</dataset>
```

Then update grade XML files to reference these addresses:
```xml
<students id="101" first_name="Aiden" last_name="Anderson" 
          ... other fields ...
          address_id="101"  <!-- Reference to address -->
          created_at="2024-01-01 00:00:00" updated_at="2024-01-01 00:00:00" />
```

### Option 3: Service Layer Auto-Creation (Simplest)
Remove address fields from grade XMLs and let the service layer create addresses on demand:

**Updated grade_1.xml:**
```xml
<students id="101" first_name="Aiden" last_name="Anderson" student_id="G1STU001" 
          email="aiden.anderson@student.school.edu" 
          date_of_birth="2018-01-15" gender="MALE" 
          enrollment_date="2024-08-15" grade_level="GRADE_1" enrollment_status="ACTIVE" 
          emergency_contact_name="Jennifer Anderson" 
          emergency_contact_phone="+1234560101" 
          emergency_contact_relation="Mother" 
          created_at="2024-01-01 00:00:00" updated_at="2024-01-01 00:00:00" />
```

When StudentService saves the entity, AddressService will create a default address if needed.

## Recommended Approach

**For Development/Testing:**
1. Use Option 3 (Service Layer Auto-Creation) - Simplest
2. Update StudentService to create default addresses during save
3. Manually add addresses via UI or API after initial load

**For Production:**
1. Run V003 migration on existing database (preserves current addresses)
2. Use Option 1 - let migration handle existing data
3. For new sample data, use Option 2 (pre-create address XMLs)

## Implementation Steps

### Step 1: Update Grade XML Files (Remove Address Fields)
Create a script to process all grade XML files:

```bash
# Script to remove address fields from grade XMLs
for file in src/main/resources/data/grade_*.xml src/main/resources/data/kindergarten.xml; do
    sed -i.bak 's/ address_line1="[^"]*"//g' "$file"
    sed -i.bak 's/ address_line2="[^"]*"//g' "$file"
    sed -i.bak 's/ city="[^"]*"//g' "$file"
    sed -i.bak 's/ state="[^"]*"//g' "$file"
    sed -i.bak 's/ postal_code="[^"]*"//g' "$file"
    sed -i.bak 's/ country="[^"]*"//g' "$file"
done
```

### Step 2: Update StudentService
Add address creation logic to StudentService:

```java
@Service
public class StudentService {
    @Autowired
    private AddressService addressService;
    
    @Transactional
    public Student createStudent(Student student) {
        // Save student first to get ID
        Student saved = studentRepository.save(student);
        
        // Create default address if not provided
        if (saved.getAddress() == null) {
            Address address = addressService.getOrCreatePrimaryAddress(
                EntityType.STUDENT, 
                saved.getId()
            );
            saved.setAddress(address);
            saved = studentRepository.save(saved);
        }
        
        return saved;
    }
}
```

### Step 3: Data Load Order
Update data loading to ensure addresses are loaded before entities:

1. `sample-addresses.xml` - Load first
2. `sample-data.xml` - Load second (references addresses)
3. Grade files - Load last (will auto-create addresses if needed)

## Testing

After migration, verify:
```sql
-- Check addresses were created
SELECT COUNT(*) FROM addresses WHERE entity_type = 'STUDENT';

-- Check students have address references
SELECT COUNT(*) FROM students WHERE address_id IS NOT NULL;

-- Verify address data
SELECT s.first_name, s.last_name, a.address_line1, a.city, a.state 
FROM students s 
LEFT JOIN addresses a ON s.address_id = a.id 
LIMIT 10;
```

## Rollback

If issues occur:
1. Restore database backup
2. Revert code changes
3. Use old XML files with embedded addresses

## Notes

- **Audit Fields**: All addresses include `created_by`, `modified_by`, `created_time`, `modified_time`, `owner_id`, `is_active`
- **Primary Flag**: All sample addresses have `is_primary="1"`
- **Address Type**: Default is `RESIDENTIAL`
- **Entity Type**: STUDENT, PARENT, STAFF, or ORGANIZATION
- **ID Sequence**: Start grade addresses at ID 100+ to avoid conflicts with sample-data addresses (IDs 1-11)

## Future Enhancements

1. **Import Tool**: Create a UI tool to import addresses from CSV
2. **Address Validation**: Add address validation service (Google Maps API)
3. **Multiple Addresses**: Support work, mailing, billing addresses per entity
4. **Address History**: Track address changes over time
