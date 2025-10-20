package krs.erp.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "health_records")
public class HealthRecord extends BaseEntity {
    
    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @NotNull(message = "Record type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false)
    private RecordType recordType;
    
    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    @Column(name = "title", nullable = false, length = 100)
    private String title;
    
    @Lob
    @Column(name = "description")
    private String description;
    
    @Column(name = "record_date")
    private LocalDate recordDate;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Column(name = "provider", length = 100)
    private String provider;
    
    @Column(name = "provider_contact", length = 100)
    private String providerContact;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private Severity severity;
    
    @Column(name = "medication", length = 255)
    private String medication;
    
    @Column(name = "dosage", length = 100)
    private String dosage;
    
    @Column(name = "frequency", length = 100)
    private String frequency;
    
    @Lob
    @Column(name = "special_instructions")
    private String specialInstructions;
    
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    
    @Column(name = "requires_attention", nullable = false)
    private Boolean requiresAttention = false;
    
    @Column(name = "document_path", length = 255)
    private String documentPath;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by")
    private User recordedBy;
    
    // Enums
    public enum RecordType {
        IMMUNIZATION, ALLERGY, MEDICAL_CONDITION, MEDICATION, 
        PHYSICAL_EXAM, VISION_SCREENING, HEARING_SCREENING,
        DENTAL_CHECKUP, EMERGENCY_CONTACT, INSURANCE_INFO, OTHER
    }
    
    public enum Severity {
        LOW, MODERATE, HIGH, CRITICAL
    }
    
    // Constructors
    public HealthRecord() {}
    
    public HealthRecord(Student student, RecordType recordType, String title) {
        this.student = student;
        this.recordType = recordType;
        this.title = title;
    }
    
    // Getters and Setters
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public RecordType getRecordType() {
        return recordType;
    }
    
    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getRecordDate() {
        return recordDate;
    }
    
    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getProviderContact() {
        return providerContact;
    }
    
    public void setProviderContact(String providerContact) {
        this.providerContact = providerContact;
    }
    
    public Severity getSeverity() {
        return severity;
    }
    
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
    
    public String getMedication() {
        return medication;
    }
    
    public void setMedication(String medication) {
        this.medication = medication;
    }
    
    public String getDosage() {
        return dosage;
    }
    
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    
    public String getFrequency() {
        return frequency;
    }
    
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public Boolean getRequiresAttention() {
        return requiresAttention;
    }
    
    public void setRequiresAttention(Boolean requiresAttention) {
        this.requiresAttention = requiresAttention;
    }
    
    public String getDocumentPath() {
        return documentPath;
    }
    
    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }
    
    public User getRecordedBy() {
        return recordedBy;
    }
    
    public void setRecordedBy(User recordedBy) {
        this.recordedBy = recordedBy;
    }
    
    // Helper methods
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }
    
    public boolean isExpiringWithin(int days) {
        return expiryDate != null && 
               expiryDate.isAfter(LocalDate.now()) && 
               expiryDate.isBefore(LocalDate.now().plusDays(days));
    }
    
    public String getRecordTypeDescription() {
        return recordType.toString().replace("_", " ").toLowerCase();
    }
    
    public boolean isCritical() {
        return severity == Severity.CRITICAL || severity == Severity.HIGH;
    }
    
    @Override
    public String toString() {
        return "HealthRecord{" +
                "id=" + getId() +
                ", student=" + (student != null ? student.getFullName() : "null") +
                ", recordType=" + recordType +
                ", title='" + title + '\'' +
                ", recordDate=" + recordDate +
                ", active=" + active +
                '}';
    }
}