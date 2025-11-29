package krs.erp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import krs.erp.dto.StudentPromotionRequest;
import krs.erp.dto.StudentPromotionResponse;
import krs.erp.entity.StudentPromotionAuditLog;
import krs.erp.entity.StudentPromotionAuditLog.ActionType;
import krs.erp.entity.StudentPromotionBatch;
import krs.erp.entity.StudentPromotionBatch.PromotionBatchStatus;
import krs.erp.entity.StudentPromotionRecord;
import krs.erp.entity.StudentPromotionRecord.PromotionStatus;
import krs.erp.model.Student;
import krs.erp.model.Student.EnrollmentStatus;
import krs.erp.model.Student.GradeLevel;
import krs.erp.repository.StudentPromotionAuditLogRepository;
import krs.erp.repository.StudentPromotionBatchRepository;
import krs.erp.repository.StudentPromotionRecordRepository;
import krs.erp.repository.StudentRepository;

@Service
public class StudentPromotionService {

    private static final Logger logger = LoggerFactory.getLogger(StudentPromotionService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentPromotionBatchRepository batchRepository;

    @Autowired
    private StudentPromotionRecordRepository recordRepository;

    @Autowired
    private StudentPromotionAuditLogRepository auditLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final List<String> SECTION_OPTIONS = Arrays.asList("A", "B", "C");
    private static final int MAX_STUDENTS_PER_SECTION = 35;

    /**
     * Create batch record and return batch ID immediately
     */
    @Transactional
    public Long createPromotionBatch(
            StudentPromotionRequest request, 
            Long currentUserId,
            HttpServletRequest httpRequest) {
        
        logger.info("Creating promotion batch: {}", request.getBatchName());

        StudentPromotionBatch batch = new StudentPromotionBatch();
        batch.setBatchName(request.getBatchName());
        batch.setAcademicYearFrom(request.getAcademicYearFrom());
        batch.setAcademicYearTo(request.getAcademicYearTo());
        batch.setPromotionDate(request.getPromotionDate());
        batch.setInitiatedBy(currentUserId);
        batch.setTotalStudents(request.getStudents().size());
        batch.setNotes(request.getNotes());
        batch.setStatus(PromotionBatchStatus.PENDING);
        
        batch = batchRepository.save(batch);

        logAudit(ActionType.BATCH_CREATED, batch.getBatchId(), null, currentUserId, null,
                "Batch created with " + request.getStudents().size() + " students", httpRequest);

        return batch.getBatchId();
    }

    /**
     * Create and execute a student promotion batch
     */
    @Transactional
    public StudentPromotionResponse createAndExecutePromotionBatch(
            StudentPromotionRequest request, 
            Long currentUserId,
            HttpServletRequest httpRequest) {
        
        logger.info("Starting promotion batch execution: {}", request.getBatchName());

        // Create batch
        Long batchId = createPromotionBatch(request, currentUserId, httpRequest);
        StudentPromotionBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found after creation"));

        // Process promotions
        batch.setStatus(PromotionBatchStatus.IN_PROGRESS);
        batch.setStartedAt(LocalDateTime.now());
        batch.setCurrentPhase("Initializing");
        batch.setProcessedStudents(0);
        batch.setProgressPercentage(0.0);
        batchRepository.save(batch);
        logAudit(ActionType.BATCH_STARTED, batch.getBatchId(), null, currentUserId, null,
                "Batch processing started", httpRequest);

        List<StudentPromotionRecord> records = new ArrayList<>();
        Map<String, Integer> sectionCounters = new HashMap<>();

        int totalStudents = request.getStudents().size();
        int processedCount = 0;

        for (StudentPromotionRequest.StudentPromotionItem item : request.getStudents()) {
            batch.setCurrentPhase("Processing student " + (processedCount + 1) + " of " + totalStudents);
            batchRepository.save(batch);
            
            StudentPromotionRecord record = processStudentPromotion(
                    batch, item, currentUserId, request.getAutoAssignSections(), 
                    sectionCounters, httpRequest);
            records.add(record);
            
            processedCount++;
            batch.setProcessedStudents(processedCount);
            batch.setProgressPercentage((processedCount * 100.0) / totalStudents);
            batchRepository.save(batch);
        }

        // Update batch statistics
        long successCount = records.stream()
                .filter(r -> r.getPromotionStatus() == PromotionStatus.SUCCESS)
                .count();
        long failCount = records.stream()
                .filter(r -> r.getPromotionStatus() == PromotionStatus.FAILED)
                .count();

        batch.setSuccessfulPromotions((int) successCount);
        batch.setFailedPromotions((int) failCount);
        batch.setStatus(PromotionBatchStatus.COMPLETED);
        batch.setCompletedAt(LocalDateTime.now());
        batch.setCurrentPhase("Completed");
        batch.setProgressPercentage(100.0);
        batchRepository.save(batch);

        logAudit(ActionType.BATCH_COMPLETED, batch.getBatchId(), null, currentUserId, null,
                String.format("Batch completed: %d successful, %d failed", successCount, failCount), httpRequest);

        logger.info("Promotion batch completed: {} - Success: {}, Failed: {}", 
                batch.getBatchId(), successCount, failCount);

        return buildResponse(batch, records);
    }

    /**
     * Process individual student promotion
     */
    private StudentPromotionRecord processStudentPromotion(
            StudentPromotionBatch batch,
            StudentPromotionRequest.StudentPromotionItem item,
            Long currentUserId,
            Boolean autoAssignSections,
            Map<String, Integer> sectionCounters,
            HttpServletRequest httpRequest) {

        StudentPromotionRecord record = new StudentPromotionRecord();
        record.setBatch(batch);
        record.setStudentId(item.getStudentId());
        record.setToGradeLevel(item.getToGradeLevel());
        record.setToSection(item.getToSection());

        try {
            // Fetch student
            Optional<Student> studentOpt = studentRepository.findById(item.getStudentId());
            if (!studentOpt.isPresent()) {
                record.setPromotionStatus(PromotionStatus.FAILED);
                record.setFailureReason("Student not found");
                recordRepository.save(record);
                return record;
            }

            Student student = studentOpt.get();

            // Store snapshot
            String snapshot = createStudentSnapshot(student);
            record.setStudentSnapshot(snapshot);
            record.setFromGradeLevel(student.getGradeLevel().name());
            record.setFromSection(student.getSection());

            // Validate promotion
            String validationError = validatePromotion(student, item.getToGradeLevel());
            if (validationError != null) {
                record.setPromotionStatus(PromotionStatus.FAILED);
                record.setFailureReason(validationError);
                recordRepository.save(record);
                
                logAudit(ActionType.VALIDATION_FAILED, batch.getBatchId(), record.getRecordId(),
                        currentUserId, student.getId(), validationError, httpRequest);
                return record;
            }

            // Auto-assign section if requested
            if (autoAssignSections && item.getToSection() == null) {
                String assignedSection = autoAssignSection(item.getToGradeLevel(), sectionCounters);
                record.setToSection(assignedSection);
            }

            // Perform promotion
            GradeLevel targetGrade = GradeLevel.valueOf(item.getToGradeLevel());
            student.setGradeLevel(targetGrade);
            
            if (record.getToSection() != null) {
                student.setSection(record.getToSection());
            }

            studentRepository.save(student);

            record.setPromotionStatus(PromotionStatus.SUCCESS);
            record.setPromotedAt(LocalDateTime.now());
            recordRepository.save(record);

            logAudit(ActionType.PROMOTION_SUCCESS, batch.getBatchId(), record.getRecordId(),
                    currentUserId, student.getId(),
                    String.format("Promoted from %s to %s", record.getFromGradeLevel(), record.getToGradeLevel()),
                    httpRequest);

            logger.debug("Successfully promoted student {} from {} to {}",
                    student.getId(), record.getFromGradeLevel(), record.getToGradeLevel());

        } catch (Exception e) {
            logger.error("Error promoting student {}: {}", item.getStudentId(), e.getMessage(), e);
            record.setPromotionStatus(PromotionStatus.FAILED);
            record.setFailureReason("System error: " + e.getMessage());
            recordRepository.save(record);

            logAudit(ActionType.PROMOTION_FAILED, batch.getBatchId(), record.getRecordId(),
                    currentUserId, item.getStudentId(), e.getMessage(), httpRequest);
        }

        return record;
    }

    /**
     * Validate if student can be promoted to target grade
     */
    private String validatePromotion(Student student, String targetGrade) {
        // Check if student is active
        if (student.getEnrollmentStatus() != EnrollmentStatus.ACTIVE) {
            return "Student is not active (Status: " + student.getEnrollmentStatus() + ")";
        }

        // Validate grade progression
        GradeLevel currentGrade = student.getGradeLevel();
        GradeLevel targetGradeLevel;
        
        try {
            targetGradeLevel = GradeLevel.valueOf(targetGrade);
        } catch (IllegalArgumentException e) {
            return "Invalid target grade: " + targetGrade;
        }

        // Check if already at target grade
        if (currentGrade == targetGradeLevel) {
            return "Student is already in " + targetGrade;
        }

        // Check for logical progression
        if (!isValidProgression(currentGrade, targetGradeLevel)) {
            return String.format("Invalid grade progression from %s to %s", 
                    currentGrade.name(), targetGradeLevel.name());
        }

        return null; // No validation errors
    }

    /**
     * Check if grade progression is valid
     */
    private boolean isValidProgression(GradeLevel from, GradeLevel to) {
        List<GradeLevel> progression = Arrays.asList(GradeLevel.values());
        int fromIndex = progression.indexOf(from);
        int toIndex = progression.indexOf(to);
        
        // Allow promotion to next grade or skip one grade maximum
        return toIndex > fromIndex && toIndex <= fromIndex + 2;
    }

    /**
     * Auto-assign section based on current distribution
     */
    private String autoAssignSection(String gradeLevel, Map<String, Integer> sectionCounters) {
        String key = gradeLevel;
        
        // Find section with minimum students
        String assignedSection = SECTION_OPTIONS.stream()
                .min((s1, s2) -> {
                    int count1 = sectionCounters.getOrDefault(key + "_" + s1, 0);
                    int count2 = sectionCounters.getOrDefault(key + "_" + s2, 0);
                    return Integer.compare(count1, count2);
                })
                .orElse("A");

        // Increment counter
        String counterKey = key + "_" + assignedSection;
        sectionCounters.put(counterKey, sectionCounters.getOrDefault(counterKey, 0) + 1);

        return assignedSection;
    }

    /**
     * Create student data snapshot for audit
     */
    private String createStudentSnapshot(Student student) {
        try {
            Map<String, Object> snapshot = new HashMap<>();
            snapshot.put("studentId", student.getId());
            snapshot.put("firstName", student.getFirstName());
            snapshot.put("lastName", student.getLastName());
            snapshot.put("gradeLevel", student.getGradeLevel().name());
            snapshot.put("section", student.getSection());
            snapshot.put("enrollmentStatus", student.getEnrollmentStatus().name());
            return objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            logger.error("Error creating student snapshot", e);
            return "{}";
        }
    }

    /**
     * Rollback promotion batch
     */
    @Transactional
    public void rollbackPromotionBatch(Long batchId, Long currentUserId, HttpServletRequest httpRequest) {
        logger.info("Rolling back promotion batch: {}", batchId);

        StudentPromotionBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found: " + batchId));

        if (batch.getStatus() != PromotionBatchStatus.COMPLETED) {
            throw new RuntimeException("Can only rollback completed batches");
        }

        List<StudentPromotionRecord> records = recordRepository.findByBatchIdAndStatus(
                batchId, PromotionStatus.SUCCESS);

        int rollbackCount = 0;
        for (StudentPromotionRecord record : records) {
            try {
                Optional<Student> studentOpt = studentRepository.findById(record.getStudentId());
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    
                    // Restore previous grade
                    GradeLevel previousGrade = GradeLevel.valueOf(record.getFromGradeLevel());
                    student.setGradeLevel(previousGrade);
                    
                    if (record.getFromSection() != null) {
                        student.setSection(record.getFromSection());
                    }
                    
                    studentRepository.save(student);

                    record.setPromotionStatus(PromotionStatus.ROLLED_BACK);
                    record.setRolledBackAt(LocalDateTime.now());
                    recordRepository.save(record);

                    rollbackCount++;
                }
            } catch (Exception e) {
                logger.error("Error rolling back student {}: {}", 
                        record.getStudentId(), e.getMessage(), e);
            }
        }

        batch.setStatus(PromotionBatchStatus.ROLLED_BACK);
        batchRepository.save(batch);

        logAudit(ActionType.BATCH_ROLLED_BACK, batchId, null, currentUserId, null,
                "Batch rolled back: " + rollbackCount + " students restored", httpRequest);

        logger.info("Promotion batch rolled back: {} - {} students restored", batchId, rollbackCount);
    }

    /**
     * Get batch details with records
     */
    @Transactional(readOnly = true)
    public StudentPromotionResponse getBatchDetails(Long batchId) {
        StudentPromotionBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found: " + batchId));

        List<StudentPromotionRecord> records = recordRepository.findByBatchBatchId(batchId);
        
        return buildResponse(batch, records);
    }

    /**
     * Get all batches with pagination
     */
    @Transactional(readOnly = true)
    public Page<StudentPromotionResponse> getAllBatches(Pageable pageable) {
        Page<StudentPromotionBatch> batches = batchRepository.findAllByOrderByCreatedAtDesc(pageable);
        return batches.map(batch -> buildResponse(batch, null));
    }

    /**
     * Get batches by status
     */
    @Transactional(readOnly = true)
    public List<StudentPromotionResponse> getBatchesByStatus(PromotionBatchStatus status) {
        List<StudentPromotionBatch> batches = batchRepository.findByStatus(status);
        return batches.stream()
                .map(batch -> buildResponse(batch, null))
                .collect(Collectors.toList());
    }

    /**
     * Get promotion history for a student
     */
    @Transactional(readOnly = true)
    public List<StudentPromotionResponse.PromotionRecordSummary> getStudentPromotionHistory(Long studentId) {
        List<StudentPromotionRecord> records = recordRepository.findPromotionHistoryByStudentId(studentId);
        
        return records.stream().map(record -> {
            StudentPromotionResponse.PromotionRecordSummary summary = 
                    new StudentPromotionResponse.PromotionRecordSummary();
            summary.setRecordId(record.getRecordId());
            summary.setStudentId(record.getStudentId());
            summary.setFromGradeLevel(record.getFromGradeLevel());
            summary.setToGradeLevel(record.getToGradeLevel());
            summary.setFromSection(record.getFromSection());
            summary.setToSection(record.getToSection());
            summary.setStatus(record.getPromotionStatus().name());
            summary.setFailureReason(record.getFailureReason());
            summary.setPromotedAt(record.getPromotedAt());
            return summary;
        }).collect(Collectors.toList());
    }

    /**
     * Build response DTO from batch and records
     */
    private StudentPromotionResponse buildResponse(StudentPromotionBatch batch, 
                                                    List<StudentPromotionRecord> records) {
        StudentPromotionResponse response = new StudentPromotionResponse();
        response.setBatchId(batch.getBatchId());
        response.setBatchName(batch.getBatchName());
        response.setAcademicYearFrom(batch.getAcademicYearFrom());
        response.setAcademicYearTo(batch.getAcademicYearTo());
        response.setPromotionDate(batch.getPromotionDate());
        response.setStatus(batch.getStatus().name());
        response.setTotalStudents(batch.getTotalStudents());
        response.setSuccessfulPromotions(batch.getSuccessfulPromotions());
        response.setFailedPromotions(batch.getFailedPromotions());
        response.setProcessedStudents(batch.getProcessedStudents());
        response.setProgressPercentage(batch.getProgressPercentage());
        response.setCurrentPhase(batch.getCurrentPhase());
        response.setSuccessRate(batch.getSuccessRate());
        response.setNotes(batch.getNotes());
        response.setInitiatedBy(batch.getInitiatedBy());
        response.setCreatedAt(batch.getCreatedAt());
        response.setCompletedAt(batch.getCompletedAt());
        response.setStartedAt(batch.getStartedAt());

        if (records != null) {
            List<StudentPromotionResponse.PromotionRecordSummary> recordSummaries = records.stream()
                    .map(record -> {
                        StudentPromotionResponse.PromotionRecordSummary summary = 
                                new StudentPromotionResponse.PromotionRecordSummary();
                        summary.setRecordId(record.getRecordId());
                        summary.setStudentId(record.getStudentId());
                        
                        // Fetch student name
                        studentRepository.findById(record.getStudentId()).ifPresent(student -> {
                            summary.setStudentName(student.getFirstName() + " " + student.getLastName());
                        });
                        
                        summary.setFromGradeLevel(record.getFromGradeLevel());
                        summary.setToGradeLevel(record.getToGradeLevel());
                        summary.setFromSection(record.getFromSection());
                        summary.setToSection(record.getToSection());
                        summary.setStatus(record.getPromotionStatus().name());
                        summary.setFailureReason(record.getFailureReason());
                        summary.setPromotedAt(record.getPromotedAt());
                        return summary;
                    })
                    .collect(Collectors.toList());
            response.setRecords(recordSummaries);
        }

        return response;
    }

    /**
     * Log audit trail
     */
    private void logAudit(ActionType actionType, Long batchId, Long recordId, 
                          Long performedBy, Long targetStudentId, String details, 
                          HttpServletRequest httpRequest) {
        try {
            StudentPromotionAuditLog log = new StudentPromotionAuditLog();
            log.setActionType(actionType);
            log.setBatchId(batchId);
            log.setRecordId(recordId);
            log.setPerformedBy(performedBy);
            log.setTargetStudentId(targetStudentId);
            log.setDetails(details);
            
            if (httpRequest != null) {
                log.setIpAddress(httpRequest.getRemoteAddr());
                log.setUserAgent(httpRequest.getHeader("User-Agent"));
            }
            
            auditLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Error logging audit", e);
        }
    }
}
