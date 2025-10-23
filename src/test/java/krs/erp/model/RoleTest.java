package krs.erp.model;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Role model
 */
class RoleTest {

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
    }

    @Test
    void testRoleCreation() {
        role.setName("ADMIN");
        role.setDescription("Administrator role");

        assertEquals("ADMIN", role.getName());
        assertEquals("Administrator role", role.getDescription());
    }

    @Test
    void testRoleDefaultValues() {
        // Test default enabled status
        assertEquals(1, role.getIsActive());
    }

    @Test
    void testRoleStatus() {
        role.setIsActive(0);
        assertEquals(0, role.getIsActive());

        role.setIsActive(1);
        assertEquals(1, role.getIsActive());
    }

    @Test
    void testRolePermissions() {
        // Create some permissions
        Permission permission1 = new Permission();
        permission1.setName("READ_USERS");
        
        Permission permission2 = new Permission();
        permission2.setName("WRITE_USERS");

        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission1);
        permissions.add(permission2);

        role.setPermissions(permissions);

        assertEquals(2, role.getPermissions().size());
        assertTrue(role.getPermissions().contains(permission1));
        assertTrue(role.getPermissions().contains(permission2));
    }

    @Test
    void testRoleAuditFields() {
        role.setCreatedBy("admin");
        role.setModifiedBy("user1");
        role.setOwnerId(500L);

        assertEquals("admin", role.getCreatedBy());
        assertEquals("user1", role.getModifiedBy());
        assertEquals(500L, role.getOwnerId());
    }

    @Test
    void testRoleEquality() {
        Role role1 = new Role();
        Role role2 = new Role();
        
        role1.setId(1L);
        role2.setId(1L);
        
        assertEquals(role1.getId(), role2.getId());
    }

    @Test
    void testRoleToString() {
        role.setName("TEST_ROLE");
        role.setDescription("Test role description");
        
        String toString = role.toString();
        assertNotNull(toString);
    }

    @Test
    void testRoleValidation() {
        // Test required fields
        role.setName("REQUIRED_ROLE");
        
        assertNotNull(role.getName());
        assertFalse(role.getName().isEmpty());
    }

    @Test
    void testRoleHierarchy() {
        // Test role hierarchy functionality if implemented
        role.setName("MANAGER");
        role.setDescription("Manager role");
        
        assertEquals("MANAGER", role.getName());
        assertEquals("Manager role", role.getDescription());
    }
}