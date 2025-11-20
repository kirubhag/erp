package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.Subject;

/**
 * Unit tests for SubjectRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SubjectRepositoryTest {
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    private Subject testSubject1;
    private Subject testSubject2;
    private Subject testSubject3;
    
    @BeforeEach
    void setUp() {
        subjectRepository.deleteAllInBatch();
        
        // Create test subject 1
        testSubject1 = new Subject();
        testSubject1.setSubjectCode("MATH8-TEST");
        testSubject1.setSubjectName("Mathematics Grade 8");
        testSubject1.setDescription("Advanced mathematics for grade 8");
        testSubject1.setGradeLevel("Grade 8");
        testSubject1.setCategory("Mathematics");
        testSubject1.setCredits(4);
        testSubject1.setHoursPerWeek(5);
        testSubject1.setDifficultyLevel("Medium");
        testSubject1.setIsMandatory(true);
        testSubject1.setIsActive(1);
        testSubject1 = subjectRepository.save(testSubject1);
        
        // Create test subject 2
        testSubject2 = new Subject();
        testSubject2.setSubjectCode("SCI8-TEST");
        testSubject2.setSubjectName("Science Grade 8");
        testSubject2.setDescription("General science for grade 8");
        testSubject2.setGradeLevel("Grade 8");
        testSubject2.setCategory("Science");
        testSubject2.setCredits(4);
        testSubject2.setHoursPerWeek(4);
        testSubject2.setDifficultyLevel("Medium");
        testSubject2.setIsMandatory(true);
        testSubject2.setIsActive(1);
        testSubject2 = subjectRepository.save(testSubject2);
        
        // Create test subject 3 - Inactive
        testSubject3 = new Subject();
        testSubject3.setSubjectCode("ART9-TEST");
        testSubject3.setSubjectName("Art Grade 9");
        testSubject3.setDescription("Visual arts for grade 9");
        testSubject3.setGradeLevel("Grade 9");
        testSubject3.setCategory("Arts");
        testSubject3.setCredits(2);
        testSubject3.setHoursPerWeek(2);
        testSubject3.setDifficultyLevel("Easy");
        testSubject3.setIsMandatory(false);
        testSubject3.setIsActive(0);
        testSubject3 = subjectRepository.save(testSubject3);
    }
    
    @Test
    void testSaveSubject() {
        Subject newSubject = new Subject();
        newSubject.setSubjectCode("ENG8-TEST");
        newSubject.setSubjectName("English Grade 8");
        newSubject.setGradeLevel("Grade 8");
        newSubject.setIsActive(1);
        
        Subject saved = subjectRepository.save(newSubject);
        
        assertNotNull(saved.getId());
        assertEquals("ENG8-TEST", saved.getSubjectCode());
    }
    
    @Test
    void testFindBySubjectCode() {
        Optional<Subject> found = subjectRepository.findBySubjectCode("MATH8-TEST");
        
        assertTrue(found.isPresent());
        assertEquals("Mathematics Grade 8", found.get().getSubjectName());
    }
    
    @Test
    void testFindByGradeLevel() {
        List<Subject> found = subjectRepository.findByGradeLevel("Grade 8");
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(2);
    }
    
    @Test
    void testFindByCategory() {
        List<Subject> found = subjectRepository.findByCategory("Mathematics");
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(1);
        assertThat(found).allMatch(s -> "Mathematics".equals(s.getCategory()));
    }
    
    @Test
    void testFindByIsActive() {
        List<Subject> activeSubjects = subjectRepository.findByIsActive(1);
        List<Subject> inactiveSubjects = subjectRepository.findByIsActive(0);
        
        assertThat(activeSubjects).hasSizeGreaterThanOrEqualTo(2);
        assertThat(inactiveSubjects).hasSizeGreaterThanOrEqualTo(1);
    }
    
    @Test
    void testFindByGradeLevelAndIsActive() {
        List<Subject> found = subjectRepository.findByGradeLevelAndIsActive("Grade 8", 1);
        
        assertThat(found).hasSize(2);
    }
    
    @Test
    void testFindByCategoryAndIsActive() {
        List<Subject> found = subjectRepository.findByCategoryAndIsActive("Mathematics", 1);
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(1);
    }
    
    @Test
    void testFindAllActiveSubjects() {
        List<Subject> activeSubjects = subjectRepository.findAllActiveSubjects();
        
        assertThat(activeSubjects).hasSizeGreaterThanOrEqualTo(2);
        assertThat(activeSubjects).allMatch(s -> s.getIsActive() == 1);
    }
    
    @Test
    void testSearchSubjects() {
        List<Subject> found = subjectRepository.searchSubjects("Math");
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(1);
    }
    
    @Test
    void testCountByGradeLevelAndActive() {
        long count = subjectRepository.countByGradeLevelAndActive("Grade 8");
        
        assertEquals(2L, count);
    }
    
    @Test
    void testExistsBySubjectCode() {
        assertTrue(subjectRepository.existsBySubjectCode("MATH8-TEST"));
        assertFalse(subjectRepository.existsBySubjectCode("NONEXISTENT"));
    }
    
    @Test
    void testUpdateSubject() {
        Subject subject = subjectRepository.findBySubjectCode("MATH8-TEST").orElseThrow();
        subject.setCredits(5);
        
        Subject updated = subjectRepository.save(subject);
        
        assertEquals(5, updated.getCredits());
    }
    
    @Test
    void testDeleteSubject() {
        Long initialCount = subjectRepository.count();
        
        subjectRepository.delete(testSubject1);
        
        Long afterDeleteCount = subjectRepository.count();
        assertEquals(initialCount - 1, afterDeleteCount);
    }
    
    @Test
    void testFindAll() {
        List<Subject> allSubjects = subjectRepository.findAll();
        
        assertThat(allSubjects).hasSizeGreaterThanOrEqualTo(3);
    }
}
