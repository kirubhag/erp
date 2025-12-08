package krs.erp.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.ErpClass;
import krs.erp.model.Room;
import krs.erp.model.Staff;
import krs.erp.model.Timetable;
import krs.erp.model.Timetable.DayOfWeek;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

       // Find by grade level
       @Query("SELECT t FROM Timetable t WHERE t.erpClass.gradeLevel = :gradeLevel")
       List<Timetable> findByGradeLevel(@Param("gradeLevel") String gradeLevel);

       // Find by grade level and active status
       @Query("SELECT t FROM Timetable t WHERE t.erpClass.gradeLevel = :gradeLevel AND t.isActive = :isActive")
       List<Timetable> findByGradeLevelAndIsActive(@Param("gradeLevel") String gradeLevel,
                     @Param("isActive") Integer isActive);

       // Find by class name
       @Query("SELECT t FROM Timetable t WHERE t.erpClass.className = :className")
       List<Timetable> findByClassName(@Param("className") String className);

       // Find by class name and active status
       @Query("SELECT t FROM Timetable t WHERE t.erpClass.className = :className AND t.isActive = :isActive")
       List<Timetable> findByClassNameAndIsActive(@Param("className") String className,
                     @Param("isActive") Integer isActive);

       // Find by day of week
       List<Timetable> findByDayOfWeek(DayOfWeek dayOfWeek);

       // Find by day of week and active status
       List<Timetable> findByDayOfWeekAndIsActive(DayOfWeek dayOfWeek, Integer isActive);

       // Find by academic year
       List<Timetable> findByAcademicYear(String academicYear);

       // Find by academic year and active status
       List<Timetable> findByAcademicYearAndIsActive(String academicYear, Integer isActive);

       // Find by subject code
       @Query("SELECT t FROM Timetable t WHERE t.subject.subjectCode = :subjectCode")
       List<Timetable> findBySubjectCode(@Param("subjectCode") String subjectCode);

       // Find by subject code and active status
       @Query("SELECT t FROM Timetable t WHERE t.subject.subjectCode = :subjectCode AND t.isActive = :isActive")
       List<Timetable> findBySubjectCodeAndIsActive(@Param("subjectCode") String subjectCode,
                     @Param("isActive") Integer isActive);

       // Find by teacher ID
       @Query("SELECT t FROM Timetable t WHERE t.teacher.staffId = :teacherId")
       List<Timetable> findByTeacherId(@Param("teacherId") String teacherId);

       // Find by teacher ID and active status
       @Query("SELECT t FROM Timetable t WHERE t.teacher.staffId = :teacherId AND t.isActive = :isActive")
       List<Timetable> findByTeacherIdAndIsActive(@Param("teacherId") String teacherId,
                     @Param("isActive") Integer isActive);

       // Find by room number
       @Query("SELECT t FROM Timetable t WHERE t.room.roomName = :roomNumber")
       List<Timetable> findByRoomNumber(@Param("roomNumber") String roomNumber);

       // Find by room number and active status
       @Query("SELECT t FROM Timetable t WHERE t.room.roomName = :roomNumber AND t.isActive = :isActive")
       List<Timetable> findByRoomNumberAndIsActive(@Param("roomNumber") String roomNumber,
                     @Param("isActive") Integer isActive);

       // Find by active status
       List<Timetable> findByIsActive(Integer isActive);

       // Find all active timetables
       @Query("SELECT t FROM Timetable t WHERE t.isActive = 1 ORDER BY t.dayOfWeek, t.startTime")
       List<Timetable> findAllActiveTimetables();

       // Find timetables by grade level and day
       @Query("SELECT t FROM Timetable t WHERE t.erpClass.gradeLevel = :gradeLevel AND t.dayOfWeek = :dayOfWeek AND t.isActive = 1 ORDER BY t.startTime")
       List<Timetable> findByGradeLevelAndDay(@Param("gradeLevel") String gradeLevel,
                     @Param("dayOfWeek") DayOfWeek dayOfWeek);

       // Find timetables by class and day
       @Query("SELECT t FROM Timetable t WHERE t.erpClass.className = :className AND t.dayOfWeek = :dayOfWeek AND t.isActive = 1 ORDER BY t.startTime")
       List<Timetable> findByClassAndDay(@Param("className") String className, @Param("dayOfWeek") DayOfWeek dayOfWeek);

       // Search timetables
       @Query("SELECT t FROM Timetable t WHERE (LOWER(t.erpClass.className) LIKE LOWER(CONCAT('%', :search, '%')) " +
                     "OR LOWER(t.erpClass.gradeLevel) LIKE LOWER(CONCAT('%', :search, '%')) " +
                     "OR LOWER(t.subject.subjectName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                     "OR LOWER(t.teacher.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                     "OR LOWER(t.teacher.lastName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
                     "AND t.isActive = 1")
       List<Timetable> searchTimetables(@Param("search") String search);

       // Count by grade level and active status
       @Query("SELECT COUNT(t) FROM Timetable t WHERE t.erpClass.gradeLevel = :gradeLevel AND t.isActive = 1")
       long countByGradeLevelAndActive(@Param("gradeLevel") String gradeLevel);

       // Find conflicting timetables (same room, day, overlapping time)
       @Query("SELECT t FROM Timetable t WHERE t.room.roomName = :roomNumber " +
                     "AND t.dayOfWeek = :dayOfWeek " +
                     "AND t.isActive = 1 " +
                     "AND ((t.startTime < :endTime AND t.endTime > :startTime))")
       List<Timetable> findConflictingTimetables(@Param("roomNumber") String roomNumber,
                     @Param("dayOfWeek") DayOfWeek dayOfWeek,
                     @Param("startTime") LocalTime startTime,
                     @Param("endTime") LocalTime endTime);

       // Find by class, academic year
       @Query("SELECT t FROM Timetable t WHERE t.erpClass.className = :className " +
                     "AND t.academicYear = :academicYear " +
                     "AND t.isActive = 1 " +
                     "ORDER BY t.dayOfWeek, t.startTime")
       List<Timetable> findByClassAndAcademicPeriod(@Param("className") String className,
                     @Param("academicYear") String academicYear);

       // Check existence/conflicts
       boolean existsByErpClassAndDayOfWeekAndPeriodNumberAndAcademicYear(ErpClass erpClass, DayOfWeek dayOfWeek,
                     int periodNumber, String academicYear);

       boolean existsByTeacherAndDayOfWeekAndPeriodNumberAndAcademicYear(Staff teacher, DayOfWeek dayOfWeek,
                     int periodNumber, String academicYear);

       boolean existsByRoomAndDayOfWeekAndPeriodNumberAndAcademicYear(Room room, DayOfWeek dayOfWeek, int periodNumber,
                     String academicYear);
}
