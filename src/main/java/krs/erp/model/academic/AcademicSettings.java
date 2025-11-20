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
 * AcademicSettings Entity - Stores academic-related settings for an organization
 */
@Entity
@Table(name = "academic_settings")
public class AcademicSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "organization_id", nullable = false, unique = true)
    private Long organizationId;
    
    // Attendance Settings
    @Column(name = "enable_attendance_tracking")
    private Boolean enableAttendanceTracking = true;
    
    @Column(name = "attendance_calculation_method", length = 20)
    private String attendanceCalculationMethod = "percentage";
    
    @Column(name = "minimum_attendance_percentage")
    private Double minimumAttendancePercentage = 75.0;
    
    @Column(name = "allow_late_marking")
    private Boolean allowLateMarking = true;
    
    @Column(name = "late_marking_cutoff_minutes")
    private Integer lateMarkingCutoffMinutes = 30;
    
    @Column(name = "enable_biometric_integration")
    private Boolean enableBiometricIntegration = false;
    
    // Exam Settings
    @Column(name = "default_exam_duration")
    private Integer defaultExamDuration = 60;
    
    @Column(name = "allow_makeup_exams")
    private Boolean allowMakeupExams = true;
    
    @Column(name = "makeup_exam_deadline_days")
    private Integer makeupExamDeadlineDays = 7;
    
    @Column(name = "passing_percentage")
    private Double passingPercentage = 40.0;
    
    @Column(name = "enable_grade_moderation")
    private Boolean enableGradeModeration = false;
    
    @Column(name = "auto_calculate_grades")
    private Boolean autoCalculateGrades = true;
    
    @Column(name = "publish_results_immediately")
    private Boolean publishResultsImmediately = false;
    
    // Promotion Rules
    @Column(name = "auto_promote_students")
    private Boolean autoPromoteStudents = false;
    
    @Column(name = "minimum_attendance_for_promotion")
    private Double minimumAttendanceForPromotion = 75.0;
    
    @Column(name = "minimum_grade_for_promotion")
    private Double minimumGradeForPromotion = 40.0;
    
    @Column(name = "allow_grace_marks")
    private Boolean allowGraceMarks = true;
    
    @Column(name = "grace_marks_limit")
    private Double graceMarksLimit = 5.0;
    
    @Column(name = "require_all_subjects_pass")
    private Boolean requireAllSubjectsPass = true;
    
    @Column(name = "allow_compartment_exams")
    private Boolean allowCompartmentExams = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public AcademicSettings() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public AcademicSettings(Long organizationId) {
        this.organizationId = organizationId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters (truncated for brevity - add all getters/setters)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
    
    public Boolean getEnableAttendanceTracking() { return enableAttendanceTracking; }
    public void setEnableAttendanceTracking(Boolean enableAttendanceTracking) { 
        this.enableAttendanceTracking = enableAttendanceTracking; 
    }
    
    public String getAttendanceCalculationMethod() { return attendanceCalculationMethod; }
    public void setAttendanceCalculationMethod(String attendanceCalculationMethod) { 
        this.attendanceCalculationMethod = attendanceCalculationMethod; 
    }
    
    public Double getMinimumAttendancePercentage() { return minimumAttendancePercentage; }
    public void setMinimumAttendancePercentage(Double minimumAttendancePercentage) { 
        this.minimumAttendancePercentage = minimumAttendancePercentage; 
    }
    
    public Boolean getAllowLateMarking() { return allowLateMarking; }
    public void setAllowLateMarking(Boolean allowLateMarking) { this.allowLateMarking = allowLateMarking; }
    
    public Integer getLateMarkingCutoffMinutes() { return lateMarkingCutoffMinutes; }
    public void setLateMarkingCutoffMinutes(Integer lateMarkingCutoffMinutes) { 
        this.lateMarkingCutoffMinutes = lateMarkingCutoffMinutes; 
    }
    
    public Boolean getEnableBiometricIntegration() { return enableBiometricIntegration; }
    public void setEnableBiometricIntegration(Boolean enableBiometricIntegration) { 
        this.enableBiometricIntegration = enableBiometricIntegration; 
    }
    
    public Integer getDefaultExamDuration() { return defaultExamDuration; }
    public void setDefaultExamDuration(Integer defaultExamDuration) { 
        this.defaultExamDuration = defaultExamDuration; 
    }
    
    public Boolean getAllowMakeupExams() { return allowMakeupExams; }
    public void setAllowMakeupExams(Boolean allowMakeupExams) { this.allowMakeupExams = allowMakeupExams; }
    
    public Integer getMakeupExamDeadlineDays() { return makeupExamDeadlineDays; }
    public void setMakeupExamDeadlineDays(Integer makeupExamDeadlineDays) { 
        this.makeupExamDeadlineDays = makeupExamDeadlineDays; 
    }
    
    public Double getPassingPercentage() { return passingPercentage; }
    public void setPassingPercentage(Double passingPercentage) { this.passingPercentage = passingPercentage; }
    
    public Boolean getEnableGradeModeration() { return enableGradeModeration; }
    public void setEnableGradeModeration(Boolean enableGradeModeration) { 
        this.enableGradeModeration = enableGradeModeration; 
    }
    
    public Boolean getAutoCalculateGrades() { return autoCalculateGrades; }
    public void setAutoCalculateGrades(Boolean autoCalculateGrades) { 
        this.autoCalculateGrades = autoCalculateGrades; 
    }
    
    public Boolean getPublishResultsImmediately() { return publishResultsImmediately; }
    public void setPublishResultsImmediately(Boolean publishResultsImmediately) { 
        this.publishResultsImmediately = publishResultsImmediately; 
    }
    
    public Boolean getAutoPromoteStudents() { return autoPromoteStudents; }
    public void setAutoPromoteStudents(Boolean autoPromoteStudents) { 
        this.autoPromoteStudents = autoPromoteStudents; 
    }
    
    public Double getMinimumAttendanceForPromotion() { return minimumAttendanceForPromotion; }
    public void setMinimumAttendanceForPromotion(Double minimumAttendanceForPromotion) { 
        this.minimumAttendanceForPromotion = minimumAttendanceForPromotion; 
    }
    
    public Double getMinimumGradeForPromotion() { return minimumGradeForPromotion; }
    public void setMinimumGradeForPromotion(Double minimumGradeForPromotion) { 
        this.minimumGradeForPromotion = minimumGradeForPromotion; 
    }
    
    public Boolean getAllowGraceMarks() { return allowGraceMarks; }
    public void setAllowGraceMarks(Boolean allowGraceMarks) { this.allowGraceMarks = allowGraceMarks; }
    
    public Double getGraceMarksLimit() { return graceMarksLimit; }
    public void setGraceMarksLimit(Double graceMarksLimit) { this.graceMarksLimit = graceMarksLimit; }
    
    public Boolean getRequireAllSubjectsPass() { return requireAllSubjectsPass; }
    public void setRequireAllSubjectsPass(Boolean requireAllSubjectsPass) { 
        this.requireAllSubjectsPass = requireAllSubjectsPass; 
    }
    
    public Boolean getAllowCompartmentExams() { return allowCompartmentExams; }
    public void setAllowCompartmentExams(Boolean allowCompartmentExams) { 
        this.allowCompartmentExams = allowCompartmentExams; 
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
