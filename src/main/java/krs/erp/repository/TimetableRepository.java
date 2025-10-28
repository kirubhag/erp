package krs.erp.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.Timetable;
import krs.erp.model.Timetable.DayOfWeek;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    // Find by timetable code
    Optional<Timetable> findByTimetableCode(String timetableCode);

    // Check if timetable code exists
    boolean existsByTimetableCode(String timetableCode);

    // Find by grade level
    List<Timetable> findByGradeLevel(String gradeLevel);

    // Find by grade level and active status
    List<Timetable> findByGradeLevelAndIsActive(String gradeLevel, Integer isActive);

    // Find by class name
    List<Timetable> findByClassName(String className);

    // Find by class name and active status
    List<Timetable> findByClassNameAndIsActive(String className, Integer isActive);

    // Find by day of week
    List<Timetable> findByDayOfWeek(DayOfWeek dayOfWeek);

    // Find by day of week and active status
    List<Timetable> findByDayOfWeekAndIsActive(DayOfWeek dayOfWeek, Integer isActive);

    // Find by academic year
    List<Timetable> findByAcademicYear(String academicYear);

    // Find by academic year and active status
    List<Timetable> findByAcademicYearAndIsActive(String academicYear, Integer isActive);

    // Find by subject code
    List<Timetable> findBySubjectCode(String subjectCode);

    // Find by subject code and active status
    List<Timetable> findBySubjectCodeAndIsActive(String subjectCode, Integer isActive);

    // Find by teacher ID
    List<Timetable> findByTeacherId(String teacherId);

    // Find by teacher ID and active status
    List<Timetable> findByTeacherIdAndIsActive(String teacherId, Integer isActive);

    // Find by room number
    List<Timetable> findByRoomNumber(String roomNumber);

    // Find by room number and active status
    List<Timetable> findByRoomNumberAndIsActive(String roomNumber, Integer isActive);

    // Find by active status
    List<Timetable> findByIsActive(Integer isActive);

    // Find all active timetables
    @Query("SELECT t FROM Timetable t WHERE t.isActive = 1 ORDER BY t.dayOfWeek, t.startTime")
    List<Timetable> findAllActiveTimetables();

    // Find timetables by grade level and day
    @Query("SELECT t FROM Timetable t WHERE t.gradeLevel = :gradeLevel AND t.dayOfWeek = :dayOfWeek AND t.isActive = 1 ORDER BY t.startTime")
    List<Timetable> findByGradeLevelAndDay(@Param("gradeLevel") String gradeLevel, @Param("dayOfWeek") DayOfWeek dayOfWeek);

    // Find timetables by class and day
    @Query("SELECT t FROM Timetable t WHERE t.className = :className AND t.dayOfWeek = :dayOfWeek AND t.isActive = 1 ORDER BY t.startTime")
    List<Timetable> findByClassAndDay(@Param("className") String className, @Param("dayOfWeek") DayOfWeek dayOfWeek);

    // Search timetables
    @Query("SELECT t FROM Timetable t WHERE (LOWER(t.timetableCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(t.className) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(t.gradeLevel) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(t.subjectName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(t.teacherName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND t.isActive = 1")
    List<Timetable> searchTimetables(@Param("search") String search);

    // Count by grade level and active status
    @Query("SELECT COUNT(t) FROM Timetable t WHERE t.gradeLevel = :gradeLevel AND t.isActive = 1")
    long countByGradeLevelAndActive(@Param("gradeLevel") String gradeLevel);

    // Find conflicting timetables (same room, day, overlapping time)
    @Query("SELECT t FROM Timetable t WHERE t.roomNumber = :roomNumber " +
           "AND t.dayOfWeek = :dayOfWeek " +
           "AND t.isActive = 1 " +
           "AND ((t.startTime < :endTime AND t.endTime > :startTime))")
    List<Timetable> findConflictingTimetables(@Param("roomNumber") String roomNumber,
                                               @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                               @Param("startTime") LocalTime startTime,
                                               @Param("endTime") LocalTime endTime);

    // Find by class, academic year and semester
    @Query("SELECT t FROM Timetable t WHERE t.className = :className " +
           "AND t.academicYear = :academicYear " +
           "AND (:semester IS NULL OR t.semester = :semester) " +
           "AND t.isActive = 1 " +
           "ORDER BY t.dayOfWeek, t.startTime")
    List<Timetable> findByClassAndAcademicPeriod(@Param("className") String className,
                                                   @Param("academicYear") String academicYear,
                                                   @Param("semester") String semester);
}
