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
import krs.erp.model.Student;
import krs.erp.repository.StudentRepository;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {
    
    @Autowired
    private StudentRepository studentRepository;
    
    // Get all students with pagination
    @GetMapping
    public ResponseEntity<Page<Student>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> students;
        
        if (status != null) {
            Student.EnrollmentStatus enrollmentStatus = Student.EnrollmentStatus.valueOf(status.toUpperCase());
            students = studentRepository.findByEnrollmentStatus(enrollmentStatus, pageable);
        } else {
            students = studentRepository.findAll(pageable);
        }
        
        return ResponseEntity.ok(students);
    }
    
    // Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Optional<Student> student = studentRepository.findById(id);
        return student.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // Get student by student ID
    @GetMapping("/studentId/{studentId}")
    public ResponseEntity<Student> getStudentByStudentId(@PathVariable String studentId) {
        Optional<Student> student = studentRepository.findByStudentId(studentId);
        return student.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new student
    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        try {
            Student savedStudent = studentRepository.save(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // Update student
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @Valid @RequestBody Student studentDetails) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            student.setFirstName(studentDetails.getFirstName());
            student.setLastName(studentDetails.getLastName());
            student.setMiddleName(studentDetails.getMiddleName());
            student.setEmail(studentDetails.getEmail());
            student.setPhone(studentDetails.getPhone());
            student.setDateOfBirth(studentDetails.getDateOfBirth());
            student.setGender(studentDetails.getGender());
            student.setGradeLevel(studentDetails.getGradeLevel());
            student.setEnrollmentStatus(studentDetails.getEnrollmentStatus());
            student.setAddressLine1(studentDetails.getAddressLine1());
            student.setAddressLine2(studentDetails.getAddressLine2());
            student.setCity(studentDetails.getCity());
            student.setState(studentDetails.getState());
            student.setPostalCode(studentDetails.getPostalCode());
            student.setCountry(studentDetails.getCountry());
            student.setEmergencyContactName(studentDetails.getEmergencyContactName());
            student.setEmergencyContactPhone(studentDetails.getEmergencyContactPhone());
            student.setEmergencyContactRelation(studentDetails.getEmergencyContactRelation());
            
            Student updatedStudent = studentRepository.save(student);
            return ResponseEntity.ok(updatedStudent);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    // Delete student
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Get active students
    @GetMapping("/active")
    public ResponseEntity<List<Student>> getActiveStudents() {
        List<Student> activeStudents = studentRepository.findActiveStudents();
        return ResponseEntity.ok(activeStudents);
    }
    
    // Get students by grade level
    @GetMapping("/grade/{gradeLevel}")
    public ResponseEntity<List<Student>> getStudentsByGradeLevel(@PathVariable String gradeLevel) {
        try {
            Student.GradeLevel grade = Student.GradeLevel.valueOf(gradeLevel.toUpperCase());
            List<Student> students = studentRepository.findByGradeLevel(grade);
            return ResponseEntity.ok(students);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Search students by name
    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudentsByName(@RequestParam String name) {
        List<Student> students = studentRepository.findByNameContaining(name);
        return ResponseEntity.ok(students);
    }
    
    // Get students enrolled between dates
    @GetMapping("/enrollment")
    public ResponseEntity<List<Student>> getStudentsByEnrollmentDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Student> students = studentRepository.findByEnrollmentDateBetween(startDate, endDate);
        return ResponseEntity.ok(students);
    }
    
    // Get students by city
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Student>> getStudentsByCity(@PathVariable String city) {
        List<Student> students = studentRepository.findActiveStudentsByCity(city);
        return ResponseEntity.ok(students);
    }
    
    // Get statistics
    @GetMapping("/statistics")
    public ResponseEntity<Object> getStudentStatistics() {
        Long totalActiveStudents = studentRepository.countActiveStudents();
        
        // You can add more statistics here
        var statistics = new Object() {
            public final Long totalActive = totalActiveStudents;
            public final Long totalKindergarten = studentRepository.countActiveStudentsByGradeLevel(Student.GradeLevel.KINDERGARTEN);
            public final Long totalGrade1 = studentRepository.countActiveStudentsByGradeLevel(Student.GradeLevel.GRADE_1);
            public final Long totalGrade2 = studentRepository.countActiveStudentsByGradeLevel(Student.GradeLevel.GRADE_2);
            public final Long totalGrade3 = studentRepository.countActiveStudentsByGradeLevel(Student.GradeLevel.GRADE_3);
            public final Long totalGrade4 = studentRepository.countActiveStudentsByGradeLevel(Student.GradeLevel.GRADE_4);
            public final Long totalGrade5 = studentRepository.countActiveStudentsByGradeLevel(Student.GradeLevel.GRADE_5);
        };
        
        return ResponseEntity.ok(statistics);
    }
    
    // Get students by parent ID
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Student>> getStudentsByParentId(@PathVariable Long parentId) {
        List<Student> students = studentRepository.findStudentsByParentId(parentId);
        return ResponseEntity.ok(students);
    }
}