package krs.erp.repository.academic;

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
import krs.erp.model.academic.GradingScale;
import krs.erp.repository.OrganizationRepository;

/**
 * Test class for GradingScaleRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GradingScaleRepositoryTest {

    @Autowired
    private GradingScaleRepository gradingScaleRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Organization testOrganization;
    private GradingScale gradeA;
    private GradingScale gradeB;
    private GradingScale gradeC;
    private GradingScale gradeD;
    private GradingScale gradeF;

    @BeforeEach
    void setUp() {
        gradingScaleRepository.deleteAllInBatch();
        organizationRepository.deleteAllInBatch();

        // Create test organization
        testOrganization = new Organization();
        testOrganization.setName("Test Academy");
        testOrganization.setCode("TEST_ACAD");
        testOrganization.setType("School");
        testOrganization = organizationRepository.save(testOrganization);

        // Create Grade A+
        gradeA = new GradingScale();
        gradeA.setName("Excellent");
        gradeA.setLetterGrade("A+");
        gradeA.setMinPercentage(90.0);
        gradeA.setMaxPercentage(100.0);
        gradeA.setGradePoint(4.0);
        gradeA.setOrganizationId(testOrganization.getId());
        gradeA = gradingScaleRepository.save(gradeA);

        // Create Grade B
        gradeB = new GradingScale();
        gradeB.setName("Very Good");
        gradeB.setLetterGrade("B");
        gradeB.setMinPercentage(75.0);
        gradeB.setMaxPercentage(89.99);
        gradeB.setGradePoint(3.0);
        gradeB.setOrganizationId(testOrganization.getId());
        gradeB = gradingScaleRepository.save(gradeB);

        // Create Grade C
        gradeC = new GradingScale();
        gradeC.setName("Good");
        gradeC.setLetterGrade("C");
        gradeC.setMinPercentage(60.0);
        gradeC.setMaxPercentage(74.99);
        gradeC.setGradePoint(2.0);
        gradeC.setOrganizationId(testOrganization.getId());
        gradeC = gradingScaleRepository.save(gradeC);

        // Create Grade D
        gradeD = new GradingScale();
        gradeD.setName("Pass");
        gradeD.setLetterGrade("D");
        gradeD.setMinPercentage(40.0);
        gradeD.setMaxPercentage(59.99);
        gradeD.setGradePoint(1.0);
        gradeD.setOrganizationId(testOrganization.getId());
        gradeD = gradingScaleRepository.save(gradeD);

        // Create Grade F
        gradeF = new GradingScale();
        gradeF.setName("Fail");
        gradeF.setLetterGrade("F");
        gradeF.setMinPercentage(0.0);
        gradeF.setMaxPercentage(39.99);
        gradeF.setGradePoint(0.0);
        gradeF.setOrganizationId(testOrganization.getId());
        gradeF = gradingScaleRepository.save(gradeF);
    }

    @Test
    void testSaveGradingScale() {
        GradingScale newGrade = new GradingScale();
        newGrade.setName("Outstanding");
        newGrade.setLetterGrade("A++");
        newGrade.setMinPercentage(95.0);
        newGrade.setMaxPercentage(100.0);
        newGrade.setGradePoint(4.5);
        newGrade.setOrganizationId(testOrganization.getId());

        GradingScale saved = gradingScaleRepository.save(newGrade);

        assertNotNull(saved.getId());
        assertEquals("Outstanding", saved.getName());
        assertEquals("A++", saved.getLetterGrade());
        assertEquals(95.0, saved.getMinPercentage());
        assertEquals(100.0, saved.getMaxPercentage());
        assertEquals(4.5, saved.getGradePoint());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testFindById() {
        Optional<GradingScale> found = gradingScaleRepository.findById(gradeA.getId());

        assertTrue(found.isPresent());
        assertEquals("Excellent", found.get().getName());
        assertEquals("A+", found.get().getLetterGrade());
        assertEquals(4.0, found.get().getGradePoint());
    }

    @Test
    void testFindByOrganizationIdOrderByMinPercentageDesc() {
        List<GradingScale> scales = gradingScaleRepository.findByOrganizationIdOrderByMinPercentageDesc(
            testOrganization.getId());

        assertThat(scales).hasSize(5);
        // Should be ordered by min percentage descending (A+, B, C, D, F)
        assertEquals("A+", scales.get(0).getLetterGrade());
        assertEquals("B", scales.get(1).getLetterGrade());
        assertEquals("C", scales.get(2).getLetterGrade());
        assertEquals("D", scales.get(3).getLetterGrade());
        assertEquals("F", scales.get(4).getLetterGrade());
    }

    @Test
    void testUpdateGradingScale() {
        gradeB.setName("Very Good - Updated");
        gradeB.setGradePoint(3.3);
        GradingScale updated = gradingScaleRepository.save(gradeB);

        assertEquals("Very Good - Updated", updated.getName());
        assertEquals(3.3, updated.getGradePoint());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void testDeleteGradingScale() {
        Long idToDelete = gradeF.getId();
        gradingScaleRepository.delete(gradeF);

        Optional<GradingScale> deleted = gradingScaleRepository.findById(idToDelete);
        assertFalse(deleted.isPresent());

        List<GradingScale> remaining = gradingScaleRepository.findByOrganizationIdOrderByMinPercentageDesc(
            testOrganization.getId());
        assertThat(remaining).hasSize(4);
    }

    @Test
    void testFindAll() {
        List<GradingScale> allScales = gradingScaleRepository.findAll();
        assertThat(allScales).hasSize(5);
    }

    @Test
    void testGradingScalePercentageRanges() {
        List<GradingScale> scales = gradingScaleRepository.findByOrganizationIdOrderByMinPercentageDesc(
            testOrganization.getId());

        for (GradingScale scale : scales) {
            assertNotNull(scale.getMinPercentage());
            assertNotNull(scale.getMaxPercentage());
            assertTrue(scale.getMaxPercentage() >= scale.getMinPercentage());
            assertTrue(scale.getMinPercentage() >= 0.0 && scale.getMinPercentage() <= 100.0);
            assertTrue(scale.getMaxPercentage() >= 0.0 && scale.getMaxPercentage() <= 100.0);
        }
    }

    @Test
    void testGradingScaleGradePoints() {
        List<GradingScale> scales = gradingScaleRepository.findByOrganizationIdOrderByMinPercentageDesc(
            testOrganization.getId());

        for (GradingScale scale : scales) {
            assertNotNull(scale.getGradePoint());
            assertTrue(scale.getGradePoint() >= 0.0);
        }
    }

    @Test
    void testGradingScalesForDifferentOrganizations() {
        // Create another organization
        Organization org2 = new Organization();
        org2.setName("Test Institute");
        org2.setCode("TEST_INST");
        org2.setType("Institute");
        org2 = organizationRepository.save(org2);

        // Create grading scale for org2
        GradingScale org2Grade = new GradingScale();
        org2Grade.setName("First Class");
        org2Grade.setLetterGrade("I");
        org2Grade.setMinPercentage(70.0);
        org2Grade.setMaxPercentage(100.0);
        org2Grade.setGradePoint(4.0);
        org2Grade.setOrganizationId(org2.getId());
        gradingScaleRepository.save(org2Grade);

        // Verify org1 has 5 scales
        List<GradingScale> org1Scales = gradingScaleRepository.findByOrganizationIdOrderByMinPercentageDesc(
            testOrganization.getId());
        assertThat(org1Scales).hasSize(5);

        // Verify org2 has 1 scale
        List<GradingScale> org2Scales = gradingScaleRepository.findByOrganizationIdOrderByMinPercentageDesc(
            org2.getId());
        assertThat(org2Scales).hasSize(1);
        assertEquals("First Class", org2Scales.get(0).getName());
    }

    @Test
    void testGradingScaleWithSpecialCharacters() {
        GradingScale specialGrade = new GradingScale();
        specialGrade.setName("A+ Plus");
        specialGrade.setLetterGrade("A+");
        specialGrade.setMinPercentage(92.0);
        specialGrade.setMaxPercentage(94.99);
        specialGrade.setGradePoint(4.0);
        specialGrade.setOrganizationId(testOrganization.getId());

        GradingScale saved = gradingScaleRepository.save(specialGrade);
        assertNotNull(saved.getId());
        assertEquals("A+", saved.getLetterGrade());
    }

    @Test
    void testGradingScaleDecimalPrecision() {
        GradingScale decimalGrade = new GradingScale();
        decimalGrade.setName("B+");
        decimalGrade.setLetterGrade("B+");
        decimalGrade.setMinPercentage(85.50);
        decimalGrade.setMaxPercentage(89.99);
        decimalGrade.setGradePoint(3.5);
        decimalGrade.setOrganizationId(testOrganization.getId());

        GradingScale saved = gradingScaleRepository.save(decimalGrade);
        assertEquals(85.50, saved.getMinPercentage());
        assertEquals(89.99, saved.getMaxPercentage());
        assertEquals(3.5, saved.getGradePoint());
    }
}
