package krs.erp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.ImportHistory;
import krs.erp.model.ImportHistory.ImportStatus;
import krs.erp.model.ImportHistory.ImportType;
import krs.erp.repository.ImportHistoryRepository;

/**
 * Service for managing import history and tracking data population events
 */
@Service
@Transactional
public class ImportHistoryService {
    
    @Autowired
    private ImportHistoryRepository importHistoryRepository;
    
    /**
     * Create a new import history record
     */
    public ImportHistory createImportHistory(String entityName, ImportType importType, 
                                              String importedBy, String source) {
        ImportHistory history = new ImportHistory(entityName, importType, importedBy, source);
        return importHistoryRepository.save(history);
    }
    
    /**
     * Mark import as completed successfully
     */
    public void markImportAsSuccessful(ImportHistory history, int recordCount) {
        history.markAsCompleted(recordCount);
        importHistoryRepository.save(history);
    }
    
    /**
     * Mark import as failed
     */
    public void markImportAsFailed(ImportHistory history, String errorMessage) {
        history.markAsFailed(errorMessage);
        importHistoryRepository.save(history);
    }
    
    /**
     * Mark import as partial (some records succeeded, some failed)
     */
    public void markImportAsPartial(ImportHistory history, int successfulCount, String errorMessage) {
        history.markAsPartial(successfulCount, errorMessage);
        importHistoryRepository.save(history);
    }
    
    /**
     * Check if an entity has already been imported
     */
    public boolean hasEntityBeenImported(String entityName) {
        return importHistoryRepository.hasEntityBeenImported(entityName);
    }
    
    /**
     * Get all successful imports for an entity
     */
    public List<ImportHistory> getImportsForEntity(String entityName) {
        return importHistoryRepository.findByEntityNameOrderByCreatedAtDesc(entityName);
    }
    
    /**
     * Get the most recent successful import for an entity
     */
    public Optional<ImportHistory> getMostRecentSuccessfulImport(String entityName) {
        return importHistoryRepository.findFirstByEntityNameAndImportStatusOrderByCreatedAtDesc(
                entityName, ImportStatus.SUCCESS);
    }
    
    /**
     * Get all imports by type
     */
    public List<ImportHistory> getImportsByType(ImportType importType) {
        return importHistoryRepository.findByImportTypeOrderByCreatedAtDesc(importType);
    }
    
    /**
     * Get all successful imports
     */
    public List<ImportHistory> getSuccessfulImports() {
        return importHistoryRepository.findByImportStatusOrderByCreatedAtDesc(ImportStatus.SUCCESS);
    }
    
    /**
     * Get imports within a date range
     */
    public List<ImportHistory> getImportsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return importHistoryRepository.findImportsBetweenDates(startDate, endDate);
    }
    
    /**
     * Get paginated import history
     */
    public Page<ImportHistory> getImportHistory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return importHistoryRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
    }
    
    /**
     * Get paginated import history for a specific entity
     */
    public Page<ImportHistory> getImportHistoryForEntity(String entityName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return importHistoryRepository.findByEntityNameAndIsActiveTrueOrderByCreatedAtDesc(entityName, pageable);
    }
    
    /**
     * Get total records imported for an entity
     */
    public Integer getTotalRecordsImportedForEntity(String entityName) {
        return importHistoryRepository.getTotalRecordsImportedForEntity(entityName);
    }
    
    /**
     * Get recent imports
     */
    public List<ImportHistory> getRecentImports(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return importHistoryRepository.getRecentImports(pageable);
    }
    
    /**
     * Get all entities that have been imported
     */
    public List<String> getImportedEntities() {
        return importHistoryRepository.getImportedEntities();
    }
    
    /**
     * Delete import history record (soft delete via isActive flag)
     */
    public void deleteImportHistory(Long id) {
        Optional<ImportHistory> history = importHistoryRepository.findById(id);
        if (history.isPresent()) {
            ImportHistory record = history.get();
            record.setIsActive(false);
            importHistoryRepository.save(record);
        }
    }
    
    /**
     * Get import history by ID
     */
    public Optional<ImportHistory> getImportHistoryById(Long id) {
        return importHistoryRepository.findById(id);
    }
    
    /**
     * Get a summary of all sample data imports
     */
    public List<ImportHistory> getSampleDataImportSummary() {
        return importHistoryRepository.findByImportTypeOrderByCreatedAtDesc(ImportType.SAMPLE_DATA);
    }
    
    /**
     * Check if any sample data has been populated
     */
    public boolean hasSampleDataBeenLoaded() {
        List<ImportHistory> sampleImports = getSampleDataImportSummary();
        return sampleImports.stream()
                .anyMatch(h -> ImportStatus.SUCCESS.equals(h.getImportStatus()));
    }
    
    /**
     * Get sample data population status - check which entities have been imported
     */
    public List<String> getPopulatedSampleDataEntities() {
        return importHistoryRepository.getImportedEntities();
    }
}
