package krs.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.enums.EntityType;
import krs.erp.model.ErpAutoNumber;
import krs.erp.model.ErpField;
import krs.erp.repository.ErpAutoNumberRepository;
import krs.erp.repository.ErpFieldRepository;
import krs.erp.service.AutoNumberService;

/**
 * REST Controller for managing auto-number configurations.
 * Provides endpoints to view, create, and update auto-number sequences.
 */
@RestController
@RequestMapping("/api/auto-numbers")
@CrossOrigin(origins = "*")
public class AutoNumberController {

    @Autowired
    private AutoNumberService autoNumberService;

    @Autowired
    private ErpAutoNumberRepository autoNumberRepository;

    @Autowired
    private ErpFieldRepository erpFieldRepository;

    /**
     * Get all auto-number configurations
     * GET /api/auto-numbers
     */
    @GetMapping
    public ResponseEntity<List<ErpAutoNumber>> getAllAutoNumbers() {
        List<ErpAutoNumber> autoNumbers = autoNumberRepository.findAll();
        return ResponseEntity.ok(autoNumbers);
    }

    /**
     * Get auto-number configurations for a specific entity type
     * GET /api/auto-numbers/entity/{entityType}
     */
    @GetMapping("/entity/{entityType}")
    public ResponseEntity<?> getAutoNumbersByEntityType(@PathVariable String entityType) {
        try {
            EntityType entityTypeEnum = EntityType.valueOf(entityType.toUpperCase());
            List<ErpAutoNumber> autoNumbers = autoNumberService.getAutoNumbersForEntity(entityTypeEnum);
            return ResponseEntity.ok(autoNumbers);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid entity type: " + entityType);
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get a specific auto-number configuration
     * GET /api/auto-numbers/{entityType}/{fieldName}
     */
    @GetMapping("/{entityType}/{fieldName}")
    public ResponseEntity<?> getAutoNumber(
            @PathVariable String entityType,
            @PathVariable String fieldName) {
        try {
            EntityType entityTypeEnum = EntityType.valueOf(entityType.toUpperCase());
            return autoNumberService.getAutoNumberConfig(entityTypeEnum, fieldName)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid entity type: " + entityType);
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Preview the next auto-number value (without generating)
     * GET /api/auto-numbers/{entityType}/{fieldName}/preview
     */
    @GetMapping("/{entityType}/{fieldName}/preview")
    public ResponseEntity<?> previewNextNumber(
            @PathVariable String entityType,
            @PathVariable String fieldName) {
        try {
            EntityType entityTypeEnum = EntityType.valueOf(entityType.toUpperCase());
            String preview = autoNumberService.previewNextNumber(entityTypeEnum, fieldName);
            
            if (preview == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, String> response = new HashMap<>();
            response.put("entityType", entityType);
            response.put("fieldName", fieldName);
            response.put("nextNumber", preview);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid entity type: " + entityType);
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Generate the next auto-number value
     * POST /api/auto-numbers/{entityType}/{fieldName}/generate
     */
    @PostMapping("/{entityType}/{fieldName}/generate")
    public ResponseEntity<?> generateNextNumber(
            @PathVariable String entityType,
            @PathVariable String fieldName) {
        try {
            EntityType entityTypeEnum = EntityType.valueOf(entityType.toUpperCase());
            String generated = autoNumberService.generateNextNumber(entityTypeEnum, fieldName);
            
            if (generated == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No auto-number configuration found for " + entityType + "." + fieldName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Map<String, String> response = new HashMap<>();
            response.put("entityType", entityType);
            response.put("fieldName", fieldName);
            response.put("generatedNumber", generated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid entity type: " + entityType);
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to generate auto-number: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Create a new auto-number configuration
     * POST /api/auto-numbers
     */
    @PostMapping
    public ResponseEntity<?> createAutoNumber(@RequestBody AutoNumberCreateRequest request) {
        try {
            EntityType entityTypeEnum = EntityType.valueOf(request.getEntityType().toUpperCase());
            
            ErpAutoNumber created = autoNumberService.createAutoNumberConfig(
                    entityTypeEnum,
                    request.getFieldName(),
                    request.getPrefix(),
                    request.getSuffix(),
                    request.getStartNumber(),
                    request.getPaddingLength());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update an existing auto-number configuration
     * PUT /api/auto-numbers/{entityType}/{fieldName}
     */
    @PutMapping("/{entityType}/{fieldName}")
    public ResponseEntity<?> updateAutoNumber(
            @PathVariable String entityType,
            @PathVariable String fieldName,
            @RequestBody AutoNumberUpdateRequest request) {
        try {
            EntityType entityTypeEnum = EntityType.valueOf(entityType.toUpperCase());
            
            return autoNumberService.updateAutoNumberConfig(
                    entityTypeEnum,
                    fieldName,
                    request.getPrefix(),
                    request.getSuffix())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid entity type: " + entityType);
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Reset the next number for an auto-number sequence
     * PUT /api/auto-numbers/{entityType}/{fieldName}/reset
     */
    @PutMapping("/{entityType}/{fieldName}/reset")
    public ResponseEntity<?> resetNextNumber(
            @PathVariable String entityType,
            @PathVariable String fieldName,
            @RequestBody Map<String, Long> request) {
        try {
            EntityType entityTypeEnum = EntityType.valueOf(entityType.toUpperCase());
            Long newNumber = request.get("nextNumber");
            
            if (newNumber == null || newNumber < 1) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "nextNumber must be a positive integer");
                return ResponseEntity.badRequest().body(error);
            }
            
            return autoNumberService.resetNextNumber(entityTypeEnum, fieldName, newNumber)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid entity type: " + entityType);
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Create auto-number by ErpField ID
     * POST /api/auto-numbers/by-field/{erpFieldId}
     */
    @PostMapping("/by-field/{erpFieldId}")
    public ResponseEntity<?> createAutoNumberByField(
            @PathVariable Long erpFieldId,
            @RequestBody AutoNumberCreateByFieldRequest request) {
        try {
            ErpField erpField = erpFieldRepository.findById(erpFieldId)
                    .orElse(null);
            
            if (erpField == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "ErpField not found with ID: " + erpFieldId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            ErpAutoNumber created = autoNumberService.createAutoNumberConfig(
                    erpField,
                    request.getPrefix(),
                    request.getSuffix(),
                    request.getStartNumber(),
                    request.getPaddingLength());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get auto-number by ErpField ID
     * GET /api/auto-numbers/by-field/{erpFieldId}
     */
    @GetMapping("/by-field/{erpFieldId}")
    public ResponseEntity<?> getAutoNumberByField(@PathVariable Long erpFieldId) {
        return autoNumberRepository.findByErpFieldId(erpFieldId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DTO classes for request bodies
    public static class AutoNumberCreateRequest {
        private String entityType;
        private String fieldName;
        private String prefix;
        private String suffix;
        private Long startNumber;
        private Integer paddingLength;

        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        public String getPrefix() { return prefix; }
        public void setPrefix(String prefix) { this.prefix = prefix; }
        public String getSuffix() { return suffix; }
        public void setSuffix(String suffix) { this.suffix = suffix; }
        public Long getStartNumber() { return startNumber; }
        public void setStartNumber(Long startNumber) { this.startNumber = startNumber; }
        public Integer getPaddingLength() { return paddingLength; }
        public void setPaddingLength(Integer paddingLength) { this.paddingLength = paddingLength; }
    }

    public static class AutoNumberCreateByFieldRequest {
        private String prefix;
        private String suffix;
        private Long startNumber;
        private Integer paddingLength;

        public String getPrefix() { return prefix; }
        public void setPrefix(String prefix) { this.prefix = prefix; }
        public String getSuffix() { return suffix; }
        public void setSuffix(String suffix) { this.suffix = suffix; }
        public Long getStartNumber() { return startNumber; }
        public void setStartNumber(Long startNumber) { this.startNumber = startNumber; }
        public Integer getPaddingLength() { return paddingLength; }
        public void setPaddingLength(Integer paddingLength) { this.paddingLength = paddingLength; }
    }

    public static class AutoNumberUpdateRequest {
        private String prefix;
        private String suffix;

        public String getPrefix() { return prefix; }
        public void setPrefix(String prefix) { this.prefix = prefix; }
        public String getSuffix() { return suffix; }
        public void setSuffix(String suffix) { this.suffix = suffix; }
    }
}
