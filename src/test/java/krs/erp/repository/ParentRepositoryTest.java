package krs.erp.repository;

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

import krs.erp.model.Parent;

/**
 * Unit tests for ParentRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ParentRepositoryTest {
    
    @Autowired
    private ParentRepository parentRepository;
    
    private Parent testParent1;
    private Parent testParent2;
    private Parent testParent3;
    
    @BeforeEach
    void setUp() {
        parentRepository.deleteAllInBatch();
        
        // Create test parent 1
        testParent1 = new Parent();
        testParent1.setFirstName("Michael");
        testParent1.setLastName("Anderson");
        testParent1.setEmail("michael.anderson@test.com");
        testParent1.setPhone("+11234567890");
        testParent1.setGender(Parent.Gender.MALE);
        testParent1.setOccupation("Engineer");
        testParent1.setReceiveNotifications(true);
        testParent1.setEmergencyContact(true);
        testParent1.setAuthorizedPickup(true);
        testParent1.setIsActive(1);
        testParent1 = parentRepository.save(testParent1);
        
        // Create test parent 2
        testParent2 = new Parent();
        testParent2.setFirstName("Sarah");
        testParent2.setLastName("Anderson");
        testParent2.setEmail("sarah.anderson@test.com");
        testParent2.setPhone("+11234567891");
        testParent2.setGender(Parent.Gender.FEMALE);
        testParent2.setOccupation("Doctor");
        testParent2.setReceiveNotifications(true);
        testParent2.setEmergencyContact(true);
        testParent2.setAuthorizedPickup(true);
        testParent2.setIsActive(1);
        testParent2 = parentRepository.save(testParent2);
        
        // Create test parent 3 - Inactive
        testParent3 = new Parent();
        testParent3.setFirstName("Robert");
        testParent3.setLastName("Brown");
        testParent3.setEmail("robert.brown@test.com");
        testParent3.setPhone("+11234567892");
        testParent3.setGender(Parent.Gender.MALE);
        testParent3.setOccupation("Businessman");
        testParent3.setReceiveNotifications(false);
        testParent3.setEmergencyContact(false);
        testParent3.setAuthorizedPickup(false);
        testParent3.setIsActive(0);
        testParent3 = parentRepository.save(testParent3);
    }
    
    @Test
    void testSaveParent() {
        Parent newParent = new Parent();
        newParent.setFirstName("Jennifer");
        newParent.setLastName("Wilson");
        newParent.setEmail("jennifer.wilson@test.com");
        newParent.setIsActive(1);
        
        Parent saved = parentRepository.save(newParent);
        
        assertNotNull(saved.getId());
        assertEquals("Jennifer", saved.getFirstName());
    }
    
    @Test
    void testFindByEmail() {
        Optional<Parent> found = parentRepository.findByEmail("michael.anderson@test.com");
        
        assertTrue(found.isPresent());
        assertEquals("Michael", found.get().getFirstName());
    }
    
    @Test
    void testFindByEmail_NotFound() {
        Optional<Parent> found = parentRepository.findByEmail("nonexistent@test.com");
        assertFalse(found.isPresent());
    }
    
    @Test
    void testFindByNameContaining() {
        List<Parent> found = parentRepository.findByNameContaining("Anderson");
        
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Parent::getLastName)
                         .containsOnly("Anderson");
    }
    
    @Test
    void testFindParentsReceivingNotifications() {
        List<Parent> parents = parentRepository.findParentsReceivingNotifications();
        
        assertThat(parents).hasSize(2);
        assertThat(parents).allMatch(Parent::getReceiveNotifications);
    }
    
    @Test
    void testFindEmergencyContacts() {
        List<Parent> emergencyContacts = parentRepository.findEmergencyContacts();
        
        assertThat(emergencyContacts).hasSize(2);
        assertThat(emergencyContacts).allMatch(Parent::getEmergencyContact);
    }
    
    @Test
    void testFindAuthorizedPickupParents() {
        List<Parent> authorizedParents = parentRepository.findAuthorizedPickupParents();
        
        assertThat(authorizedParents).hasSize(2);
        assertThat(authorizedParents).allMatch(Parent::getAuthorizedPickup);
    }
    
    @Test
    void testFindByIsActiveTrue() {
        List<Parent> activeParents = parentRepository.findByIsActiveTrue();
        
        assertThat(activeParents).hasSize(2);
        assertThat(activeParents).allMatch(p -> p.getIsActive() == 1);
    }
    
    @Test
    void testCountByIsActiveTrue() {
        Long count = parentRepository.countByIsActiveTrue();
        
        assertEquals(2L, count);
    }
    
    @Test
    void testFindByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase() {
        List<Parent> found = parentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            "michael", "anderson");
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(2);
    }
    
    @Test
    void testExistsByEmail() {
        assertTrue(parentRepository.existsByEmail("michael.anderson@test.com"));
        assertFalse(parentRepository.existsByEmail("nonexistent@test.com"));
    }
    
    @Test
    void testFindByIsActive() {
        List<Parent> activeParents = parentRepository.findByIsActive(1);
        List<Parent> inactiveParents = parentRepository.findByIsActive(0);
        
        assertThat(activeParents).hasSize(2);
        assertThat(inactiveParents).hasSize(1);
    }
    
    @Test
    void testFindAllActive() {
        List<Parent> activeParents = parentRepository.findAllActive();
        
        assertThat(activeParents).hasSize(2);
        assertThat(activeParents).allMatch(p -> p.getIsActive() == 1);
    }
    
    @Test
    void testCountByIsActive() {
        Long activeCount = parentRepository.countByIsActive(1);
        Long inactiveCount = parentRepository.countByIsActive(0);
        
        assertEquals(2L, activeCount);
        assertEquals(1L, inactiveCount);
    }
    
    @Test
    void testUpdateParent() {
        Parent parent = parentRepository.findByEmail("michael.anderson@test.com").orElseThrow();
        parent.setOccupation("Senior Engineer");
        
        Parent updated = parentRepository.save(parent);
        
        assertEquals("Senior Engineer", updated.getOccupation());
    }
    
    @Test
    void testDeleteParent() {
        Long initialCount = parentRepository.count();
        
        parentRepository.delete(testParent1);
        
        Long afterDeleteCount = parentRepository.count();
        assertEquals(initialCount - 1, afterDeleteCount);
    }
    
    @Test
    void testFindAll() {
        List<Parent> allParents = parentRepository.findAll();
        
        assertThat(allParents).hasSizeGreaterThanOrEqualTo(3);
    }
    
    @Test
    void testSoftDelete() {
        testParent1.setIsActive(0);
        parentRepository.save(testParent1);
        
        List<Parent> activeParents = parentRepository.findByIsActive(1);
        assertThat(activeParents).hasSize(1);
        
        List<Parent> inactiveParents = parentRepository.findByIsActive(0);
        assertThat(inactiveParents).hasSize(2);
    }
}
