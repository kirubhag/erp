package krs.erp.model.admission;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import krs.erp.model.BaseEntity;
import krs.erp.model.Student.GradeLevel;

@Entity
@Table(name = "erp_admission_inquiries")
public class AdmissionInquiry extends BaseEntity {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Email(message = "Invalid email format")
    @Column(name = "email", length = 100)
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
    @Column(name = "phone", length = 20)
    private String phone;

    @NotNull(message = "Grade level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "grade_interested", nullable = false)
    private GradeLevel gradeInterested;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InquiryStatus status = InquiryStatus.NEW;

    @Column(name = "inquiry_date", nullable = false)
    private LocalDate inquiryDate;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Size(max = 500)
    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "source", length = 50)
    private String source; // e.g., Website, Referral, Walk-in

    public enum InquiryStatus {
        NEW, CONTACTED, VISITED, APPLICATION_FORM_ISSUED, APPROVED, REJECTED, CLOSED
    }

    // specific getters/setters omitted for brevity, keeping standard ones
    public AdmissionInquiry() {
        this.inquiryDate = LocalDate.now();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public GradeLevel getGradeInterested() {
        return gradeInterested;
    }

    public void setGradeInterested(GradeLevel gradeInterested) {
        this.gradeInterested = gradeInterested;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public void setStatus(InquiryStatus status) {
        this.status = status;
    }

    public LocalDate getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(LocalDate inquiryDate) {
        this.inquiryDate = inquiryDate;
    }

    public LocalDate getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDate followUpDate) {
        this.followUpDate = followUpDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
