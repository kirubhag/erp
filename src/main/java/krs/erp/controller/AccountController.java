package krs.erp.controller;

import krs.erp.config.CustomUserDetails;
import krs.erp.service.AccountClosureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountClosureService accountClosureService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> closeAccount(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String password = request.get("password");
            String reason = request.get("reason");

            if (password == null || password.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Verify Password
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // Note: In a real app, we might need to fetch the user again to get the hashed
            // password
            // if it's not in the UserDetails, but CustomUserDetails usually has it.
            // However, for security, let's assume we need to verify against the current
            // authenticated user's credentials.
            // Since we don't have easy access to the raw password hash here without
            // querying DB (which CustomUserDetails might have),
            // we'll rely on the fact that the user is authenticated.
            // BUT, for a destructive action, we MUST re-verify the password.

            // We need to check if the provided password matches the user's password.
            // Since we don't have the user repository injected here, we can't easily fetch
            // the hash.
            // But wait, CustomUserDetails extends User, which implements UserDetails.
            // UserDetails.getPassword() returns the password (hash).

            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                response.put("status", "error");
                response.put("message", "Invalid password");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            String tenantId = userDetails.getTenantId();
            if (tenantId == null) {
                response.put("status", "error");
                response.put("message", "Tenant ID not found for user");
                return ResponseEntity.badRequest().body(response);
            }

            accountClosureService.closeAccount(tenantId, reason);

            response.put("status", "success");
            response.put("message", "Account closed successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to close account: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
