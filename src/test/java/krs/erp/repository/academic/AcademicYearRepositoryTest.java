package krs.erp.repository.academic;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.Organization;
import krs.erp.model.academic.AcademicYear;
import krs.erp.repository.OrganizationRepository;

/**
 * Test class for AcademicYearRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AcademicYearRepositoryTest {

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Organization testOrganization;
    private AcademicYear year2024;
    private AcademicYear year2025;
    private AcademicYear year2026;

    @BeforeEach
    void setUp() {
        academicYearRepository.deleteAllInBatch();
        organizationRepository.deleteAllInBatch();

        // Create test organization
        testOrganization = new Organization();
        testOrganization.setName("Test School");
        testOrganization.setCode("TEST_SCHOOL");
        testOrganization.setType("School");
        testOrganization = organizationRepository.save(testOrganization);

        // Create academic year 2024-2025 (Active)
        year2024 = new AcademicYear();
        year2024.setName("2024-2025");
        year2024.setStartDate(LocalDate.of(2024, 6, 1));
        year2024.setEndDate(LocalDate.of(2025, 5, 31));
        year2024.setIsActive(true);
        year2024.setOrganizationId(testOrganization.getId());
        year2024 = academicYearRepository.save(year2024);

        // Create academic year 2025-2026 (Inactive)
        year2025 = new AcademicYear();
        year2025.setName("2025-2026");
        year2025.setStartDate(LocalDate.of(2025, 6, 1));
        year2025.setEndDate(LocalDate.of(2026, 5, 31));
        year2025.setIsActive(false);
        year2025.setOrganizationId(testOrganization.getId());
        year2025 = academicYearRepository.save(year2025);

        // Create academic year 2026-2027 (Inactive)
        year2026 = new AcademicYear();
        year2026.setName("2026-2027");
        year2026.setStartDate(LocalDate.of(2026, 6, 1));
        year2026.setEndDate(LocalDate.of(2027, 5, 31));
        year2026.setIsActive(false);
        year2026.setOrganizationId(testOrganization.getId());
        year2026 = academicYearRepository.save(year2026);
    }

    @Test
    void testSaveAcademicYear() {
        AcademicYear newYear = new AcademicYear();
        newYear.setName("2027-2028");
        newYear.setStartDate(LocalDate.of(2027, 6, 1));
        newYear.setEndDate(LocalDate.of(2028, 5, 31));
        newYear.setIsActive(false);
        newYear.setOrganizationId(testOrganization.getId());

        AcademicYear saved = academicYearRepository.save(newYear);

        assertNotNull(saved.getId());
        assertEquals("2027-2028", saved.getName());
        assertEquals(LocalDate.of(2027, 6, 1), saved.getStartDate());
        assertEquals(LocalDate.of(2028, 5, 31), saved.getEndDate());
        assertFalse(saved.getIsActive());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testFindById() {
        Optional<AcademicYear> found = academicYearRepository.findById(year2024.getId());

        assertTrue(found.isPresent());
        assertEquals("2024-2025", found.get().getName());
        assertTrue(found.get().getIsActive());
    }

    @Test
    void testFindByOrganizationIdOrderByStartDateDesc() {
        List<AcademicYear> years = academicYearRepository.findByOrganizationIdOrderByStartDateDesc(testOrganization.getId());

        assertThat(years).hasSize(3);
        // Should be ordered by start date descending (2026, 2025, 2024)
        assertEquals("2026-2027", years.get(0).getName());
        assertEquals("2025-2026", years.get(1).getName());
        assertEquals("2024-2025", years.get(2).getName());
    }

    @Test
    void testFindByOrganizationIdAndIsActive() {
        Optional<AcademicYear> activeYear = academicYearRepository.findByOrganizationIdAndIsActive(
            testOrganization.getId(), true);

        assertTrue(activeYear.isPresent());
        assertEquals("2024-2025", activeYear.get().getName());
        assertTrue(activeYear.get().getIsActive());
    }

    @Test
    void testDeactivateAllForOrganization() {
        // First verify there is an active year
        Optional<AcademicYear> activeBefore = academicYearRepository.findByOrganizationIdAndIsActive(
            testOrganization.getId(), true);
        assertTrue(activeBefore.isPresent());

        // Deactivate all
        academicYearRepository.deactivateAllForOrganization(testOrganization.getId());
        academicYearRepository.flush();

        // Verify all are inactive
        List<AcademicYear> allYears = academicYearRepository.findByOrganizationIdOrderByStartDateDesc(
            testOrganization.getId());
        assertThat(allYears).hasSize(3);
        assertThat(allYears).allMatch(year -> !year.getIsActive());

        // Verify no active year exists
        Optional<AcademicYear> activeAfter = academicYearRepository.findByOrganizationIdAndIsActive(
            testOrganization.getId(), true);
        assertFalse(activeAfter.isPresent());
    }

    @Test
    void testUpdateAcademicYear() {
        year2025.setName("2025-2026 Updated");
        year2025.setIsActive(true);
        AcademicYear updated = academicYearRepository.save(year2025);

        assertEquals("2025-2026 Updated", updated.getName());
        assertTrue(updated.getIsActive());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void testDeleteAcademicYear() {
        Long idToDelete = year2026.getId();
        academicYearRepository.delete(year2026);

        Optional<AcademicYear> deleted = academicYearRepository.findById(idToDelete);
        assertFalse(deleted.isPresent());

        List<AcademicYear> remaining = academicYearRepository.findByOrganizationIdOrderByStartDateDesc(
            testOrganization.getId());
        assertThat(remaining).hasSize(2);
    }

    @Test
    void testFindAll() {
        List<AcademicYear> allYears = academicYearRepository.findAll();
        assertThat(allYears).hasSize(3);
    }

    @Test
    void testOnlyOneActiveYear() {
        // Deactivate all years
        academicYearRepository.deactivateAllForOrganization(testOrganization.getId());
        academicYearRepository.flush();

        // Activate year 2025
        year2025.setIsActive(true);
        academicYearRepository.save(year2025);

        // Verify only one active year
        Optional<AcademicYear> activeYear = academicYearRepository.findByOrganizationIdAndIsActive(
            testOrganization.getId(), true);
        assertTrue(activeYear.isPresent());
        assertEquals("2025-2026", activeYear.get().getName());

        List<AcademicYear> allYears = academicYearRepository.findAll();
        long activeCount = allYears.stream().filter(AcademicYear::getIsActive).count();
        assertEquals(1, activeCount);
    }

    @Test
    void testAcademicYearWithDifferentOrganizations() {
        // Create another organization
        Organization org2 = new Organization();
        org2.setName("Test College");
        org2.setCode("TEST_COLLEGE");
        org2.setType("College");
        org2 = organizationRepository.save(org2);

        // Create academic year for org2
        AcademicYear org2Year = new AcademicYear();
        org2Year.setName("2024-2025");
        org2Year.setStartDate(LocalDate.of(2024, 8, 1));
        org2Year.setEndDate(LocalDate.of(2025, 7, 31));
        org2Year.setIsActive(true);
        org2Year.setOrganizationId(org2.getId());
        academicYearRepository.save(org2Year);

        // Verify org1 has 3 years
        List<AcademicYear> org1Years = academicYearRepository.findByOrganizationIdOrderByStartDateDesc(
            testOrganization.getId());
        assertThat(org1Years).hasSize(3);

        // Verify org2 has 1 year
        List<AcademicYear> org2Years = academicYearRepository.findByOrganizationIdOrderByStartDateDesc(
            org2.getId());
        assertThat(org2Years).hasSize(1);
        assertEquals("2024-2025", org2Years.get(0).getName());
    }

    @Test
    void testAcademicYearDateValidation() {
        AcademicYear year = academicYearRepository.findById(year2024.getId()).get();
        
        assertNotNull(year.getStartDate());
        assertNotNull(year.getEndDate());
        assertTrue(year.getEndDate().isAfter(year.getStartDate()));
    }
}
