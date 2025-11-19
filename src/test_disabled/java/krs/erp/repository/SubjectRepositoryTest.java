package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import krs.erp.model.Subject;

@DataJpaTest
@ActiveProfiles("test")
class SubjectRepositoryTest {

    @Autowired
    private SubjectRepository subjectRepository;

    private Subject mathSubject;
    private Subject scienceSubject;

    @BeforeEach
    void setUp() {
        subjectRepository.deleteAll();

        mathSubject = new Subject();
        mathSubject.setSubjectCode("G1-MATH");
        mathSubject.setSubjectName("Mathematics");
        mathSubject.setDescription("Basic mathematics");
        mathSubject.setGradeLevel("Grade 1");
        mathSubject.setCategory("Core");
        mathSubject.setCredits(3);
        mathSubject.setHoursPerWeek(5);
        mathSubject.setDifficultyLevel("Easy");
        mathSubject.setIsMandatory(true);
        mathSubject.markAsActive();

        scienceSubject = new Subject();
        scienceSubject.setSubjectCode("G1-SCI");
        scienceSubject.setSubjectName("Science");
        scienceSubject.setDescription("Basic science");
        scienceSubject.setGradeLevel("Grade 1");
        scienceSubject.setCategory("Core");
        scienceSubject.setCredits(2);
        scienceSubject.setHoursPerWeek(4);
        scienceSubject.setDifficultyLevel("Easy");
        scienceSubject.setIsMandatory(true);
        scienceSubject.markAsActive();
    }

    @Test
    void testSaveSubject() {
        Subject saved = subjectRepository.save(mathSubject);

        assertNotNull(saved.getId());
        assertEquals("G1-MATH", saved.getSubjectCode());
        assertEquals("Mathematics", saved.getSubjectName());
    }

    @Test
    void testFindBySubjectCode() {
        subjectRepository.save(mathSubject);

        Optional<Subject> found = subjectRepository.findBySubjectCode("G1-MATH");

        assertTrue(found.isPresent());
        assertEquals("Mathematics", found.get().getSubjectName());
    }

    @Test
    void testFindByGradeLevel() {
        subjectRepository.save(mathSubject);
        subjectRepository.save(scienceSubject);

        List<Subject> subjects = subjectRepository.findByGradeLevel("Grade 1");

        assertEquals(2, subjects.size());
    }

    @Test
    void testFindByCategory() {
        subjectRepository.save(mathSubject);
        subjectRepository.save(scienceSubject);

        List<Subject> coreSubjects = subjectRepository.findByCategory("Core");

        assertEquals(2, coreSubjects.size());
    }

    @Test
    void testFindByIsActive() {
        mathSubject.markAsActive();
        scienceSubject.markAsInactive();
        
        subjectRepository.save(mathSubject);
        subjectRepository.save(scienceSubject);

        List<Subject> activeSubjects = subjectRepository.findByIsActive(1);
        List<Subject> inactiveSubjects = subjectRepository.findByIsActive(0);

        assertEquals(1, activeSubjects.size());
        assertEquals("G1-MATH", activeSubjects.get(0).getSubjectCode());
        
        assertEquals(1, inactiveSubjects.size());
        assertEquals("G1-SCI", inactiveSubjects.get(0).getSubjectCode());
    }

    @Test
    void testFindByGradeLevelAndIsActive() {
        mathSubject.markAsActive();
        scienceSubject.markAsInactive();
        
        subjectRepository.save(mathSubject);
        subjectRepository.save(scienceSubject);

        List<Subject> activeGrade1 = subjectRepository.findByGradeLevelAndIsActive("Grade 1", 1);

        assertEquals(1, activeGrade1.size());
        assertEquals("G1-MATH", activeGrade1.get(0).getSubjectCode());
    }

    @Test
    void testFindByCategoryAndIsActive() {
        mathSubject.markAsActive();
        scienceSubject.markAsInactive();
        
        subjectRepository.save(mathSubject);
        subjectRepository.save(scienceSubject);

        List<Subject> activeCore = subjectRepository.findByCategoryAndIsActive("Core", 1);

        assertEquals(1, activeCore.size());
        assertEquals("G1-MATH", activeCore.get(0).getSubjectCode());
    }

    @Test
    void testFindAllActiveSubjects() {
        mathSubject.markAsActive();
        scienceSubject.markAsDeleted();
        
        subjectRepository.save(mathSubject);
        subjectRepository.save(scienceSubject);

        List<Subject> activeSubjects = subjectRepository.findAllActiveSubjects();

        assertEquals(1, activeSubjects.size());
        assertEquals("G1-MATH", activeSubjects.get(0).getSubjectCode());
    }

    @Test
    void testSearchSubjects() {
        subjectRepository.save(mathSubject);
        subjectRepository.save(scienceSubject);

        List<Subject> mathResults = subjectRepository.searchSubjects("math");
        List<Subject> grade1Results = subjectRepository.searchSubjects("grade 1");

        assertEquals(1, mathResults.size());
        assertTrue(mathResults.get(0).getSubjectName().contains("Mathematics"));

        assertEquals(2, grade1Results.size());
    }

    @Test
    void testCountByGradeLevelAndActive() {
        mathSubject.markAsActive();
        scienceSubject.markAsActive();
        
        subjectRepository.save(mathSubject);
        subjectRepository.save(scienceSubject);

        long count = subjectRepository.countByGradeLevelAndActive("Grade 1");

        assertEquals(2, count);
    }

    @Test
    void testExistsBySubjectCode() {
        subjectRepository.save(mathSubject);

        boolean exists = subjectRepository.existsBySubjectCode("G1-MATH");
        boolean notExists = subjectRepository.existsBySubjectCode("G2-MATH");

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testSoftDelete() {
        Subject saved = subjectRepository.save(mathSubject);
        
        saved.markAsDeleted();
        subjectRepository.save(saved);

        Subject found = subjectRepository.findById(saved.getId()).orElse(null);
        
        assertNotNull(found);
        assertTrue(found.isDeleted());
        assertEquals(-1, found.getIsActive());
    }
}
