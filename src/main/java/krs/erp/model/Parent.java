package krs.erp.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "parents")
@AttributeOverride(name = "id", column = @Column(name = "parent_id"))
public class Parent extends BaseEntity {
    
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
    
    @Email(message = "Email should be valid")
    @Column(name = "email", unique = true, length = 100)
    private String email;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "alternate_phone", length = 20)
    private String alternatePhone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;
    
    @Column(name = "occupation", length = 100)
    private String occupation;
    
    @Column(name = "workplace", length = 100)
    private String workplace;
    
    @Column(name = "work_phone", length = 20)
    private String workPhone;
    
    @Column(name = "emergency_contact", nullable = false)
    private Boolean emergencyContact = false;
    
    @Column(name = "authorized_pickup", nullable = false)
    private Boolean authorizedPickup = true;
    
    @Column(name = "receive_notifications", nullable = false)
    private Boolean receiveNotifications = true;
    
    // Address relationship - uses polymorphic association via Address entity
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address address;
    
    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ParentStudentRelation> studentRelations = new HashSet<>();
    
    // Enums
    public enum Gender {
        MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
    }
    
    // Constructors
    public Parent() {}
    
    public Parent(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
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
    
    public String getAlternatePhone() {
        return alternatePhone;
    }
    
    public void setAlternatePhone(String alternatePhone) {
        this.alternatePhone = alternatePhone;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public String getOccupation() {
        return occupation;
    }
    
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    
    public String getWorkplace() {
        return workplace;
    }
    
    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }
    
    public String getWorkPhone() {
        return workPhone;
    }
    
    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }
    
    public Address getAddress() {
        return address;
    }
    
    public void setAddress(Address address) {
        this.address = address;
    }
    
    public Boolean getEmergencyContact() {
        return emergencyContact;
    }
    
    public void setEmergencyContact(Boolean emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    
    public Boolean getAuthorizedPickup() {
        return authorizedPickup;
    }
    
    public void setAuthorizedPickup(Boolean authorizedPickup) {
        this.authorizedPickup = authorizedPickup;
    }
    
    public Boolean getReceiveNotifications() {
        return receiveNotifications;
    }
    
    public void setReceiveNotifications(Boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Set<ParentStudentRelation> getStudentRelations() {
        return studentRelations;
    }
    
    public void setStudentRelations(Set<ParentStudentRelation> studentRelations) {
        this.studentRelations = studentRelations;
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
        if (this.address != null) {
            return this.address.getFullAddress();
        }
        return "";
    }
}