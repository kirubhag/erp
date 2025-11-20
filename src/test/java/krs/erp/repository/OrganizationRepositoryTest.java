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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.Organization;

/**
 * Unit tests for OrganizationRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrganizationRepositoryTest {
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    private Organization testOrg1;
    private Organization testOrg2;
    private Organization testOrg3;
    
    @BeforeEach
    void setUp() {
        organizationRepository.deleteAllInBatch();
        
        // Create test organization 1
        testOrg1 = new Organization();
        testOrg1.setName("Test School 1");
        testOrg1.setCode("TS001");
        testOrg1.setType("School");
        testOrg1.setEmail("school1@test.com");
        testOrg1.setCity("New York");
        testOrg1.setCountry("USA");
        testOrg1.setIsActive(1);
        testOrg1 = organizationRepository.save(testOrg1);
        
        // Create test organization 2
        testOrg2 = new Organization();
        testOrg2.setName("Test University 1");
        testOrg2.setCode("TU001");
        testOrg2.setType("University");
        testOrg2.setEmail("university1@test.com");
        testOrg2.setCity("Boston");
        testOrg2.setCountry("USA");
        testOrg2.setIsActive(1);
        testOrg2 = organizationRepository.save(testOrg2);
        
        // Create test organization 3
        testOrg3 = new Organization();
        testOrg3.setName("Test School 2");
        testOrg3.setCode("TS002");
        testOrg3.setType("School");
        testOrg3.setEmail("school2@test.com");
        testOrg3.setCity("New York");
        testOrg3.setCountry("USA");
        testOrg3.setIsActive(1);
        testOrg3 = organizationRepository.save(testOrg3);
    }
    
    @Test
    void testSaveOrganization() {
        Organization newOrg = new Organization();
        newOrg.setName("New Test Org");
        newOrg.setCode("NTO001");
        newOrg.setType("School");
        newOrg.setIsActive(1);
        
        Organization saved = organizationRepository.save(newOrg);
        
        assertNotNull(saved.getId());
        assertEquals("New Test Org", saved.getName());
    }
    
    @Test
    void testFindByCodeAndIsActiveTrue() {
        Optional<Organization> found = organizationRepository.findByCodeAndIsActiveTrue("TS001");
        
        assertTrue(found.isPresent());
        assertEquals("Test School 1", found.get().getName());
    }
    
    @Test
    void testFindByNameAndIsActiveTrue() {
        Optional<Organization> found = organizationRepository.findByNameAndIsActiveTrue("Test School 1");
        
        assertTrue(found.isPresent());
        assertEquals("TS001", found.get().getCode());
    }
    
    @Test
    void testFindByTypeAndIsActiveTrueOrderByNameAsc() {
        List<Organization> found = organizationRepository.findByTypeAndIsActiveTrueOrderByNameAsc("School");
        
        assertThat(found).hasSize(2);
    }
    
    @Test
    void testFindByIsActiveTrueOrderByNameAsc() {
        var found = organizationRepository.findByIsActiveTrueOrderByNameAsc(PageRequest.of(0, 10));
        
        assertThat(found.getContent()).hasSizeGreaterThanOrEqualTo(3);
    }
    
    @Test
    void testSearchByNameOrCode() {
        var found = organizationRepository.searchByNameOrCode("Test", PageRequest.of(0, 10));
        
        assertThat(found.getContent()).hasSizeGreaterThanOrEqualTo(3);
    }
    
    @Test
    void testFindByCityAndIsActiveTrueOrderByNameAsc() {
        List<Organization> found = organizationRepository.findByCityAndIsActiveTrueOrderByNameAsc("New York");
        
        assertThat(found).hasSize(2);
    }
    
    @Test
    void testFindByCountryAndIsActiveTrueOrderByNameAsc() {
        List<Organization> found = organizationRepository.findByCountryAndIsActiveTrueOrderByNameAsc("USA");
        
        assertThat(found).hasSize(3);
    }
    
    @Test
    void testExistsByCodeAndIsActiveTrue() {
        assertTrue(organizationRepository.existsByCodeAndIsActiveTrue("TS001"));
        assertFalse(organizationRepository.existsByCodeAndIsActiveTrue("NONEXISTENT"));
    }
    
    @Test
    void testExistsByNameAndIsActiveTrue() {
        assertTrue(organizationRepository.existsByNameAndIsActiveTrue("Test School 1"));
        assertFalse(organizationRepository.existsByNameAndIsActiveTrue("Nonexistent Org"));
    }
    
    @Test
    void testFindDistinctTypes() {
        List<String> types = organizationRepository.findDistinctTypes();
        
        assertThat(types).hasSizeGreaterThanOrEqualTo(2);
        assertThat(types).contains("School", "University");
    }
    
    @Test
    void testUpdateOrganization() {
        Organization org = organizationRepository.findByCodeAndIsActiveTrue("TS001").orElseThrow();
        org.setPhone("+1234567890");
        
        Organization updated = organizationRepository.save(org);
        
        assertEquals("+1234567890", updated.getPhone());
    }
    
    @Test
    void testDeleteOrganization() {
        Long initialCount = organizationRepository.count();
        
        organizationRepository.delete(testOrg1);
        
        Long afterDeleteCount = organizationRepository.count();
        assertEquals(initialCount - 1, afterDeleteCount);
    }
    
    @Test
    void testFindAll() {
        List<Organization> allOrgs = organizationRepository.findAll();
        
        assertThat(allOrgs).hasSizeGreaterThanOrEqualTo(3);
    }
}
