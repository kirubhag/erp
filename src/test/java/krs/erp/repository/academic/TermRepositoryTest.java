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
import krs.erp.model.academic.Term;
import krs.erp.repository.OrganizationRepository;

/**
 * Test class for TermRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TermRepositoryTest {

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Organization testOrganization;
    private AcademicYear academicYear;
    private Term fallTerm;
    private Term springTerm;
    private Term summerTerm;

    @BeforeEach
    void setUp() {
        termRepository.deleteAllInBatch();
        academicYearRepository.deleteAllInBatch();
        organizationRepository.deleteAllInBatch();

        // Create test organization
        testOrganization = new Organization();
        testOrganization.setName("Test University");
        testOrganization.setCode("TEST_UNI");
        testOrganization.setType("University");
        testOrganization = organizationRepository.save(testOrganization);

        // Create academic year
        academicYear = new AcademicYear();
        academicYear.setName("2024-2025");
        academicYear.setStartDate(LocalDate.of(2024, 8, 1));
        academicYear.setEndDate(LocalDate.of(2025, 7, 31));
        academicYear.setIsActive(true);
        academicYear.setOrganizationId(testOrganization.getId());
        academicYear = academicYearRepository.save(academicYear);

        // Create Fall Semester
        fallTerm = new Term();
        fallTerm.setName("Fall Semester 2024");
        fallTerm.setStartDate(LocalDate.of(2024, 8, 15));
        fallTerm.setEndDate(LocalDate.of(2024, 12, 20));
        fallTerm.setAcademicYearId(academicYear.getId());
        fallTerm.setOrganizationId(testOrganization.getId());
        fallTerm = termRepository.save(fallTerm);

        // Create Spring Semester
        springTerm = new Term();
        springTerm.setName("Spring Semester 2025");
        springTerm.setStartDate(LocalDate.of(2025, 1, 10));
        springTerm.setEndDate(LocalDate.of(2025, 5, 25));
        springTerm.setAcademicYearId(academicYear.getId());
        springTerm.setOrganizationId(testOrganization.getId());
        springTerm = termRepository.save(springTerm);

        // Create Summer Semester
        summerTerm = new Term();
        summerTerm.setName("Summer Semester 2025");
        summerTerm.setStartDate(LocalDate.of(2025, 6, 1));
        summerTerm.setEndDate(LocalDate.of(2025, 7, 31));
        summerTerm.setAcademicYearId(academicYear.getId());
        summerTerm.setOrganizationId(testOrganization.getId());
        summerTerm = termRepository.save(summerTerm);
    }

    @Test
    void testSaveTerm() {
        Term newTerm = new Term();
        newTerm.setName("Winter Term 2024");
        newTerm.setStartDate(LocalDate.of(2024, 12, 21));
        newTerm.setEndDate(LocalDate.of(2025, 1, 9));
        newTerm.setAcademicYearId(academicYear.getId());
        newTerm.setOrganizationId(testOrganization.getId());

        Term saved = termRepository.save(newTerm);

        assertNotNull(saved.getId());
        assertEquals("Winter Term 2024", saved.getName());
        assertEquals(academicYear.getId(), saved.getAcademicYearId());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testFindById() {
        Optional<Term> found = termRepository.findById(fallTerm.getId());

        assertTrue(found.isPresent());
        assertEquals("Fall Semester 2024", found.get().getName());
        assertEquals(LocalDate.of(2024, 8, 15), found.get().getStartDate());
    }

    @Test
    void testFindByOrganizationIdOrderByStartDateAsc() {
        List<Term> terms = termRepository.findByOrganizationIdOrderByStartDateAsc(testOrganization.getId());

        assertThat(terms).hasSize(3);
        // Should be ordered by start date ascending
        assertEquals("Fall Semester 2024", terms.get(0).getName());
        assertEquals("Spring Semester 2025", terms.get(1).getName());
        assertEquals("Summer Semester 2025", terms.get(2).getName());
    }

    @Test
    void testFindByAcademicYearIdOrderByStartDateAsc() {
        List<Term> terms = termRepository.findByAcademicYearIdOrderByStartDateAsc(academicYear.getId());

        assertThat(terms).hasSize(3);
        assertEquals("Fall Semester 2024", terms.get(0).getName());
        assertEquals("Spring Semester 2025", terms.get(1).getName());
        assertEquals("Summer Semester 2025", terms.get(2).getName());
    }

    @Test
    void testUpdateTerm() {
        springTerm.setName("Spring Semester 2025 - Updated");
        springTerm.setEndDate(LocalDate.of(2025, 5, 30));
        Term updated = termRepository.save(springTerm);

        assertEquals("Spring Semester 2025 - Updated", updated.getName());
        assertEquals(LocalDate.of(2025, 5, 30), updated.getEndDate());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void testDeleteTerm() {
        Long idToDelete = summerTerm.getId();
        termRepository.delete(summerTerm);

        Optional<Term> deleted = termRepository.findById(idToDelete);
        assertFalse(deleted.isPresent());

        List<Term> remaining = termRepository.findByAcademicYearIdOrderByStartDateAsc(academicYear.getId());
        assertThat(remaining).hasSize(2);
    }

    @Test
    void testFindAll() {
        List<Term> allTerms = termRepository.findAll();
        assertThat(allTerms).hasSize(3);
    }

    @Test
    void testTermsForMultipleAcademicYears() {
        // Create another academic year
        AcademicYear year2025 = new AcademicYear();
        year2025.setName("2025-2026");
        year2025.setStartDate(LocalDate.of(2025, 8, 1));
        year2025.setEndDate(LocalDate.of(2026, 7, 31));
        year2025.setIsActive(false);
        year2025.setOrganizationId(testOrganization.getId());
        year2025 = academicYearRepository.save(year2025);

        // Create term for new year
        Term fall2025 = new Term();
        fall2025.setName("Fall Semester 2025");
        fall2025.setStartDate(LocalDate.of(2025, 8, 15));
        fall2025.setEndDate(LocalDate.of(2025, 12, 20));
        fall2025.setAcademicYearId(year2025.getId());
        fall2025.setOrganizationId(testOrganization.getId());
        termRepository.save(fall2025);

        // Verify first academic year has 3 terms
        List<Term> year2024Terms = termRepository.findByAcademicYearIdOrderByStartDateAsc(academicYear.getId());
        assertThat(year2024Terms).hasSize(3);

        // Verify second academic year has 1 term
        List<Term> year2025Terms = termRepository.findByAcademicYearIdOrderByStartDateAsc(year2025.getId());
        assertThat(year2025Terms).hasSize(1);
        assertEquals("Fall Semester 2025", year2025Terms.get(0).getName());
    }

    @Test
    void testTermDateValidation() {
        Term term = termRepository.findById(fallTerm.getId()).get();
        
        assertNotNull(term.getStartDate());
        assertNotNull(term.getEndDate());
        assertTrue(term.getEndDate().isAfter(term.getStartDate()));
    }

    @Test
    void testTermWithinAcademicYearDates() {
        Term term = termRepository.findById(fallTerm.getId()).get();
        AcademicYear year = academicYearRepository.findById(term.getAcademicYearId()).get();

        // Term should be within academic year dates
        assertTrue(term.getStartDate().isAfter(year.getStartDate()) || 
                   term.getStartDate().isEqual(year.getStartDate()));
        assertTrue(term.getEndDate().isBefore(year.getEndDate()) || 
                   term.getEndDate().isEqual(year.getEndDate()));
    }

    @Test
    void testTermsForDifferentOrganizations() {
        // Create another organization
        Organization org2 = new Organization();
        org2.setName("Test College");
        org2.setCode("TEST_COL");
        org2.setType("College");
        org2 = organizationRepository.save(org2);

        // Create academic year for org2
        AcademicYear org2Year = new AcademicYear();
        org2Year.setName("2024-2025");
        org2Year.setStartDate(LocalDate.of(2024, 7, 1));
        org2Year.setEndDate(LocalDate.of(2025, 6, 30));
        org2Year.setIsActive(true);
        org2Year.setOrganizationId(org2.getId());
        org2Year = academicYearRepository.save(org2Year);

        // Create term for org2
        Term org2Term = new Term();
        org2Term.setName("Semester 1");
        org2Term.setStartDate(LocalDate.of(2024, 7, 15));
        org2Term.setEndDate(LocalDate.of(2024, 12, 15));
        org2Term.setAcademicYearId(org2Year.getId());
        org2Term.setOrganizationId(org2.getId());
        termRepository.save(org2Term);

        // Verify org1 has 3 terms
        List<Term> org1Terms = termRepository.findByOrganizationIdOrderByStartDateAsc(testOrganization.getId());
        assertThat(org1Terms).hasSize(3);

        // Verify org2 has 1 term
        List<Term> org2Terms = termRepository.findByOrganizationIdOrderByStartDateAsc(org2.getId());
        assertThat(org2Terms).hasSize(1);
        assertEquals("Semester 1", org2Terms.get(0).getName());
    }
}
