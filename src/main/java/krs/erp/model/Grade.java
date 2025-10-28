package krs.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Grade entity representing student academic grades/marks
 */
@Entity
@Table(name = "grades")
public class Grade extends BaseEntity {

    @NotNull(message = "Student ID is required")
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @NotBlank(message = "Student name is required")
    @Size(min = 2, max = 200, message = "Student name must be between 2 and 200 characters")
    @Column(name = "student_name", nullable = false, length = 200)
    private String studentName;

    @NotBlank(message = "Grade level is required")
    @Size(max = 50, message = "Grade level must not exceed 50 characters")
    @Column(name = "grade_level", nullable = false, length = 50)
    private String gradeLevel;

    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code must not exceed 20 characters")
    @Column(name = "course_code", nullable = false, length = 20)
    private String courseCode;

    @NotBlank(message = "Course name is required")
    @Size(max = 200, message = "Course name must not exceed 200 characters")
    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName;

    @NotBlank(message = "Exam type is required")
    @Size(max = 50, message = "Exam type must not exceed 50 characters")
    @Column(name = "exam_type", nullable = false, length = 50)
    private String examType;

    @NotNull(message = "Marks obtained is required")
    @Min(value = 0, message = "Marks obtained must be at least 0")
    @Column(name = "marks_obtained", nullable = false, precision = 5, scale = 2)
    private BigDecimal marksObtained;

    @NotNull(message = "Total marks is required")
    @Min(value = 1, message = "Total marks must be at least 1")
    @Column(name = "total_marks", nullable = false, precision = 5, scale = 2)
    private BigDecimal totalMarks;

    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    @Size(max = 5, message = "Letter grade must not exceed 5 characters")
    @Column(name = "letter_grade", length = 5)
    private String letterGrade;

    @DecimalMin(value = "0.0", message = "Grade point must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Grade point must not exceed 10.0")
    @Column(name = "grade_point", precision = 4, scale = 2)
    private BigDecimal gradePoint;

    @NotNull(message = "Exam date is required")
    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @NotBlank(message = "Semester is required")
    @Size(max = 20, message = "Semester must not exceed 20 characters")
    @Column(name = "semester", nullable = false, length = 20)
    private String semester;

    @NotBlank(message = "Academic year is required")
    @Size(max = 20, message = "Academic year must not exceed 20 characters")
    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "teacher_id", length = 50)
    private String teacherId;

    @Size(max = 200, message = "Teacher name must not exceed 200 characters")
    @Column(name = "teacher_name", length = 200)
    private String teacherName;

    @Column(name = "organization_id")
    private Long organizationId;

    // Constructors
    public Grade() {
        super();
    }

    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public BigDecimal getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(BigDecimal marksObtained) {
        this.marksObtained = marksObtained;
        calculatePercentage();
        calculateLetterGrade();
        calculateGradePoint();
    }

    public BigDecimal getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(BigDecimal totalMarks) {
        this.totalMarks = totalMarks;
        calculatePercentage();
        calculateLetterGrade();
        calculateGradePoint();
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }

    public BigDecimal getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(BigDecimal gradePoint) {
        this.gradePoint = gradePoint;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    // Helper methods
    private void calculatePercentage() {
        if (marksObtained != null && totalMarks != null && totalMarks.compareTo(BigDecimal.ZERO) > 0) {
            this.percentage = marksObtained
                .multiply(BigDecimal.valueOf(100))
                .divide(totalMarks, 2, java.math.RoundingMode.HALF_UP);
        }
    }

    private void calculateLetterGrade() {
        if (percentage != null) {
            if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) {
                this.letterGrade = "A+";
            } else if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
                this.letterGrade = "A";
            } else if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) {
                this.letterGrade = "B+";
            } else if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) {
                this.letterGrade = "B";
            } else if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) {
                this.letterGrade = "C";
            } else if (percentage.compareTo(BigDecimal.valueOf(40)) >= 0) {
                this.letterGrade = "D";
            } else {
                this.letterGrade = "F";
            }
        }
    }

    private void calculateGradePoint() {
        if (percentage != null) {
            if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) {
                this.gradePoint = BigDecimal.valueOf(10.0);
            } else if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
                this.gradePoint = BigDecimal.valueOf(9.0);
            } else if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) {
                this.gradePoint = BigDecimal.valueOf(8.0);
            } else if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) {
                this.gradePoint = BigDecimal.valueOf(7.0);
            } else if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) {
                this.gradePoint = BigDecimal.valueOf(6.0);
            } else if (percentage.compareTo(BigDecimal.valueOf(40)) >= 0) {
                this.gradePoint = BigDecimal.valueOf(5.0);
            } else {
                this.gradePoint = BigDecimal.valueOf(0.0);
            }
        }
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + getId() +
                ", studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", gradeLevel='" + gradeLevel + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", examType='" + examType + '\'' +
                ", marksObtained=" + marksObtained +
                ", totalMarks=" + totalMarks +
                ", percentage=" + percentage +
                ", letterGrade='" + letterGrade + '\'' +
                ", gradePoint=" + gradePoint +
                ", examDate=" + examDate +
                ", semester='" + semester + '\'' +
                ", academicYear='" + academicYear + '\'' +
                ", isActive=" + getIsActive() +
                '}';
    }
}
