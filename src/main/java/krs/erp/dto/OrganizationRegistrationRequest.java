package krs.erp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for organization registration form
 */
public class OrganizationRegistrationRequest {
    
    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Organization type is required")
    @Size(max = 50, message = "Organization type cannot exceed 50 characters")
    private String type; // e.g., "School", "University", "Training Center"
    
    @Size(max = 20, message = "Organization code cannot exceed 20 characters")
    private String code;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;
    
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;
    
    @Size(max = 20, message = "Fax number cannot exceed 20 characters")
    private String fax;
    
    @Size(max = 100, message = "Website cannot exceed 100 characters")
    private String website;
    
    // Address Information
    @Size(max = 200, message = "Street address cannot exceed 200 characters")
    private String streetAddress;
    
    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;
    
    @Size(max = 50, message = "State cannot exceed 50 characters")
    private String state;
    
    @Size(max = 10, message = "Postal code cannot exceed 10 characters")
    private String postalCode;
    
    @Size(max = 50, message = "Country cannot exceed 50 characters")
    private String country;
    
    // Registration Information
    @Size(max = 50, message = "Registration number cannot exceed 50 characters")
    private String registrationNumber;
    
    @Size(max = 20, message = "Tax ID cannot exceed 20 characters")
    private String taxId;
    
    private Integer establishedYear;
    
    @Size(max = 50, message = "Accreditation cannot exceed 50 characters")
    private String accreditation;
    
    // Sample Data Population Option
    private Boolean loadSampleData = false; // Whether to load sample data after registration
    
    // Constructors
    public OrganizationRegistrationRequest() {}
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public String getFax() {
        return fax;
    }
    
    public void setFax(String fax) {
        this.fax = fax;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getStreetAddress() {
        return streetAddress;
    }
    
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
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
    
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    public String getTaxId() {
        return taxId;
    }
    
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }
    
    public Integer getEstablishedYear() {
        return establishedYear;
    }
    
    public void setEstablishedYear(Integer establishedYear) {
        this.establishedYear = establishedYear;
    }
    
    public String getAccreditation() {
        return accreditation;
    }
    
    public void setAccreditation(String accreditation) {
        this.accreditation = accreditation;
    }
    
    public Boolean getLoadSampleData() {
        return loadSampleData;
    }
    
    public void setLoadSampleData(Boolean loadSampleData) {
        this.loadSampleData = loadSampleData;
    }
}
