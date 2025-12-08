package krs.erp.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.ErpClass;
import krs.erp.model.Room;
import krs.erp.model.Room.RoomType;
import krs.erp.model.Staff;
import krs.erp.model.Subject;
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

    @Autowired
    private ErpClassRepository classRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private RoomRepository roomRepository;

    private Timetable timetable1;
    private Timetable timetable2;
    private ErpClass class10A;
    private Subject mathSubject;
    private Staff teacherMrJohnson;
    private Room room101;

    @BeforeEach
    void setUp() {
        timetableRepository.deleteAllInBatch();
        classRepository.deleteAllInBatch();
        subjectRepository.deleteAllInBatch();
        staffRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();

        // Setup dependencies
        class10A = new ErpClass();
        class10A.setClassName("Class 10-A");
        class10A.setGradeLevel("Grade 10");
        class10A.setSection("A");
        class10A.setAcademicYear("2024-2025");
        class10A.setClassCode("C10A");
        class10A.markAsActive();
        class10A = classRepository.save(class10A);

        mathSubject = new Subject();
        mathSubject.setSubjectName("Mathematics");
        mathSubject.setSubjectCode("MATH101");
        mathSubject.markAsActive();
        mathSubject = subjectRepository.save(mathSubject);

        teacherMrJohnson = new Staff();
        teacherMrJohnson.setFirstName("Mr.");
        teacherMrJohnson.setLastName("Johnson");
        teacherMrJohnson.setStaffId("TCH001");
        teacherMrJohnson.setEmail("johnson@test.com");
        teacherMrJohnson.setDateOfBirth(java.time.LocalDate.of(1980, 1, 1));
        teacherMrJohnson.setGender(Staff.Gender.MALE);
        teacherMrJohnson.setHireDate(java.time.LocalDate.of(2010, 1, 1));
        teacherMrJohnson.setEmploymentStatus(Staff.EmploymentStatus.ACTIVE);
        teacherMrJohnson.setStaffType(Staff.StaffType.TEACHER);
        teacherMrJohnson.markAsActive();
        teacherMrJohnson = staffRepository.save(teacherMrJohnson);

        room101 = new Room();
        room101.setRoomName("Room 101");
        room101.setCapacity(30);
        room101.setRoomType(RoomType.CLASSROOM);
        room101.setBuilding("Main Building");
        room101.markAsActive();
        room101 = roomRepository.save(room101);

        // Create timetable 1 - Grade 10 Math Monday
        timetable1 = new Timetable();
        timetable1.setErpClass(class10A); // Relationship
        timetable1.setAcademicYear("2024-2025");
        timetable1.setDayOfWeek(Timetable.DayOfWeek.MONDAY);
        timetable1.setStartTime(LocalTime.of(9, 0));
        timetable1.setEndTime(LocalTime.of(10, 0));
        timetable1.setSubject(mathSubject); // Relationship
        timetable1.setTeacher(teacherMrJohnson); // Relationship
        timetable1.setRoom(room101); // Relationship
        timetable1.setPeriodNumber(1);
        timetable1.setDescription("Math Class");
        timetable1.markAsActive();
        timetable1 = timetableRepository.save(timetable1);

        // Create timetable 2 - Grade 10 Math Wednesday (Same class/subject, diff day)
        timetable2 = new Timetable();
        timetable2.setErpClass(class10A);
        timetable2.setAcademicYear("2024-2025");
        timetable2.setDayOfWeek(Timetable.DayOfWeek.WEDNESDAY);
        timetable2.setStartTime(LocalTime.of(9, 0));
        timetable2.setEndTime(LocalTime.of(10, 0));
        timetable2.setSubject(mathSubject);
        timetable2.setTeacher(teacherMrJohnson);
        timetable2.setRoom(room101);
        timetable2.setPeriodNumber(1);
        timetable2.markAsActive();
        timetable2 = timetableRepository.save(timetable2);
    }

    @Test
    void testFindByGradeLevel() {
        List<Timetable> grade10 = timetableRepository.findByGradeLevel("Grade 10");
        assertThat(grade10).hasSize(2); // timetable1 and timetable2
    }

    @Test
    void testFindByGradeLevelAndIsActive() {
        List<Timetable> activeGrade10 = timetableRepository.findByGradeLevelAndIsActive("Grade 10", true);
        assertThat(activeGrade10).hasSize(2);
    }

    @Test
    void testFindByClassName() {
        List<Timetable> class10A = timetableRepository.findByClassName("Class 10-A");
        assertThat(class10A).hasSize(2);
    }

    @Test
    void testFindByClassNameAndIsActive() {
        List<Timetable> activeClass10A = timetableRepository.findByClassNameAndIsActive("Class 10-A", true);
        assertThat(activeClass10A).hasSize(2);
    }

    @Test
    void testFindByDayOfWeek() {
        List<Timetable> mondayClasses = timetableRepository.findByDayOfWeek(Timetable.DayOfWeek.MONDAY);
        assertThat(mondayClasses).hasSize(1);
    }

    @Test
    void testFindByDayOfWeekAndIsActive() {
        List<Timetable> activeMondayClasses = timetableRepository.findByDayOfWeekAndIsActive(
                Timetable.DayOfWeek.MONDAY, true);
        assertThat(activeMondayClasses).hasSize(1);
    }

    @Test
    void testFindByAcademicYear() {
        List<Timetable> currentYear = timetableRepository.findByAcademicYear("2024-2025");
        assertThat(currentYear).hasSize(2);
    }

    @Test
    void testFindBySubjectCode() {
        List<Timetable> mathClasses = timetableRepository.findBySubjectCode("MATH101");
        assertThat(mathClasses).hasSize(2);
        assertEquals("Mathematics", mathClasses.get(0).getSubject().getSubjectName());
    }

    @Test
    void testFindByTeacherId() {
        List<Timetable> teacherClasses = timetableRepository.findByTeacherId("TCH001");
        assertThat(teacherClasses).hasSize(2);
        assertEquals("Johnson", teacherClasses.get(0).getTeacher().getLastName());
    }

    @Test
    void testFindByRoomNumber() {
        List<Timetable> room101Classes = timetableRepository.findByRoomNumber("Room 101");
        assertThat(room101Classes).hasSize(2);
    }

    @Test
    void testFindAllActiveTimetables() {
        List<Timetable> active = timetableRepository.findAllActiveTimetables();
        assertThat(active).hasSize(2);
        assertThat(active.get(0).getDayOfWeek()).isEqualTo(Timetable.DayOfWeek.MONDAY);
    }

    @Test
    void testFindByGradeLevelAndDay() {
        List<Timetable> grade10Monday = timetableRepository.findByGradeLevelAndDay(
                "Grade 10", Timetable.DayOfWeek.MONDAY);
        assertThat(grade10Monday).hasSize(1);
        assertEquals("Mathematics", grade10Monday.get(0).getSubject().getSubjectName());
    }

    @Test
    void testFindByClassAndDay() {
        List<Timetable> class10AMonday = timetableRepository.findByClassAndDay(
                "Class 10-A", Timetable.DayOfWeek.MONDAY);
        assertThat(class10AMonday).hasSize(1);
    }

    @Test
    void testSearchTimetables() {
        // Search by subject name
        List<Timetable> searchMath = timetableRepository.searchTimetables("Mathematics");
        assertThat(searchMath).hasSize(2);

        // Search by teacher name
        List<Timetable> searchJohnson = timetableRepository.searchTimetables("Johnson");
        assertThat(searchJohnson).hasSize(2);
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
    }

    @Test
    void testFindByClassAndAcademicPeriod() {
        List<Timetable> schedule = timetableRepository.findByClassAndAcademicPeriod(
                "Class 10-A", "2024-2025");
        assertThat(schedule).hasSize(2);
    }

    @Test
    void testUpdateTimetable() {
        timetable1.setDescription("Updated Description");
        Timetable updated = timetableRepository.save(timetable1);
        assertEquals("Updated Description", updated.getDescription());
    }

    @Test
    void testDeleteTimetable() {
        Long timetableId = timetable1.getId();
        timetableRepository.delete(timetable1);
        assertThat(timetableRepository.findById(timetableId)).isEmpty();
    }
}
