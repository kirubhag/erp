package krs.erp.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import krs.erp.model.User;
import krs.erp.repository.UserRepository;

/**
 * Unit tests for UserController
 * Tests all CRUD operations and custom query endpoints
 */
@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private UserRepository userRepository;
    
    @MockitoBean
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private User testUser;
    private User testUser2;
    
    @BeforeEach
    void setUp() {
        testUser = createTestUser(1L, "john_doe", "john@example.com", "John", "Doe", User.UserType.STAFF);
        testUser2 = createTestUser(2L, "jane_smith", "jane@example.com", "Jane", "Smith", User.UserType.STUDENT);
    }
    
    // ==================== GET ALL USERS ====================
    
    @Test
    @WithMockUser
    void testGetAllUsers() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser, testUser2);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 2);
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);
        
        // When & Then
        mockMvc.perform(get("/settings/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].username").value("john_doe"))
                .andExpect(jsonPath("$.content[1].username").value("jane_smith"));
        
        verify(userRepository).findAll(any(PageRequest.class));
    }
    
    @Test
    @WithMockUser
    void testGetAllUsersEmptyList() throws Exception {
        // Given
        Page<User> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);
        
        // When & Then
        mockMvc.perform(get("/settings/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
    
    // ==================== GET USER BY ID ====================
    
    @Test
    @WithMockUser
    void testGetUserById() throws Exception {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // When & Then
        mockMvc.perform(get("/settings/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
        
        verify(userRepository).findById(1L);
    }
    
    @Test
    @WithMockUser
    void testGetUserByIdNotFound() throws Exception {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/settings/users/999"))
                .andExpect(status().isNotFound());
        
        verify(userRepository).findById(999L);
    }
    
    // ==================== GET USER BY USERNAME ====================
    
    @Test
    @WithMockUser
    void testGetUserByUsername() throws Exception {
        // Given
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        
        // When & Then
        mockMvc.perform(get("/settings/users/username/john_doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
        
        verify(userRepository).findByUsername("john_doe");
    }
    
    @Test
    @WithMockUser
    void testGetUserByUsernameNotFound() throws Exception {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/settings/users/username/nonexistent"))
                .andExpect(status().isNotFound());
    }
    
    // ==================== GET USER BY EMAIL ====================
    
    @Test
    @WithMockUser
    void testGetUserByEmail() throws Exception {
        // Given
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        
        // When & Then
        mockMvc.perform(get("/settings/users/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"));
        
        verify(userRepository).findByEmail("john@example.com");
    }
    
    // ==================== GET USERS BY TYPE ====================
    
    @Test
    @WithMockUser
    void testGetUsersByType() throws Exception {
        // Given
        List<User> staffUsers = Arrays.asList(testUser);
        when(userRepository.findByUserType(User.UserType.STAFF)).thenReturn(staffUsers);
        
        // When & Then
        mockMvc.perform(get("/settings/users/type/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userType").value("STAFF"));
        
        verify(userRepository).findByUserType(User.UserType.STAFF);
    }
    
    @Test
    @WithMockUser
    void testGetUsersByTypeInvalidType() throws Exception {
        // When & Then
        mockMvc.perform(get("/settings/users/type/invalid"))
                .andExpect(status().isBadRequest());
    }
    
    // ==================== GET ENABLED/DISABLED USERS ====================
    
    @Test
    @WithMockUser
    void testGetEnabledUsers() throws Exception {
        // Given
        List<User> enabledUsers = Arrays.asList(testUser, testUser2);
        when(userRepository.findByEnabledTrue()).thenReturn(enabledUsers);
        
        // When & Then
        mockMvc.perform(get("/settings/users/status/enabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        
        verify(userRepository).findByEnabledTrue();
    }
    
    @Test
    @WithMockUser
    void testGetDisabledUsers() throws Exception {
        // Given
        List<User> disabledUsers = Arrays.asList();
        when(userRepository.findByEnabledFalse()).thenReturn(disabledUsers);
        
        // When & Then
        mockMvc.perform(get("/settings/users/status/disabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    
    // ==================== CREATE USER ====================
    
    @Test
    @WithMockUser
    void testCreateUser() throws Exception {
        // Given
        User newUser = createTestUser(null, "new_user", "new@example.com", "New", "User", User.UserType.STUDENT);
        User savedUser = createTestUser(3L, "new_user", "new@example.com", "New", "User", User.UserType.STUDENT);
        
        when(userRepository.existsByUsername("new_user")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When & Then
        mockMvc.perform(post("/settings/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.username").value("new_user"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
        
        verify(userRepository).existsByUsername("new_user");
        verify(userRepository).existsByEmail("new@example.com");
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @WithMockUser
    void testCreateUserDuplicateUsername() throws Exception {
        // Given
        User newUser = createTestUser(null, "john_doe", "different@example.com", "John", "Smith", User.UserType.STUDENT);
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/settings/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest());
        
        verify(userRepository).existsByUsername("john_doe");
    }
    
    @Test
    @WithMockUser
    void testCreateUserDuplicateEmail() throws Exception {
        // Given
        User newUser = createTestUser(null, "different_user", "john@example.com", "John", "Smith", User.UserType.STUDENT);
        when(userRepository.existsByUsername("different_user")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/settings/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest());
    }
    
    // ==================== UPDATE USER ====================
    
    @Test
    @WithMockUser
    void testUpdateUser() throws Exception {
        // Given
        User updateDetails = createTestUser(1L, "john_doe", "newemail@example.com", "John", "Updated", User.UserType.STAFF);
        User updatedUser = createTestUser(1L, "john_doe", "newemail@example.com", "John", "Updated", User.UserType.STAFF);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_new_password");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        
        // When & Then
        mockMvc.perform(put("/settings/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lastName").value("Updated"));
        
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @WithMockUser
    void testUpdateUserNotFound() throws Exception {
        // Given
        User updateDetails = createTestUser(null, "john_doe", "newemail@example.com", "John", "Updated", User.UserType.STAFF);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(put("/settings/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }
    
    // ==================== DELETE USER ====================
    
    @Test
    @WithMockUser
    void testDeleteUser() throws Exception {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        
        // When & Then
        mockMvc.perform(delete("/settings/users/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
        
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }
    
    @Test
    @WithMockUser
    void testDeleteUserNotFound() throws Exception {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);
        
        // When & Then
        mockMvc.perform(delete("/settings/users/999")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
        
        verify(userRepository).existsById(999L);
    }
    
    // ==================== LOCKED USERS ====================
    
    @Test
    @WithMockUser
    void testGetLockedUsers() throws Exception {
        // Given
        User lockedUser = createTestUser(4L, "locked_user", "locked@example.com", "Locked", "User", User.UserType.STUDENT);
        lockedUser.setAccountNonLocked(false);
        List<User> lockedUsers = Arrays.asList(lockedUser);
        when(userRepository.findLockedUsers()).thenReturn(lockedUsers);
        
        // When & Then
        mockMvc.perform(get("/settings/users/locked/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountNonLocked").value(false));
        
        verify(userRepository).findLockedUsers();
    }
    
    // ==================== EXPIRED CREDENTIALS ====================
    
    @Test
    @WithMockUser
    void testGetUsersWithExpiredCredentials() throws Exception {
        // Given
        User expiredUser = createTestUser(5L, "expired_user", "expired@example.com", "Expired", "User", User.UserType.STUDENT);
        expiredUser.setCredentialsNonExpired(false);
        List<User> expiredUsers = Arrays.asList(expiredUser);
        when(userRepository.findUsersWithExpiredCredentials()).thenReturn(expiredUsers);
        
        // When & Then
        mockMvc.perform(get("/settings/users/credentials/expired"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].credentialsNonExpired").value(false));
        
        verify(userRepository).findUsersWithExpiredCredentials();
    }
    
    // ==================== SEARCH USERS ====================
    
    @Test
    @WithMockUser
    void testSearchUsersByName() throws Exception {
        // Given
        List<User> searchResults = Arrays.asList(testUser);
        when(userRepository.findByNameContaining("john")).thenReturn(searchResults);
        
        // When & Then
        mockMvc.perform(get("/settings/users/search")
                .param("name", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("John"));
        
        verify(userRepository).findByNameContaining("john");
    }
    
    @Test
    @WithMockUser
    void testSearchUsersByNameNoResults() throws Exception {
        // Given
        when(userRepository.findByNameContaining("xyz")).thenReturn(Arrays.asList());
        
        // When & Then
        mockMvc.perform(get("/settings/users/search")
                .param("name", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    
    // ==================== COUNT ACTIVE USERS BY TYPE ====================
    
    @Test
    @WithMockUser
    void testCountActiveUsersByType() throws Exception {
        // Given
        when(userRepository.countActiveUsersByType(User.UserType.STAFF)).thenReturn(5L);
        
        // When & Then
        mockMvc.perform(get("/settings/users/count/staff"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
        
        verify(userRepository).countActiveUsersByType(User.UserType.STAFF);
    }
    
    @Test
    @WithMockUser
    void testCountActiveUsersByTypeInvalidType() throws Exception {
        // When & Then
        mockMvc.perform(get("/settings/users/count/invalid"))
                .andExpect(status().isBadRequest());
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Helper method to create a test user
     */
    private User createTestUser(Long id, String username, String email, String firstName, 
                               String lastName, User.UserType userType) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserType(userType);
        user.setPasswordHash("password123");
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        return user;
    }
}
