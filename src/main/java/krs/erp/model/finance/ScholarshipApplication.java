package krs.erp.model.finance;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import krs.erp.model.Student;
import krs.erp.model.academic.AcademicYear;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Tracks a student's application for a scholarship.
 */
@Data
@Entity
@Table(name = "fin_scholarship_applications")
@EqualsAndHashCode(callSuper = true)
public class ScholarshipApplication extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ScholarshipCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status; // SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED

    private String remarks;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    public enum ApplicationStatus {
        SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED
    }
}
