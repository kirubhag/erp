package krs.erp.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.Timetable;

/**
 * Unit tests for TimetableRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TimetableRepositoryTest {
    
    @Autowired
    private TimetableRepository timetableRepository;
    
    private Timetable timetable1;
    private Timetable timetable2;
    private Timetable timetable3;
    
    @BeforeEach
    void setUp() {
        timetableRepository.deleteAllInBatch();
        
        // Create timetable 1 - Grade 10 Math Monday
        timetable1 = new Timetable();
        timetable1.setTimetableCode("TT-G10-MATH-MON-01");
        timetable1.setClassName("Class 10-A");
        timetable1.setGradeLevel("Grade 10");
        timetable1.setAcademicYear("2024-2025");
        timetable1.setSemester("Fall");
        timetable1.setDayOfWeek(Timetable.DayOfWeek.MONDAY);
        timetable1.setStartTime(LocalTime.of(9, 0));
        timetable1.setEndTime(LocalTime.of(10, 0));
        timetable1.setSubjectName("Mathematics");
        timetable1.setSubjectCode("MATH101");
        timetable1.setTeacherName("Mr. Johnson");
        timetable1.setTeacherId("TCH001");
        timetable1.setRoomNumber("Room 101");
        timetable1.setBuilding("Main Building");
        timetable1.setPeriodNumber(1);
        timetable1.setIsLabSession(false);
        timetable1.setIsActive(1);
        timetable1 = timetableRepository.save(timetable1);
        
        // Create timetable 2 - Grade 10 Science Tuesday (Lab Session)
        timetable2 = new Timetable();
        timetable2.setTimetableCode("TT-G10-SCI-TUE-02");
        timetable2.setClassName("Class 10-A");
        timetable2.setGradeLevel("Grade 10");
        timetable2.setAcademicYear("2024-2025");
        timetable2.setSemester("Fall");
        timetable2.setDayOfWeek(Timetable.DayOfWeek.TUESDAY);
        timetable2.setStartTime(LocalTime.of(10, 0));
        timetable2.setEndTime(LocalTime.of(11, 30));
        timetable2.setSubjectName("Chemistry");
        timetable2.setSubjectCode("CHEM101");
        timetable2.setTeacherName("Dr. Smith");
        timetable2.setTeacherId("TCH002");
        timetable2.setRoomNumber("Lab 201");
        timetable2.setBuilding("Science Block");
        timetable2.setPeriodNumber(2);
        timetable2.setIsLabSession(true);
        timetable2.setIsActive(1);
        timetable2 = timetableRepository.save(timetable2);
        
        // Create timetable 3 - Grade 11 English Monday (Different Room)
        timetable3 = new Timetable();
        timetable3.setTimetableCode("TT-G11-ENG-MON-03");
        timetable3.setClassName("Class 11-B");
        timetable3.setGradeLevel("Grade 11");
        timetable3.setAcademicYear("2024-2025");
        timetable3.setSemester("Fall");
        timetable3.setDayOfWeek(Timetable.DayOfWeek.MONDAY);
        timetable3.setStartTime(LocalTime.of(9, 0));
        timetable3.setEndTime(LocalTime.of(10, 0));
        timetable3.setSubjectName("English Literature");
        timetable3.setSubjectCode("ENG201");
        timetable3.setTeacherName("Mrs. Davis");
        timetable3.setTeacherId("TCH003");
        timetable3.setRoomNumber("Room 102");
        timetable3.setBuilding("Main Building");
        timetable3.setPeriodNumber(1);
        timetable3.setIsLabSession(false);
        timetable3.setIsActive(1);
        timetable3 = timetableRepository.save(timetable3);
    }
    
    @Test
    void testFindByTimetableCode() {
        Optional<Timetable> found = timetableRepository.findByTimetableCode("TT-G10-MATH-MON-01");
        
        assertThat(found).isPresent();
        assertEquals("Mathematics", found.get().getSubjectName());
    }
    
    @Test
    void testExistsByTimetableCode() {
        assertTrue(timetableRepository.existsByTimetableCode("TT-G10-MATH-MON-01"));
        assertFalse(timetableRepository.existsByTimetableCode("NON-EXISTENT"));
    }
    
    @Test
    void testFindByGradeLevel() {
        List<Timetable> grade10 = timetableRepository.findByGradeLevel("Grade 10");
        assertThat(grade10).hasSize(2);
        
        List<Timetable> grade11 = timetableRepository.findByGradeLevel("Grade 11");
        assertThat(grade11).hasSize(1);
    }
    
    @Test
    void testFindByGradeLevelAndIsActive() {
        List<Timetable> activeGrade10 = timetableRepository.findByGradeLevelAndIsActive("Grade 10", 1);
        assertThat(activeGrade10).hasSize(2);
    }
    
    @Test
    void testFindByClassName() {
        List<Timetable> class10A = timetableRepository.findByClassName("Class 10-A");
        assertThat(class10A).hasSize(2);
    }
    
    @Test
    void testFindByClassNameAndIsActive() {
        List<Timetable> activeClass10A = timetableRepository.findByClassNameAndIsActive("Class 10-A", 1);
        assertThat(activeClass10A).hasSize(2);
    }
    
    @Test
    void testFindByDayOfWeek() {
        List<Timetable> mondayClasses = timetableRepository.findByDayOfWeek(Timetable.DayOfWeek.MONDAY);
        assertThat(mondayClasses).hasSize(2);
        
        List<Timetable> tuesdayClasses = timetableRepository.findByDayOfWeek(Timetable.DayOfWeek.TUESDAY);
        assertThat(tuesdayClasses).hasSize(1);
    }
    
    @Test
    void testFindByDayOfWeekAndIsActive() {
        List<Timetable> activeMondayClasses = timetableRepository.findByDayOfWeekAndIsActive(
            Timetable.DayOfWeek.MONDAY, 1);
        assertThat(activeMondayClasses).hasSize(2);
    }
    
    @Test
    void testFindByAcademicYear() {
        List<Timetable> currentYear = timetableRepository.findByAcademicYear("2024-2025");
        assertThat(currentYear).hasSize(3);
    }
    
    @Test
    void testFindByAcademicYearAndIsActive() {
        List<Timetable> activeCurrentYear = timetableRepository.findByAcademicYearAndIsActive("2024-2025", 1);
        assertThat(activeCurrentYear).hasSize(3);
    }
    
    @Test
    void testFindBySubjectCode() {
        List<Timetable> mathClasses = timetableRepository.findBySubjectCode("MATH101");
        assertThat(mathClasses).hasSize(1);
        assertEquals("Mathematics", mathClasses.get(0).getSubjectName());
    }
    
    @Test
    void testFindByTeacherId() {
        List<Timetable> teacherClasses = timetableRepository.findByTeacherId("TCH001");
        assertThat(teacherClasses).hasSize(1);
        assertEquals("Mr. Johnson", teacherClasses.get(0).getTeacherName());
    }
    
    @Test
    void testFindByRoomNumber() {
        List<Timetable> room101Classes = timetableRepository.findByRoomNumber("Room 101");
        assertThat(room101Classes).hasSize(1);
    }
    
    @Test
    void testFindAllActiveTimetables() {
        List<Timetable> active = timetableRepository.findAllActiveTimetables();
        assertThat(active).hasSize(3);
        
        // Verify ordering by day and start time
        assertThat(active.get(0).getDayOfWeek()).isEqualTo(Timetable.DayOfWeek.MONDAY);
    }
    
    @Test
    void testFindByGradeLevelAndDay() {
        List<Timetable> grade10Monday = timetableRepository.findByGradeLevelAndDay(
            "Grade 10", Timetable.DayOfWeek.MONDAY);
        assertThat(grade10Monday).hasSize(1);
        assertEquals("Mathematics", grade10Monday.get(0).getSubjectName());
    }
    
    @Test
    void testFindByClassAndDay() {
        List<Timetable> class10AMonday = timetableRepository.findByClassAndDay(
            "Class 10-A", Timetable.DayOfWeek.MONDAY);
        assertThat(class10AMonday).hasSize(1);
    }
    
    @Test
    void testSearchTimetables() {
        List<Timetable> searchMath = timetableRepository.searchTimetables("math");
        assertThat(searchMath).hasSize(1);
        
        List<Timetable> searchJohnson = timetableRepository.searchTimetables("johnson");
        assertThat(searchJohnson).hasSize(1);
    }
    
    @Test
    void testCountByGradeLevelAndActive() {
        long count = timetableRepository.countByGradeLevelAndActive("Grade 10");
        assertEquals(2L, count);
    }
    
    @Test
    void testFindConflictingTimetables() {
        // Check for conflicts in Room 101 on Monday from 9:00-10:00
        List<Timetable> conflicts = timetableRepository.findConflictingTimetables(
            "Room 101", Timetable.DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0));
        
        assertThat(conflicts).hasSize(1);
        assertEquals("TT-G10-MATH-MON-01", conflicts.get(0).getTimetableCode());
    }
    
    @Test
    void testFindByClassAndAcademicPeriod() {
        List<Timetable> schedule = timetableRepository.findByClassAndAcademicPeriod(
            "Class 10-A", "2024-2025", "Fall");
        assertThat(schedule).hasSize(2);
    }
    
    @Test
    void testSaveTimetable() {
        Timetable newTimetable = new Timetable();
        newTimetable.setTimetableCode("TT-G10-PHY-WED-04");
        newTimetable.setClassName("Class 10-A");
        newTimetable.setGradeLevel("Grade 10");
        newTimetable.setAcademicYear("2024-2025");
        newTimetable.setDayOfWeek(Timetable.DayOfWeek.WEDNESDAY);
        newTimetable.setStartTime(LocalTime.of(11, 0));
        newTimetable.setEndTime(LocalTime.of(12, 0));
        newTimetable.setSubjectName("Physics");
        newTimetable.setIsActive(1);
        
        Timetable saved = timetableRepository.save(newTimetable);
        
        assertNotNull(saved.getId());
        assertEquals("Physics", saved.getSubjectName());
    }
    
    @Test
    void testUpdateTimetable() {
        timetable1.setRoomNumber("Room 105");
        timetable1.setTeacherName("Mr. Anderson");
        
        Timetable updated = timetableRepository.save(timetable1);
        
        assertEquals("Room 105", updated.getRoomNumber());
        assertEquals("Mr. Anderson", updated.getTeacherName());
    }
    
    @Test
    void testDeleteTimetable() {
        Long timetableId = timetable1.getId();
        timetableRepository.delete(timetable1);
        
        assertThat(timetableRepository.findById(timetableId)).isEmpty();
    }
}
