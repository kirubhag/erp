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
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.ImportHistory;
import krs.erp.model.ImportHistory.ImportStatus;
import krs.erp.model.ImportHistory.ImportType;

/**
 * Unit tests for ImportHistoryRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ImportHistoryRepositoryTest {
    
    @Autowired
    private ImportHistoryRepository importHistoryRepository;
    
    private ImportHistory history1;
    private ImportHistory history2;
    private ImportHistory history3;
    private ImportHistory history4;
    
    @BeforeEach
    void setUp() {
        importHistoryRepository.deleteAllInBatch();
        
        // Create import history 1 - Student import (Success)
        history1 = new ImportHistory();
        history1.setEntityName("Student");
        history1.setImportType(ImportType.MANUAL_IMPORT);
        history1.setImportStatus(ImportStatus.SUCCESS);
        history1.setSource("students_jan.csv");
        history1.setRecordCount(100);
        history1.setImportedBy("admin");
        history1.setCreatedAt(LocalDateTime.now().minusDays(5));
        history1.setIsActive(true);
        history1 = importHistoryRepository.save(history1);
        
        // Create import history 2 - Student import (Failed)
        history2 = new ImportHistory();
        history2.setEntityName("Student");
        history2.setImportType(ImportType.MANUAL_IMPORT);
        history2.setImportStatus(ImportStatus.FAILED);
        history2.setSource("students_feb.xlsx");
        history2.setRecordCount(0);
        history2.setImportedBy("admin");
        history2.setCreatedAt(LocalDateTime.now().minusDays(2));
        history2.setIsActive(true);
        history2 = importHistoryRepository.save(history2);
        
        // Create import history 3 - Teacher import (Success)
        history3 = new ImportHistory();
        history3.setEntityName("Teacher");
        history3.setImportType(ImportType.SAMPLE_DATA);
        history3.setImportStatus(ImportStatus.SUCCESS);
        history3.setSource("teachers.csv");
        history3.setRecordCount(20);
        history3.setImportedBy("system");
        history3.setCreatedAt(LocalDateTime.now().minusDays(1));
        history3.setIsActive(true);
        history3 = importHistoryRepository.save(history3);
        
        // Create import history 4 - Inactive Student import (Success)
        history4 = new ImportHistory();
        history4.setEntityName("Student");
        history4.setImportType(ImportType.MANUAL_IMPORT);
        history4.setImportStatus(ImportStatus.SUCCESS);
        history4.setSource("students_old.csv");
        history4.setRecordCount(50);
        history4.setImportedBy("admin");
        history4.setCreatedAt(LocalDateTime.now().minusDays(10));
        history4.setIsActive(false);
        history4 = importHistoryRepository.save(history4);
    }
    
    @Test
    void testFindByEntityNameOrderByCreatedAtDesc() {
        List<ImportHistory> studentImports = importHistoryRepository.findByEntityNameOrderByCreatedAtDesc("Student");
        
        // Should return 3 students (including inactive one)
        assertThat(studentImports).hasSize(3);
        // Verify descending order (most recent first)
        assertTrue(studentImports.get(0).getCreatedAt().isAfter(studentImports.get(1).getCreatedAt()));
    }
    
    @Test
    void testFindFirstByEntityNameAndImportStatusOrderByCreatedAtDesc() {
        Optional<ImportHistory> lastSuccess = importHistoryRepository
            .findFirstByEntityNameAndImportStatusOrderByCreatedAtDesc("Student", ImportStatus.SUCCESS);
        
        assertTrue(lastSuccess.isPresent());
        assertEquals("students_jan.csv", lastSuccess.get().getSource());
        assertEquals(ImportStatus.SUCCESS, lastSuccess.get().getImportStatus());
    }
    
    @Test
    void testFindFirstByEntityNameAndImportStatusNotFound() {
        Optional<ImportHistory> result = importHistoryRepository
            .findFirstByEntityNameAndImportStatusOrderByCreatedAtDesc("NonExistent", ImportStatus.SUCCESS);
        
        assertFalse(result.isPresent());
    }
    
    @Test
    void testHasEntityBeenImportedTrue() {
        assertTrue(importHistoryRepository.hasEntityBeenImported("Student"));
        assertTrue(importHistoryRepository.hasEntityBeenImported("Teacher"));
    }
    
    @Test
    void testHasEntityBeenImportedFalse() {
        assertFalse(importHistoryRepository.hasEntityBeenImported("NonExistent"));
    }
    
    @Test
    void testFindByImportStatusOrderByCreatedAtDesc() {
        List<ImportHistory> successImports = importHistoryRepository
            .findByImportStatusOrderByCreatedAtDesc(ImportStatus.SUCCESS);
        
        // Should include history1, history3, and history4 (3 successes)
        assertThat(successImports).hasSize(3);
        assertThat(successImports).allMatch(h -> h.getImportStatus() == ImportStatus.SUCCESS);
    }
    
    @Test
    void testFindByImportTypeOrderByCreatedAtDesc() {
        List<ImportHistory> manualImports = importHistoryRepository
            .findByImportTypeOrderByCreatedAtDesc(ImportType.MANUAL_IMPORT);
        
        // Should include history1, history2, and history4
        assertThat(manualImports).hasSize(3);
        assertThat(manualImports).allMatch(h -> h.getImportType() == ImportType.MANUAL_IMPORT);
    }
    
    @Test
    void testFindImportsBetweenDates() {
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now();
        
        List<ImportHistory> recentImports = importHistoryRepository
            .findImportsBetweenDates(start, end);
        
        // Should include history2 and history3 (within date range and active)
        assertThat(recentImports).hasSize(2);
    }
    
    @Test
    void testFindByIsActiveTrueOrderByCreatedAtDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ImportHistory> activePage = importHistoryRepository
            .findByIsActiveTrueOrderByCreatedAtDesc(pageable);
        
        // Should have 3 active imports
        assertEquals(3, activePage.getTotalElements());
        assertThat(activePage.getContent()).allMatch(ImportHistory::getIsActive);
    }
    
    @Test
    void testFindByEntityNameAndIsActiveTrueOrderByCreatedAtDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ImportHistory> studentPage = importHistoryRepository
            .findByEntityNameAndIsActiveTrueOrderByCreatedAtDesc("Student", pageable);
        
        // Should have 2 active student imports (history1 and history2)
        assertEquals(2, studentPage.getTotalElements());
        assertThat(studentPage.getContent()).allMatch(h -> "Student".equals(h.getEntityName()));
    }
    
    @Test
    void testFindByImportStatusAndIsActiveTrueOrderByCreatedAtDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ImportHistory> successPage = importHistoryRepository
            .findByImportStatusAndIsActiveTrueOrderByCreatedAtDesc(ImportStatus.SUCCESS, pageable);
        
        // Should have 2 active successful imports (history1 and history3)
        assertEquals(2, successPage.getTotalElements());
        assertThat(successPage.getContent()).allMatch(h -> h.getImportStatus() == ImportStatus.SUCCESS);
    }
    
    @Test
    void testGetTotalRecordsImportedForEntity() {
        Integer total = importHistoryRepository.getTotalRecordsImportedForEntity("Student");
        
        // Only history1 is active and successful with 100 records
        assertEquals(100, total);
    }
    
    @Test
    void testGetRecentImports() {
        Pageable pageable = PageRequest.of(0, 5);
        List<ImportHistory> recent = importHistoryRepository.getRecentImports(pageable);
        
        // Should return active imports ordered by created date desc
        assertNotNull(recent);
        assertThat(recent).isNotEmpty();
        assertThat(recent).allMatch(ImportHistory::getIsActive);
    }
    
    @Test
    void testCountSuccessfulImportsForEntity() {
        long count = importHistoryRepository.countSuccessfulImportsForEntity("Student");
        
        // Only history1 is active and successful
        assertEquals(1L, count);
    }
    
    @Test
    void testGetImportedEntities() {
        List<String> entities = importHistoryRepository.getImportedEntities();
        
        // Should return Student and Teacher
        assertThat(entities).hasSize(2);
        assertThat(entities).containsExactlyInAnyOrder("Student", "Teacher");
    }
    
    @Test
    void testSaveImportHistory() {
        ImportHistory newHistory = new ImportHistory();
        newHistory.setEntityName("Parent");
        newHistory.setImportType(ImportType.SAMPLE_DATA);
        newHistory.setImportStatus(ImportStatus.PENDING);
        newHistory.setSource("parents.json");
        newHistory.setRecordCount(30);
        newHistory.setImportedBy("admin");
        newHistory.setIsActive(true);
        
        ImportHistory saved = importHistoryRepository.save(newHistory);
        
        assertNotNull(saved.getId());
        assertEquals("Parent", saved.getEntityName());
        assertEquals(ImportType.SAMPLE_DATA, saved.getImportType());
        assertEquals(30, saved.getRecordCount());
    }
    
    @Test
    void testUpdateImportHistory() {
        history2.setImportStatus(ImportStatus.SUCCESS);
        history2.setRecordCount(45);
        history2.setErrorMessage(null);
        
        ImportHistory updated = importHistoryRepository.save(history2);
        
        assertEquals(ImportStatus.SUCCESS, updated.getImportStatus());
        assertEquals(45, updated.getRecordCount());
    }
    
    @Test
    void testDeleteImportHistory() {
        Long historyId = history3.getId();
        importHistoryRepository.delete(history3);
        
        assertFalse(importHistoryRepository.findById(historyId).isPresent());
    }
}
