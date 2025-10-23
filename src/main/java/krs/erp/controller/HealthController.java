package krs.erp.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import krs.erp.enums.EntityType;
import krs.erp.model.HealthRecord;
import krs.erp.repository.HealthRecordRepository;
import krs.erp.service.RecycleBinService;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthController {
    
    @Autowired
    private HealthRecordRepository healthRecordRepository;
    
    @Autowired
    private RecycleBinService recycleBinService;
    
    // Get all health records with pagination
    @GetMapping
    public ResponseEntity<Page<HealthRecord>> getAllHealthRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String recordType) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<HealthRecord> healthRecords;
        
        if (recordType != null) {
            HealthRecord.RecordType type = HealthRecord.RecordType.valueOf(recordType.toUpperCase());
            // For simplicity, we'll get all records and convert to Page manually
            List<HealthRecord> allRecords = healthRecordRepository.findByRecordType(type);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allRecords.size());
            List<HealthRecord> pageContent = allRecords.subList(start, end);
            healthRecords = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allRecords.size());
        } else {
            healthRecords = healthRecordRepository.findAll(pageable);
        }
        
        return ResponseEntity.ok(healthRecords);
    }
    
    // Get health record by ID
    @GetMapping("/{id}")
    public ResponseEntity<HealthRecord> getHealthRecordById(@PathVariable Long id) {
        Optional<HealthRecord> healthRecord = healthRecordRepository.findById(id);
        return healthRecord.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new health record
    @PostMapping
    public ResponseEntity<HealthRecord> createHealthRecord(@Valid @RequestBody HealthRecord healthRecord) {
        try {
            HealthRecord savedRecord = healthRecordRepository.save(healthRecord);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRecord);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // Update health record
    @PutMapping("/{id}")
    public ResponseEntity<HealthRecord> updateHealthRecord(@PathVariable Long id, 
                                                          @Valid @RequestBody HealthRecord healthRecordDetails) {
        return healthRecordRepository.findById(id)
                .map(record -> {
                    record.setTitle(healthRecordDetails.getTitle());
                    record.setDescription(healthRecordDetails.getDescription());
                    record.setRecordType(healthRecordDetails.getRecordType());
                    record.setRecordDate(healthRecordDetails.getRecordDate());
                    record.setSeverity(healthRecordDetails.getSeverity());
                    record.setRequiresAttention(healthRecordDetails.getRequiresAttention());
                    record.setProvider(healthRecordDetails.getProvider());
                    record.setProviderContact(healthRecordDetails.getProviderContact());
                    record.setMedication(healthRecordDetails.getMedication());
                    record.setDosage(healthRecordDetails.getDosage());
                    record.setFrequency(healthRecordDetails.getFrequency());
                    record.setSpecialInstructions(healthRecordDetails.getSpecialInstructions());
                    record.setExpiryDate(healthRecordDetails.getExpiryDate());
                    record.setActive(healthRecordDetails.getActive());
                    
                    return ResponseEntity.ok(healthRecordRepository.save(record));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Delete health record (soft delete with recycle bin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHealthRecord(@PathVariable Long id) {
        Optional<HealthRecord> recordOpt = healthRecordRepository.findById(id);
        if (recordOpt.isPresent()) {
            // Soft delete using recycle bin service
            recycleBinService.softDeleteEntity(id, EntityType.HEALTH, "current-user", "User deleted health record");
            
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Get health records by student ID (only active, isActive = 1)
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<HealthRecord>> getHealthRecordsByStudentId(@PathVariable Long studentId) {
        List<HealthRecord> records = healthRecordRepository.findByStudentIdAndIsActive(studentId, 1);
        return ResponseEntity.ok(records);
    }
    
    // Get active health records by student ID (isActive = 1)
    @GetMapping("/student/{studentId}/active")
    public ResponseEntity<List<HealthRecord>> getActiveHealthRecordsByStudentId(@PathVariable Long studentId) {
        List<HealthRecord> records = healthRecordRepository.findByStudentIdAndIsActive(studentId, 1);
        return ResponseEntity.ok(records);
    }
    
    // Get health records by type and student
    @GetMapping("/student/{studentId}/type/{recordType}")
    public ResponseEntity<List<HealthRecord>> getHealthRecordsByStudentAndType(
            @PathVariable Long studentId,
            @PathVariable String recordType) {
        try {
            HealthRecord.RecordType type = HealthRecord.RecordType.valueOf(recordType.toUpperCase());
            List<HealthRecord> records = healthRecordRepository.findByStudentIdAndRecordType(studentId, type);
            return ResponseEntity.ok(records);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get health records requiring attention
    @GetMapping("/attention")
    public ResponseEntity<List<HealthRecord>> getRecordsRequiringAttention() {
        List<HealthRecord> records = healthRecordRepository.findByRequiresAttentionTrueAndActiveTrue();
        return ResponseEntity.ok(records);
    }
    
    // Get expiring records
    @GetMapping("/expiring")
    public ResponseEntity<List<HealthRecord>> getExpiringRecords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beforeDate) {
        List<HealthRecord> records = healthRecordRepository.findByExpiryDateBeforeAndActiveTrue(beforeDate);
        return ResponseEntity.ok(records);
    }
}