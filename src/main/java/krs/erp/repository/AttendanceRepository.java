package krs.erp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);
    
    List<Attendance> findByAttendanceDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Attendance> findByStudentIdAndAttendanceDate(Long studentId, LocalDate attendanceDate);
    
    List<Attendance> findByStaffIdAndAttendanceDate(Long staffId, LocalDate attendanceDate);
    
    List<Attendance> findByStudentIdAndAttendanceDateBetween(Long studentId, 
                                                           LocalDate startDate, 
                                                           LocalDate endDate);
    
    List<Attendance> findByStaffIdAndAttendanceDateBetween(Long staffId, 
                                                         LocalDate startDate, 
                                                         LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = :date AND a.attendanceType = 'STUDENT'")
    List<Attendance> findStudentAttendanceByDate(@Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = :date AND a.attendanceType = 'STAFF'")
    List<Attendance> findStaffAttendanceByDate(@Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId " +
           "AND a.attendanceDate BETWEEN :startDate AND :endDate ORDER BY a.attendanceDate DESC")
    List<Attendance> findStudentAttendanceHistory(@Param("studentId") Long studentId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId " +
           "AND a.status = 'PRESENT' AND a.attendanceDate BETWEEN :startDate AND :endDate")
    Long countStudentPresentDays(@Param("studentId") Long studentId,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId " +
           "AND a.status = 'ABSENT' AND a.attendanceDate BETWEEN :startDate AND :endDate")
    Long countStudentAbsentDays(@Param("studentId") Long studentId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.attendanceDate = :date " +
           "AND a.attendanceType = 'STUDENT' AND a.status = 'PRESENT'")
    Long countPresentStudentsByDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.attendanceDate = :date " +
           "AND a.attendanceType = 'STUDENT' AND a.status = 'ABSENT'")
    Long countAbsentStudentsByDate(@Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.student.gradeLevel = :gradeLevel " +
           "AND a.attendanceDate = :date ORDER BY a.student.firstName, a.student.lastName")
    List<Attendance> findAttendanceByGradeLevelAndDate(@Param("gradeLevel") krs.erp.model.Student.GradeLevel gradeLevel,
                                                      @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.status IN ('LATE', 'EARLY_DEPARTURE') " +
           "AND a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findLateOrEarlyDepartures(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}