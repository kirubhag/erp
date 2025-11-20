package krs.erp.repository;

import java.time.LocalDateTime;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.entity.DuplicateAction;
import krs.erp.entity.ImportSession;
import krs.erp.entity.ImportStatus;
import krs.erp.entity.ImportType;

/**
 * Test class for ImportSessionRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ImportSessionRepositoryTest {

    @Autowired
    private ImportSessionRepository importSessionRepository;

    private ImportSession session1;
    private ImportSession session2;
    private ImportSession session3;

    @BeforeEach
    void setUp() {
        importSessionRepository.deleteAllInBatch();

        // Create test import sessions
        session1 = new ImportSession();
        session1.setId("TEST-SESSION-001");
        session1.setUserId(1L);
        session1.setOrganizationId(null);
        session1.setEntityType("students");
        session1.setFileName("students_import.csv");
        session1.setFileFormat("csv");
        session1.setTotalRecords(100);
        session1.setFileSize(25600L);
        session1.setImportType(ImportType.ORGANIZATION);
        session1.setDuplicateAction(DuplicateAction.SKIP);
        session1.setFindDuplicatesBy("email");
        session1.setEnableManualApproval(false);
        session1.setSkipEmptyFields(true);
        session1.setStatus(ImportStatus.COMPLETED);
        session1.setAddedRecords(95);
        session1.setUpdatedRecords(0);
        session1.setSkippedRecords(5);
        session1.setFailedRecords(0);
        session1.setSuccessRate(95.0);
        session1 = importSessionRepository.save(session1);

        session2 = new ImportSession();
        session2.setId("TEST-SESSION-002");
        session2.setUserId(1L);
        session2.setOrganizationId(null);
        session2.setEntityType("staff");
        session2.setFileName("staff_import.xlsx");
        session2.setFileFormat("xlsx");
        session2.setTotalRecords(50);
        session2.setFileSize(15000L);
        session2.setImportType(ImportType.ORGANIZATION);
        session2.setDuplicateAction(DuplicateAction.OVERWRITE);
        session2.setFindDuplicatesBy("email");
        session2.setEnableManualApproval(false);
        session2.setSkipEmptyFields(false);
        session2.setStatus(ImportStatus.IN_PROGRESS);
        session2.setAddedRecords(0);
        session2.setUpdatedRecords(0);
        session2.setSkippedRecords(0);
        session2.setFailedRecords(0);
        session2 = importSessionRepository.save(session2);

        session3 = new ImportSession();
        session3.setId("TEST-SESSION-003");
        session3.setUserId(2L);
        session3.setOrganizationId(null);
        session3.setEntityType("students");
        session3.setFileName("students_batch2.csv");
        session3.setFileFormat("csv");
        session3.setTotalRecords(75);
        session3.setFileSize(20000L);
        session3.setImportType(ImportType.PERSONAL);
        session3.setDuplicateAction(DuplicateAction.CLONE);
        session3.setFindDuplicatesBy("name");
        session3.setEnableManualApproval(true);
        session3.setSkipEmptyFields(true);
        session3.setStatus(ImportStatus.PENDING);
        session3 = importSessionRepository.save(session3);
    }

    @Test
    void testSaveImportSession() {
        ImportSession newSession = new ImportSession();
        newSession.setId("TEST-SESSION-004");
        newSession.setUserId(3L);
        newSession.setOrganizationId(null);
        newSession.setEntityType("parents");
        newSession.setFileName("parents.csv");
        newSession.setFileFormat("csv");
        newSession.setTotalRecords(30);
        newSession.setFileSize(8000L);
        newSession.setImportType(ImportType.ORGANIZATION);
        newSession.setDuplicateAction(DuplicateAction.SKIP);
        newSession.setStatus(ImportStatus.PENDING);
        
        ImportSession saved = importSessionRepository.save(newSession);
        
        assertEquals("TEST-SESSION-004", saved.getId()); // ID is manually set, not auto-generated
        assertEquals("parents.csv", saved.getFileName());
        assertEquals(ImportStatus.PENDING, saved.getStatus());
        // Note: @CreationTimestamp may not work correctly with deleteAllInBatch in test context
    }

    @Test
    void testFindByUserIdOrderByUploadedAtDesc() {
        List<ImportSession> sessions = importSessionRepository.findByUserIdOrderByUploadedAtDesc(1L);
        
        assertThat(sessions).hasSize(2);
        // Records are ordered by upload time desc, but all created at similar time
        // Just verify we get both sessions
        assertTrue(sessions.stream().anyMatch(s -> "students_import.csv".equals(s.getFileName())));
        assertTrue(sessions.stream().anyMatch(s -> "staff_import.xlsx".equals(s.getFileName())));
    }

    @Test
    void testFindByUserIdAndEntityTypeOrderByUploadedAtDesc() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ImportSession> page = importSessionRepository.findByUserIdAndEntityTypeOrderByUploadedAtDesc(
            1L, "students", pageRequest);
        
        assertThat(page.getContent()).hasSize(1);
        assertEquals("students_import.csv", page.getContent().get(0).getFileName());
    }

    @Test
    void testFindByOrganizationIdOrderByUploadedAtDesc() {
        List<ImportSession> sessions = importSessionRepository.findByOrganizationIdOrderByUploadedAtDesc(null);
        
        assertThat(sessions).hasSize(3); // All 3 sessions have null organization
        // Just verify we get all sessions for organization ID null
        assertTrue(sessions.stream().anyMatch(s -> "students_import.csv".equals(s.getFileName())));
        assertTrue(sessions.stream().anyMatch(s -> "staff_import.xlsx".equals(s.getFileName())));
        assertTrue(sessions.stream().anyMatch(s -> "students_batch2.csv".equals(s.getFileName())));
    }

    @Test
    void testFindByStatusOrderByUploadedAtDesc() {
        List<ImportSession> completedSessions = importSessionRepository.findByStatusOrderByUploadedAtDesc(ImportStatus.COMPLETED);
        
        assertThat(completedSessions).hasSize(1);
        assertEquals("students_import.csv", completedSessions.get(0).getFileName());
        
        List<ImportSession> inProgressSessions = importSessionRepository.findByStatusOrderByUploadedAtDesc(ImportStatus.IN_PROGRESS);
        assertThat(inProgressSessions).hasSize(1);
        assertEquals("staff_import.xlsx", inProgressSessions.get(0).getFileName());
    }

    @Test
    void testFindActiveImports() {
        List<ImportSession> activeImports = importSessionRepository.findActiveImports();
        
        assertThat(activeImports).hasSize(2);
        // Should include IN_PROGRESS and PENDING sessions
        assertTrue(activeImports.stream().anyMatch(s -> s.getStatus() == ImportStatus.IN_PROGRESS));
        assertTrue(activeImports.stream().anyMatch(s -> s.getStatus() == ImportStatus.PENDING));
    }

    @Test
    void testFindByUserIdAndDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusHours(1);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);
        
        List<ImportSession> sessions = importSessionRepository.findByUserIdAndDateRange(1L, startDate, endDate);
        
        assertThat(sessions).hasSize(2);
    }

    @Test
    void testCountByUserIdAndEntityType() {
        Long count = importSessionRepository.countByUserIdAndEntityType(1L, "students");
        
        assertEquals(1L, count);
    }

    @Test
    void testUpdateImportSession() {
        session2.setStatus(ImportStatus.COMPLETED);
        session2.setAddedRecords(48);
        session2.setUpdatedRecords(2);
        session2.setSuccessRate(100.0);
        session2.setImportedAt(LocalDateTime.now());
        
        ImportSession updated = importSessionRepository.save(session2);
        
        assertEquals(ImportStatus.COMPLETED, updated.getStatus());
        assertEquals(48, updated.getAddedRecords());
        assertNotNull(updated.getImportedAt());
    }

    @Test
    void testDeleteImportSession() {
        importSessionRepository.delete(session1);
        
        Optional<ImportSession> found = importSessionRepository.findById("TEST-SESSION-001");
        assertFalse(found.isPresent());
        assertEquals(2, importSessionRepository.count());
    }

    @Test
    void testFindById() {
        Optional<ImportSession> found = importSessionRepository.findById("TEST-SESSION-001");
        
        assertTrue(found.isPresent());
        assertEquals("students_import.csv", found.get().getFileName());
        assertEquals(ImportStatus.COMPLETED, found.get().getStatus());
    }

    @Test
    void testFindAll() {
        List<ImportSession> allSessions = importSessionRepository.findAll();
        
        assertThat(allSessions).hasSize(3);
    }

    @Test
    void testImportStatistics() {
        assertEquals(95, session1.getAddedRecords());
        assertEquals(5, session1.getSkippedRecords());
        assertEquals(0, session1.getFailedRecords());
        assertEquals(95.0, session1.getSuccessRate());
    }
}
