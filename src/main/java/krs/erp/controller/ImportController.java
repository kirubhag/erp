package krs.erp.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import krs.erp.dto.FieldMappingDTO;
import krs.erp.dto.ImportPreviewDTO;
import krs.erp.dto.ImportSessionDTO;
import krs.erp.dto.ValidationResultDTO;
import krs.erp.entity.DuplicateAction;
import krs.erp.entity.ImportType;
import krs.erp.service.ImportService;

/**
 * REST Controller for import operations
 */
@RestController
@RequestMapping("/api/import")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ImportController {
    
    private final ImportService importService;
    
    public ImportController(ImportService importService) {
        this.importService = importService;
    }
    
    /**
     * Upload file and get preview
     * POST /api/import/upload-preview
     */
    @PostMapping("/upload-preview")
    public ResponseEntity<ImportPreviewDTO> uploadFilePreview(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entityType") String entityType) {
        try {
            ImportPreviewDTO preview = importService.uploadFilePreview(file, entityType);
            return ResponseEntity.ok(preview);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create import session
     * POST /api/import/sessions
     */
    @PostMapping("/sessions")
    public ResponseEntity<ImportSessionDTO> createImportSession(
            @RequestParam("entityType") String entityType,
            @RequestParam("fileName") String fileName,
            @RequestParam("fileFormat") String fileFormat,
            @RequestParam("totalRecords") Integer totalRecords,
            @RequestParam("importType") String importType,
            @RequestParam("duplicateAction") String duplicateAction,
            @RequestParam("findDuplicatesBy") String findDuplicatesBy,
            @RequestParam("enableManualApproval") Boolean enableManualApproval,
            @RequestParam("skipEmptyFields") Boolean skipEmptyFields,
            @RequestParam("headerRow") String[] headerRow) {
        try {
            String sessionId = UUID.randomUUID().toString();
            Long userId = 1L; // TODO: Get from authenticated user
            Long organizationId = 1L; // TODO: Get from authenticated user
            
            ImportSessionDTO session = importService.createSession(
                    sessionId,
                    userId,
                    organizationId,
                    entityType,
                    fileName,
                    fileFormat,
                    totalRecords,
                    headerRow,
                    ImportType.valueOf(importType),
                    DuplicateAction.valueOf(duplicateAction),
                    findDuplicatesBy,
                    enableManualApproval,
                    skipEmptyFields
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(session);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get field mapping template for entity type
     * GET /api/import/mapping-templates/{entityType}
     */
    @GetMapping("/mapping-templates/{entityType}")
    public ResponseEntity<List<FieldMappingDTO>> getFieldMappingTemplate(
            @PathVariable String entityType) {
        try {
            List<FieldMappingDTO> template = importService.getFieldMappingTemplate(entityType);
            return ResponseEntity.ok(template);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Save field mappings
     * PUT /api/import/sessions/{sessionId}/mappings
     */
    @PutMapping("/sessions/{sessionId}/mappings")
    public ResponseEntity<ImportSessionDTO> saveFieldMappings(
            @PathVariable String sessionId,
            @RequestBody List<FieldMappingDTO> mappings) {
        try {
            ImportSessionDTO session = importService.saveFieldMappings(sessionId, mappings);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Validate field mappings
     * POST /api/import/sessions/{sessionId}/validate-mappings
     */
    @PostMapping("/sessions/{sessionId}/validate-mappings")
    public ResponseEntity<ValidationResultDTO> validateMappings(
            @PathVariable String sessionId) {
        try {
            ValidationResultDTO result = importService.validateMappings(sessionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Auto-detect field mappings
     * POST /api/import/sessions/{sessionId}/auto-detect-mappings
     */
    @PostMapping("/sessions/{sessionId}/auto-detect-mappings")
    public ResponseEntity<List<FieldMappingDTO>> autoDetectMappings(
            @PathVariable String sessionId) {
        try {
            List<FieldMappingDTO> mappings = importService.autoDetectMappings(sessionId);
            return ResponseEntity.ok(mappings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Start import
     * POST /api/import/sessions/{sessionId}/import
     */
    @PostMapping("/sessions/{sessionId}/import")
    public ResponseEntity<Void> startImport(
            @PathVariable String sessionId) {
        try {
            importService.startImport(sessionId);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get import summary
     * GET /api/import/sessions/{sessionId}/summary
     */
    @GetMapping("/sessions/{sessionId}/summary")
    public ResponseEntity<ImportSessionDTO> getImportSummary(
            @PathVariable String sessionId) {
        try {
            ImportSessionDTO summary = importService.getImportSummary(sessionId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Get import history
     * GET /api/import/history/{entityType}
     */
    @GetMapping("/history/{entityType}")
    public ResponseEntity<Page<ImportSessionDTO>> getImportHistory(
            @PathVariable String entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long userId = 1L; // TODO: Get from authenticated user
            Pageable pageable = PageRequest.of(page, size);
            Page<ImportSessionDTO> history = importService.getImportHistory(userId, entityType, pageable);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Undo import
     * POST /api/import/sessions/{sessionId}/undo
     */
    @PostMapping("/sessions/{sessionId}/undo")
    public ResponseEntity<Void> undoImport(
            @PathVariable String sessionId) {
        try {
            importService.undoImport(sessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Cancel session
     * POST /api/import/sessions/{sessionId}/cancel
     */
    @PostMapping("/sessions/{sessionId}/cancel")
    public ResponseEntity<Void> cancelSession(
            @PathVariable String sessionId) {
        try {
            importService.cancelSession(sessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
