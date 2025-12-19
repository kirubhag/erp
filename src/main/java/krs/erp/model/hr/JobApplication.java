package krs.erp.model.hr;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import krs.erp.model.BaseEntity;

@Entity
@Table(name = "erp_hr_job_applications")
public class JobApplication extends BaseEntity {

    @NotNull
    @Column(name = "job_posting_id", nullable = false)
    private Long jobPostingId;

    @NotBlank(message = "Candidate name is required")
    @Column(name = "candidate_name", nullable = false, length = 100)
    private String candidateName;

    @NotBlank(message = "Email is required")
    @Email
    @Column(name = "candidate_email", nullable = false, length = 100)
    private String candidateEmail;

    @Column(name = "candidate_phone", length = 20)
    private String candidatePhone;

    @Column(name = "resume_url", length = 500)
    private String resumeUrl;

    @NotNull
    @Column(name = "applied_date", nullable = false)
    private LocalDate appliedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status = ApplicationStatus.NEW;

    public enum ApplicationStatus {
        NEW, SCREENING, INTERVIEW, OFFER_EXTENDED, HIRED, REJECTED
    }

    // Getters and Setters
    public Long getJobPostingId() {
        return jobPostingId;
    }

    public void setJobPostingId(Long jobPostingId) {
        this.jobPostingId = jobPostingId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public String getCandidatePhone() {
        return candidatePhone;
    }

    public void setCandidatePhone(String candidatePhone) {
        this.candidatePhone = candidatePhone;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}
