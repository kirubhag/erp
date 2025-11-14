package krs.erp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.dto.UserDTO;
import krs.erp.model.User;
import krs.erp.repository.UserRepository;

/**
 * Controller for managing IAM users (Identity and Access Management)
 * This controller provides endpoints for user management and authentication
 */
@RestController
@RequestMapping("/api/iam")
public class IAMUserController {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get all users with their credentials (FOR TESTING ONLY)
     * Shows username, email, password hash, and user type
     * 
     * @return List of all users in the system
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        
        // Return user information including password hashes
        return ResponseEntity.ok(users.stream().map(user -> new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPasswordHash(),
            user.getUserType().toString(),
            user.getFirstName(),
            user.getLastName(),
            user.getEnabled()
        )).toList());
    }
    
    /**
     * Get a specific user by username
     */
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(user -> ResponseEntity.ok(new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getUserType().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getEnabled()
            )))
            .orElse(ResponseEntity.notFound().build());
    }
    
}
