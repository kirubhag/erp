package krs.erp.model.academic;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * GradingScale Entity - Defines grading scales and grade points
 */
@Entity
@Table(name = "erp_grading_scales")
public class GradingScale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @Column(name = "letter_grade", nullable = false, length = 10)
    private String letterGrade;
    
    @Column(name = "min_percentage", nullable = false)
    private Double minPercentage;
    
    @Column(name = "max_percentage", nullable = false)
    private Double maxPercentage;
    
    @Column(name = "grade_point", nullable = false)
    private Double gradePoint;
    
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public GradingScale() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public GradingScale(String name, String letterGrade, Double minPercentage, Double maxPercentage, 
                       Double gradePoint, Long organizationId) {
        this.name = name;
        this.letterGrade = letterGrade;
        this.minPercentage = minPercentage;
        this.maxPercentage = maxPercentage;
        this.gradePoint = gradePoint;
        this.organizationId = organizationId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLetterGrade() {
        return letterGrade;
    }
    
    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }
    
    public Double getMinPercentage() {
        return minPercentage;
    }
    
    public void setMinPercentage(Double minPercentage) {
        this.minPercentage = minPercentage;
    }
    
    public Double getMaxPercentage() {
        return maxPercentage;
    }
    
    public void setMaxPercentage(Double maxPercentage) {
        this.maxPercentage = maxPercentage;
    }
    
    public Double getGradePoint() {
        return gradePoint;
    }
    
    public void setGradePoint(Double gradePoint) {
        this.gradePoint = gradePoint;
    }
    
    public Long getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
