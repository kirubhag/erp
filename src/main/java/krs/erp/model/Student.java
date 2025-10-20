package krs.erp.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "students")
public class Student extends BaseEntity {
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    @Column(name = "middle_name", length = 50)
    private String middleName;
    
    @NotBlank(message = "Student ID is required")
    @Column(name = "student_id", unique = true, nullable = false, length = 20)
    private String studentId;
    
    @Email(message = "Email should be valid")
    @Column(name = "email", unique = true, length = 100)
    private String email;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column(name = "phone", length = 20)
    private String phone;
    
    @NotNull(message = "Date of birth is required")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;
    
    @NotNull(message = "Enrollment date is required")
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "grade_level", nullable = false)
    private GradeLevel gradeLevel;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_status", nullable = false)
    private EnrollmentStatus enrollmentStatus = EnrollmentStatus.ACTIVE;
    
    // Address Information
    @Column(name = "address_line1", length = 100)
    private String addressLine1;
    
    @Column(name = "address_line2", length = 100)
    private String addressLine2;
    
    @Column(name = "city", length = 50)
    private String city;
    
    @Column(name = "state", length = 50)
    private String state;
    
    @Column(name = "postal_code", length = 20)
    private String postalCode;
    
    @Column(name = "country", length = 50)
    private String country;
    
    // Emergency Contact
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;
    
    @Column(name = "emergency_contact_relation", length = 50)
    private String emergencyContactRelation;
    
    // Relationships
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Attendance> attendanceRecords = new HashSet<>();
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<HealthRecord> healthRecords = new HashSet<>();
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ParentStudentRelation> parentRelations = new HashSet<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    // Enums
    public enum Gender {
        MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
    }
    
    public enum GradeLevel {
        KINDERGARTEN, GRADE_1, GRADE_2, GRADE_3, GRADE_4, GRADE_5,
        GRADE_6, GRADE_7, GRADE_8, GRADE_9, GRADE_10, GRADE_11, GRADE_12
    }
    
    public enum EnrollmentStatus {
        ACTIVE, INACTIVE, GRADUATED, TRANSFERRED, SUSPENDED, EXPELLED
    }
    
    // Constructors
    public Student() {}
    
    public Student(String firstName, String lastName, String studentId, LocalDate dateOfBirth, 
                  LocalDate enrollmentDate, GradeLevel gradeLevel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.dateOfBirth = dateOfBirth;
        this.enrollmentDate = enrollmentDate;
        this.gradeLevel = gradeLevel;
    }
    
    // Getters and Setters
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
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
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
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    
    public GradeLevel getGradeLevel() {
        return gradeLevel;
    }
    
    public void setGradeLevel(GradeLevel gradeLevel) {
        this.gradeLevel = gradeLevel;
    }
    
    public EnrollmentStatus getEnrollmentStatus() {
        return enrollmentStatus;
    }
    
    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }
    
    public String getAddressLine1() {
        return addressLine1;
    }
    
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }
    
    public String getAddressLine2() {
        return addressLine2;
    }
    
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }
    
    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
    
    public String getEmergencyContactRelation() {
        return emergencyContactRelation;
    }
    
    public void setEmergencyContactRelation(String emergencyContactRelation) {
        this.emergencyContactRelation = emergencyContactRelation;
    }
    
    public Set<Attendance> getAttendanceRecords() {
        return attendanceRecords;
    }
    
    public void setAttendanceRecords(Set<Attendance> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;
    }
    
    public Set<HealthRecord> getHealthRecords() {
        return healthRecords;
    }
    
    public void setHealthRecords(Set<HealthRecord> healthRecords) {
        this.healthRecords = healthRecords;
    }
    
    public Set<ParentStudentRelation> getParentRelations() {
        return parentRelations;
    }
    
    public void setParentRelations(Set<ParentStudentRelation> parentRelations) {
        this.parentRelations = parentRelations;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    // Helper methods
    public String getFullName() {
        StringBuilder fullName = new StringBuilder(firstName);
        if (middleName != null && !middleName.isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        fullName.append(" ").append(lastName);
        return fullName.toString();
    }
    
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (addressLine1 != null && !addressLine1.isEmpty()) {
            address.append(addressLine1);
        }
        if (addressLine2 != null && !addressLine2.isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(addressLine2);
        }
        if (city != null && !city.isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(city);
        }
        if (state != null && !state.isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(state);
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(postalCode);
        }
        if (country != null && !country.isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(country);
        }
        return address.toString();
    }
    
    public int getAge() {
        if (dateOfBirth != null) {
            return LocalDate.now().getYear() - dateOfBirth.getYear();
        }
        return 0;
    }
}