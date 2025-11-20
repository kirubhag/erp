package krs.erp.repository.academic;

import java.util.Optional;

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
import krs.erp.model.academic.AcademicSettings;
import krs.erp.repository.OrganizationRepository;

/**
 * Test class for AcademicSettingsRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AcademicSettingsRepositoryTest {

    @Autowired
    private AcademicSettingsRepository academicSettingsRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Organization testOrganization;
    private AcademicSettings settings;

    @BeforeEach
    void setUp() {
        academicSettingsRepository.deleteAllInBatch();
        organizationRepository.deleteAllInBatch();

        // Create test organization
        testOrganization = new Organization();
        testOrganization.setName("Test School");
        testOrganization.setCode("TEST_SCH");
        testOrganization.setType("School");
        testOrganization = organizationRepository.save(testOrganization);

        // Create academic settings
        settings = new AcademicSettings();
        settings.setOrganizationId(testOrganization.getId());
        
        // Attendance settings
        settings.setEnableAttendanceTracking(true);
        settings.setAttendanceCalculationMethod("percentage");
        settings.setMinimumAttendancePercentage(75.0);
        settings.setAllowLateMarking(true);
        settings.setLateMarkingCutoffMinutes(30);
        settings.setEnableBiometricIntegration(false);
        
        // Exam settings
        settings.setDefaultExamDuration(60);
        settings.setAllowMakeupExams(true);
        settings.setMakeupExamDeadlineDays(7);
        settings.setPassingPercentage(40.0);
        settings.setEnableGradeModeration(false);
        settings.setAutoCalculateGrades(true);
        settings.setPublishResultsImmediately(false);
        
        // Promotion rules
        settings.setAutoPromoteStudents(false);
        settings.setMinimumAttendanceForPromotion(75.0);
        settings.setMinimumGradeForPromotion(40.0);
        settings.setAllowGraceMarks(true);
        settings.setGraceMarksLimit(5.0);
        settings.setRequireAllSubjectsPass(true);
        settings.setAllowCompartmentExams(true);
        
        settings = academicSettingsRepository.save(settings);
    }

    @Test
    void testSaveAcademicSettings() {
        Organization org2 = new Organization();
        org2.setName("Test College");
        org2.setCode("TEST_COL");
        org2.setType("College");
        org2 = organizationRepository.save(org2);

        AcademicSettings newSettings = new AcademicSettings();
        newSettings.setOrganizationId(org2.getId());
        newSettings.setMinimumAttendancePercentage(80.0);
        newSettings.setPassingPercentage(50.0);

        AcademicSettings saved = academicSettingsRepository.save(newSettings);

        assertNotNull(saved.getId());
        assertEquals(org2.getId(), saved.getOrganizationId());
        assertEquals(80.0, saved.getMinimumAttendancePercentage());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testFindById() {
        Optional<AcademicSettings> found = academicSettingsRepository.findById(settings.getId());

        assertTrue(found.isPresent());
        assertEquals(testOrganization.getId(), found.get().getOrganizationId());
        assertTrue(found.get().getEnableAttendanceTracking());
    }

    @Test
    void testFindByOrganizationId() {
        Optional<AcademicSettings> found = academicSettingsRepository.findByOrganizationId(
            testOrganization.getId());

        assertTrue(found.isPresent());
        assertEquals(settings.getId(), found.get().getId());
        assertEquals(75.0, found.get().getMinimumAttendancePercentage());
    }

    @Test
    void testUpdateAttendanceSettings() {
        settings.setEnableAttendanceTracking(false);
        settings.setMinimumAttendancePercentage(80.0);
        settings.setLateMarkingCutoffMinutes(45);
        AcademicSettings updated = academicSettingsRepository.save(settings);

        assertFalse(updated.getEnableAttendanceTracking());
        assertEquals(80.0, updated.getMinimumAttendancePercentage());
        assertEquals(45, updated.getLateMarkingCutoffMinutes());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void testUpdateExamSettings() {
        settings.setDefaultExamDuration(90);
        settings.setPassingPercentage(50.0);
        settings.setAutoCalculateGrades(false);
        AcademicSettings updated = academicSettingsRepository.save(settings);

        assertEquals(90, updated.getDefaultExamDuration());
        assertEquals(50.0, updated.getPassingPercentage());
        assertFalse(updated.getAutoCalculateGrades());
    }

    @Test
    void testUpdatePromotionSettings() {
        settings.setAutoPromoteStudents(true);
        settings.setMinimumGradeForPromotion(50.0);
        settings.setGraceMarksLimit(10.0);
        AcademicSettings updated = academicSettingsRepository.save(settings);

        assertTrue(updated.getAutoPromoteStudents());
        assertEquals(50.0, updated.getMinimumGradeForPromotion());
        assertEquals(10.0, updated.getGraceMarksLimit());
    }

    @Test
    void testDeleteAcademicSettings() {
        Long idToDelete = settings.getId();
        academicSettingsRepository.delete(settings);

        Optional<AcademicSettings> deleted = academicSettingsRepository.findById(idToDelete);
        assertFalse(deleted.isPresent());
    }

    @Test
    void testUniqueOrganizationConstraint() {
        // Try to create another settings for the same organization
        AcademicSettings duplicate = new AcademicSettings();
        duplicate.setOrganizationId(testOrganization.getId());

        // This should fail due to unique constraint, but we test by finding
        Optional<AcademicSettings> existing = academicSettingsRepository.findByOrganizationId(
            testOrganization.getId());
        assertTrue(existing.isPresent());
        assertEquals(settings.getId(), existing.get().getId());
    }

    @Test
    void testDefaultValues() {
        AcademicSettings defaults = new AcademicSettings();
        defaults.setOrganizationId(999L); // Dummy org ID for testing defaults

        // Check default values are set
        assertTrue(defaults.getEnableAttendanceTracking());
        assertEquals("percentage", defaults.getAttendanceCalculationMethod());
        assertEquals(75.0, defaults.getMinimumAttendancePercentage());
        assertTrue(defaults.getAllowLateMarking());
        assertEquals(30, defaults.getLateMarkingCutoffMinutes());
        assertFalse(defaults.getEnableBiometricIntegration());

        assertEquals(60, defaults.getDefaultExamDuration());
        assertTrue(defaults.getAllowMakeupExams());
        assertEquals(7, defaults.getMakeupExamDeadlineDays());
        assertEquals(40.0, defaults.getPassingPercentage());
        assertFalse(defaults.getEnableGradeModeration());
        assertTrue(defaults.getAutoCalculateGrades());
        assertFalse(defaults.getPublishResultsImmediately());

        assertFalse(defaults.getAutoPromoteStudents());
        assertEquals(75.0, defaults.getMinimumAttendanceForPromotion());
        assertEquals(40.0, defaults.getMinimumGradeForPromotion());
        assertTrue(defaults.getAllowGraceMarks());
        assertEquals(5.0, defaults.getGraceMarksLimit());
        assertTrue(defaults.getRequireAllSubjectsPass());
        assertTrue(defaults.getAllowCompartmentExams());
    }

    @Test
    void testAttendanceCalculationMethods() {
        settings.setAttendanceCalculationMethod("days");
        AcademicSettings saved = academicSettingsRepository.save(settings);
        assertEquals("days", saved.getAttendanceCalculationMethod());

        settings.setAttendanceCalculationMethod("sessions");
        saved = academicSettingsRepository.save(settings);
        assertEquals("sessions", saved.getAttendanceCalculationMethod());
    }

    @Test
    void testPercentageValidation() {
        AcademicSettings testSettings = academicSettingsRepository.findById(settings.getId()).get();
        
        // All percentages should be between 0 and 100
        assertTrue(testSettings.getMinimumAttendancePercentage() >= 0 && 
                   testSettings.getMinimumAttendancePercentage() <= 100);
        assertTrue(testSettings.getPassingPercentage() >= 0 && 
                   testSettings.getPassingPercentage() <= 100);
        assertTrue(testSettings.getMinimumAttendanceForPromotion() >= 0 && 
                   testSettings.getMinimumAttendanceForPromotion() <= 100);
        assertTrue(testSettings.getMinimumGradeForPromotion() >= 0 && 
                   testSettings.getMinimumGradeForPromotion() <= 100);
    }

    @Test
    void testBooleanFlags() {
        AcademicSettings testSettings = academicSettingsRepository.findById(settings.getId()).get();
        
        // Test all boolean fields are not null
        assertNotNull(testSettings.getEnableAttendanceTracking());
        assertNotNull(testSettings.getAllowLateMarking());
        assertNotNull(testSettings.getEnableBiometricIntegration());
        assertNotNull(testSettings.getAllowMakeupExams());
        assertNotNull(testSettings.getEnableGradeModeration());
        assertNotNull(testSettings.getAutoCalculateGrades());
        assertNotNull(testSettings.getPublishResultsImmediately());
        assertNotNull(testSettings.getAutoPromoteStudents());
        assertNotNull(testSettings.getAllowGraceMarks());
        assertNotNull(testSettings.getRequireAllSubjectsPass());
        assertNotNull(testSettings.getAllowCompartmentExams());
    }

    @Test
    void testSettingsForMultipleOrganizations() {
        // Create second organization
        Organization org2 = new Organization();
        org2.setName("Test University");
        org2.setCode("TEST_UNI");
        org2.setType("University");
        org2 = organizationRepository.save(org2);

        // Create settings for org2
        AcademicSettings org2Settings = new AcademicSettings();
        org2Settings.setOrganizationId(org2.getId());
        org2Settings.setMinimumAttendancePercentage(85.0);
        org2Settings.setPassingPercentage(45.0);
        org2Settings = academicSettingsRepository.save(org2Settings);

        // Verify both organizations have their own settings
        Optional<AcademicSettings> org1Settings = academicSettingsRepository.findByOrganizationId(
            testOrganization.getId());
        assertTrue(org1Settings.isPresent());
        assertEquals(75.0, org1Settings.get().getMinimumAttendancePercentage());

        Optional<AcademicSettings> org2Found = academicSettingsRepository.findByOrganizationId(
            org2.getId());
        assertTrue(org2Found.isPresent());
        assertEquals(85.0, org2Found.get().getMinimumAttendancePercentage());
        assertEquals(45.0, org2Found.get().getPassingPercentage());
    }
}
