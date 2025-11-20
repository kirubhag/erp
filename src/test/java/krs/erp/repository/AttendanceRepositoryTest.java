package krs.erp.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.Attendance;
import krs.erp.model.Student;

/**
 * Unit tests for AttendanceRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AttendanceRepositoryTest {
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    private Student testStudent;
    private Attendance testAttendance1;
    private Attendance testAttendance2;
    private Attendance testAttendance3;
    
    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAllInBatch();
        studentRepository.deleteAllInBatch();
        
        // Create test student
        testStudent = new Student();
        testStudent.setStudentId("ATT-STU001");
        testStudent.setFirstName("Alice");
        testStudent.setLastName("Johnson");
        testStudent.setEmail("alice.johnson.att@test.com");
        testStudent.setDateOfBirth(LocalDate.of(2010, 5, 15));
        testStudent.setGradeLevel(Student.GradeLevel.GRADE_8);
        testStudent.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        testStudent.setEnrollmentDate(LocalDate.now().minusYears(1));
        testStudent.setIsActive(1);
        testStudent = studentRepository.save(testStudent);
        
        // Create test attendance 1 - Present
        testAttendance1 = new Attendance();
        testAttendance1.setAttendanceDate(LocalDate.now());
        testAttendance1.setStatus(Attendance.AttendanceStatus.PRESENT);
        testAttendance1.setAttendanceType(Attendance.AttendanceType.STUDENT);
        testAttendance1.setCheckInTime(LocalTime.of(8, 0));
        testAttendance1.setStudent(testStudent);
        testAttendance1.setExcused(false);
        testAttendance1 = attendanceRepository.save(testAttendance1);
        
        // Create test attendance 2 - Absent
        testAttendance2 = new Attendance();
        testAttendance2.setAttendanceDate(LocalDate.now().minusDays(1));
        testAttendance2.setStatus(Attendance.AttendanceStatus.ABSENT);
        testAttendance2.setAttendanceType(Attendance.AttendanceType.STUDENT);
        testAttendance2.setStudent(testStudent);
        testAttendance2.setExcused(false);
        testAttendance2 = attendanceRepository.save(testAttendance2);
        
        // Create test attendance 3 - Late
        testAttendance3 = new Attendance();
        testAttendance3.setAttendanceDate(LocalDate.now().minusDays(2));
        testAttendance3.setStatus(Attendance.AttendanceStatus.LATE);
        testAttendance3.setAttendanceType(Attendance.AttendanceType.STUDENT);
        testAttendance3.setCheckInTime(LocalTime.of(9, 30));
        testAttendance3.setStudent(testStudent);
        testAttendance3.setExcused(true);
        testAttendance3 = attendanceRepository.save(testAttendance3);
    }
    
    @Test
    void testSaveAttendance() {
        Attendance newAttendance = new Attendance();
        newAttendance.setAttendanceDate(LocalDate.now().minusDays(3));
        newAttendance.setStatus(Attendance.AttendanceStatus.PRESENT);
        newAttendance.setAttendanceType(Attendance.AttendanceType.STUDENT);
        newAttendance.setStudent(testStudent);
        
        Attendance saved = attendanceRepository.save(newAttendance);
        
        assertNotNull(saved.getId());
        assertEquals(Attendance.AttendanceStatus.PRESENT, saved.getStatus());
    }
    
    @Test
    void testFindByAttendanceDate() {
        List<Attendance> found = attendanceRepository.findByAttendanceDate(LocalDate.now());
        
        assertThat(found).hasSize(1);
        assertEquals(Attendance.AttendanceStatus.PRESENT, found.get(0).getStatus());
    }
    
    @Test
    void testFindByAttendanceDateBetween() {
        List<Attendance> found = attendanceRepository.findByAttendanceDateBetween(
            LocalDate.now().minusDays(5), LocalDate.now());
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(3);
    }
    
    @Test
    void testFindByStudentIdAndAttendanceDate() {
        List<Attendance> found = attendanceRepository.findByStudentIdAndAttendanceDate(
            testStudent.getId(), LocalDate.now());
        
        assertThat(found).hasSize(1);
        assertEquals(Attendance.AttendanceStatus.PRESENT, found.get(0).getStatus());
    }
    
    @Test
    void testFindByStudentIdAndAttendanceDateBetween() {
        List<Attendance> found = attendanceRepository.findByStudentIdAndAttendanceDateBetween(
            testStudent.getId(), LocalDate.now().minusDays(5), LocalDate.now());
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(3);
    }
    
    @Test
    void testFindStudentAttendanceByDate() {
        List<Attendance> found = attendanceRepository.findStudentAttendanceByDate(LocalDate.now());
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(1);
    }
    
    @Test
    void testFindStudentAttendanceHistory() {
        List<Attendance> history = attendanceRepository.findStudentAttendanceHistory(
            testStudent.getId(), LocalDate.now().minusDays(5), LocalDate.now());
        
        assertThat(history).hasSizeGreaterThanOrEqualTo(3);
    }
    
    @Test
    void testCountStudentPresentDays() {
        Long count = attendanceRepository.countStudentPresentDays(
            testStudent.getId(), LocalDate.now().minusDays(5), LocalDate.now());
        
        assertEquals(1L, count);
    }
    
    @Test
    void testCountStudentAbsentDays() {
        Long count = attendanceRepository.countStudentAbsentDays(
            testStudent.getId(), LocalDate.now().minusDays(5), LocalDate.now());
        
        assertEquals(1L, count);
    }
    
    @Test
    void testCountPresentStudentsByDate() {
        Long count = attendanceRepository.countPresentStudentsByDate(LocalDate.now());
        
        assertThat(count).isGreaterThanOrEqualTo(1L);
    }
    
    @Test
    void testCountAbsentStudentsByDate() {
        Long count = attendanceRepository.countAbsentStudentsByDate(LocalDate.now().minusDays(1));
        
        assertThat(count).isGreaterThanOrEqualTo(1L);
    }
    
    @Test
    void testCountLateStudentsByDate() {
        Long count = attendanceRepository.countLateStudentsByDate(LocalDate.now().minusDays(2));
        
        assertThat(count).isGreaterThanOrEqualTo(1L);
    }
    
    @Test
    void testFindLateOrEarlyDepartures() {
        List<Attendance> found = attendanceRepository.findLateOrEarlyDepartures(
            LocalDate.now().minusDays(5), LocalDate.now());
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(1);
    }
    
    @Test
    void testUpdateAttendance() {
        Attendance attendance = attendanceRepository.findByAttendanceDate(LocalDate.now()).get(0);
        attendance.setCheckOutTime(LocalTime.of(15, 30));
        
        Attendance updated = attendanceRepository.save(attendance);
        
        assertEquals(LocalTime.of(15, 30), updated.getCheckOutTime());
    }
    
    @Test
    void testDeleteAttendance() {
        Long initialCount = attendanceRepository.count();
        
        attendanceRepository.delete(testAttendance1);
        
        Long afterDeleteCount = attendanceRepository.count();
        assertEquals(initialCount - 1, afterDeleteCount);
    }
    
    @Test
    void testFindAll() {
        List<Attendance> allRecords = attendanceRepository.findAll();
        
        assertThat(allRecords).hasSizeGreaterThanOrEqualTo(3);
    }
    
    @Test
    void testFindByStudentIdAndIsActive() {
        List<Attendance> studentRecords = attendanceRepository.findByStudentIdAndIsActive(
            testStudent.getId(), 1);
        
        assertThat(studentRecords).hasSizeGreaterThanOrEqualTo(3);
    }
    
    @Test
    void testFindAllActive() {
        List<Attendance> activeRecords = attendanceRepository.findAllActive();
        
        assertThat(activeRecords).hasSizeGreaterThanOrEqualTo(3);
    }
}
