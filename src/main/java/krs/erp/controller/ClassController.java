package krs.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import krs.erp.model.ErpClass;
import krs.erp.model.Staff;
import krs.erp.model.Student;
import krs.erp.model.Subject;
import krs.erp.repository.ErpClassRepository;
import krs.erp.repository.StaffRepository;
import krs.erp.repository.StudentRepository;
import krs.erp.repository.SubjectRepository;

/**
 * REST controller for ErpClass (Class/Section) entity
 * Provides CRUD operations and association management for Teachers, Students,
 * and Subjects
 */
@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*")
public class ClassController {

    @Autowired
    private ErpClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    // ========== CRUD Operations ==========

    /**
     * Get all classes with pagination
     */
    @GetMapping
    public ResponseEntity<Page<ErpClass>> getAllClasses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ErpClass> classes = classRepository.findAllActive(pageable);
        return ResponseEntity.ok(classes);
    }

    /**
     * Get class by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ErpClass> getClassById(@PathVariable Long id) {
        return classRepository.findById(id)
                .filter(c -> c.getIsActive() == 1)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search classes
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ErpClass>> searchClasses(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ErpClass> classes = classRepository.searchClasses(q, pageable);
        return ResponseEntity.ok(classes);
    }

    /**
     * Get classes by grade level
     */
    @GetMapping("/grade/{gradeLevel}")
    public ResponseEntity<List<ErpClass>> getClassesByGradeLevel(@PathVariable String gradeLevel) {
        List<ErpClass> classes = classRepository.findByGradeLevel(gradeLevel);
        return ResponseEntity.ok(classes);
    }

    /**
     * Get classes by academic year
     */
    @GetMapping("/year/{academicYear}")
    public ResponseEntity<List<ErpClass>> getClassesByAcademicYear(@PathVariable String academicYear) {
        List<ErpClass> classes = classRepository.findByAcademicYear(academicYear);
        return ResponseEntity.ok(classes);
    }

    /**
     * Create a new class
     */
    @PostMapping
    public ResponseEntity<?> createClass(@Valid @RequestBody ClassRequest request) {
        // Check if class already exists
        if (classRepository.existsByGradeLevelAndSectionAndAcademicYear(
                request.getGradeLevel(), request.getSection(), request.getAcademicYear())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Class already exists for this grade level, section, and academic year");
        }

        ErpClass erpClass = new ErpClass();
        erpClass.setClassCode(request.getClassCode());
        erpClass.setClassName(request.getClassName());
        erpClass.setGradeLevel(request.getGradeLevel());
        erpClass.setSection(request.getSection());
        erpClass.setAcademicYear(request.getAcademicYear());
        erpClass.setCapacity(request.getCapacity());
        erpClass.setRoomNumber(request.getRoomNumber());
        erpClass.setDescription(request.getDescription());
        erpClass.setOrganizationId(request.getOrganizationId());

        // Set class teacher if provided
        if (request.getClassTeacherId() != null) {
            staffRepository.findById(request.getClassTeacherId())
                    .ifPresent(erpClass::setClassTeacher);
        }

        erpClass.markAsActive();
        ErpClass savedClass = classRepository.save(erpClass);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClass);
    }

    /**
     * Update an existing class
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClass(@PathVariable Long id, @Valid @RequestBody ClassRequest request) {
        return classRepository.findById(id)
                .filter(c -> c.getIsActive() == 1)
                .map(erpClass -> {
                    erpClass.setClassCode(request.getClassCode());
                    erpClass.setClassName(request.getClassName());
                    erpClass.setGradeLevel(request.getGradeLevel());
                    erpClass.setSection(request.getSection());
                    erpClass.setAcademicYear(request.getAcademicYear());
                    erpClass.setCapacity(request.getCapacity());
                    erpClass.setRoomNumber(request.getRoomNumber());
                    erpClass.setDescription(request.getDescription());
                    erpClass.setOrganizationId(request.getOrganizationId());

                    // Update class teacher
                    if (request.getClassTeacherId() != null) {
                        staffRepository.findById(request.getClassTeacherId())
                                .ifPresent(erpClass::setClassTeacher);
                    } else {
                        erpClass.setClassTeacher(null);
                    }

                    ErpClass updatedClass = classRepository.save(erpClass);
                    return ResponseEntity.ok(updatedClass);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a class (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        return classRepository.findById(id)
                .filter(c -> c.getIsActive() == 1)
                .map(erpClass -> {
                    erpClass.markAsDeleted();
                    classRepository.save(erpClass);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== Student Association Management ==========

    /**
     * Get all students in a class
     */
    @GetMapping("/{id}/students")
    public ResponseEntity<?> getClassStudents(@PathVariable Long id) {
        return classRepository.findById(id)
                .filter(c -> c.getIsActive() == 1)
                .map(erpClass -> {
                    Set<Student> students = erpClass.getStudents();
                    return ResponseEntity.ok(students);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Add students to a class (bulk operation)
     */
    @PostMapping("/{id}/students")
    public ResponseEntity<?> addStudentsToClass(
            @PathVariable Long id,
            @RequestBody List<Long> studentIds) {

        return classRepository.findById(id)
                .filter(c -> c.getIsActive() == 1)
                .map(erpClass -> {
                    int added = 0;
                    int skipped = 0;

                    for (Long studentId : studentIds) {
                        studentRepository.findById(studentId).ifPresent(student -> {
                            erpClass.addStudent(student);
                        });
                        added++;
                    }

                    classRepository.save(erpClass);

                    Map<String, Object> response = new HashMap<>();
                    response.put("added", added);
                    response.put("skipped", skipped);
                    response.put("totalStudents", erpClass.getStudentCount());

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Remove a student from a class
     */
    @DeleteMapping("/{id}/students/{studentId}")
    public ResponseEntity<?> removeStudentFromClass(
            @PathVariable Long id,
            @PathVariable Long studentId) {

        return classRepository.findById(id)
                .filter(c -> c.getIsActive() == 1)
                .map(erpClass -> {
                    studentRepository.findById(studentId).ifPresent(student -> {
                        erpClass.removeStudent(student);
                        classRepository.save(erpClass);
                    });
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== Teacher Association Management ==========

    /**
     * Get all teachers in a class
     */
    @GetMapping("/{id}/teachers")
    public ResponseEntity<?> getClassTeachers(@PathVariable Long id) {
        return classRepository.findById(id)
                .filter(c -> c.getIsActive() == 1)
                .map(erpClass -> {
                    Set<Staff> teachers = erpClass.getTeachers();
                    return ResponseEntity.ok(teachers);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Add teachers to a class (bulk operation)
     */
    @PostMapping("/{id}/teachers")
    public ResponseEntity<?> addTeachersToClass(
            @PathVariable Long id,
            @RequestBody List<Long> teacherIds) {

        return classRepository.findById(id)
                .filter(c -> c.getIsActive() == 1)
                .map(erpClass -> {
                    int added = 0;

                    for (Long teacherId : teacherIds) {
                        staffRepository.findById(teacherId).ifPresent(teacher -> {
                            erpClass.addTeacher(teacher);
                        });
                        added++;
                    }

                    classRepository.save(erpClass);

                    Map<String, Object> response = new HashMap<>();
                    response.put("added", added);
                    response.put("totalTeachers", erpClass.getTeacherCount());

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Remove a teacher from a class
     */
    @DeleteMapping("/{id}/teachers/{teacherId}")
    public ResponseEntity<?> removeTeacherFromClass(
            @PathVariable Long id,
            @PathVariable Long teacherId) {

        return classRepository.findById(id)
                .filter(c -> c.getIsActive() == 1)
                .map(erpClass -> {
                    staffRepository.findById(teacherId).ifPresent(teacher -> {
                        erpClass.removeTeacher(teacher);
                        classRepository.save(erpClass);
                    });
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== Subject Association Management ==========

    /**
     * Get all subjects in a class
     */
    @GetMapping("/{id}/subjects")
    public ResponseEntity<?> getClassSubjects(@PathVariable Long id) {
        return classRepository.findById(id)
                .filter(c -> c.getIsActive() == 1)
                .map(erpClass -> {
                    Set<Subject> subjects = erpClass.getSubjects();
                    return ResponseEntity.ok(subjects);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Add subjects to a class (bulk operation)
     */
    @PostMapping("/{id}/subjects")
    public ResponseEntity<?> addSubjectsToClass(
            @PathVariable Long id,
            @RequestBody List<Long> subjectIds) {

        return classRepository.findById(id)
                .filter(c -> c.getIsActive() == 1)
                .map(erpClass -> {
                    int added = 0;

                    for (Long subjectId : subjectIds) {
                        subjectRepository.findById(subjectId).ifPresent(subject -> {
                            erpClass.addSubject(subject);
                        });
                        added++;
                    }

                    classRepository.save(erpClass);

                    Map<String, Object> response = new HashMap<>();
                    response.put("added", added);
                    response.put("totalSubjects", erpClass.getSubjectCount());

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Remove a subject from a class
     */
    @DeleteMapping("/{id}/subjects/{subjectId}")
    public ResponseEntity<?> removeSubjectFromClass(
            @PathVariable Long id,
            @PathVariable Long subjectId) {

        return classRepository.findById(id)
                .filter(c -> c.getIsActive() == 1)
                .map(erpClass -> {
                    subjectRepository.findById(subjectId).ifPresent(subject -> {
                        erpClass.removeSubject(subject);
                        classRepository.save(erpClass);
                    });
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== DTO Classes ==========

    /**
     * DTO for Class requests
     */
    public static class ClassRequest {
        private String classCode;
        private String className;
        private String gradeLevel;
        private String section;
        private String academicYear;
        private Integer capacity;
        private String roomNumber;
        private Long classTeacherId;
        private String description;
        private Long organizationId;

        // Getters and setters
        public String getClassCode() {
            return classCode;
        }

        public void setClassCode(String classCode) {
            this.classCode = classCode;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getGradeLevel() {
            return gradeLevel;
        }

        public void setGradeLevel(String gradeLevel) {
            this.gradeLevel = gradeLevel;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public String getAcademicYear() {
            return academicYear;
        }

        public void setAcademicYear(String academicYear) {
            this.academicYear = academicYear;
        }

        public Integer getCapacity() {
            return capacity;
        }

        public void setCapacity(Integer capacity) {
            this.capacity = capacity;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public void setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
        }

        public Long getClassTeacherId() {
            return classTeacherId;
        }

        public void setClassTeacherId(Long classTeacherId) {
            this.classTeacherId = classTeacherId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(Long organizationId) {
            this.organizationId = organizationId;
        }
    }
}
