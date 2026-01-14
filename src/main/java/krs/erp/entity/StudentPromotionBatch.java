package krs.erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "erp_student_promotion_batch")
public class StudentPromotionBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    @NotBlank(message = "Batch name is required")
    @Column(name = "batch_name", nullable = false, length = 200)
    private String batchName;

    @NotBlank(message = "Academic year from is required")
    @Column(name = "academic_year_from", nullable = false, length = 20)
    private String academicYearFrom;

    @NotBlank(message = "Academic year to is required")
    @Column(name = "academic_year_to", nullable = false, length = 20)
    private String academicYearTo;

    @NotNull(message = "Promotion date is required")
    @Column(name = "promotion_date", nullable = false)
    private LocalDate promotionDate;

    @NotNull(message = "Initiated by user ID is required")
    @Column(name = "initiated_by", nullable = false)
    private Long initiatedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PromotionBatchStatus status = PromotionBatchStatus.PENDING;

    @Column(name = "total_students")
    private Integer totalStudents = 0;

    @Column(name = "successful_promotions")
    private Integer successfulPromotions = 0;

    @Column(name = "failed_promotions")
    private Integer failedPromotions = 0;

    @Column(name = "processed_students")
    private Integer processedStudents = 0;

    @Column(name = "progress_percentage")
    private Double progressPercentage = 0.0;

    @Column(name = "current_phase", length = 50)
    private String currentPhase;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<StudentPromotionRecord> promotionRecords = new ArrayList<>();

    public enum PromotionBatchStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED, ROLLED_BACK
    }

    // Constructors
    public StudentPromotionBatch() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public Long getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(Long initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public PromotionBatchStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionBatchStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
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

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<StudentPromotionRecord> getPromotionRecords() {
        return promotionRecords;
    }

    public void setPromotionRecords(List<StudentPromotionRecord> promotionRecords) {
        this.promotionRecords = promotionRecords;
    }

    // Helper methods
    public void incrementSuccessful() {
        this.successfulPromotions++;
        this.processedStudents++;
        updateProgress();
    }

    public void incrementFailed() {
        this.failedPromotions++;
        this.processedStudents++;
        updateProgress();
    }

    public void updateProgress() {
        if (totalStudents != null && totalStudents > 0) {
            this.progressPercentage = (processedStudents * 100.0) / totalStudents;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public double getSuccessRate() {
        if (totalStudents == null || totalStudents == 0) {
            return 0.0;
        }
        return (successfulPromotions * 100.0) / totalStudents;
    }
}
