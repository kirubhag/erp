package krs.erp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for User model
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testUserCreation() {
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPasswordHash("hashedpassword123");

        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("hashedpassword123", user.getPasswordHash());
    }

    @Test
    void testUserDefaultValues() {
        // Test default enabled status
        assertTrue(user.getEnabled());
        
        // Test default account non-expired
        assertTrue(user.getAccountNonExpired());
        
        // Test default account non-locked
        assertTrue(user.getAccountNonLocked());
        
        // Test default credentials non-expired
        assertTrue(user.getCredentialsNonExpired());
    }

    @Test
    void testUserStatus() {
        user.setEnabled(false);
        assertFalse(user.getEnabled());

        user.setAccountNonExpired(false);
        assertFalse(user.getAccountNonExpired());

        user.setAccountNonLocked(false);
        assertFalse(user.getAccountNonLocked());

        user.setCredentialsNonExpired(false);
        assertFalse(user.getCredentialsNonExpired());
    }

    @Test
    void testUserFullName() {
        user.setFirstName("Jane");
        user.setLastName("Smith");

        String expectedFullName = "Jane Smith";
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    void testUserAuditFields() {
        user.setCreatedBy("admin");
        user.setModifiedBy("user1");
        user.setOwnerId(100L);
        user.setIsActive(true);

        assertEquals("admin", user.getCreatedBy());
        assertEquals("user1", user.getModifiedBy());
        assertEquals(100L, user.getOwnerId());
        assertTrue(user.getIsActive());
    }

    @Test
    void testUserValidation() {
        // Test username validation
        user.setUsername("");
        assertEquals("", user.getUsername());

        // Test email validation
        user.setEmail("invalid-email");
        assertEquals("invalid-email", user.getEmail());
        
        // In real scenario, validation would be handled by Bean Validation
    }

    @Test
    void testUserEquality() {
        User user1 = new User();
        User user2 = new User();
        
        user1.setId(1L);
        user2.setId(1L);
        
        assertEquals(user1.getId(), user2.getId());
    }

    @Test
    void testUserToString() {
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        String toString = user.toString();
        assertNotNull(toString);
    }
}