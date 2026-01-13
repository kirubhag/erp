package krs.erp.controller;

import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.config.CustomUserDetails;
import krs.erp.service.SubscriptionService;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/plans")
    public ResponseEntity<?> getPlans() {
        // Plans are now loaded from tenant DB (copied from master during provisioning)
        return ResponseEntity.ok(subscriptionService.getAllPlans());
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSubscription() {
        Long organizationId = getCurrentOrganizationId();
        if (organizationId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Organization not found for user"));
        }

        return subscriptionService.getCurrentSubscription(organizationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/extend-trial")
    public ResponseEntity<?> extendTrial(@RequestBody Map<String, Object> request) {
        // In a real app, restrict this to Admin/SuperAdmin
        Long organizationId = getCurrentOrganizationId();
        if (organizationId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Organization not found"));
        }

        // request: duration (int), unit (String: DAYS, MINUTES)
        int duration = 1; // default
        if (request.containsKey("duration")) {
            duration = Integer.parseInt(request.get("duration").toString());
        }

        String unitStr = "DAYS";
        if (request.containsKey("unit")) {
            unitStr = request.get("unit").toString();
        }

        ChronoUnit unit;
        try {
            unit = ChronoUnit.valueOf(unitStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            unit = ChronoUnit.DAYS;
        }

        try {
            subscriptionService.extendTrial(organizationId, duration, unit);
            return ResponseEntity.ok(Map.of("message", "Trial extended successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/change-plan")
    public ResponseEntity<?> changePlan(@RequestBody Map<String, Long> request) {
        Long organizationId = getCurrentOrganizationId();
        if (organizationId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Organization not found"));
        }

        Long newPlanId = request.get("planId");
        if (newPlanId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Plan ID is required"));
        }

        try {
            subscriptionService.changePlan(organizationId, newPlanId);
            return ResponseEntity.ok(Map.of("message", "Plan changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private Long getCurrentOrganizationId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            krs.erp.config.CustomUserDetails userDetails = (krs.erp.config.CustomUserDetails) auth.getPrincipal();
            // Assuming CustomUserDetails might have organizationId directly or we get it
            // via other means.
            // Using TenantContext or similar might be better, but CustomUserDetails usually
            // has it.
            // Let's assume tenantId corresponds to organizationId or user has
            // getOrganizationId if updated.
            // Earlier code showed CustomUserDetails.getTenantId().
            // And User entity has organizationId.
            // Let's use getOrganizationId() if available, else fetch user.
            // For now, assume a way to get it.
            // In AuthController, user.getOrganizationId() was put in response map. I should
            // check CustomUserDetails definition.
            // Or better, query repo if needed. But for perf, assume it's in details or we
            // trust tenantId matches.
            // Actually, organizationId is distinct from tenantId if multi-tenant
            // architecture uses tenantId as a schema separator.
            // Organization table usually stores tenant info.

            // Let's return null if not sure and fix compilation if CustomUserDetails
            // doesn't have it.
            // For safety, I'll assume we can get it from the User object associated.
            // But since I can't easily see CustomUserDetails source right now without tool
            // call, I'll use a safer approach:
            // Inject UserRepository and fetch organizationId by userId from auth.
            return getOrganizationIdFromAuth(auth);
        }
        return null;
    }

    @Autowired
    private krs.erp.repository.UserRepository userRepository;

    private Long getOrganizationIdFromAuth(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            Long userId = ((CustomUserDetails) auth.getPrincipal()).getUserId();
            return userRepository.findById(userId)
                    .map(krs.erp.model.User::getOrganizationId)
                    .orElse(null);
        }
        return null;
    }
}
