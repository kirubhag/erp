package krs.erp.model;

import jakarta.persistence.Column;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

/**
 * Entity representing guardian/parent information for a student.
 * This is a separate table to avoid bloating the main students table.
 * One-to-One relationship with Student entity.
 */
@Entity
@Table(name = "student_guardian_info")
@AttributeOverride(name = "id", column = @Column(name = "student_guardian_info_id"))
public class StudentGuardianInfo extends BaseEntity {

    // Relationship to Student (FK is in this table)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    // Father Information
    @Column(name = "father_name", length = 100)
    private String fatherName;

    @Column(name = "father_occupation", length = 100)
    private String fatherOccupation;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column(name = "father_phone", length = 20)
    private String fatherPhone;

    @Email(message = "Email should be valid")
    @Column(name = "father_email", length = 100)
    private String fatherEmail;

    // Mother Information
    @Column(name = "mother_name", length = 100)
    private String motherName;

    @Column(name = "mother_occupation", length = 100)
    private String motherOccupation;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column(name = "mother_phone", length = 20)
    private String motherPhone;

    @Email(message = "Email should be valid")
    @Column(name = "mother_email", length = 100)
    private String motherEmail;

    // Guardian Information (if different from parents)
    @Column(name = "guardian_name", length = 100)
    private String guardianName;

    @Column(name = "guardian_relation", length = 50)
    private String guardianRelation;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column(name = "guardian_phone", length = 20)
    private String guardianPhone;

    @Email(message = "Email should be valid")
    @Column(name = "guardian_email", length = 100)
    private String guardianEmail;

    @Column(name = "guardian_address", length = 300)
    private String guardianAddress;

    // Constructors
    public StudentGuardianInfo() {
    }

    public StudentGuardianInfo(Student student) {
        this.student = student;
    }

    // Getters and Setters
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherOccupation() {
        return fatherOccupation;
    }

    public void setFatherOccupation(String fatherOccupation) {
        this.fatherOccupation = fatherOccupation;
    }

    public String getFatherPhone() {
        return fatherPhone;
    }

    public void setFatherPhone(String fatherPhone) {
        this.fatherPhone = fatherPhone;
    }

    public String getFatherEmail() {
        return fatherEmail;
    }

    public void setFatherEmail(String fatherEmail) {
        this.fatherEmail = fatherEmail;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getMotherOccupation() {
        return motherOccupation;
    }

    public void setMotherOccupation(String motherOccupation) {
        this.motherOccupation = motherOccupation;
    }

    public String getMotherPhone() {
        return motherPhone;
    }

    public void setMotherPhone(String motherPhone) {
        this.motherPhone = motherPhone;
    }

    public String getMotherEmail() {
        return motherEmail;
    }

    public void setMotherEmail(String motherEmail) {
        this.motherEmail = motherEmail;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianRelation() {
        return guardianRelation;
    }

    public void setGuardianRelation(String guardianRelation) {
        this.guardianRelation = guardianRelation;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }

    public String getGuardianEmail() {
        return guardianEmail;
    }

    public void setGuardianEmail(String guardianEmail) {
        this.guardianEmail = guardianEmail;
    }

    public String getGuardianAddress() {
        return guardianAddress;
    }

    public void setGuardianAddress(String guardianAddress) {
        this.guardianAddress = guardianAddress;
    }
}
