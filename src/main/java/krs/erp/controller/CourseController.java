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
import krs.erp.model.Course;
import krs.erp.repository.CourseRepository;

/**
 * REST Controller for managing courses
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    /**
     * Get all courses with pagination and sorting
     * GET /api/courses
     */
    @GetMapping
    public ResponseEntity<Page<Course>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Course> courses = courseRepository.findAll(pageable);
        
        return ResponseEntity.ok(courses);
    }

    /**
     * Get course by ID
     * GET /api/courses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Optional<Course> course = courseRepository.findById(id);
        return course.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get course by code
     * GET /api/courses/code/{courseCode}
     */
    @GetMapping("/code/{courseCode}")
    public ResponseEntity<Course> getCourseByCourseCode(@PathVariable String courseCode) {
        Optional<Course> course = courseRepository.findByCourseCode(courseCode);
        return course.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new course
     * POST /api/courses
     */
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        // Check if course code already exists
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Course savedCourse = courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    /**
     * Update an existing course
     * PUT /api/courses/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @Valid @RequestBody Course course) {
        if (!courseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        course.setId(id);
        Course updatedCourse = courseRepository.save(course);
        return ResponseEntity.ok(updatedCourse);
    }

    /**
     * Delete a course
     * DELETE /api/courses/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        if (!courseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        courseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
