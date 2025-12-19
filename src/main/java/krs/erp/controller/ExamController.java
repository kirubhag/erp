package krs.erp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import krs.erp.model.Exam;
import krs.erp.repository.ExamRepository;

/**
 * REST Controller for managing exams
 */
@RestController
@RequestMapping("/api/exams")
public class ExamController {

    @Autowired
    private ExamRepository examRepository;

    /**
     * Get all exams with pagination and sorting
     * GET /api/exams
     */
    @GetMapping
    public ResponseEntity<Page<Exam>> getAllExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Exam> exams = examRepository.findAll(pageable);
        
        return ResponseEntity.ok(exams);
    }

    /**
     * Get exam by ID
     * GET /api/exams/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long id) {
        Optional<Exam> exam = examRepository.findById(id);
        return exam.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get exam by name
     * GET /api/exams/name/{examName}
     */
    @GetMapping("/name/{examName}")
    public ResponseEntity<Exam> getExamByName(@PathVariable String examName) {
        Optional<Exam> exam = examRepository.findByExamName(examName);
        return exam.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new exam
     * POST /api/exams
     */
    @PostMapping
    public ResponseEntity<Exam> createExam(@Valid @RequestBody Exam exam) {
        // Check if exam name already exists
        Optional<Exam> existing = examRepository.findByExamName(exam.getExamName());
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Exam savedExam = examRepository.save(exam);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExam);
    }

    /**
     * Update an existing exam
     * PUT /api/exams/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @Valid @RequestBody Exam exam) {
        if (!examRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        exam.setId(id);
        Exam updatedExam = examRepository.save(exam);
        return ResponseEntity.ok(updatedExam);
    }

    /**
     * Delete an exam
     * DELETE /api/exams/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        if (!examRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        examRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
