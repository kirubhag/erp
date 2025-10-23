package krs.erp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Organization model
 */
class OrganizationTest {

    private Organization organization;

    @BeforeEach
    void setUp() {
        organization = new Organization();
    }

    @Test
    void testOrganizationCreation() {
        organization.setName("Springfield Elementary School");
        organization.setCode("SES001");
        organization.setType("SCHOOL");

        assertEquals("Springfield Elementary School", organization.getName());
        assertEquals("SES001", organization.getCode());
        assertEquals("SCHOOL", organization.getType());
    }

    @Test
    void testOrganizationContact() {
        organization.setEmail("contact@school.com");
        organization.setPhone("555-0123");
        organization.setWebsite("www.school.com");

        assertEquals("contact@school.com", organization.getEmail());
        assertEquals("555-0123", organization.getPhone());
        assertEquals("www.school.com", organization.getWebsite());
    }

    @Test
    void testOrganizationAddress() {
        organization.setStreetAddress("123 School Street, Suite 100");
        organization.setCity("Springfield");
        organization.setState("IL");
        organization.setPostalCode("62701");
        organization.setCountry("USA");

        assertEquals("123 School Street, Suite 100", organization.getStreetAddress());
        assertEquals("Springfield", organization.getCity());
        assertEquals("IL", organization.getState());
        assertEquals("62701", organization.getPostalCode());
        assertEquals("USA", organization.getCountry());
    }

    @Test
    void testOrganizationDefaultValues() {
        // Test default active status
        assertTrue(organization.getIsActive());
    }

    @Test
    void testOrganizationStatus() {
        organization.setIsActive(false);
        assertFalse(organization.getIsActive());

        organization.setIsActive(true);
        assertTrue(organization.getIsActive());
    }

    @Test
    void testOrganizationAuditFields() {
        organization.setCreatedBy("admin");
        organization.setModifiedBy("user1");
        organization.setOwnerId(700L);

        assertEquals("admin", organization.getCreatedBy());
        assertEquals("user1", organization.getModifiedBy());
        assertEquals(700L, organization.getOwnerId());
    }

    @Test
    void testOrganizationEquality() {
        Organization org1 = new Organization();
        Organization org2 = new Organization();
        
        org1.setId(1L);
        org2.setId(1L);
        
        assertEquals(org1.getId(), org2.getId());
    }

    @Test
    void testOrganizationToString() {
        organization.setName("Test School");
        organization.setCode("TS001");
        
        String toString = organization.toString();
        assertNotNull(toString);
    }

    @Test
    void testOrganizationValidation() {
        // Test required fields
        organization.setName("Required School Name");
        organization.setCode("REQ001");
        
        assertNotNull(organization.getName());
        assertNotNull(organization.getCode());
        
        assertFalse(organization.getName().isEmpty());
        assertFalse(organization.getCode().isEmpty());
    }

    @Test
    void testOrganizationType() {
        // Test organization types as strings
        organization.setType("SCHOOL");
        assertEquals("SCHOOL", organization.getType());

        organization.setType("UNIVERSITY");
        assertEquals("UNIVERSITY", organization.getType());

        organization.setType("COLLEGE");
        assertEquals("COLLEGE", organization.getType());
    }
}