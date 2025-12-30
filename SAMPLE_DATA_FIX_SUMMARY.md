# Sample Data Loading - Issues Resolved

## Date: December 26, 2025

## Problem Summary
The student table and other entity tables were empty despite XML files containing sample data. The root cause was a combination of issues in the data import system.

## Issues Fixed

### 1. Transaction Rollback Issue ✅ FIXED
**Problem:** Data inserts were being executed but not committed to the database.

**Root Cause:** The `TenantFilter` was clearing the tenant context in its `finally` block BEFORE Spring's transaction interceptor could commit the changes. When Hibernate attempted to flush changes during commit, the tenant context was already cleared, preventing proper database routing.

**Solution:** Modified `TenantFilter.java` to defer tenant context clearing until after transaction completion using `TransactionSynchronizationManager`:

```java
if (tenantWasSet && TransactionSynchronizationManager.isSynchronizationActive()) {
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCompletion(int status) {
            TenantContext.clear();
        }
    });
}
```

**Files Modified:**
- `src/main/java/krs/erp/config/multitenant/TenantFilter.java`

### 2. Missing importAddressesDataFromXml Method ✅ FIXED
**Problem:** The controller was calling `importDataFromXml()` for addresses, which expected a comprehensive XML file with all entities, not just addresses.

**Solution:** Created a new `importAddressesDataFromXml()` method in `DataImportService.java` that only imports addresses from the address XML file.

**Files Modified:**
- `src/main/java/krs/erp/service/DataImportService.java` (added new method)
- `src/main/java/krs/erp/controller/SampleDataController.java` (updated to call new method)

### 3. Incomplete importAddresses Method ✅ FIXED
**Problem:** The `importAddresses()` method was missing required field mappings causing NULL constraint violations.

**Fields Added:**
- `entity_id` - Required foreign key (was causing "Column 'entity_id' cannot be null" error)
- `is_primary` - Boolean flag (was trying to pass int instead of Boolean)
- `address_type` - Enum value (was trying to pass String instead of AddressType enum)

**Files Modified:**
- `src/main/java/krs/erp/service/DataImportService.java`

### 4. Wrong Method Call in loadAddressesIntoCache ✅ FIXED
**Problem:** Used `address.getAddressId()` which doesn't exist.

**Solution:** Changed to `address.getId()` since Address extends BaseEntity.

**Files Modified:**
- `src/main/java/krs/erp/service/DataImportService.java`

### 5. Dependency Order Updated ✅ FIXED
**Problem:** Students were being loaded before addresses, causing null address references.

**Solution:** Updated `DEPENDENCY_ORDER` in `SampleDataController` to load "addresses" before "STUDENTS".

**Files Modified:**
- `src/main/java/krs/erp/controller/SampleDataController.java`

### 6. Duplicate Controller Conflict ✅ FIXED
**Problem:** Two `RecruitmentController` classes existed, preventing application startup.

**Solution:** Renamed the duplicate controller to `.bak`.

**Files Modified:**
- `src/main/java/krs/erp/controller/RecruitmentController.java` → `RecruitmentController.java.bak`

### 7. Unauthenticated Request Support ✅ FIXED
**Problem:** Sample data loading API couldn't set tenant context for unauthenticated requests.

**Solution:** Enhanced `TenantFilter` to check `X-Tenant-ID` header even for unauthenticated requests.

**Files Modified:**
- `src/main/java/krs/erp/config/multitenant/TenantFilter.java`

## Test Results

### Addresses Import ✅ SUCCESS
```bash
curl -X POST http://localhost:8081/api/v1/sample-data/populate \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: 1766593643501" \
  -d '{"entityNames": ["addresses"]}'
```

**Result:** 596 addresses successfully loaded
- 590 student addresses
- 4 parent addresses
- 2 staff addresses

### Database Verification
```sql
SELECT COUNT(*) FROM erpdbf4001e2d.addresses;
-- Result: 596
```

## Known Issues Remaining

### 1. Student Table Schema Mismatch ⚠️ NOT FIXED
**Error:** `Unknown column 's1_0.admission_date' in 'field list'`

**Cause:** The Student entity class has an `admission_date` field that doesn't exist in the database table.

**Impact:** Students cannot be imported until the schema is updated or the entity is modified.

**Recommendation:** Either:
1. Add `admission_date` column to students table, OR
2. Remove/rename the field in Student.java entity

### 2. Similar Cache Loading Needed for Other Entities ⚠️ TODO
The following import methods may need similar cache population fixes:
- `importStaffDataFromXml()` - Should load addresses cache first
- `importParentsDataFromXml()` - Should load addresses cache first
- `importClassesDataFromXml()` - Should load staff and rooms caches
- `importTimetablesDataFromXml()` - Should load classes, subjects, staff, rooms caches

## How to Use

### Import Addresses
```bash
curl -X POST http://localhost:8081/api/v1/sample-data/populate \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: 1766593643501" \
  -d '{"entityNames": ["addresses"]}'
```

### Import All Sample Data (Once schema issues are fixed)
```bash
curl -X POST http://localhost:8081/api/v1/sample-data/populate-all \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: 1766593643501"
```

### Important Notes
1. **Always include the `X-Tenant-ID` header** when calling the sample data API
2. **Load addresses first** before loading students, staff, or parents
3. **Check dependency order** - some entities require others to be loaded first

## Tenant ID Information
The tenant ID can be found in the master database:
```sql
SELECT id, tenant_id, tenant_name, db_name FROM IAM_MasterDB.erp_tenants;
```

Current test tenant: `1766593643501` → Database: `erpdbf4001e2d`

## Files Changed Summary
1. `TenantFilter.java` - Fixed transaction/tenant context timing issue
2. `DataImportService.java` - Added importAddressesDataFromXml, fixed importAddresses, fixed loadAddressesIntoCache
3. `SampleDataController.java` - Updated to call new address import method, fixed dependency order
4. `RecruitmentController.java` - Renamed duplicate to .bak

## Conclusion
The core transaction and data persistence issue has been resolved. Addresses can now be successfully imported and persisted to the database. The fix ensures that the tenant context remains active until after transaction commit, allowing multi-tenant data to be properly routed and committed.

Student imports are blocked by a schema mismatch issue that needs to be addressed separately.
