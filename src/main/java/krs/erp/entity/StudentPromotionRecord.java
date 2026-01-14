package krs.erp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "erp_student_promotion_record")
public class StudentPromotionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private StudentPromotionBatch batch;

    @NotNull(message = "Student ID is required")
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @NotBlank(message = "From grade level is required")
    @Column(name = "from_grade_level", nullable = false, length = 20)
    private String fromGradeLevel;

    @NotBlank(message = "To grade level is required")
    @Column(name = "to_grade_level", nullable = false, length = 20)
    private String toGradeLevel;

    @Column(name = "from_section", length = 20)
    private String fromSection;

    @Column(name = "to_section", length = 20)
    private String toSection;

    @Enumerated(EnumType.STRING)
    @Column(name = "promotion_status", nullable = false)
    private PromotionStatus promotionStatus = PromotionStatus.PENDING;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "promoted_at")
    private LocalDateTime promotedAt;

    @Column(name = "rolled_back_at")
    private LocalDateTime rolledBackAt;

    @Column(name = "student_snapshot", columnDefinition = "JSON")
    private String studentSnapshot;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PromotionStatus {
        SUCCESS, FAILED, ROLLED_BACK, PENDING
    }

    // Constructors
    public StudentPromotionRecord() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public StudentPromotionBatch getBatch() {
        return batch;
    }

    public void setBatch(StudentPromotionBatch batch) {
        this.batch = batch;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public PromotionStatus getPromotionStatus() {
        return promotionStatus;
    }

    public void setPromotionStatus(PromotionStatus promotionStatus) {
        this.promotionStatus = promotionStatus;
        this.updatedAt = LocalDateTime.now();
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

    public LocalDateTime getRolledBackAt() {
        return rolledBackAt;
    }

    public void setRolledBackAt(LocalDateTime rolledBackAt) {
        this.rolledBackAt = rolledBackAt;
    }

    public String getStudentSnapshot() {
        return studentSnapshot;
    }

    public void setStudentSnapshot(String studentSnapshot) {
        this.studentSnapshot = studentSnapshot;
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
}
