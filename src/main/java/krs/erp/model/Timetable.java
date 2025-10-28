package krs.erp.model;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "timetables")
public class Timetable extends BaseEntity {

    @NotBlank(message = "Timetable code is required")
    @Column(name = "timetable_code", unique = true, nullable = false, length = 50)
    private String timetableCode;

    @NotBlank(message = "Class name is required")
    @Size(min = 2, max = 100, message = "Class name must be between 2 and 100 characters")
    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    @NotBlank(message = "Grade level is required")
    @Column(name = "grade_level", nullable = false, length = 50)
    private String gradeLevel;

    @NotBlank(message = "Academic year is required")
    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "semester", length = 20)
    private String semester;

    @NotNull(message = "Day of week is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotBlank(message = "Subject is required")
    @Column(name = "subject_name", nullable = false, length = 100)
    private String subjectName;

    @Column(name = "subject_code", length = 50)
    private String subjectCode;

    @Column(name = "teacher_name", length = 100)
    private String teacherName;

    @Column(name = "teacher_id", length = 50)
    private String teacherId;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Column(name = "building", length = 50)
    private String building;

    @Column(name = "period_number")
    private Integer periodNumber;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "is_lab_session")
    private Boolean isLabSession = false;

    // Enums
    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    // Constructors
    public Timetable() {
    }

    public Timetable(String timetableCode, String className, String gradeLevel, String academicYear, 
                     DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, String subjectName) {
        this.timetableCode = timetableCode;
        this.className = className;
        this.gradeLevel = gradeLevel;
        this.academicYear = academicYear;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subjectName = subjectName;
    }

    // Getters and Setters
    public String getTimetableCode() {
        return timetableCode;
    }

    public void setTimetableCode(String timetableCode) {
        this.timetableCode = timetableCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Integer getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(Integer periodNumber) {
        this.periodNumber = periodNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsLabSession() {
        return isLabSession;
    }

    public void setIsLabSession(Boolean isLabSession) {
        this.isLabSession = isLabSession;
    }

    @Override
    public String toString() {
        return "Timetable{" +
                "id=" + getId() +
                ", timetableCode='" + timetableCode + '\'' +
                ", className='" + className + '\'' +
                ", gradeLevel='" + gradeLevel + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", subjectName='" + subjectName + '\'' +
                '}';
    }
}
