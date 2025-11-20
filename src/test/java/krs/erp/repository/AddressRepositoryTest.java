package krs.erp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.Address;

/**
 * Unit tests for AddressRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AddressRepositoryTest {
    
    @Autowired
    private AddressRepository addressRepository;
    
    private Address address1;
    private Address address2;
    private Address address3;
    
    private static final Long TEST_STUDENT_ID = 1001L;
    private static final Long TEST_PARENT_ID = 2001L;
    
    @BeforeEach
    void setUp() {
        addressRepository.deleteAllInBatch();
        
        // Create address 1 - Student Primary Residential
        address1 = new Address();
        address1.setEntityType(Address.EntityType.STUDENT);
        address1.setEntityId(TEST_STUDENT_ID);
        address1.setAddressLine1("123 Main Street");
        address1.setAddressLine2("Apt 4B");
        address1.setCity("Springfield");
        address1.setState("IL");
        address1.setPostalCode("62701");
        address1.setCountry("USA");
        address1.setIsPrimary(true);
        address1.setAddressType(Address.AddressType.RESIDENTIAL);
        address1.setIsActive(1);
        address1.setCreatedTime(LocalDateTime.now());
        address1 = addressRepository.save(address1);
        
        // Create address 2 - Student Secondary Mailing
        address2 = new Address();
        address2.setEntityType(Address.EntityType.STUDENT);
        address2.setEntityId(TEST_STUDENT_ID);
        address2.setAddressLine1("456 Oak Avenue");
        address2.setCity("Springfield");
        address2.setState("IL");
        address2.setPostalCode("62702");
        address2.setCountry("USA");
        address2.setIsPrimary(false);
        address2.setAddressType(Address.AddressType.MAILING);
        address2.setIsActive(1);
        address2.setCreatedTime(LocalDateTime.now());
        address2 = addressRepository.save(address2);
        
        // Create address 3 - Parent Primary Residential
        address3 = new Address();
        address3.setEntityType(Address.EntityType.PARENT);
        address3.setEntityId(TEST_PARENT_ID);
        address3.setAddressLine1("789 Elm Street");
        address3.setCity("Chicago");
        address3.setState("IL");
        address3.setPostalCode("60601");
        address3.setCountry("USA");
        address3.setIsPrimary(true);
        address3.setAddressType(Address.AddressType.RESIDENTIAL);
        address3.setIsActive(1);
        address3.setCreatedTime(LocalDateTime.now());
        address3 = addressRepository.save(address3);
    }
    
    @Test
    void testFindByEntityTypeAndEntityId() {
        List<Address> studentAddresses = addressRepository.findByEntityTypeAndEntityId(
            Address.EntityType.STUDENT, TEST_STUDENT_ID);
        
        assertThat(studentAddresses).hasSize(2);
        assertThat(studentAddresses).allMatch(a -> a.getEntityType() == Address.EntityType.STUDENT);
        
        List<Address> parentAddresses = addressRepository.findByEntityTypeAndEntityId(
            Address.EntityType.PARENT, TEST_PARENT_ID);
        
        assertThat(parentAddresses).hasSize(1);
    }
    
    @Test
    void testFindPrimaryAddress() {
        Optional<Address> primaryAddress = addressRepository.findPrimaryAddress(
            Address.EntityType.STUDENT, TEST_STUDENT_ID);
        
        assertThat(primaryAddress).isPresent();
        assertEquals("123 Main Street", primaryAddress.get().getAddressLine1());
        assertTrue(primaryAddress.get().getIsPrimary());
    }
    
    @Test
    void testFindPrimaryAddressNotFound() {
        Optional<Address> primaryAddress = addressRepository.findPrimaryAddress(
            Address.EntityType.STAFF, 9999L);
        
        assertThat(primaryAddress).isEmpty();
    }
    
    @Test
    void testFindByEntityAndAddressType() {
        List<Address> residentialAddresses = addressRepository.findByEntityAndAddressType(
            Address.EntityType.STUDENT, TEST_STUDENT_ID, Address.AddressType.RESIDENTIAL);
        
        assertThat(residentialAddresses).hasSize(1);
        assertEquals(Address.AddressType.RESIDENTIAL, residentialAddresses.get(0).getAddressType());
        
        List<Address> mailingAddresses = addressRepository.findByEntityAndAddressType(
            Address.EntityType.STUDENT, TEST_STUDENT_ID, Address.AddressType.MAILING);
        
        assertThat(mailingAddresses).hasSize(1);
        assertEquals("456 Oak Avenue", mailingAddresses.get(0).getAddressLine1());
    }
    
    @Test
    void testExistsByEntityTypeAndEntityId() {
        boolean exists = addressRepository.existsByEntityTypeAndEntityId(
            Address.EntityType.STUDENT, TEST_STUDENT_ID);
        assertTrue(exists);
        
        boolean notExists = addressRepository.existsByEntityTypeAndEntityId(
            Address.EntityType.ORGANIZATION, 9999L);
        assertFalse(notExists);
    }
    
    @Test
    void testDeleteByEntityTypeAndEntityId() {
        addressRepository.deleteByEntityTypeAndEntityId(Address.EntityType.STUDENT, TEST_STUDENT_ID);
        
        List<Address> remainingAddresses = addressRepository.findByEntityTypeAndEntityId(
            Address.EntityType.STUDENT, TEST_STUDENT_ID);
        
        assertThat(remainingAddresses).isEmpty();
    }
    
    @Test
    void testSaveAddress() {
        Address newAddress = new Address();
        newAddress.setEntityType(Address.EntityType.STAFF);
        newAddress.setEntityId(3001L);
        newAddress.setAddressLine1("321 Pine Road");
        newAddress.setCity("Naperville");
        newAddress.setState("IL");
        newAddress.setPostalCode("60540");
        newAddress.setCountry("USA");
        newAddress.setIsPrimary(true);
        newAddress.setAddressType(Address.AddressType.WORK);
        newAddress.setIsActive(1);
        newAddress.setCreatedTime(LocalDateTime.now());
        
        Address saved = addressRepository.save(newAddress);
        
        assertNotNull(saved.getId());
        assertEquals("321 Pine Road", saved.getAddressLine1());
        assertEquals(Address.EntityType.STAFF, saved.getEntityType());
    }
    
    @Test
    void testUpdateAddress() {
        address1.setAddressLine2("Suite 10");
        address1.setPostalCode("62703");
        address1.setModifiedTime(LocalDateTime.now());
        
        Address updated = addressRepository.save(address1);
        
        assertEquals("Suite 10", updated.getAddressLine2());
        assertEquals("62703", updated.getPostalCode());
    }
    
    @Test
    void testDeleteAddress() {
        Long addressId = address1.getId();
        addressRepository.delete(address1);
        
        assertThat(addressRepository.findById(addressId)).isEmpty();
    }
    
    @Test
    void testFindAll() {
        List<Address> allAddresses = addressRepository.findAll();
        assertThat(allAddresses).hasSize(3);
    }
    
    @Test
    void testGetFullAddress() {
        String fullAddress = address1.getFullAddress();
        assertTrue(fullAddress.contains("123 Main Street"));
        assertTrue(fullAddress.contains("Springfield"));
        assertTrue(fullAddress.contains("IL"));
        assertTrue(fullAddress.contains("62701"));
    }
    
    @Test
    void testMultipleEntityTypes() {
        // Verify we can store addresses for different entity types
        List<Address> allAddresses = addressRepository.findAll();
        
        long studentCount = allAddresses.stream()
            .filter(a -> a.getEntityType() == Address.EntityType.STUDENT)
            .count();
        
        long parentCount = allAddresses.stream()
            .filter(a -> a.getEntityType() == Address.EntityType.PARENT)
            .count();
        
        assertEquals(2L, studentCount);
        assertEquals(1L, parentCount);
    }
    
    @Test
    void testAddressTypeDistribution() {
        List<Address> allAddresses = addressRepository.findAll();
        
        long residentialCount = allAddresses.stream()
            .filter(a -> a.getAddressType() == Address.AddressType.RESIDENTIAL)
            .count();
        
        long mailingCount = allAddresses.stream()
            .filter(a -> a.getAddressType() == Address.AddressType.MAILING)
            .count();
        
        assertEquals(2L, residentialCount);
        assertEquals(1L, mailingCount);
    }
}
