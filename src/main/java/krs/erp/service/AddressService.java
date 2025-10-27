package krs.erp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.Address;
import krs.erp.model.Address.AddressType;
import krs.erp.model.Address.EntityType;
import krs.erp.repository.AddressRepository;

/**
 * Service for managing addresses across all entity types
 */
@Service
@Transactional
public class AddressService {
    
    @Autowired
    private AddressRepository addressRepository;
    
    /**
     * Get or create primary address for an entity
     * 
     * @param entityType The type of entity (STUDENT, PARENT, STAFF, etc.)
     * @param entityId The ID of the entity
     * @return The primary address for the entity
     */
    public Address getOrCreatePrimaryAddress(EntityType entityType, Long entityId) {
        Optional<Address> existingAddress = addressRepository.findPrimaryAddress(entityType, entityId);
        
        if (existingAddress.isPresent()) {
            return existingAddress.get();
        }
        
        // Create new primary address
        Address address = new Address(entityType, entityId);
        address.setIsPrimary(true);
        address.setAddressType(AddressType.RESIDENTIAL);
        return addressRepository.save(address);
    }
    
    /**
     * Get primary address for an entity
     * 
     * @param entityType The type of entity
     * @param entityId The ID of the entity
     * @return Optional containing the primary address if found
     */
    public Optional<Address> getPrimaryAddress(EntityType entityType, Long entityId) {
        return addressRepository.findPrimaryAddress(entityType, entityId);
    }
    
    /**
     * Get all addresses for an entity
     * 
     * @param entityType The type of entity
     * @param entityId The ID of the entity
     * @return List of addresses
     */
    public List<Address> getAddressesForEntity(EntityType entityType, Long entityId) {
        return addressRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
    
    /**
     * Save or update an address
     * 
     * @param address The address to save
     * @return The saved address
     */
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }
    
    /**
     * Update address for an entity (creates if doesn't exist)
     * 
     * @param entityType The type of entity
     * @param entityId The ID of the entity
     * @param addressLine1 Address line 1
     * @param addressLine2 Address line 2
     * @param city City
     * @param state State
     * @param postalCode Postal code
     * @param country Country
     * @return The updated address
     */
    public Address updateAddress(EntityType entityType, Long entityId,
                                String addressLine1, String addressLine2,
                                String city, String state, 
                                String postalCode, String country) {
        
        Address address = getOrCreatePrimaryAddress(entityType, entityId);
        
        address.setAddressLine1(addressLine1);
        address.setAddressLine2(addressLine2);
        address.setCity(city);
        address.setState(state);
        address.setPostalCode(postalCode);
        address.setCountry(country);
        
        return addressRepository.save(address);
    }
    
    /**
     * Delete all addresses for an entity
     * 
     * @param entityType The type of entity
     * @param entityId The ID of the entity
     */
    public void deleteAddressesForEntity(EntityType entityType, Long entityId) {
        addressRepository.deleteByEntityTypeAndEntityId(entityType, entityId);
    }
    
    /**
     * Delete a specific address
     * 
     * @param addressId The ID of the address to delete
     */
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }
    
    /**
     * Check if entity has any addresses
     * 
     * @param entityType The type of entity
     * @param entityId The ID of the entity
     * @return true if the entity has at least one address
     */
    public boolean hasAddress(EntityType entityType, Long entityId) {
        return addressRepository.existsByEntityTypeAndEntityId(entityType, entityId);
    }
}
