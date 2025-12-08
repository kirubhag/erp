package krs.erp.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ErpClass entity representing academic classes/sections
 * Manages associations with Students, Teachers (Staff), and Subjects
 */
@Entity
@Table(name = "erp_class")
@AttributeOverride(name = "id", column = @Column(name = "class_id"))
public class ErpClass extends BaseEntity {

    @NotBlank(message = "Class code is required")
    @Size(max = 50, message = "Class code must not exceed 50 characters")
    @Column(name = "class_code", nullable = false, unique = true, length = 50)
    private String classCode;

    @NotBlank(message = "Class name is required")
    @Size(max = 100, message = "Class name must not exceed 100 characters")
    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    @NotBlank(message = "Grade level is required")
    @Size(max = 50, message = "Grade level must not exceed 50 characters")
    @Column(name = "grade_level", nullable = false, length = 50)
    private String gradeLevel;

    @Size(max = 20, message = "Section must not exceed 20 characters")
    @Column(name = "section", length = 20)
    private String section;

    @NotBlank(message = "Academic year is required")
    @Size(max = 20, message = "Academic year must not exceed 20 characters")
    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(name = "capacity")
    private Integer capacity = 40;

    @Size(max = 20, message = "Room number must not exceed 20 characters")
    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_teacher_id")
    private Staff classTeacher;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "organization_id")
    private Long organizationId;

    // Many-to-Many relationships
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "erp_class_student", joinColumns = @JoinColumn(name = "class_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<Student> students = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "erp_class_teacher", joinColumns = @JoinColumn(name = "class_id"), inverseJoinColumns = @JoinColumn(name = "teacher_id"))
    private Set<Staff> teachers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "erp_class_subject", joinColumns = @JoinColumn(name = "class_id"), inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subject> subjects = new HashSet<>();

    // Constructors
    public ErpClass() {
        super();
    }

    public ErpClass(String classCode, String className, String gradeLevel, String academicYear) {
        this.classCode = classCode;
        this.className = className;
        this.gradeLevel = gradeLevel;
        this.academicYear = academicYear;
        markAsActive();
    }

    // Getters and Setters
    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
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

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Staff getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(Staff classTeacher) {
        this.classTeacher = classTeacher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public Set<Staff> getTeachers() {
        return teachers;
    }

    public void setTeachers(Set<Staff> teachers) {
        this.teachers = teachers;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    // Helper methods for managing associations
    public void addStudent(Student student) {
        this.students.add(student);
    }

    public void removeStudent(Student student) {
        this.students.remove(student);
    }

    public void addTeacher(Staff teacher) {
        this.teachers.add(teacher);
    }

    public void removeTeacher(Staff teacher) {
        this.teachers.remove(teacher);
    }

    public void addSubject(Subject subject) {
        this.subjects.add(subject);
    }

    public void removeSubject(Subject subject) {
        this.subjects.remove(subject);
    }

    // Convenience methods
    public int getStudentCount() {
        return students != null ? students.size() : 0;
    }

    public int getTeacherCount() {
        return teachers != null ? teachers.size() : 0;
    }

    public int getSubjectCount() {
        return subjects != null ? subjects.size() : 0;
    }

    public boolean isFull() {
        return capacity != null && getStudentCount() >= capacity;
    }

    public int getAvailableSeats() {
        return capacity != null ? Math.max(0, capacity - getStudentCount()) : 0;
    }

    public Long getClassTeacherId() {
        return classTeacher != null ? classTeacher.getId() : null;
    }

    public String getClassTeacherName() {
        if (classTeacher != null) {
            return classTeacher.getFirstName() + " " + classTeacher.getLastName();
        }
        return null;
    }

    @Override
    public String toString() {
        return "ErpClass{" +
                "id=" + getId() +
                ", classCode='" + classCode + '\'' +
                ", className='" + className + '\'' +
                ", gradeLevel='" + gradeLevel + '\'' +
                ", section='" + section + '\'' +
                ", academicYear='" + academicYear + '\'' +
                ", capacity=" + capacity +
                ", roomNumber='" + roomNumber + '\'' +
                ", studentCount=" + getStudentCount() +
                ", teacherCount=" + getTeacherCount() +
                ", subjectCount=" + getSubjectCount() +
                ", isActive=" + getIsActive() +
                '}';
    }
}
