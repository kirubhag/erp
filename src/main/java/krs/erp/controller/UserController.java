package krs.erp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import krs.erp.model.User;
import krs.erp.repository.UserRepository;

/**
 * REST Controller for User CRUD operations
 * Provides endpoints for managing IAM users with full CRUD functionality
 */
@RestController
@RequestMapping("/settings/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Get all users
     * 
     * @return List of all users
     */
    @PermitAll
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get user by ID
     * 
     * @param id User ID
     * @return User details or 404 if not found
     */
    @PermitAll
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get user by username
     * 
     * @param username Username
     * @return User details or 404 if not found
     */
    @PermitAll
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get user by email
     * 
     * @param email Email address
     * @return User details or 404 if not found
     */
    @PermitAll
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all users by type
     * 
     * @param userType User type (STUDENT, STAFF, PARENT, ADMIN)
     * @return List of users of specified type
     */
    @PermitAll
    @GetMapping("/type/{userType}")
    public ResponseEntity<List<User>> getUsersByType(@PathVariable String userType) {
        try {
            User.UserType type = User.UserType.valueOf(userType.toUpperCase());
            List<User> users = userRepository.findByUserType(type);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all enabled users
     * 
     * @return List of enabled users
     */
    @PermitAll
    @GetMapping("/status/enabled")
    public ResponseEntity<List<User>> getEnabledUsers() {
        List<User> users = userRepository.findByEnabledTrue();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get all disabled users
     * 
     * @return List of disabled users
     */
    @PermitAll
    @GetMapping("/status/disabled")
    public ResponseEntity<List<User>> getDisabledUsers() {
        List<User> users = userRepository.findByEnabledFalse();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Create new user
     * 
     * @param user User details
     * @return Created user with 201 status
     */
    @PermitAll
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        try {
            // Check if username already exists
            if (userRepository.existsByUsername(user.getUsername())) {
                return ResponseEntity.badRequest().build();
            }
            
            // Check if email already exists
            if (userRepository.existsByEmail(user.getEmail())) {
                return ResponseEntity.badRequest().build();
            }
            
            // Encode password before saving
            String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
            user.setPasswordHash(encodedPassword);
            
            User savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Update user
     * 
     * @param id User ID
     * @param userDetails Updated user details
     * @return Updated user or 404 if not found
     */
    @PermitAll
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Update fields if provided
            if (userDetails.getFirstName() != null) {
                user.setFirstName(userDetails.getFirstName());
            }
            if (userDetails.getLastName() != null) {
                user.setLastName(userDetails.getLastName());
            }
            if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
                // Check if new email already exists
                if (userRepository.existsByEmail(userDetails.getEmail())) {
                    return ResponseEntity.badRequest().build();
                }
                user.setEmail(userDetails.getEmail());
            }
            if (userDetails.getPhone() != null) {
                user.setPhone(userDetails.getPhone());
            }
            if (userDetails.getUserType() != null) {
                user.setUserType(userDetails.getUserType());
            }
            if (userDetails.getEnabled() != null) {
                user.setEnabled(userDetails.getEnabled());
            }
            if (userDetails.getAccountNonExpired() != null) {
                user.setAccountNonExpired(userDetails.getAccountNonExpired());
            }
            if (userDetails.getCredentialsNonExpired() != null) {
                user.setCredentialsNonExpired(userDetails.getCredentialsNonExpired());
            }
            if (userDetails.getAccountNonLocked() != null) {
                user.setAccountNonLocked(userDetails.getAccountNonLocked());
            }
            
            // Update password if provided
            if (userDetails.getPasswordHash() != null && !userDetails.getPasswordHash().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(userDetails.getPasswordHash());
                user.setPasswordHash(encodedPassword);
            }
            
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Delete user
     * 
     * @param id User ID
     * @return 204 No Content if deleted, 404 if not found
     */
    @PermitAll
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Get locked users
     * 
     * @return List of locked users
     */
    @PermitAll
    @GetMapping("/locked/users")
    public ResponseEntity<List<User>> getLockedUsers() {
        List<User> lockedUsers = userRepository.findLockedUsers();
        return ResponseEntity.ok(lockedUsers);
    }
    
    /**
     * Get users with expired credentials
     * 
     * @return List of users with expired credentials
     */
    @PermitAll
    @GetMapping("/credentials/expired")
    public ResponseEntity<List<User>> getUsersWithExpiredCredentials() {
        List<User> expiredUsers = userRepository.findUsersWithExpiredCredentials();
        return ResponseEntity.ok(expiredUsers);
    }
    
    /**
     * Search users by name
     * 
     * @param name First or last name to search
     * @return List of matching users
     */
    @PermitAll
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String name) {
        List<User> users = userRepository.findByNameContaining(name);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Count active users by type
     * 
     * @param userType User type
     * @return Count of active users
     */
    @PermitAll
    @GetMapping("/count/{userType}")
    public ResponseEntity<Long> countActiveUsersByType(@PathVariable String userType) {
        try {
            User.UserType type = User.UserType.valueOf(userType.toUpperCase());
            Long count = userRepository.countActiveUsersByType(type);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
