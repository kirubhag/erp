package krs.erp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class StudentPromotionResponse {

    private Long batchId;
    private String batchName;
    private String academicYearFrom;
    private String academicYearTo;
    private LocalDate promotionDate;
    private String status;
    private Integer totalStudents;
    private Integer successfulPromotions;
    private Integer failedPromotions;
    private Integer processedStudents;
    private Double progressPercentage;
    private String currentPhase;
    private Double successRate;
    private String notes;
    private Long initiatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime startedAt;
    private List<PromotionRecordSummary> records;

    public static class PromotionRecordSummary {
        private Long recordId;
        private Long studentId;
        private String studentName;
        private String fromGradeLevel;
        private String toGradeLevel;
        private String fromSection;
        private String toSection;
        private String status;
        private String failureReason;
        private LocalDateTime promotedAt;

        // Getters and Setters
        public Long getRecordId() {
            return recordId;
        }

        public void setRecordId(Long recordId) {
            this.recordId = recordId;
        }

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

        public String getFromGradeLevel() {
            return fromGradeLevel;
        }

        public void setFromGradeLevel(String fromGradeLevel) {
            this.fromGradeLevel = fromGradeLevel;
        }

        public String getToGradeLevel() {
            return toGradeLevel;
        }

        public void setToGradeLevel(String toGradeLevel) {
            this.toGradeLevel = toGradeLevel;
        }

        public String getFromSection() {
            return fromSection;
        }

        public void setFromSection(String fromSection) {
            this.fromSection = fromSection;
        }

        public String getToSection() {
            return toSection;
        }

        public void setToSection(String toSection) {
            this.toSection = toSection;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFailureReason() {
            return failureReason;
        }

        public void setFailureReason(String failureReason) {
            this.failureReason = failureReason;
        }

        public LocalDateTime getPromotedAt() {
            return promotedAt;
        }

        public void setPromotedAt(LocalDateTime promotedAt) {
            this.promotedAt = promotedAt;
        }
    }

    // Getters and Setters
    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Integer getSuccessfulPromotions() {
        return successfulPromotions;
    }

    public void setSuccessfulPromotions(Integer successfulPromotions) {
        this.successfulPromotions = successfulPromotions;
    }

    public Integer getFailedPromotions() {
        return failedPromotions;
    }

    public void setFailedPromotions(Integer failedPromotions) {
        this.failedPromotions = failedPromotions;
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<PromotionRecordSummary> getRecords() {
        return records;
    }

    public void setRecords(List<PromotionRecordSummary> records) {
        this.records = records;
    }

    public Integer getProcessedStudents() {
        return processedStudents;
    }

    public void setProcessedStudents(Integer processedStudents) {
        this.processedStudents = processedStudents;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
    }

    public Long getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(Long initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
}
