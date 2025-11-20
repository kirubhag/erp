package krs.erp.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.entity.DuplicateAction;
import krs.erp.entity.ImportResult;
import krs.erp.entity.ImportResultStatus;
import krs.erp.entity.ImportSession;
import krs.erp.entity.ImportStatus;
import krs.erp.entity.ImportType;

/**
 * Test class for ImportResultRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ImportResultRepositoryTest {

    @Autowired
    private ImportResultRepository importResultRepository;

    @Autowired
    private ImportSessionRepository importSessionRepository;

    private ImportSession testSession;
    private ImportResult result1;
    private ImportResult result2;
    private ImportResult result3;
    private ImportResult result4;

    @BeforeEach
    void setUp() {
        importResultRepository.deleteAllInBatch();
        importSessionRepository.deleteAllInBatch();

        // Create test import session
        testSession = new ImportSession();
        testSession.setId("TEST-SESSION-RESULT-001");
        testSession.setUserId(1L);
        testSession.setOrganizationId(null);
        testSession.setEntityType("students");
        testSession.setFileName("test_students.csv");
        testSession.setFileFormat("csv");
        testSession.setTotalRecords(100);
        testSession.setFileSize(20000L);
        testSession.setImportType(ImportType.ORGANIZATION);
        testSession.setDuplicateAction(DuplicateAction.SKIP);
        testSession.setStatus(ImportStatus.IN_PROGRESS);
        testSession = importSessionRepository.save(testSession);

        // Create test import results
        result1 = new ImportResult();
        result1.setImportSession(testSession);
        result1.setRowNumber(1);
        result1.setRecordId("STU001");
        result1.setStatus(ImportResultStatus.ADDED);
        result1.setData("{\"name\":\"John Doe\",\"email\":\"john@example.com\"}");
        result1 = importResultRepository.save(result1);

        result2 = new ImportResult();
        result2.setImportSession(testSession);
        result2.setRowNumber(2);
        result2.setRecordId("STU002");
        result2.setStatus(ImportResultStatus.UPDATED);
        result2.setData("{\"name\":\"Jane Smith\",\"email\":\"jane@example.com\"}");
        result2 = importResultRepository.save(result2);

        result3 = new ImportResult();
        result3.setImportSession(testSession);
        result3.setRowNumber(3);
        result3.setRecordId(null);
        result3.setStatus(ImportResultStatus.SKIPPED);
        result3.setData("{\"name\":\"Bob Johnson\",\"email\":\"bob@example.com\"}");
        result3.setErrors("{\"reason\":\"Duplicate email address\"}");
        result3 = importResultRepository.save(result3);

        result4 = new ImportResult();
        result4.setImportSession(testSession);
        result4.setRowNumber(4);
        result4.setRecordId(null);
        result4.setStatus(ImportResultStatus.FAILED);
        result4.setData("{\"name\":\"Invalid\",\"email\":\"invalid-email\"}");
        result4.setErrors("{\"email\":\"Invalid email format\"}");
        result4 = importResultRepository.save(result4);
    }

    @Test
    void testSaveImportResult() {
        ImportResult newResult = new ImportResult();
        newResult.setImportSession(testSession);
        newResult.setRowNumber(5);
        newResult.setRecordId("STU005");
        newResult.setStatus(ImportResultStatus.ADDED);
        newResult.setData("{\"name\":\"Test User\"}");
        
        ImportResult saved = importResultRepository.save(newResult);
        
        assertNotNull(saved.getId());
        assertEquals(5, saved.getRowNumber());
        assertEquals(ImportResultStatus.ADDED, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testFindByImportSessionIdOrderByRowNumber() {
        List<ImportResult> results = importResultRepository.findByImportSessionIdOrderByRowNumber("TEST-SESSION-RESULT-001");
        
        assertThat(results).hasSize(4);
        assertEquals(1, results.get(0).getRowNumber());
        assertEquals(2, results.get(1).getRowNumber());
        assertEquals(3, results.get(2).getRowNumber());
        assertEquals(4, results.get(3).getRowNumber());
    }

    @Test
    void testFindByImportSessionIdOrderByRowNumberWithPagination() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<ImportResult> page = importResultRepository.findByImportSessionIdOrderByRowNumber("TEST-SESSION-RESULT-001", pageRequest);
        
        assertThat(page.getContent()).hasSize(2);
        assertEquals(4, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(1, page.getContent().get(0).getRowNumber());
    }

    @Test
    void testFindByImportSessionIdAndStatusOrderByRowNumber() {
        List<ImportResult> addedResults = importResultRepository.findByImportSessionIdAndStatusOrderByRowNumber(
            "TEST-SESSION-RESULT-001", ImportResultStatus.ADDED);
        
        assertThat(addedResults).hasSize(1);
        assertEquals("STU001", addedResults.get(0).getRecordId());
        
        List<ImportResult> failedResults = importResultRepository.findByImportSessionIdAndStatusOrderByRowNumber(
            "TEST-SESSION-RESULT-001", ImportResultStatus.FAILED);
        
        assertThat(failedResults).hasSize(1);
        assertEquals(4, failedResults.get(0).getRowNumber());
    }

    @Test
    void testCountByImportSessionIdAndStatus() {
        Long addedCount = importResultRepository.countByImportSessionIdAndStatus("TEST-SESSION-RESULT-001", ImportResultStatus.ADDED);
        Long updatedCount = importResultRepository.countByImportSessionIdAndStatus("TEST-SESSION-RESULT-001", ImportResultStatus.UPDATED);
        Long skippedCount = importResultRepository.countByImportSessionIdAndStatus("TEST-SESSION-RESULT-001", ImportResultStatus.SKIPPED);
        Long failedCount = importResultRepository.countByImportSessionIdAndStatus("TEST-SESSION-RESULT-001", ImportResultStatus.FAILED);
        
        assertEquals(1L, addedCount);
        assertEquals(1L, updatedCount);
        assertEquals(1L, skippedCount);
        assertEquals(1L, failedCount);
    }

    @Test
    void testFindFailedResults() {
        List<ImportResult> failedResults = importResultRepository.findFailedResults("TEST-SESSION-RESULT-001");
        
        assertThat(failedResults).hasSize(1);
        assertEquals(ImportResultStatus.FAILED, failedResults.get(0).getStatus());
        assertEquals(4, failedResults.get(0).getRowNumber());
        assertNotNull(failedResults.get(0).getErrors());
    }

    @Test
    void testUpdateImportResult() {
        result3.setStatus(ImportResultStatus.ADDED);
        result3.setRecordId("STU003");
        result3.setErrors(null);
        
        ImportResult updated = importResultRepository.save(result3);
        
        assertEquals(ImportResultStatus.ADDED, updated.getStatus());
        assertEquals("STU003", updated.getRecordId());
        assertNull(updated.getErrors());
    }

    @Test
    void testDeleteImportResult() {
        importResultRepository.delete(result1);
        
        List<ImportResult> results = importResultRepository.findByImportSessionIdOrderByRowNumber("TEST-SESSION-RESULT-001");
        assertThat(results).hasSize(3);
    }

    @Test
    void testFindAll() {
        List<ImportResult> allResults = importResultRepository.findAll();
        
        assertThat(allResults).hasSize(4);
    }

    @Test
    void testResultStatuses() {
        List<ImportResult> results = importResultRepository.findByImportSessionIdOrderByRowNumber("TEST-SESSION-RESULT-001");
        
        assertEquals(ImportResultStatus.ADDED, results.get(0).getStatus());
        assertEquals(ImportResultStatus.UPDATED, results.get(1).getStatus());
        assertEquals(ImportResultStatus.SKIPPED, results.get(2).getStatus());
        assertEquals(ImportResultStatus.FAILED, results.get(3).getStatus());
    }

    @Test
    void testResultData() {
        assertNotNull(result1.getData());
        assertNotNull(result2.getData());
        assertNull(result1.getErrors());
        assertNotNull(result4.getErrors());
    }
}
