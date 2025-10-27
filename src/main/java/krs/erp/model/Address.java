package krs.erp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;

/**
 * Address entity to store address information for various entities (Student, Parent, Staff, etc.)
 * Uses polymorphic association pattern with entity_type and entity_id
 */
@Entity
@Table(name = "addresses")
public class Address extends BaseEntity {
    
    @Column(name = "entity_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EntityType entityType;
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @Size(max = 100, message = "Address line 1 must not exceed 100 characters")
    @Column(name = "address_line1", length = 100)
    private String addressLine1;
    
    @Size(max = 100, message = "Address line 2 must not exceed 100 characters")
    @Column(name = "address_line2", length = 100)
    private String addressLine2;
    
    @Size(max = 50, message = "City must not exceed 50 characters")
    @Column(name = "city", length = 50)
    private String city;
    
    @Size(max = 50, message = "State must not exceed 50 characters")
    @Column(name = "state", length = 50)
    private String state;
    
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Column(name = "postal_code", length = 20)
    private String postalCode;
    
    @Size(max = 50, message = "Country must not exceed 50 characters")
    @Column(name = "country", length = 50)
    private String country;
    
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", length = 20)
    private AddressType addressType = AddressType.RESIDENTIAL;
    
    // Enums
    public enum EntityType {
        STUDENT, PARENT, STAFF, ORGANIZATION, OTHER
    }
    
    public enum AddressType {
        RESIDENTIAL, MAILING, WORK, OTHER
    }
    
    // Constructors
    public Address() {}
    
    public Address(EntityType entityType, Long entityId) {
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    public Address(EntityType entityType, Long entityId, String addressLine1, 
                  String city, String state, String postalCode, String country) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }
    
    // Getters and Setters
    public EntityType getEntityType() {
        return entityType;
    }
    
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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
    
    public Boolean getIsPrimary() {
        return isPrimary;
    }
    
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
    
    public AddressType getAddressType() {
        return addressType;
    }
    
    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }
    
    // Frontend compatibility - transient getters/setters
    // Maps frontend 'street' field to backend 'addressLine1'
    @Transient
    public String getStreet() {
        return addressLine1;
    }
    
    public void setStreet(String street) {
        this.addressLine1 = street;
    }
    
    // Maps frontend 'zipCode' field to backend 'postalCode'
    @Transient
    public String getZipCode() {
        return postalCode;
    }
    
    public void setZipCode(String zipCode) {
        this.postalCode = zipCode;
    }
    
    // Helper methods
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
    
    @Override
    public String toString() {
        return "Address{" +
                "id=" + getId() +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", addressLine1='" + addressLine1 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", isPrimary=" + isPrimary +
                ", addressType=" + addressType +
                '}';
    }
}
