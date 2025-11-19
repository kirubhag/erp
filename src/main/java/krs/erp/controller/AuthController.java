package krs.erp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/settings/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    /**
     * Login endpoint - authenticates user with email and password
     * 
     * @param loginRequest Contains email and password
     * @param request HttpServletRequest
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
            
            // Find user by username or email
            User user = null;
            if (username != null && !username.isEmpty()) {
                user = userRepository.findByUsername(username).orElse(null);
            } else if (email != null && !email.isEmpty()) {
                user = userRepository.findByEmail(email).orElse(null);
            }
            
            if (user == null || !user.getEnabled()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Verify password
            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Load user details and create authentication token
            org.springframework.security.core.userdetails.UserDetails userDetails = 
                    customUserDetailsService.loadUserByUsername(user.getUsername());
            
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
        
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            Map<String, Object> user = new HashMap<>();
            user.put("username", authentication.getName());
            return ResponseEntity.ok(user);
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    /**
     * Logout endpoint
     * Invalidates session and clears security context
     * 
     * @param request HttpServletRequest
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
            response.put("message", "CSRF token not available in this request context. Use the X-CSRF-TOKEN header from login response.");
            response.put("headerName", "X-CSRF-TOKEN");
            response.put("instruction", "After login, the CSRF token will be automatically managed by the session cookie for same-origin requests");
        }
        
        return ResponseEntity.ok(response);
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
