package krs.erp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.User;

/**
 * Unit tests for UserRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    private User testUser1;
    private User testUser2;
    private User testUser3;
    
    @BeforeEach
    void setUp() {
        // Use deleteAllInBatch for better performance in tests
        // This will ignore foreign key constraints temporarily
        userRepository.deleteAllInBatch();
        
        // Create test user 1 - Admin
        testUser1 = new User();
        testUser1.setUsername("testadmin");
        testUser1.setPasswordHash("$2a$10$hashedPassword1");
        testUser1.setEmail("testadmin@test.com");
        testUser1.setFirstName("Admin");
        testUser1.setLastName("User");
        testUser1.setUserType(User.UserType.ADMIN);
        testUser1.setEnabled(true);
        testUser1.setAccountNonExpired(true);
        testUser1.setAccountNonLocked(true);
        testUser1.setCredentialsNonExpired(true);
        testUser1.setCreatedTime(LocalDateTime.now());
        testUser1 = userRepository.save(testUser1);
        
        // Create test user 2 - Teacher (enabled)
        testUser2 = new User();
        testUser2.setUsername("teacher1");
        testUser2.setPasswordHash("$2a$10$hashedPassword2");
        testUser2.setEmail("teacher1@erp.com");
        testUser2.setFirstName("John");
        testUser2.setLastName("Doe");
        testUser2.setPhone("1234567890");
        testUser2.setUserType(User.UserType.STAFF);
        testUser2.setEnabled(true);
        testUser2.setAccountNonExpired(true);
        testUser2.setAccountNonLocked(true);
        testUser2.setCredentialsNonExpired(true);
        testUser2.setCreatedTime(LocalDateTime.now());
        testUser2 = userRepository.save(testUser2);
        
        // Create test user 3 - Student (disabled)
        testUser3 = new User();
        testUser3.setUsername("student1");
        testUser3.setPasswordHash("$2a$10$hashedPassword3");
        testUser3.setEmail("student1@erp.com");
        testUser3.setFirstName("Jane");
        testUser3.setLastName("Smith");
        testUser3.setUserType(User.UserType.STUDENT);
        testUser3.setEnabled(false);
        testUser3.setAccountNonExpired(true);
        testUser3.setAccountNonLocked(false); // Locked account
        testUser3.setCredentialsNonExpired(false); // Expired credentials
        testUser3.setCreatedTime(LocalDateTime.now());
        testUser3 = userRepository.save(testUser3);
    }
    
    @Test
    void testFindByUsername() {
        Optional<User> found = userRepository.findByUsername("testadmin");
        assertTrue(found.isPresent());
        assertEquals("Admin", found.get().getFirstName());
    }
    
    @Test
    void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("teacher1@erp.com");
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
    }
    
    @Test
    void testFindByUserType() {
        List<User> staff = userRepository.findByUserType(User.UserType.STAFF);
        assertEquals(1, staff.size());
        assertEquals("John", staff.get(0).getFirstName());
    }
    
    @Test
    void testFindByEnabledTrue() {
        List<User> enabledUsers = userRepository.findByEnabledTrue();
        assertEquals(2, enabledUsers.size());
    }
    
    @Test
    void testFindByEnabledFalse() {
        List<User> disabledUsers = userRepository.findByEnabledFalse();
        assertEquals(1, disabledUsers.size());
        assertEquals("student1", disabledUsers.get(0).getUsername());
    }
    
    @Test
    void testFindByNameContaining() {
        List<User> users = userRepository.findByNameContaining("John");
        assertEquals(1, users.size());
        assertEquals("John", users.get(0).getFirstName());
        
        // Test case insensitive search
        users = userRepository.findByNameContaining("john");
        assertEquals(1, users.size());
    }
    
    @Test
    void testCountActiveUsersByType() {
        Long count = userRepository.countActiveUsersByType(User.UserType.STAFF);
        assertEquals(1L, count);
        
        count = userRepository.countActiveUsersByType(User.UserType.ADMIN);
        assertEquals(1L, count);
        
        count = userRepository.countActiveUsersByType(User.UserType.STUDENT);
        assertEquals(0L, count); // Student is disabled
    }
    
    @Test
    void testFindLockedUsers() {
        List<User> lockedUsers = userRepository.findLockedUsers();
        assertEquals(1, lockedUsers.size());
        assertEquals("student1", lockedUsers.get(0).getUsername());
    }
    
    @Test
    void testFindUsersWithExpiredCredentials() {
        List<User> expiredUsers = userRepository.findUsersWithExpiredCredentials();
        assertEquals(1, expiredUsers.size());
        assertEquals("student1", expiredUsers.get(0).getUsername());
    }
    
    @Test
    void testExistsByUsername() {
        assertTrue(userRepository.existsByUsername("testadmin"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }
    
    @Test
    void testExistsByEmail() {
        assertTrue(userRepository.existsByEmail("testadmin@test.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@erp.com"));
    }
    
    @Test
    void testSaveUser() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPasswordHash("$2a$10$hashedPassword");
        newUser.setEmail("newuser@erp.com");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setUserType(User.UserType.PARENT);
        newUser.setEnabled(true);
        newUser.setAccountNonExpired(true);
        newUser.setAccountNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        newUser.setCreatedTime(LocalDateTime.now());
        
        User saved = userRepository.save(newUser);
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
    }
    
    @Test
    void testUpdateUser() {
        testUser1.setPhone("9876543210");
        User updated = userRepository.save(testUser1);
        assertEquals("9876543210", updated.getPhone());
    }
    
    @Test
    void testDeleteUser() {
        Long userId = testUser1.getId();
        userRepository.delete(testUser1);
        assertFalse(userRepository.findById(userId).isPresent());
    }
    
    @Test
    void testFindAll() {
        List<User> allUsers = userRepository.findAll();
        assertEquals(3, allUsers.size());
    }
}
