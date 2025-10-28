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
import krs.erp.repository.GradeRepository;

/**
 * REST controller for Grade entity
 */
@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeRepository gradeRepository;

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
     * Get grades by course code
     */
    @GetMapping("/course/{courseCode}")
    public ResponseEntity<List<Grade>> getGradesByCourseCode(@PathVariable String courseCode) {
        List<Grade> grades = gradeRepository.findByCourseCode(courseCode);
        return ResponseEntity.ok(grades);
    }

    /**
     * Get grades by grade level
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
    public ResponseEntity<List<Grade>> getGradesByTeacherId(@PathVariable String teacherId) {
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
    public ResponseEntity<?> createGrade(@Valid @RequestBody Grade grade) {
        // Check if grade already exists
        if (gradeRepository.existsByStudentIdAndCourseCodeAndExamTypeAndSemester(
                grade.getStudentId(),
                grade.getCourseCode(),
                grade.getExamType(),
                grade.getSemester())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Grade already exists for this student, course, exam type and semester");
        }

        grade.markAsActive();
        Grade savedGrade = gradeRepository.save(grade);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGrade);
    }

    /**
     * Update an existing grade
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGrade(@PathVariable Long id, @Valid @RequestBody Grade gradeDetails) {
        return gradeRepository.findById(id)
                .filter(grade -> grade.getIsActive() == 1)
                .map(grade -> {
                    // Update fields
                    grade.setStudentId(gradeDetails.getStudentId());
                    grade.setStudentName(gradeDetails.getStudentName());
                    grade.setGradeLevel(gradeDetails.getGradeLevel());
                    grade.setCourseCode(gradeDetails.getCourseCode());
                    grade.setCourseName(gradeDetails.getCourseName());
                    grade.setExamType(gradeDetails.getExamType());
                    grade.setMarksObtained(gradeDetails.getMarksObtained());
                    grade.setTotalMarks(gradeDetails.getTotalMarks());
                    grade.setExamDate(gradeDetails.getExamDate());
                    grade.setSemester(gradeDetails.getSemester());
                    grade.setAcademicYear(gradeDetails.getAcademicYear());
                    grade.setRemarks(gradeDetails.getRemarks());
                    grade.setTeacherId(gradeDetails.getTeacherId());
                    grade.setTeacherName(gradeDetails.getTeacherName());
                    grade.setOrganizationId(gradeDetails.getOrganizationId());

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
}
