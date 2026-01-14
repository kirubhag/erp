package krs.erp.model;

import jakarta.persistence.Column;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;

/**
 * Entity representing medical and health information for a student.
 * This is a separate table to avoid bloating the main students table.
 * One-to-One relationship with Student entity.
 */
@Entity
@Table(name = "erp_student_medical_info")
@AttributeOverride(name = "id", column = @Column(name = "student_medical_info_id"))
public class StudentMedicalInfo extends BaseEntity {

    // Relationship to Student (FK is in this table)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    // Critical Health Information
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "medical_conditions", columnDefinition = "TEXT")
    private String medicalConditions;

    @Column(name = "medications", columnDefinition = "TEXT")
    private String medications;

    // Medical Contact Information
    @Column(name = "doctor_name", length = 100)
    private String doctorName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column(name = "doctor_phone", length = 20)
    private String doctorPhone;

    @Column(name = "hospital_preference", length = 200)
    private String hospitalPreference;

    // Insurance Information
    @Column(name = "insurance_provider", length = 100)
    private String insuranceProvider;

    @Column(name = "insurance_policy_number", length = 50)
    private String insurancePolicyNumber;

    // Constructors
    public StudentMedicalInfo() {
    }

    public StudentMedicalInfo(Student student) {
        this.student = student;
    }

    // Getters and Setters
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMedicalConditions() {
        return medicalConditions;
    }

    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = medicalConditions;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    public String getHospitalPreference() {
        return hospitalPreference;
    }

    public void setHospitalPreference(String hospitalPreference) {
        this.hospitalPreference = hospitalPreference;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }
}
