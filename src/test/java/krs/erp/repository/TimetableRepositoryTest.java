package krs.erp.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import krs.erp.model.Timetable;
import krs.erp.model.Timetable.DayOfWeek;

@DataJpaTest
@ActiveProfiles("test")
class TimetableRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TimetableRepository timetableRepository;

    private Timetable timetable1;
    private Timetable timetable2;
    private Timetable timetable3;

    @BeforeEach
    void setUp() {
        // Create test timetables
        timetable1 = new Timetable();
        timetable1.setTimetableCode("TT-G1-2024-MON-1");
        timetable1.setClassName("1-A");
        timetable1.setGradeLevel("Grade 1");
        timetable1.setAcademicYear("2024-2025");
        timetable1.setSemester("Fall 2024");
        timetable1.setDayOfWeek(DayOfWeek.MONDAY);
        timetable1.setStartTime(LocalTime.of(8, 0));
        timetable1.setEndTime(LocalTime.of(8, 50));
        timetable1.setSubjectName("Mathematics");
        timetable1.setSubjectCode("MATH-G1");
        timetable1.setTeacherName("Mrs. Anderson");
        timetable1.setTeacherId("T001");
        timetable1.setRoomNumber("101");
        timetable1.setBuilding("Elementary Wing");
        timetable1.setPeriodNumber(1);
        timetable1.setIsLabSession(false);
        timetable1.markAsActive();

        timetable2 = new Timetable();
        timetable2.setTimetableCode("TT-G1-2024-TUE-2");
        timetable2.setClassName("1-A");
        timetable2.setGradeLevel("Grade 1");
        timetable2.setAcademicYear("2024-2025");
        timetable2.setSemester("Fall 2024");
        timetable2.setDayOfWeek(DayOfWeek.TUESDAY);
        timetable2.setStartTime(LocalTime.of(9, 0));
        timetable2.setEndTime(LocalTime.of(9, 50));
        timetable2.setSubjectName("Science");
        timetable2.setSubjectCode("SCI-G1");
        timetable2.setTeacherName("Mr. Williams");
        timetable2.setTeacherId("T002");
        timetable2.setRoomNumber("Lab-101");
        timetable2.setBuilding("Science Block");
        timetable2.setPeriodNumber(2);
        timetable2.setIsLabSession(true);
        timetable2.markAsActive();

        timetable3 = new Timetable();
        timetable3.setTimetableCode("TT-G2-2024-MON-1");
        timetable3.setClassName("2-B");
        timetable3.setGradeLevel("Grade 2");
        timetable3.setAcademicYear("2024-2025");
        timetable3.setSemester("Fall 2024");
        timetable3.setDayOfWeek(DayOfWeek.MONDAY);
        timetable3.setStartTime(LocalTime.of(8, 0));
        timetable3.setEndTime(LocalTime.of(8, 50));
        timetable3.setSubjectName("English");
        timetable3.setSubjectCode("ENG-G2");
        timetable3.setTeacherName("Ms. Johnson");
        timetable3.setTeacherId("T003");
        timetable3.setRoomNumber("102");
        timetable3.setBuilding("Elementary Wing");
        timetable3.setPeriodNumber(1);
        timetable3.setIsLabSession(false);
        timetable3.markAsActive();

        entityManager.persist(timetable1);
        entityManager.persist(timetable2);
        entityManager.persist(timetable3);
        entityManager.flush();
    }

    @Test
    void testFindByTimetableCode() {
        Optional<Timetable> found = timetableRepository.findByTimetableCode("TT-G1-2024-MON-1");
        
        assertThat(found).isPresent();
        assertThat(found.get().getClassName()).isEqualTo("1-A");
        assertThat(found.get().getSubjectName()).isEqualTo("Mathematics");
    }

    @Test
    void testExistsByTimetableCode() {
        boolean exists = timetableRepository.existsByTimetableCode("TT-G1-2024-MON-1");
        assertThat(exists).isTrue();

        boolean notExists = timetableRepository.existsByTimetableCode("NON-EXISTENT");
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindByGradeLevel() {
        List<Timetable> grade1Timetables = timetableRepository.findByGradeLevel("Grade 1");
        
        assertThat(grade1Timetables).hasSize(2);
        assertThat(grade1Timetables).extracting(Timetable::getGradeLevel)
            .containsOnly("Grade 1");
    }

    @Test
    void testFindByGradeLevelAndIsActive() {
        List<Timetable> activeTimetables = timetableRepository.findByGradeLevelAndIsActive("Grade 1", 1);
        
        assertThat(activeTimetables).hasSize(2);
        assertThat(activeTimetables).allMatch(t -> t.getIsActive() == 1);
    }

    @Test
    void testFindByClassName() {
        List<Timetable> classATimetables = timetableRepository.findByClassName("1-A");
        
        assertThat(classATimetables).hasSize(2);
        assertThat(classATimetables).extracting(Timetable::getClassName)
            .containsOnly("1-A");
    }

    @Test
    void testFindByDayOfWeek() {
        List<Timetable> mondayTimetables = timetableRepository.findByDayOfWeek(DayOfWeek.MONDAY);
        
        assertThat(mondayTimetables).hasSize(2);
        assertThat(mondayTimetables).extracting(Timetable::getDayOfWeek)
            .containsOnly(DayOfWeek.MONDAY);
    }

    @Test
    void testFindByDayOfWeekAndIsActive() {
        List<Timetable> activeMondayTimetables = timetableRepository.findByDayOfWeekAndIsActive(DayOfWeek.MONDAY, 1);
        
        assertThat(activeMondayTimetables).hasSize(2);
        assertThat(activeMondayTimetables).allMatch(t -> t.getDayOfWeek() == DayOfWeek.MONDAY && t.getIsActive() == 1);
    }

    @Test
    void testFindBySubjectCode() {
        List<Timetable> mathTimetables = timetableRepository.findBySubjectCode("MATH-G1");
        
        assertThat(mathTimetables).hasSize(1);
        assertThat(mathTimetables.get(0).getSubjectName()).isEqualTo("Mathematics");
    }

    @Test
    void testFindByTeacherId() {
        List<Timetable> teacherTimetables = timetableRepository.findByTeacherId("T001");
        
        assertThat(teacherTimetables).hasSize(1);
        assertThat(teacherTimetables.get(0).getTeacherName()).isEqualTo("Mrs. Anderson");
    }

    @Test
    void testFindByRoomNumber() {
        List<Timetable> roomTimetables = timetableRepository.findByRoomNumber("101");
        
        assertThat(roomTimetables).hasSize(1);
        assertThat(roomTimetables.get(0).getBuilding()).isEqualTo("Elementary Wing");
    }

    @Test
    void testFindAllActiveTimetables() {
        List<Timetable> activeTimetables = timetableRepository.findAllActiveTimetables();
        
        assertThat(activeTimetables).hasSize(3);
        assertThat(activeTimetables).allMatch(t -> t.getIsActive() == 1);
    }

    @Test
    void testFindByGradeLevelAndDay() {
        List<Timetable> timetables = timetableRepository.findByGradeLevelAndDay("Grade 1", DayOfWeek.MONDAY);
        
        assertThat(timetables).hasSize(1);
        assertThat(timetables.get(0).getTimetableCode()).isEqualTo("TT-G1-2024-MON-1");
    }

    @Test
    void testFindByClassAndDay() {
        List<Timetable> timetables = timetableRepository.findByClassAndDay("1-A", DayOfWeek.MONDAY);
        
        assertThat(timetables).hasSize(1);
        assertThat(timetables.get(0).getSubjectName()).isEqualTo("Mathematics");
    }

    @Test
    void testSearchTimetables() {
        List<Timetable> searchResults = timetableRepository.searchTimetables("Math");
        
        assertThat(searchResults).hasSize(1);
        assertThat(searchResults.get(0).getSubjectName()).isEqualTo("Mathematics");
    }

    @Test
    void testCountByGradeLevelAndActive() {
        long count = timetableRepository.countByGradeLevelAndActive("Grade 1");
        
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testFindConflictingTimetables() {
        // Try to find conflicts for room 101 on Monday from 8:00 to 8:50
        List<Timetable> conflicts = timetableRepository.findConflictingTimetables(
            "101",
            DayOfWeek.MONDAY,
            LocalTime.of(8, 0),
            LocalTime.of(8, 50)
        );
        
        assertThat(conflicts).hasSize(1);
        assertThat(conflicts.get(0).getTimetableCode()).isEqualTo("TT-G1-2024-MON-1");
    }

    @Test
    void testFindByClassAndAcademicPeriod() {
        List<Timetable> timetables = timetableRepository.findByClassAndAcademicPeriod(
            "1-A",
            "2024-2025",
            "Fall 2024"
        );
        
        assertThat(timetables).hasSize(2);
        assertThat(timetables).extracting(Timetable::getClassName).containsOnly("1-A");
    }

    @Test
    void testSoftDelete() {
        timetable1.markAsDeleted();
        entityManager.persist(timetable1);
        entityManager.flush();

        List<Timetable> activeTimetables = timetableRepository.findAllActiveTimetables();
        assertThat(activeTimetables).hasSize(2);
        assertThat(activeTimetables).doesNotContain(timetable1);
    }
}
