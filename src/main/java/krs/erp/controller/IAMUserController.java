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
import krs.erp.service.EmailService;
import krs.erp.enums.EntityType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

/**
 * Controller for managing IAM users (Identity and Access Management)
 * This controller provides endpoints for user management and authentication
 */
@RestController
@RequestMapping("/api/iam")
public class IAMUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

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
                user.getEnabled())).toList());
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
                        user.getEnabled())))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new user and send invitation
     */
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getEmail() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and Email are required"));
        }

        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(null); // Add phone to DTO if needed

        // Handle UserType string to enum conversion
        try {
            if (userDTO.getUserType() != null) {
                user.setUserType(User.UserType.valueOf(userDTO.getUserType().toUpperCase()));
            } else {
                user.setUserType(User.UserType.STAFF); // Default
            }
        } catch (IllegalArgumentException e) {
            user.setUserType(User.UserType.STAFF);
        }

        user.setEnabled(userDTO.getEnabled() != null ? userDTO.getEnabled() : true);

        // Set Password
        String rawPassword = userDTO.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            // Generate random password or require it? For now require it or handle
            // gracefully
            return ResponseEntity.badRequest().body(Map.of("message", "Password is required"));
        }

        if (passwordEncoder != null) {
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
        } else {
            user.setPasswordHash("{noop}" + rawPassword); // Fallback for dev/test without security
        }

        user = userRepository.save(user);

        // Send Invitation Email
        sendInvitationEmail(user, rawPassword);

        return ResponseEntity.ok(new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                null, // Do not return hash
                user.getUserType().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getEnabled()));
    }

    /**
     * Re-invite user (resend using current password if known, or just a simplified
     * invite??)
     * For security, we usually don't resend the password.
     * We'll send a "Welcome back / Login details" email without password, or a
     * reset link.
     * The requirement says "re-invite", usually implying a new welcome email.
     * Since we can't recover the password, we'll verify if we should just send a
     * "You have access" email.
     * 
     * However, if the user was just created and didn't get the email, the admin
     * might want to resend.
     * Use a generic "Your account is ready" email.
     */
    @PostMapping("/users/{id}/reinvite")
    public ResponseEntity<?> reinviteUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    sendInvitationEmail(user, null); // Cannot send password
                    return ResponseEntity.ok(Map.of("message", "Invitation sent successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private void sendInvitationEmail(User user, String rawPassword) {
        String subject = "Welcome to ERP System";
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(user.getFirstName()).append(",\n\n");
        body.append("Your account has been created successfully.\n\n");
        body.append("Username: ").append(user.getUsername()).append("\n");
        if (rawPassword != null) {
            body.append("Password: ").append(rawPassword).append("\n");
        } else {
            body.append("Password: (Hidden for security - please use your existing password or reset it)\n");
        }
        body.append("\nLogin here: http://localhost:4200/login\n"); // TODO: Make URL configurable
        body.append("\nRegards,\nERP Team");

        if (user.getId() != null) {
            emailService.sendDirectEmail(
                    EntityType.GENERAL, // utilizing GENERAL for system user emails
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    subject,
                    body.toString(),
                    "System");
        }
    }

}
