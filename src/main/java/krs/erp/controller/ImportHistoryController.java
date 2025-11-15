package krs.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.ImportHistory;
import krs.erp.repository.ImportHistoryRepository;
import krs.erp.service.ImportHistoryService;

/**
 * REST API for managing import history
 */
@RestController
@RequestMapping("/api/v1/import-history")
@CrossOrigin(origins = "*")
public class ImportHistoryController {
    
    @Autowired
    private ImportHistoryService importHistoryService;
    
    @Autowired
    private ImportHistoryRepository importHistoryRepository;
    
    /**
     * Get all import history with pagination
     */
    @GetMapping
    public ResponseEntity<Page<ImportHistory>> getImportHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ImportHistory> history = importHistoryRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get import history by entity name
     */
    @GetMapping("/entity/{entityName}")
    public ResponseEntity<List<ImportHistory>> getImportHistoryByEntity(@PathVariable String entityName) {
        try {
            List<ImportHistory> history = importHistoryRepository.findByEntityNameOrderByCreatedAtDesc(entityName);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Check if an entity has been imported
     */
    @GetMapping("/check/{entityName}")
    public ResponseEntity<Map<String, Object>> checkEntityImported(@PathVariable String entityName) {
        try {
            boolean imported = importHistoryRepository.hasEntityBeenImported(entityName);
            Optional<ImportHistory> latest = importHistoryRepository
                    .findFirstByEntityNameAndImportStatusOrderByCreatedAtDesc(
                            entityName, 
                            ImportHistory.ImportStatus.SUCCESS);
            
            Map<String, Object> response = new HashMap<>();
            response.put("entityName", entityName);
            response.put("isImported", imported);
            if (latest.isPresent()) {
                response.put("lastImportTime", latest.get().getCreatedAt());
                response.put("recordCount", latest.get().getRecordCount());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get all imported entities
     */
    @GetMapping("/entities")
    public ResponseEntity<List<String>> getImportedEntities() {
        try {
            List<String> entities = importHistoryRepository.getImportedEntities();
            return ResponseEntity.ok(entities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get recent imports (last 10)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ImportHistory>> getRecentImports(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<ImportHistory> recent = importHistoryRepository.getRecentImports(pageable);
            return ResponseEntity.ok(recent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get import history by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ImportHistory> getImportHistoryById(@PathVariable Long id) {
        try {
            Optional<ImportHistory> history = importHistoryRepository.findById(id);
            return history.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get sample data population summary
     */
    @GetMapping("/summary/sample-data")
    public ResponseEntity<Map<String, Object>> getSampleDataSummary() {
        try {
            List<ImportHistory> sampleImports = importHistoryRepository
                    .findByImportTypeOrderByCreatedAtDesc(ImportHistory.ImportType.SAMPLE_DATA);
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalImports", sampleImports.size());
            summary.put("imports", sampleImports);
            summary.put("hasSampleDataBeenLoaded", !sampleImports.isEmpty());
            
            // Count by status
            long successful = sampleImports.stream()
                    .filter(h -> ImportHistory.ImportStatus.SUCCESS.equals(h.getImportStatus()))
                    .count();
            long failed = sampleImports.stream()
                    .filter(h -> ImportHistory.ImportStatus.FAILED.equals(h.getImportStatus()))
                    .count();
            
            summary.put("successfulImports", successful);
            summary.put("failedImports", failed);
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
