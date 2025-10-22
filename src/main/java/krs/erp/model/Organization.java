package krs.erp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {
    
    @NotBlank(message = "Organization name is required")
    @Size(max = 100, message = "Organization name cannot exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @NotBlank(message = "Organization type is required")
    @Size(max = 50, message = "Organization type cannot exceed 50 characters")
    @Column(name = "type", nullable = false, length = 50)
    private String type; // e.g., "School", "University", "Training Center", etc.
    
    @Size(max = 20, message = "Organization code cannot exceed 20 characters")
    @Column(name = "code", unique = true, length = 20)
    private String code; // Unique identifier code
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;
    
    // Contact Information
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(name = "email", length = 100)
    private String email;
    
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Size(max = 20, message = "Fax number cannot exceed 20 characters")
    @Column(name = "fax", length = 20)
    private String fax;
    
    @Size(max = 100, message = "Website cannot exceed 100 characters")
    @Column(name = "website", length = 100)
    private String website;
    
    // Address Information
    @Size(max = 200, message = "Street address cannot exceed 200 characters")
    @Column(name = "street_address", length = 200)
    private String streetAddress;
    
    @Size(max = 50, message = "City cannot exceed 50 characters")
    @Column(name = "city", length = 50)
    private String city;
    
    @Size(max = 50, message = "State cannot exceed 50 characters")
    @Column(name = "state", length = 50)
    private String state;
    
    @Size(max = 10, message = "Postal code cannot exceed 10 characters")
    @Column(name = "postal_code", length = 10)
    private String postalCode;
    
    @Size(max = 50, message = "Country cannot exceed 50 characters")
    @Column(name = "country", length = 50)
    private String country;
    
    // Registration Information
    @Size(max = 50, message = "Registration number cannot exceed 50 characters")
    @Column(name = "registration_number", length = 50)
    private String registrationNumber;
    
    @Size(max = 20, message = "Tax ID cannot exceed 20 characters")
    @Column(name = "tax_id", length = 20)
    private String taxId;
    
    // Academic Information (for educational institutions)
    @Column(name = "established_year")
    private Integer establishedYear;
    
    @Size(max = 50, message = "Accreditation cannot exceed 50 characters")
    @Column(name = "accreditation", length = 50)
    private String accreditation;
    
    @Size(max = 20, message = "Academic year format cannot exceed 20 characters")
    @Column(name = "academic_year_format", length = 20)
    private String academicYearFormat; // e.g., "2024-2025", "2024"
    
    // Configuration
    @Size(max = 10, message = "Default language cannot exceed 10 characters")
    @Column(name = "default_language", length = 10)
    private String defaultLanguage = "en";
    
    @Size(max = 10, message = "Default currency cannot exceed 10 characters")
    @Column(name = "default_currency", length = 10)
    private String defaultCurrency = "USD";
    
    @Size(max = 50, message = "Timezone cannot exceed 50 characters")
    @Column(name = "timezone", length = 50)
    private String timezone = "UTC";
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    // Constructors
    public Organization() {
        super();
    }
    
    public Organization(String name, String type) {
        this();
        this.name = name;
        this.type = type;
    }
    
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
    
    public String getAcademicYearFormat() {
        return academicYearFormat;
    }
    
    public void setAcademicYearFormat(String academicYearFormat) {
        this.academicYearFormat = academicYearFormat;
    }
    
    public String getDefaultLanguage() {
        return defaultLanguage;
    }
    
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }
    
    public String getDefaultCurrency() {
        return defaultCurrency;
    }
    
    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getLogoUrl() {
        return logoUrl;
    }
    
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
    
    @Override
    public String toString() {
        return "Organization{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", code='" + code + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}