package krs.erp.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "subjects")
@AttributeOverride(name = "id", column = @Column(name = "subject_id"))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Subject extends BaseEntity {

    @NotBlank(message = "Subject code is required")
    @Size(max = 20, message = "Subject code must not exceed 20 characters")
    @Column(name = "subject_code", nullable = false, unique = true, length = 20)
    private String subjectCode;

    @NotBlank(message = "Subject name is required")
    @Size(max = 100, message = "Subject name must not exceed 100 characters")
    @Column(name = "subject_name", nullable = false, length = 100)
    private String subjectName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Grade level is required")
    @Size(max = 50, message = "Grade level must not exceed 50 characters")
    @Column(name = "grade_level", nullable = false, length = 50)
    private String gradeLevel;

    @Column(name = "category", length = 50)
    private String category; // e.g., Mathematics, Science, Language, Arts, etc.

    @Column(name = "credits")
    private Integer credits;

    @Column(name = "hours_per_week")
    private Integer hoursPerWeek;

    @Column(name = "prerequisites", length = 200)
    private String prerequisites;

    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel; // Easy, Medium, Hard

    @Column(name = "is_mandatory")
    private Boolean isMandatory = true;

    // Constructors
    public Subject() {
        super();
    }

    public Subject(String subjectCode, String subjectName, String gradeLevel) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.gradeLevel = gradeLevel;
        markAsActive(); // Use BaseEntity method
    }

    // Getters and Setters
    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Integer getHoursPerWeek() {
        return hoursPerWeek;
    }

    public void setHoursPerWeek(Integer hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(subjectCode, subject.subjectCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectCode);
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + getId() +
                ", subjectCode='" + subjectCode + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", gradeLevel='" + gradeLevel + '\'' +
                ", category='" + category + '\'' +
                ", isActive=" + getIsActive() +
                '}';
    }
}
