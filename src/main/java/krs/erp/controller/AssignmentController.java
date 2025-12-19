package krs.erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.Grade;
import krs.erp.repository.GradeRepository;

/**
 * REST Controller for managing assignments
 * This controller provides access to grades as assignments
 * (Assignments result in grades)
 */
@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private GradeRepository gradeRepository;

    /**
     * Get all assignments (grades) with pagination and sorting
     * GET /api/assignments
     */
    @GetMapping
    public ResponseEntity<Page<Grade>> getAllAssignments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Grade> assignments = gradeRepository.findAllActive(pageable);
        
        return ResponseEntity.ok(assignments);
    }

    /**
     * Get assignment by ID
     * GET /api/assignments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Grade> getAssignmentById(@PathVariable Long id) {
        return gradeRepository.findById(id)
                .filter(grade -> grade.getIsActive() == 1)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
