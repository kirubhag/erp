package krs.erp.controller;

import com.razorpay.RazorpayException;
import krs.erp.entity.ErpPaymentLog;
import krs.erp.repository.ErpPaymentLogRepository;
import krs.erp.service.RazorpayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/billing")
public class BillingController {

    private final RazorpayService razorpayService;
    private final ErpPaymentLogRepository paymentLogRepository;
    private final krs.erp.repository.UserRepository userRepository;
    private final krs.erp.repository.OrganizationRepository organizationRepository;

    public BillingController(RazorpayService razorpayService,
            ErpPaymentLogRepository paymentLogRepository,
            krs.erp.repository.UserRepository userRepository,
            krs.erp.repository.OrganizationRepository organizationRepository) {
        this.razorpayService = razorpayService;
        this.paymentLogRepository = paymentLogRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    @PostMapping("/subscription")
    public ResponseEntity<?> createSubscription(@RequestParam String planType, @RequestParam Long userId) {
        try {
            // Find user and their organization
            krs.erp.model.User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            if (user.getOrganizationId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not linked to an organization"));
            }

            krs.erp.model.Organization organization = organizationRepository.findById(user.getOrganizationId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Organization not found: " + user.getOrganizationId()));

            String subscriptionId = razorpayService.createSubscription(organization, planType);
            return ResponseEntity.ok(Map.of("subscriptionId", subscriptionId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verification")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload) {
        String paymentId = payload.get("razorpay_payment_id");
        String subscriptionId = payload.get("razorpay_subscription_id");
        String signature = payload.get("razorpay_signature");
        // Optional: userId for logging if passed
        Long userId = payload.containsKey("user_id") ? Long.parseLong(payload.get("user_id")) : null;

        ErpPaymentLog log = new ErpPaymentLog();
        log.setUserId(userId);
        log.setRazorpayPaymentId(paymentId);
        log.setRazorpayOrderId(subscriptionId); // Subscription flow uses sub_id as order ref
        log.setRazorpaySignature(signature);

        try {
            boolean isValid = razorpayService.verifySignature(paymentId, subscriptionId, signature);
            if (isValid) {
                razorpayService.activateSubscription(subscriptionId, paymentId);
                log.setStatus("SUCCESS");
                paymentLogRepository.save(log);
                return ResponseEntity.ok(Map.of("status", "success"));
            } else {
                log.setStatus("FAILED");
                log.setNotes("Signature mismatch");
                paymentLogRepository.save(log);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid signature"));
            }
        } catch (RazorpayException e) {
            log.setStatus("ERROR");
            log.setNotes(e.getMessage());
            paymentLogRepository.save(log);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
