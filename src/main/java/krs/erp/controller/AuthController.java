package krs.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

import krs.erp.enums.EntityType;
import krs.erp.service.EmailService;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import krs.erp.config.CustomUserDetailsService;
import krs.erp.model.User;
import krs.erp.repository.UserRepository;

/**
 * REST Controller for Authentication operations
 * Provides endpoints for login, logout, and getting current authenticated user
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Autowired
    private EmailService emailService;

    /**
     * Login endpoint - authenticates user with email and password
     * 
     * @param loginRequest Contains email and password
     * @param request      HttpServletRequest
     * @return User details if successful
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest,
            HttpServletRequest request) {
        try {
            String username = loginRequest.get("username");
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            if ((username == null || username.isEmpty()) && (email == null || email.isEmpty())) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Username/email and password are required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (password == null || password.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Password is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Query IAM_MasterDB directly for user authentication
            JdbcTemplate jdbcTemplate = new JdbcTemplate(java.util.Objects.requireNonNull(masterDataSource));
            String sql = "SELECT user_id, username, password_hash, email, first_name, last_name, " +
                    "phone, user_type, enabled, tenant_id, organization_id " +
                    "FROM iam_users " +
                    "WHERE (username = ? OR email = ?) AND enabled = 1";

            String identifier = username != null && !username.isEmpty() ? username : email;

            List<User> users = jdbcTemplate.query(sql, new Object[] { identifier, identifier }, (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getLong("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setEmail(rs.getString("email"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setPhone(rs.getString("phone"));
                user.setUserType(User.UserType.valueOf(rs.getString("user_type")));
                user.setEnabled(rs.getBoolean("enabled"));
                user.setTenantId(rs.getLong("tenant_id"));
                user.setOrganizationId(rs.getLong("organization_id"));
                return user;
            });

            if (users.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            User user = users.get(0);

            // Verify password
            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Load user details and create authentication token
            org.springframework.security.core.userdetails.UserDetails userDetails = customUserDetailsService
                    .loadUserByUsername(user.getUsername());

            Authentication authenticatedToken = new UsernamePasswordAuthenticationToken(
                    userDetails, password, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticatedToken);

            // Store in session
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // Build response with user details
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Login successful");

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("firstName", user.getFirstName());
            userData.put("lastName", user.getLastName());
            userData.put("userType", user.getUserType());
            userData.put("enabled", user.getEnabled());
            userData.put("organizationId", user.getOrganizationId());

            response.put("user", userData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get current authenticated user
     * 
     * @return Current user details or empty response if not authenticated
     */
    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);

            if (user != null) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("username", user.getUsername());
                userData.put("email", user.getEmail());
                userData.put("firstName", user.getFirstName());
                userData.put("lastName", user.getLastName());
                userData.put("userType", user.getUserType());
                userData.put("enabled", user.getEnabled());
                userData.put("organizationId", user.getOrganizationId());
                userData.put("avatarUrl", user.getAvatarUrl());
                userData.put("staffId", user.getStaff() != null ? user.getStaff().getId() : null);

                return ResponseEntity.ok(userData);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Logout endpoint
     * Invalidates session and clears security context
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return Success response
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Invalidate the session
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // Clear the security context
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(null);
            SecurityContextHolder.clearContext();

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Logged out successfully");
            responseBody.put("status", "success");

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Logout failed: " + e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get CSRF token for use in API requests
     * Required for authenticated API calls (PUT, POST, DELETE)
     * The token should be included in the X-CSRF-TOKEN header for API requests
     * 
     * @param request HttpServletRequest containing CSRF token
     * @return CSRF token details
     */
    @GetMapping("/csrf")
    public ResponseEntity<Map<String, Object>> getCsrfToken(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Try to get CSRF token from request
        Object csrfTokenObj = request.getAttribute("_csrf");

        if (csrfTokenObj != null) {
            try {
                // Reflect on the token object to get the token string
                String tokenString = csrfTokenObj.toString();
                response.put("token", tokenString);
                response.put("headerName", "X-CSRF-TOKEN");
                response.put("parameterName", "_csrf");
                response.put("message", "CSRF token retrieved successfully");
            } catch (Exception e) {
                response.put("message", "Error retrieving CSRF token: " + e.getMessage());
            }
        } else {
            // Return a message if token is not available
            response.put("message",
                    "CSRF token not available in this request context. Use the X-CSRF-TOKEN header from login response.");
            response.put("headerName", "X-CSRF-TOKEN");
            response.put("instruction",
                    "After login, the CSRF token will be automatically managed by the session cookie for same-origin requests");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Change Password
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "User not found"));
        }

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Old and new passwords are required"));
        }

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Incorrect old password"));
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "New password must be at least 6 characters"));
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangeDate(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password changed successfully", "status", "success"));
    }

    /**
     * Forgot Password - Send Reset Link
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            // Do not reveal that email does not exist for security, but for dev we might
            // want to know
            return ResponseEntity.ok(Map.of("message",
                    "If an account exists with this email, a reset link has been sent.", "status", "success"));
        }

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        // Send Email
        String subject = "Password Reset Request";
        String resetLink = "http://localhost:4200/reset-password?token=" + token; // TODO: Configurable URL
        String body = "Dear " + user.getFirstName() + ",\n\n" +
                "We received a request to reset your password. Click the link below to reset it:\n\n" +
                resetLink + "\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you did not request this, please ignore this email.";

        emailService.sendDirectEmail(EntityType.GENERAL, user.getId(), user.getEmail(), user.getFullName(), subject,
                body, "System");

        return ResponseEntity.ok(Map.of("message", "Reset link sent to your email", "status", "success"));
    }

    /**
     * Reset Password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token and new password are required"));
        }

        User user = userRepository.findByResetPasswordToken(token).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid token"));
        }

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token has expired"));
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password must be at least 6 characters"));
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        user.setPasswordChangeDate(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password has been reset successfully", "status", "success"));
    }

    /**
     * Health check for authentication service
     * 
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        return ResponseEntity.ok(response);
    }
}
