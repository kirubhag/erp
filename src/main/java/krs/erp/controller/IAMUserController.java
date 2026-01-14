package krs.erp.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.config.CustomUserDetails;
import krs.erp.config.multitenant.TenantContext;
import krs.erp.dto.UserDTO;
import krs.erp.enums.EntityType;
import krs.erp.model.User;
import krs.erp.repository.UserRepository;
import krs.erp.service.EmailService;

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

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

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
                user.getPhone(),
                user.getEnabled(),
                user.getIsPrimaryUser())).toList());
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
                        user.getPhone(),
                        user.getEnabled(),
                        user.getIsPrimaryUser())))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new user and send invitation email with password setup link
     * User will set their own password via the invitation link
     */
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getEmail() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and Email are required"));
        }

        // Check if username already exists in Master DB
        if (usernameExistsInMasterDb(userDTO.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
        }

        // Check if email already exists in Master DB
        if (emailExistsInMasterDb(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
        }

        // Get current user's tenant ID and organization ID
        Long tenantId = getCurrentTenantId();
        Long organizationId = getCurrentOrganizationId();

        if (tenantId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Unable to determine tenant. Please login again."));
        }

        // Generate invitation token
        String invitationToken = UUID.randomUUID().toString();

        // Handle UserType string to enum conversion
        User.UserType userType;
        try {
            if (userDTO.getUserType() != null) {
                userType = User.UserType.valueOf(userDTO.getUserType().toUpperCase());
            } else {
                userType = User.UserType.STAFF; // Default
            }
        } catch (IllegalArgumentException e) {
            userType = User.UserType.STAFF;
        }

        // 1. Create user in Master DB first (with invitation token, no password yet)
        Long userId = createUserInMasterDb(userDTO, tenantId, organizationId, invitationToken, userType);

        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to create user in master database"));
        }

        // 2. Create user in Tenant DB
        User user = new User();
        user.setId(userId); // Use same ID as master DB
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        user.setUserType(userType);
        user.setEnabled(false); // Disabled until password is set
        user.setTenantId(tenantId);
        user.setOrganizationId(organizationId);
        user.setPasswordHash(""); // No password yet - will be set via invitation
        user.setConfirmationToken(invitationToken);
        user.setCreatedTime(LocalDateTime.now());
        user.setIsActive(1);

        try {
            userRepository.save(user);
        } catch (Exception e) {
            // Log but don't fail - Master DB is the source of truth for auth
            System.err.println("Warning: Failed to save user to tenant DB: " + e.getMessage());
        }

        // 3. Send Invitation Email with password setup link
        sendInvitationEmail(user, invitationToken);

        return ResponseEntity.ok(new UserDTO(
                userId,
                user.getUsername(),
                user.getEmail(),
                null, // Do not return hash
                user.getUserType().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getEnabled(),
                false)); // New users are not primary users
    }

    /**
     * Get current authenticated user's tenant ID
     */
    private Long getCurrentTenantId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getTenantId();
        }
        // Fallback to TenantContext
        String tenantIdStr = TenantContext.getCurrentTenant();
        if (tenantIdStr != null) {
            try {
                return Long.parseLong(tenantIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Get current authenticated user's organization ID
     */
    private Long getCurrentOrganizationId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            // Get organization from current user in DB
            return userRepository.findById(userDetails.getUserId())
                    .map(User::getOrganizationId)
                    .orElse(1L); // Default to 1 if not found
        }
        return 1L;
    }

    /**
     * Create user in Master DB
     */
    private Long createUserInMasterDb(UserDTO userDTO, Long tenantId, Long organizationId,
            String invitationToken, User.UserType userType) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);

        String sql = "INSERT INTO iam_users (username, password_hash, email, first_name, last_name, phone, " +
                "user_type, enabled, tenant_id, organization_id, created_time, is_active, " +
                "account_non_expired, credentials_non_expired, account_non_locked, confirmation_token) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, 1, 1, 1, ?)";

        try {
            jdbcTemplate.update(sql,
                    userDTO.getUsername(),
                    "", // No password - will be set via invitation
                    userDTO.getEmail(),
                    userDTO.getFirstName() != null ? userDTO.getFirstName() : "",
                    userDTO.getLastName() != null ? userDTO.getLastName() : "",
                    userDTO.getPhone(),
                    userType.name(),
                    false, // Disabled until password is set
                    tenantId,
                    organizationId,
                    LocalDateTime.now(),
                    invitationToken);

            // Get the generated ID
            Long generatedId = jdbcTemplate.queryForObject(
                    "SELECT LAST_INSERT_ID()", Long.class);
            return generatedId;
        } catch (Exception e) {
            System.err.println("Error creating user in Master DB: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check if username exists in Master DB
     */
    private boolean usernameExistsInMasterDb(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        String sql = "SELECT COUNT(*) FROM iam_users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    /**
     * Check if email exists in Master DB
     */
    private boolean emailExistsInMasterDb(String email) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        String sql = "SELECT COUNT(*) FROM iam_users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
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
        // Generate new invitation token
        String newToken = UUID.randomUUID().toString();

        // Update token in Master DB
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        String sql = "UPDATE iam_users SET confirmation_token = ?, enabled = 0 WHERE user_id = ?";
        int updated = jdbcTemplate.update(sql, newToken, id);

        if (updated == 0) {
            return ResponseEntity.notFound().build();
        }

        // Get user details for email
        return userRepository.findById(id)
                .map(user -> {
                    user.setConfirmationToken(newToken);
                    user.setEnabled(false);
                    userRepository.save(user);
                    sendInvitationEmail(user, newToken);
                    return ResponseEntity.ok(Map.of("message", "Invitation sent successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Accept invitation and set password
     */
    @PostMapping("/accept-invitation")
    public ResponseEntity<?> acceptInvitation(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String password = request.get("password");

        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid invitation token"));
        }

        if (password == null || password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password must be at least 6 characters"));
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);

        // Find user by confirmation token in Master DB
        String findSql = "SELECT user_id, email, first_name FROM iam_users WHERE confirmation_token = ?";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(findSql, token);

        if (users.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired invitation token"));
        }

        Map<String, Object> userData = users.get(0);
        Long userId = ((Number) userData.get("user_id")).longValue();

        // Encode password
        String encodedPassword = passwordEncoder.encode(password);

        // Update Master DB - set password, enable user, clear token
        String updateMasterSql = "UPDATE iam_users SET password_hash = ?, enabled = 1, confirmation_token = NULL WHERE user_id = ?";
        jdbcTemplate.update(updateMasterSql, encodedPassword, userId);

        // Update Tenant DB
        userRepository.findById(userId).ifPresent(user -> {
            user.setPasswordHash(encodedPassword);
            user.setEnabled(true);
            user.setConfirmationToken(null);
            userRepository.save(user);
        });

        return ResponseEntity.ok(Map.of(
                "message", "Password set successfully. You can now login.",
                "email", userData.get("email")));
    }

    /**
     * Validate invitation token
     */
    @GetMapping("/validate-invitation/{token}")
    public ResponseEntity<?> validateInvitationToken(@PathVariable String token) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);

        String sql = "SELECT user_id, email, first_name, last_name FROM iam_users WHERE confirmation_token = ?";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, token);

        if (users.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("valid", false, "message", "Invalid or expired invitation token"));
        }

        Map<String, Object> userData = users.get(0);
        return ResponseEntity.ok(Map.of(
                "valid", true,
                "email", userData.get("email"),
                "firstName", userData.get("first_name") != null ? userData.get("first_name") : "",
                "lastName", userData.get("last_name") != null ? userData.get("last_name") : ""));
    }

    private void sendInvitationEmail(User user, String invitationToken) {
        String subject = "You've been invited to ERP System";

        // Invitation link for setting password
        String invitationLink = "http://localhost:4200/accept-invitation?token=" + invitationToken;

        // Log the invitation link for development/debugging purposes (in case email
        // fails)
        System.out.println("=================================================================");
        System.out.println("INVITATION LINK (DEV): " + invitationLink);
        System.out.println("=================================================================");

        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(user.getFirstName() != null ? user.getFirstName() : "User").append(",\n\n");
        body.append("You have been invited to join the ERP System.\n\n");
        body.append("Username: ").append(user.getUsername()).append("\n\n");
        body.append("Please click the link below to set your password and activate your account:\n\n");
        body.append(invitationLink).append("\n\n");
        body.append("This link will expire after you set your password.\n\n");
        body.append("If you did not expect this invitation, please ignore this email.\n\n");
        body.append("Regards,\nERP Team");

        if (user.getId() != null) {
            emailService.sendDirectEmail(
                    EntityType.GENERAL,
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    subject,
                    body.toString(),
                    "System");
        }
    }

}
