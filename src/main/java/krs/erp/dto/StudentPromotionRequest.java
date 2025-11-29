package krs.erp.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class StudentPromotionRequest {

    @NotBlank(message = "Batch name is required")
    private String batchName;

    @NotBlank(message = "Academic year from is required")
    private String academicYearFrom;

    @NotBlank(message = "Academic year to is required")
    private String academicYearTo;

    @NotNull(message = "Promotion date is required")
    private LocalDate promotionDate;

    @NotEmpty(message = "At least one student must be selected")
    private List<StudentPromotionItem> students;

    private String notes;

    private Boolean autoAssignSections = false;

    // Nested class for individual student promotion
    public static class StudentPromotionItem {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotBlank(message = "Target grade level is required")
        private String toGradeLevel;

        private String toSection;

        // Getters and Setters
        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getToGradeLevel() {
            return toGradeLevel;
        }

        public void setToGradeLevel(String toGradeLevel) {
            this.toGradeLevel = toGradeLevel;
        }

        public String getToSection() {
            return toSection;
        }

        public void setToSection(String toSection) {
            this.toSection = toSection;
        }
    }

    // Getters and Setters
    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getAcademicYearFrom() {
        return academicYearFrom;
    }

    public void setAcademicYearFrom(String academicYearFrom) {
        this.academicYearFrom = academicYearFrom;
    }

    public String getAcademicYearTo() {
        return academicYearTo;
    }

    public void setAcademicYearTo(String academicYearTo) {
        this.academicYearTo = academicYearTo;
    }

    public LocalDate getPromotionDate() {
        return promotionDate;
    }

    public void setPromotionDate(LocalDate promotionDate) {
        this.promotionDate = promotionDate;
    }

    public List<StudentPromotionItem> getStudents() {
        return students;
    }

    public void setStudents(List<StudentPromotionItem> students) {
        this.students = students;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getAutoAssignSections() {
        return autoAssignSections;
    }

    public void setAutoAssignSections(Boolean autoAssignSections) {
        this.autoAssignSections = autoAssignSections;
    }
}
