package krs.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Grade entity representing student academic grades/marks
 * Normalized design using JPA relationships to Student, Subject, and Staff
 * entities
 */
@Entity
@Table(name = "erp_grade")
@AttributeOverride(name = "id", column = @Column(name = "grade_id"))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Grade extends BaseEntity {

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Student student;

    @NotNull(message = "Subject is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Staff teacher;

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

    @Column(name = "organization_id")
    private Long organizationId;

    // Constructors
    public Grade() {
        super();
    }

    // Getters and Setters
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Staff getTeacher() {
        return teacher;
    }

    public void setTeacher(Staff teacher) {
        this.teacher = teacher;
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

    // Convenience methods for accessing related entity data
    public Long getStudentId() {
        return student != null ? student.getId() : null;
    }

    public String getStudentName() {
        if (student != null) {
            return student.getFirstName() + " " + student.getLastName();
        }
        return null;
    }

    public String getGradeLevel() {
        return student != null && student.getGradeLevel() != null
                ? student.getGradeLevel().name()
                : null;
    }

    public Long getSubjectId() {
        return subject != null ? subject.getId() : null;
    }

    public String getSubjectCode() {
        return subject != null ? subject.getSubjectCode() : null;
    }

    public String getSubjectName() {
        return subject != null ? subject.getSubjectName() : null;
    }

    public Long getTeacherId() {
        return teacher != null ? teacher.getId() : null;
    }

    public String getTeacherName() {
        if (teacher != null) {
            return teacher.getFirstName() + " " + teacher.getLastName();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + getId() +
                ", studentId=" + getStudentId() +
                ", subjectId=" + getSubjectId() +
                ", teacherId=" + getTeacherId() +
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
