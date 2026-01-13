package krs.erp.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import krs.erp.model.Grade;
import krs.erp.model.Student;
import krs.erp.model.Subject;
import krs.erp.repository.GradeRepository;
import krs.erp.repository.StaffRepository;
import krs.erp.repository.StudentRepository;
import krs.erp.repository.SubjectRepository;

/**
 * REST controller for Grade entity
 * Updated to use entity relationships instead of denormalized fields
 */
@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StaffRepository staffRepository;

    /**
     * Get all grades with pagination
     */
    @GetMapping
    public ResponseEntity<Page<Grade>> getAllGrades(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Grade> grades = gradeRepository.findAllActive(pageable);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grade by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Grade> getGradeById(@PathVariable Long id) {
        return gradeRepository.findById(id)
                .filter(grade -> grade.getIsActive() == 1)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all active grades
     */
    @GetMapping("/active")
    public ResponseEntity<Page<Grade>> getActiveGrades(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Grade> grades = gradeRepository.findAllActive(pageable);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by student ID
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Grade>> getGradesByStudentId(@PathVariable Long studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by student ID and academic year
     */
    @GetMapping("/student/{studentId}/year/{academicYear}")
    public ResponseEntity<List<Grade>> getGradesByStudentIdAndAcademicYear(
            @PathVariable Long studentId,
            @PathVariable String academicYear) {
        List<Grade> grades = gradeRepository.findByStudentIdAndAcademicYear(studentId, academicYear);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by student ID and semester
     */
    @GetMapping("/student/{studentId}/semester/{semester}")
    public ResponseEntity<List<Grade>> getGradesByStudentIdAndSemester(
            @PathVariable Long studentId,
            @PathVariable String semester) {
        List<Grade> grades = gradeRepository.findByStudentIdAndSemester(studentId, semester);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by subject code
     */
    @GetMapping("/subject/{subjectCode}")
    public ResponseEntity<List<Grade>> getGradesBySubjectCode(@PathVariable String subjectCode) {
        List<Grade> grades = gradeRepository.findBySubjectCode(subjectCode);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by subject ID
     */
    @GetMapping("/subject/id/{subjectId}")
    public ResponseEntity<List<Grade>> getGradesBySubjectId(@PathVariable Long subjectId) {
        List<Grade> grades = gradeRepository.findBySubjectId(subjectId);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by grade level (from student)
     */
    @GetMapping("/grade-level/{gradeLevel}")
    public ResponseEntity<List<Grade>> getGradesByGradeLevel(@PathVariable String gradeLevel) {
        List<Grade> grades = gradeRepository.findByGradeLevel(gradeLevel);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by grade level and academic year
     */
    @GetMapping("/grade-level/{gradeLevel}/year/{academicYear}")
    public ResponseEntity<List<Grade>> getGradesByGradeLevelAndAcademicYear(
            @PathVariable String gradeLevel,
            @PathVariable String academicYear) {
        List<Grade> grades = gradeRepository.findByGradeLevelAndAcademicYear(gradeLevel, academicYear);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by exam type
     */
    @GetMapping("/exam-type/{examType}")
    public ResponseEntity<List<Grade>> getGradesByExamType(@PathVariable String examType) {
        List<Grade> grades = gradeRepository.findByExamType(examType);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by academic year
     */
    @GetMapping("/year/{academicYear}")
    public ResponseEntity<List<Grade>> getGradesByAcademicYear(@PathVariable String academicYear) {
        List<Grade> grades = gradeRepository.findByAcademicYear(academicYear);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by semester
     */
    @GetMapping("/semester/{semester}")
    public ResponseEntity<List<Grade>> getGradesBySemester(@PathVariable String semester) {
        List<Grade> grades = gradeRepository.findBySemester(semester);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by exam date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Grade>> getGradesByExamDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Grade> grades = gradeRepository.findByExamDateBetween(startDate, endDate);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by teacher ID
     */
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Grade>> getGradesByTeacherId(@PathVariable Long teacherId) {
        List<Grade> grades = gradeRepository.findByTeacherId(teacherId);
        return ResponseEntity.ok(grades);
    }

    /**
     * Search grades
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Grade>> searchGrades(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Grade> grades = gradeRepository.searchGrades(q, pageable);
        return ResponseEntity.ok(grades);
    }

    /**
     * Create a new grade
     */
    @PostMapping
    public ResponseEntity<?> createGrade(@Valid @RequestBody GradeRequest request) {
        // Fetch required entities
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + request.getStudentId()));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + request.getSubjectId()));

        // Check if grade already exists
        if (gradeRepository.existsByStudentAndSubjectAndExamTypeAndSemester(
                student, subject, request.getExamType(), request.getSemester())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Grade already exists for this student, subject, exam type and semester");
        }

        // Create grade entity
        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setSubject(subject);

        // Set optional teacher
        if (request.getTeacherId() != null) {
            staffRepository.findById(request.getTeacherId())
                    .ifPresent(grade::setTeacher);
        }

        // Set grade fields
        grade.setExamType(request.getExamType());
        grade.setMarksObtained(request.getMarksObtained());
        grade.setTotalMarks(request.getTotalMarks());
        grade.setExamDate(request.getExamDate());
        grade.setSemester(request.getSemester());
        grade.setAcademicYear(request.getAcademicYear());
        grade.setRemarks(request.getRemarks());
        grade.setOrganizationId(request.getOrganizationId());

        grade.markAsActive();
        Grade savedGrade = gradeRepository.save(grade);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGrade);
    }

    /**
     * Update an existing grade
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGrade(@PathVariable Long id, @Valid @RequestBody GradeRequest request) {
        return gradeRepository.findById(id)
                .filter(grade -> grade.getIsActive() == 1)
                .map(grade -> {
                    // Update student if changed
                    if (!grade.getStudentId().equals(request.getStudentId())) {
                        Student student = studentRepository.findById(request.getStudentId())
                                .orElseThrow(() -> new RuntimeException("Student not found"));
                        grade.setStudent(student);
                    }

                    // Update subject if changed
                    if (!grade.getSubjectId().equals(request.getSubjectId())) {
                        Subject subject = subjectRepository.findById(request.getSubjectId())
                                .orElseThrow(() -> new RuntimeException("Subject not found"));
                        grade.setSubject(subject);
                    }

                    // Update teacher if changed
                    if (request.getTeacherId() != null) {
                        if (grade.getTeacherId() == null || !grade.getTeacherId().equals(request.getTeacherId())) {
                            staffRepository.findById(request.getTeacherId())
                                    .ifPresent(grade::setTeacher);
                        }
                    } else {
                        grade.setTeacher(null);
                    }

                    // Update other fields
                    grade.setExamType(request.getExamType());
                    grade.setMarksObtained(request.getMarksObtained());
                    grade.setTotalMarks(request.getTotalMarks());
                    grade.setExamDate(request.getExamDate());
                    grade.setSemester(request.getSemester());
                    grade.setAcademicYear(request.getAcademicYear());
                    grade.setRemarks(request.getRemarks());
                    grade.setOrganizationId(request.getOrganizationId());

                    Grade updatedGrade = gradeRepository.save(grade);
                    return ResponseEntity.ok(updatedGrade);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a grade (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGrade(@PathVariable Long id) {
        return gradeRepository.findById(id)
                .filter(grade -> grade.getIsActive() == 1)
                .map(grade -> {
                    grade.markAsDeleted();
                    gradeRepository.save(grade);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get count of grades by student ID and academic year
     */
    @GetMapping("/count/student/{studentId}/year/{academicYear}")
    public ResponseEntity<Long> countGradesByStudentIdAndAcademicYear(
            @PathVariable Long studentId,
            @PathVariable String academicYear) {
        long count = gradeRepository.countByStudentIdAndAcademicYear(studentId, academicYear);
        return ResponseEntity.ok(count);
    }

    /**
     * DTO for Grade requests
     */
    public static class GradeRequest {
        private Long studentId;
        private Long subjectId;
        private Long teacherId;
        private String examType;
        private java.math.BigDecimal marksObtained;
        private java.math.BigDecimal totalMarks;
        private LocalDate examDate;
        private String semester;
        private String academicYear;
        private String remarks;
        private Long organizationId;

        // Getters and setters
        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public Long getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
        }

        public Long getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(Long teacherId) {
            this.teacherId = teacherId;
        }

        public String getExamType() {
            return examType;
        }

        public void setExamType(String examType) {
            this.examType = examType;
        }

        public java.math.BigDecimal getMarksObtained() {
            return marksObtained;
        }

        public void setMarksObtained(java.math.BigDecimal marksObtained) {
            this.marksObtained = marksObtained;
        }

        public java.math.BigDecimal getTotalMarks() {
            return totalMarks;
        }

        public void setTotalMarks(java.math.BigDecimal totalMarks) {
            this.totalMarks = totalMarks;
        }

        public LocalDate getExamDate() {
            return examDate;
        }

        public void setExamDate(LocalDate examDate) {
            this.examDate = examDate;
        }

        public String getSemester() {
            return semester;
        }

        public void setSemester(String semester) {
            this.semester = semester;
        }

        public String getAcademicYear() {
            return academicYear;
        }

        public void setAcademicYear(String academicYear) {
            this.academicYear = academicYear;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public Long getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(Long organizationId) {
            this.organizationId = organizationId;
        }
    }
}
