package krs.erp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Permission model
 */
class PermissionTest {

    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = new Permission();
    }

    @Test
    void testPermissionCreation() {
        permission.setName("READ_USERS");
        permission.setDescription("Permission to read user data");
        permission.setResource("USER");
        permission.setAction("READ");

        assertEquals("READ_USERS", permission.getName());
        assertEquals("Permission to read user data", permission.getDescription());
        assertEquals("USER", permission.getResource());
        assertEquals("READ", permission.getAction());
    }

    @Test
    void testPermissionDefaultValues() {
        // Test default active status
        assertEquals(1, permission.getIsActive());
    }

    @Test
    void testPermissionStatus() {
        permission.setIsActive(0);
        assertEquals(0, permission.getIsActive());

        permission.setIsActive(1);
        assertEquals(1, permission.getIsActive());
    }

    @Test
    void testPermissionAuditFields() {
        permission.setCreatedBy("admin");
        permission.setModifiedBy("user1");
        permission.setOwnerId(600L);

        assertEquals("admin", permission.getCreatedBy());
        assertEquals("user1", permission.getModifiedBy());
        assertEquals(600L, permission.getOwnerId());
    }

    @Test
    void testPermissionEquality() {
        Permission permission1 = new Permission();
        Permission permission2 = new Permission();
        
        permission1.setId(1L);
        permission2.setId(1L);
        
        assertEquals(permission1.getId(), permission2.getId());
    }

    @Test
    void testPermissionToString() {
        permission.setName("TEST_PERMISSION");
        permission.setDescription("Test permission description");
        
        String toString = permission.toString();
        assertNotNull(toString);
    }

    @Test
    void testPermissionValidation() {
        // Test required fields
        permission.setName("REQUIRED_PERMISSION");
        
        assertNotNull(permission.getName());
        assertFalse(permission.getName().isEmpty());
    }

    @Test
    void testPermissionResourceAction() {
        permission.setResource("STUDENT");
        permission.setAction("MANAGE");
        assertEquals("STUDENT", permission.getResource());
        assertEquals("MANAGE", permission.getAction());

        permission.setResource("STAFF");
        permission.setAction("VIEW");
        assertEquals("STAFF", permission.getResource());
        assertEquals("VIEW", permission.getAction());
    }
}