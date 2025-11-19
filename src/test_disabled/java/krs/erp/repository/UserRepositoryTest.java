package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import krs.erp.model.User;

/**
 * Unit tests for UserRepository
 * Tests all custom query methods for User entity CRUD operations
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    private User testUser1;
    private User testUser2;
    private User testUser3;
    
    @BeforeEach
    void setUp() {
        // Clear repository before each test
        userRepository.deleteAll();
        
        // Create test users
        testUser1 = createUser("john_doe", "john@example.com", "John", "Doe", User.UserType.STAFF, true);
        testUser2 = createUser("jane_smith", "jane@example.com", "Jane", "Smith", User.UserType.STUDENT, true);
        testUser3 = createUser("admin_user", "admin@example.com", "Admin", "User", User.UserType.ADMIN, true);
        
        // Save test users
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        userRepository.save(testUser3);
    }
    
    // ==================== BASIC CRUD TESTS ====================
    
    @Test
    void testSaveUser() {
        // Given
        User newUser = createUser("test_user", "test@example.com", "Test", "User", User.UserType.STUDENT, true);
        
        // When
        User savedUser = userRepository.save(newUser);
        
        // Then
        assertNotNull(savedUser.getId());
        assertEquals("test_user", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
    }
    
    @Test
    void testUpdateUser() {
        // Given
        testUser1.setEmail("newemail@example.com");
        testUser1.setFirstName("UpdatedJohn");
        
        // When
        User updatedUser = userRepository.save(testUser1);
        
        // Then
        assertEquals("newemail@example.com", updatedUser.getEmail());
        assertEquals("UpdatedJohn", updatedUser.getFirstName());
    }
    
    @Test
    void testDeleteUser() {
        // Given
        Long userId = testUser1.getId();
        
        // When
        userRepository.deleteById(userId);
        
        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }
    
    @Test
    void testGetUserById() {
        // When
        Optional<User> foundUser = userRepository.findById(testUser1.getId());
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("john_doe", foundUser.get().getUsername());
    }
    
    @Test
    void testGetAllUsers() {
        // When
        List<User> users = userRepository.findAll();
        
        // Then
        assertEquals(3, users.size());
    }
    
    // ==================== FIND BY USERNAME ====================
    
    @Test
    void testFindByUsername() {
        // When
        Optional<User> foundUser = userRepository.findByUsername("john_doe");
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("john@example.com", foundUser.get().getEmail());
    }
    
    @Test
    void testFindByUsernameNotFound() {
        // When
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");
        
        // Then
        assertFalse(foundUser.isPresent());
    }
    
    // ==================== FIND BY EMAIL ====================
    
    @Test
    void testFindByEmail() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("jane@example.com");
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("jane_smith", foundUser.get().getUsername());
    }
    
    @Test
    void testFindByEmailNotFound() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("notfound@example.com");
        
        // Then
        assertFalse(foundUser.isPresent());
    }
    
    // ==================== FIND BY USER TYPE ====================
    
    @Test
    void testFindByUserTypeStaff() {
        // When
        List<User> staffUsers = userRepository.findByUserType(User.UserType.STAFF);
        
        // Then
        assertEquals(1, staffUsers.size());
        assertEquals("john_doe", staffUsers.get(0).getUsername());
    }
    
    @Test
    void testFindByUserTypeStudent() {
        // When
        List<User> studentUsers = userRepository.findByUserType(User.UserType.STUDENT);
        
        // Then
        assertEquals(1, studentUsers.size());
        assertEquals("jane_smith", studentUsers.get(0).getUsername());
    }
    
    @Test
    void testFindByUserTypeAdmin() {
        // When
        List<User> adminUsers = userRepository.findByUserType(User.UserType.ADMIN);
        
        // Then
        assertEquals(1, adminUsers.size());
        assertEquals("admin_user", adminUsers.get(0).getUsername());
    }
    
    // ==================== FIND BY ENABLED STATUS ====================
    
    @Test
    void testFindByEnabledTrue() {
        // When
        List<User> enabledUsers = userRepository.findByEnabledTrue();
        
        // Then
        assertEquals(3, enabledUsers.size());
    }
    
    @Test
    void testFindByEnabledFalse() {
        // Given
        testUser1.setEnabled(false);
        userRepository.save(testUser1);
        
        // When
        List<User> disabledUsers = userRepository.findByEnabledFalse();
        
        // Then
        assertEquals(1, disabledUsers.size());
        assertEquals("john_doe", disabledUsers.get(0).getUsername());
    }
    
    // ==================== EXISTS TESTS ====================
    
    @Test
    void testExistsByUsername() {
        // When
        boolean exists = userRepository.existsByUsername("john_doe");
        
        // Then
        assertTrue(exists);
    }
    
    @Test
    void testExistsByUsernameNotFound() {
        // When
        boolean exists = userRepository.existsByUsername("nonexistent");
        
        // Then
        assertFalse(exists);
    }
    
    @Test
    void testExistsByEmail() {
        // When
        boolean exists = userRepository.existsByEmail("jane@example.com");
        
        // Then
        assertTrue(exists);
    }
    
    @Test
    void testExistsByEmailNotFound() {
        // When
        boolean exists = userRepository.existsByEmail("notfound@example.com");
        
        // Then
        assertFalse(exists);
    }
    
    // ==================== LOCKED USERS ====================
    
    @Test
    void testFindLockedUsers() {
        // Given
        testUser1.setAccountNonLocked(false);
        testUser2.setAccountNonLocked(false);
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        
        // When
        List<User> lockedUsers = userRepository.findLockedUsers();
        
        // Then
        assertEquals(2, lockedUsers.size());
    }
    
    @Test
    void testFindLockedUsersNone() {
        // When
        List<User> lockedUsers = userRepository.findLockedUsers();
        
        // Then
        assertEquals(0, lockedUsers.size());
    }
    
    // ==================== EXPIRED CREDENTIALS ====================
    
    @Test
    void testFindUsersWithExpiredCredentials() {
        // Given
        testUser1.setCredentialsNonExpired(false);
        testUser3.setCredentialsNonExpired(false);
        userRepository.save(testUser1);
        userRepository.save(testUser3);
        
        // When
        List<User> expiredUsers = userRepository.findUsersWithExpiredCredentials();
        
        // Then
        assertEquals(2, expiredUsers.size());
    }
    
    @Test
    void testFindUsersWithExpiredCredentialsNone() {
        // When
        List<User> expiredUsers = userRepository.findUsersWithExpiredCredentials();
        
        // Then
        assertEquals(0, expiredUsers.size());
    }
    
    // ==================== FIND BY NAME CONTAINING ====================
    
    @Test
    void testFindByNameContainingFirstName() {
        // When
        List<User> users = userRepository.findByNameContaining("John");
        
        // Then
        assertEquals(1, users.size());
        assertEquals("john_doe", users.get(0).getUsername());
    }
    
    @Test
    void testFindByNameContainingLastName() {
        // When
        List<User> users = userRepository.findByNameContaining("Smith");
        
        // Then
        assertEquals(1, users.size());
        assertEquals("jane_smith", users.get(0).getUsername());
    }
    
    @Test
    void testFindByNameContainingMultipleResults() {
        // When
        List<User> users = userRepository.findByNameContaining("User");
        
        // Then
        assertEquals(1, users.size());
        assertEquals("admin_user", users.get(0).getUsername());
    }
    
    @Test
    void testFindByNameContainingNoResults() {
        // When
        List<User> users = userRepository.findByNameContaining("xyz");
        
        // Then
        assertEquals(0, users.size());
    }
    
    // ==================== COUNT ACTIVE USERS BY TYPE ====================
    
    @Test
    void testCountActiveUsersByTypeStaff() {
        // When
        Long count = userRepository.countActiveUsersByType(User.UserType.STAFF);
        
        // Then
        assertEquals(1L, count);
    }
    
    @Test
    void testCountActiveUsersByTypeStudent() {
        // When
        Long count = userRepository.countActiveUsersByType(User.UserType.STUDENT);
        
        // Then
        assertEquals(1L, count);
    }
    
    @Test
    void testCountActiveUsersByTypeDisabledUsers() {
        // Given
        testUser2.setEnabled(false);
        userRepository.save(testUser2);
        
        // When
        Long count = userRepository.countActiveUsersByType(User.UserType.STUDENT);
        
        // Then
        assertEquals(0L, count);
    }
    
    @Test
    void testCountActiveUsersByTypeNoUsers() {
        // When
        Long count = userRepository.countActiveUsersByType(User.UserType.PARENT);
        
        // Then
        assertEquals(0L, count);
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Helper method to create a test user
     */
    private User createUser(String username, String email, String firstName, String lastName, 
                           User.UserType userType, boolean enabled) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserType(userType);
        user.setPasswordHash("password123");
        user.setEnabled(enabled);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        return user;
    }
}
