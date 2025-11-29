package krs.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import krs.erp.dto.StudentPromotionRequest;
import krs.erp.dto.StudentPromotionResponse;
import krs.erp.entity.StudentPromotionBatch.PromotionBatchStatus;
import krs.erp.model.Student;
import krs.erp.repository.StudentRepository;
import krs.erp.service.StudentPromotionSchedulerService;
import krs.erp.service.StudentPromotionService;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentPromotionController {

    private static final Logger logger = LoggerFactory.getLogger(StudentPromotionController.class);

    @Autowired
    private StudentPromotionService promotionService;

    @Autowired
    private StudentPromotionSchedulerService schedulerService;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Create and execute promotion batch asynchronously
     * POST /api/promotions/execute
     */
    @PostMapping("/execute")
    public ResponseEntity<?> executePromotion(
            @Valid @RequestBody StudentPromotionRequest request,
            HttpServletRequest httpRequest) {
        try {
            // TODO: Get current user ID from security context
            Long currentUserId = 1L; // Placeholder - replace with actual user from authentication

            // Create batch and execute asynchronously in background thread
            Long batchId = schedulerService.createAndSchedulePromotion(request, currentUserId, httpRequest);

            // Return immediately with batch ID for progress polling
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Promotion batch initiated successfully. Processing in background.");
            response.put("batchId", batchId);
            response.put("status", "IN_PROGRESS");

            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            logger.error("Error executing promotion batch", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to execute promotion: " + e.getMessage()));
        }
    }

    /**
     * Get promotion batch progress
     * GET /api/promotions/batch/{batchId}/progress
     */
    @GetMapping("/batch/{batchId}/progress")
    public ResponseEntity<?> getBatchProgress(@PathVariable Long batchId) {
        try {
            StudentPromotionResponse response = schedulerService.getPromotionProgress(batchId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving batch progress", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve batch progress"));
        }
    }

    /**
     * Get batch details by ID
     * GET /api/promotions/batch/{batchId}
     */
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<?> getBatchDetails(@PathVariable Long batchId) {
        try {
            StudentPromotionResponse response = promotionService.getBatchDetails(batchId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving batch details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve batch details"));
        }
    }

    /**
     * Get all promotion batches with pagination
     * GET /api/promotions/batches?page=0&size=10
     */
    @GetMapping("/batches")
    public ResponseEntity<?> getAllBatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<StudentPromotionResponse> batches = promotionService.getAllBatches(pageable);
            return ResponseEntity.ok(batches);
        } catch (Exception e) {
            logger.error("Error retrieving batches", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve batches"));
        }
    }

    /**
     * Get batches by status
     * GET /api/promotions/batches/status/{status}
     */
    @GetMapping("/batches/status/{status}")
    public ResponseEntity<?> getBatchesByStatus(@PathVariable String status) {
        try {
            PromotionBatchStatus batchStatus = PromotionBatchStatus.valueOf(status.toUpperCase());
            List<StudentPromotionResponse> batches = promotionService.getBatchesByStatus(batchStatus);
            return ResponseEntity.ok(batches);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Invalid status: " + status));
        } catch (Exception e) {
            logger.error("Error retrieving batches by status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve batches"));
        }
    }

    /**
     * Rollback promotion batch
     * POST /api/promotions/batch/{batchId}/rollback
     */
    @PostMapping("/batch/{batchId}/rollback")
    public ResponseEntity<?> rollbackBatch(
            @PathVariable Long batchId,
            HttpServletRequest httpRequest) {
        try {
            // TODO: Get current user ID from security context
            Long currentUserId = 1L; // Placeholder

            promotionService.rollbackPromotionBatch(batchId, currentUserId, httpRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Batch rollback completed successfully");
            response.put("batchId", batchId);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error rolling back batch", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to rollback batch"));
        }
    }

    /**
     * Get student promotion history
     * GET /api/promotions/student/{studentId}/history
     */
    @GetMapping("/student/{studentId}/history")
    public ResponseEntity<?> getStudentHistory(@PathVariable Long studentId) {
        try {
            List<StudentPromotionResponse.PromotionRecordSummary> history = 
                    promotionService.getStudentPromotionHistory(studentId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error retrieving student history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve student history"));
        }
    }

    /**
     * Get students eligible for promotion with filters
     * GET /api/promotions/eligible?gradeLevel=GRADE_1&section=A&search=John
     */
    @GetMapping("/eligible")
    public ResponseEntity<?> getEligibleStudents(
            @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Student> students;

            // Filter by grade, section, and search term
            if (gradeLevel != null && !gradeLevel.isEmpty()) {
                Student.GradeLevel grade = Student.GradeLevel.valueOf(gradeLevel);
                
                if (section != null && !section.isEmpty()) {
                    students = studentRepository.findByGradeLevelAndSectionAndEnrollmentStatus(
                            grade, section, Student.EnrollmentStatus.ACTIVE, pageable);
                } else {
                    students = studentRepository.findByGradeLevelAndEnrollmentStatus(
                            grade, Student.EnrollmentStatus.ACTIVE, pageable);
                }
            } else {
                // Get all active students
                students = studentRepository.findByEnrollmentStatus(
                        Student.EnrollmentStatus.ACTIVE, pageable);
            }

            // Filter by search term (name or admission number) if provided
            List<Student> filteredStudents = students.getContent();
            if (search != null && !search.isEmpty()) {
                String searchLower = search.toLowerCase();
                filteredStudents = filteredStudents.stream()
                        .filter(s -> 
                            s.getFirstName().toLowerCase().contains(searchLower) ||
                            s.getLastName().toLowerCase().contains(searchLower) ||
                            (s.getAdmissionNumber() != null && s.getAdmissionNumber().toLowerCase().contains(searchLower))
                        )
                        .collect(java.util.stream.Collectors.toList());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("students", filteredStudents);
            response.put("totalElements", filteredStudents.size());
            response.put("totalPages", students.getTotalPages());
            response.put("currentPage", students.getNumber());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Invalid grade level: " + gradeLevel));
        } catch (Exception e) {
            logger.error("Error retrieving eligible students", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve eligible students"));
        }
    }

    /**
     * Get promotion statistics
     * GET /api/promotions/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Get counts by grade level
            for (Student.GradeLevel grade : Student.GradeLevel.values()) {
                Long count = studentRepository.countByGradeLevelAndEnrollmentStatus(
                        grade, Student.EnrollmentStatus.ACTIVE);
                stats.put(grade.name(), count);
            }

            // Get total active students
            Long totalActive = studentRepository.countByEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
            stats.put("TOTAL_ACTIVE", totalActive);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error retrieving statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve statistics"));
        }
    }

    /**
     * Get available grade levels
     * GET /api/promotions/grade-levels
     */
    @GetMapping("/grade-levels")
    public ResponseEntity<?> getGradeLevels() {
        try {
            Map<String, String> gradeLevels = new HashMap<>();
            for (Student.GradeLevel grade : Student.GradeLevel.values()) {
                gradeLevels.put(grade.name(), grade.name().replace("_", " "));
            }
            return ResponseEntity.ok(gradeLevels);
        } catch (Exception e) {
            logger.error("Error retrieving grade levels", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve grade levels"));
        }
    }

    /**
     * Create error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
