package krs.erp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import krs.erp.model.Subject;
import krs.erp.repository.SubjectRepository;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    public ResponseEntity<Page<Subject>> getAllSubjects(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer isActive,
            Pageable pageable) {

        List<Subject> subjects;

        if (search != null && !search.trim().isEmpty()) {
            subjects = subjectRepository.searchSubjects(search);
        } else if (gradeLevel != null && isActive != null) {
            subjects = subjectRepository.findByGradeLevelAndIsActive(gradeLevel, isActive);
        } else if (category != null && isActive != null) {
            subjects = subjectRepository.findByCategoryAndIsActive(category, isActive);
        } else if (gradeLevel != null) {
            subjects = subjectRepository.findByGradeLevel(gradeLevel);
        } else if (category != null) {
            subjects = subjectRepository.findByCategory(category);
        } else if (isActive != null) {
            subjects = subjectRepository.findByIsActive(isActive);
        } else {
            subjects = subjectRepository.findAll();
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), subjects.size());
        List<Subject> pageContent = subjects.subList(start, end);
        Page<Subject> page = new PageImpl<>(pageContent, pageable, subjects.size());

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        return subject.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{subjectCode}")
    public ResponseEntity<Subject> getSubjectByCode(@PathVariable String subjectCode) {
        Optional<Subject> subject = subjectRepository.findBySubjectCode(subjectCode);
        return subject.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createSubject(@Valid @RequestBody Subject subject) {
        if (subjectRepository.existsBySubjectCode(subject.getSubjectCode())) {
            return ResponseEntity.badRequest()
                    .body("Subject with code " + subject.getSubjectCode() + " already exists");
        }
        Subject savedSubject = subjectRepository.save(subject);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSubject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubject(@PathVariable Long id, @Valid @RequestBody Subject subjectDetails) {
        Optional<Subject> subjectOptional = subjectRepository.findById(id);
        
        if (!subjectOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Subject subject = subjectOptional.get();
        
        // Check if subject code is being changed and if new code already exists
        if (!subject.getSubjectCode().equals(subjectDetails.getSubjectCode()) &&
            subjectRepository.existsBySubjectCode(subjectDetails.getSubjectCode())) {
            return ResponseEntity.badRequest()
                    .body("Subject with code " + subjectDetails.getSubjectCode() + " already exists");
        }

        subject.setSubjectCode(subjectDetails.getSubjectCode());
        subject.setSubjectName(subjectDetails.getSubjectName());
        subject.setDescription(subjectDetails.getDescription());
        subject.setGradeLevel(subjectDetails.getGradeLevel());
        subject.setCategory(subjectDetails.getCategory());
        subject.setCredits(subjectDetails.getCredits());
        subject.setHoursPerWeek(subjectDetails.getHoursPerWeek());
        subject.setIsActive(subjectDetails.getIsActive());
        subject.setPrerequisites(subjectDetails.getPrerequisites());
        subject.setDifficultyLevel(subjectDetails.getDifficultyLevel());
        subject.setIsMandatory(subjectDetails.getIsMandatory());

        Subject updatedSubject = subjectRepository.save(subject);
        return ResponseEntity.ok(updatedSubject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        Optional<Subject> subjectOptional = subjectRepository.findById(id);
        
        if (!subjectOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Subject subject = subjectOptional.get();
        subject.markAsDeleted(); // Soft delete
        subjectRepository.save(subject);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<Subject>> getActiveSubjects() {
        List<Subject> subjects = subjectRepository.findAllActiveSubjects();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/grade/{gradeLevel}")
    public ResponseEntity<List<Subject>> getSubjectsByGrade(@PathVariable String gradeLevel) {
        List<Subject> subjects = subjectRepository.findByGradeLevelAndIsActive(gradeLevel, 1);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Subject>> getSubjectsByCategory(@PathVariable String category) {
        List<Subject> subjects = subjectRepository.findByCategoryAndIsActive(category, 1);
        return ResponseEntity.ok(subjects);
    }
}
