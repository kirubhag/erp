package krs.erp.controller;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AccountConfirmationController {

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/confirm-account")
    public ResponseEntity<Map<String, Object>> confirmAccount(@RequestParam("token") String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. Check if token exists in Master DB
            if (verifyAndEnableInMasterDb(token)) {
                // 2. Ideally, we should also check Tenant DB if we were syncing tokens,
                // but since we blindly enabled in Tenant DB in RegistrationService, we are
                // good.
                // If we implemented token sync to User entity in Tenant DB, we would update it
                // here too.
                // For now, Master DB is the authority for login.

                response.put("status", "success");
                response.put("message", "Account confirmed successfully! You can now login.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Invalid or expired confirmation token.");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred during confirmation: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private boolean verifyAndEnableInMasterDb(String token) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        String query = "SELECT COUNT(*) FROM erp_iam_users WHERE confirmation_token = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, token);

        if (count != null && count > 0) {
            // Update user to enabled = true and clear token
            String updateSql = "UPDATE erp_iam_users SET enabled = true, confirmation_token = NULL WHERE confirmation_token = ?";
            jdbcTemplate.update(updateSql, token);
            return true;
        }
        return false;
    }
}
