# Academic Module - Unit Tests and Sample Data

## Overview
Comprehensive unit tests and XML-based sample data for the academic module entities.

## Test Coverage Summary

### Repository Tests Created: 4 classes, 46 test methods

#### 1. AcademicYearRepositoryTest (11 tests)
- `testSaveAcademicYear` - Create new academic year
- `testFindById` - Find by primary key
- `testFindByOrganizationIdOrderByStartDateDesc` - List by organization with ordering
- `testFindByOrganizationIdAndIsActive` - Find active year for organization
- `testDeactivateAllForOrganization` - Test @Modifying query with cache clearing
- `testUpdateAcademicYear` - Update existing year
- `testDeleteAcademicYear` - Delete year
- `testFindAll` - List all years
- `testOnlyOneActiveYear` - Validate single active year constraint
- `testAcademicYearWithDifferentOrganizations` - Multi-tenancy test
- `testAcademicYearDateValidation` - Date range validation

#### 2. TermRepositoryTest (11 tests)
- `testSaveTerm` - Create new term
- `testFindById` - Find by primary key
- `testFindByOrganizationIdOrderByStartDateAsc` - List by organization
- `testFindByAcademicYearIdOrderByStartDateAsc` - Filter by academic year
- `testUpdateTerm` - Update existing term
- `testDeleteTerm` - Delete term
- `testFindAll` - List all terms
- `testTermsForMultipleAcademicYears` - Multiple years association
- `testTermDateValidation` - Date range validation
- `testTermWithinAcademicYearDates` - Term within year boundaries
- `testTermsForDifferentOrganizations` - Multi-tenancy test

#### 3. GradingScaleRepositoryTest (11 tests)
- `testSaveGradingScale` - Create new grading scale
- `testFindById` - Find by primary key
- `testFindByOrganizationIdOrderByMinPercentageDesc` - List with ordering
- `testUpdateGradingScale` - Update existing scale
- `testDeleteGradingScale` - Delete scale
- `testFindAll` - List all scales
- `testGradingScalePercentageRanges` - Percentage validation
- `testGradingScaleGradePoints` - Grade point validation
- `testGradingScalesForDifferentOrganizations` - Multi-tenancy test
- `testGradingScaleWithSpecialCharacters` - Special characters in letter grades
- `testGradingScaleDecimalPrecision` - Decimal precision test

#### 4. AcademicSettingsRepositoryTest (13 tests)
- `testSaveAcademicSettings` - Create new settings
- `testFindById` - Find by primary key
- `testFindByOrganizationId` - Find by organization (unique constraint)
- `testUpdateAttendanceSettings` - Update attendance configuration
- `testUpdateExamSettings` - Update exam configuration
- `testUpdatePromotionSettings` - Update promotion rules
- `testDeleteAcademicSettings` - Delete settings
- `testUniqueOrganizationConstraint` - Validate unique constraint
- `testDefaultValues` - Test default field values
- `testAttendanceCalculationMethods` - Different calculation methods
- `testPercentageValidation` - Percentage range validation
- `testBooleanFlags` - All boolean fields not null
- `testSettingsForMultipleOrganizations` - Multi-tenancy test

## XML Sample Data Files

### Location: `src/main/resources/data/academic/`

### 1. academic-years.xml
Sample academic years for testing and initial data:
- **2023-2024** (Inactive) - June 1, 2023 to May 31, 2024
- **2024-2025** (Active) - June 1, 2024 to May 31, 2025
- **2025-2026** (Inactive) - June 1, 2025 to May 31, 2026

### 2. terms.xml
Sample terms across academic years:
- **2024-2025 Academic Year:**
  - Fall Semester 2024: Aug 15 - Dec 20, 2024
  - Spring Semester 2025: Jan 10 - May 25, 2025
  - Summer Term 2025: Jun 1 - Jul 31, 2025

- **2025-2026 Academic Year:**
  - Fall Semester 2025: Aug 15 - Dec 20, 2025
  - Spring Semester 2026: Jan 10 - May 25, 2026

### 3. grading-scales.xml
Standard US grading scale (9 grades):
- **A+** (Outstanding): 95-100%, 4.0 GPA
- **A** (Excellent): 90-94.99%, 4.0 GPA
- **B+** (Very Good): 85-89.99%, 3.5 GPA
- **B** (Good): 80-84.99%, 3.0 GPA
- **C+** (Above Average): 75-79.99%, 2.5 GPA
- **C** (Average): 70-74.99%, 2.0 GPA
- **D+** (Below Average): 65-69.99%, 1.5 GPA
- **D** (Pass): 60-64.99%, 1.0 GPA
- **F** (Fail): 0-59.99%, 0.0 GPA

## Data Initializer

### AcademicDataInitializer.java
- **Package:** `krs.erp.initializer`
- **Order:** 8 (after GradeDataInitializer @Order(7))
- **Implements:** CommandLineRunner

#### Features:
1. **Sequential Loading:**
   - Academic years first (no dependencies)
   - Terms second (depends on academic years)
   - Grading scales (no dependencies)

2. **Duplicate Prevention:**
   - Academic Year: `name + organizationId` unique key
   - Term: `name + academicYearId` unique key
   - Grading Scale: `letterGrade + organizationId` unique key

3. **Error Handling:**
   - Graceful handling of missing XML files
   - Comprehensive logging (total/loaded/skipped counts)
   - Exception handling with detailed error messages

4. **Date Parsing:**
   - Uses `DateTimeFormatter` with "yyyy-MM-dd" pattern
   - Converts XML strings to `LocalDate` objects

## Test Results

```
[INFO] Tests run: 463, Failures: 0, Errors: 0, Skipped: 0
```

- **Total Repository Tests:** 463
- **Existing Tests:** 417
- **New Academic Tests:** 46
- **Success Rate:** 100%

## Test Patterns Used

### Test Structure
```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class XxxRepositoryTest {
    @Autowired
    private XxxRepository repository;
    
    @BeforeEach
    void setUp() {
        repository.deleteAllInBatch();
        // Create fixtures
    }
    
    @Test
    void testXxx() {
        // Arrange, Act, Assert
        assertThat(actual).hasSize(expected);
    }
}
```

### Key Testing Practices
- Use `@Transactional` for automatic rollback
- Clear all data in `@BeforeEach` with `deleteAllInBatch()`
- Create complete test fixtures with all required fields
- Use AssertJ's `assertThat()` for fluent assertions
- Test custom query methods and edge cases
- Validate multi-tenancy with different organizations
- Test data constraints and validation rules

## Repository Fix

### AcademicYearRepository
Fixed `@Modifying` query to clear Hibernate cache:

**Before:**
```java
@Modifying
@Query("UPDATE AcademicYear a SET a.isActive = false WHERE a.organizationId = :organizationId")
void deactivateAllForOrganization(Long organizationId);
```

**After:**
```java
@Modifying(clearAutomatically = true)
@Query("UPDATE AcademicYear a SET a.isActive = false WHERE a.organizationId = :organizationId")
void deactivateAllForOrganization(Long organizationId);
```

**Reason:** Without `clearAutomatically = true`, subsequent queries might return stale cached entities.

## Files Created

### Test Files (4)
- `src/test/java/krs/erp/repository/academic/AcademicYearRepositoryTest.java` (285 lines)
- `src/test/java/krs/erp/repository/academic/TermRepositoryTest.java` (295 lines)
- `src/test/java/krs/erp/repository/academic/GradingScaleRepositoryTest.java` (255 lines)
- `src/test/java/krs/erp/repository/academic/AcademicSettingsRepositoryTest.java` (280 lines)

### XML Data Files (3)
- `src/main/resources/data/academic/academic-years.xml` (18 lines)
- `src/main/resources/data/academic/terms.xml` (38 lines)
- `src/main/resources/data/academic/grading-scales.xml` (64 lines)

### Data Initializer (1)
- `src/main/java/krs/erp/initializer/AcademicDataInitializer.java` (268 lines)

### Modified Files (1)
- `src/main/java/krs/erp/repository/academic/AcademicYearRepository.java` (added clearAutomatically)

## Usage

### Running Academic Tests Only
```bash
./mvnw test -Dtest="krs.erp.repository.academic.*Test"
```

### Running All Repository Tests
```bash
./mvnw test -Dtest="*RepositoryTest"
```

### Sample Data Loading
Sample data is automatically loaded on application startup via `AcademicDataInitializer`. To disable:
```java
// Comment out @Component annotation
// @Component
@Order(8)
public class AcademicDataInitializer implements CommandLineRunner {
    // ...
}
```

## Future Enhancements

1. **Service Layer Tests:** Create unit tests for academic services
2. **Controller Tests:** Add integration tests for REST endpoints
3. **Additional Sample Data:** Add more diverse academic configurations
4. **Performance Tests:** Test with large datasets
5. **Academic Settings Defaults:** More XML sample data for different educational institutions

---

**Created:** November 20, 2025  
**Status:** ✅ Complete - All 463 tests passing  
**Committed:** d2dcfa8
