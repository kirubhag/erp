package krs.erp.service;

import com.razorpay.Plan;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Subscription;
import com.razorpay.Utils;
import krs.erp.entity.ErpPlan;
import krs.erp.entity.ErpSubscription;
import krs.erp.repository.ErpPlanRepository;
import krs.erp.repository.ErpSubscriptionRepository;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RazorpayService {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    private RazorpayClient client;

    private final ErpPlanRepository planRepository;
    private final ErpSubscriptionRepository subscriptionRepository;

    public RazorpayService(ErpPlanRepository planRepository, ErpSubscriptionRepository subscriptionRepository) {
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostConstruct
    public void init() throws RazorpayException {
        this.client = new RazorpayClient(keyId, keySecret);
        // Sync plans on startup if needed, or ensure they exist
        ensurePlansExist();
    }

    private void ensurePlansExist() {
        // Check if plans exist with Razorpay ID, if not create them
        List<ErpPlan> plans = planRepository.findAll();
        for (ErpPlan plan : plans) {
            if (plan.getRazorpayPlanId() == null || plan.getRazorpayPlanId().isEmpty()) {
                try {
                    String rzpPlanId = createRazorpayPlan(plan);
                    plan.setRazorpayPlanId(rzpPlanId);
                    planRepository.save(plan);
                } catch (RazorpayException e) {
                    System.err.println("Error creating plan in Razorpay: " + e.getMessage());
                }
            }
        }
    }

    private String createRazorpayPlan(ErpPlan plan) throws RazorpayException {
        JSONObject sessionRequest = new JSONObject();
        sessionRequest.put("period", plan.getType().equalsIgnoreCase("MONTHLY") ? "monthly" : "yearly");
        sessionRequest.put("interval", 1);

        JSONObject item = new JSONObject();
        item.put("name", plan.getName());
        item.put("amount", plan.getAmount().multiply(new BigDecimal(100)).intValue()); // Amount in paise
        item.put("currency", plan.getCurrency());
        item.put("description", plan.getDescription());

        sessionRequest.put("item", item);

        Plan rzpPlan = client.plans.create(sessionRequest);
        return rzpPlan.get("id");
    }

    /**
     * Create a subscription for a user.
     * Yearly plan has 10% discount already built into the plan price in DB.
     */
    @Transactional
    public String createSubscription(Long userId, String planType) throws RazorpayException {
        ErpPlan plan = planRepository.findByType(planType)
                .orElseThrow(() -> new IllegalArgumentException("Invalid plan type: " + planType));

        JSONObject subscriptionRequest = new JSONObject();
        subscriptionRequest.put("plan_id", plan.getRazorpayPlanId());
        subscriptionRequest.put("total_count", planType.equalsIgnoreCase("MONTHLY") ? 12 : 5); // Example counts
        subscriptionRequest.put("quantity", 1);
        subscriptionRequest.put("customer_notify", 1);

        Subscription subscription = client.subscriptions.create(subscriptionRequest);

        // Save pending subscription
        ErpSubscription erpSub = new ErpSubscription();
        erpSub.setUserId(userId);
        erpSub.setPlan(plan);
        erpSub.setRazorpaySubscriptionId(subscription.get("id"));
        erpSub.setStatus("CREATED");
        subscriptionRepository.save(erpSub);

        return subscription.get("id");
    }

    public boolean verifySignature(String paymentId, String subscriptionId, String signature) throws RazorpayException {
        JSONObject options = new JSONObject();
        options.put("razorpay_payment_id", paymentId);
        options.put("razorpay_subscription_id", subscriptionId);
        options.put("razorpay_signature", signature);

        return Utils.verifyPaymentSignature(options, keySecret);
    }

    @Transactional
    public void activateSubscription(String subscriptionId, String paymentId) {
        ErpSubscription sub = subscriptionRepository.findByRazorpaySubscriptionId(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        sub.setStatus("ACTIVE");
        // Update period start/end from Razorpay if needed, for now current time
        sub.setCurrentPeriodStart(LocalDateTime.now());
        // Simple logic: add 1 month or 1 year
        if (sub.getPlan().getType().equalsIgnoreCase("MONTHLY")) {
            sub.setCurrentPeriodEnd(LocalDateTime.now().plusMonths(1));
        } else {
            sub.setCurrentPeriodEnd(LocalDateTime.now().plusYears(1));
        }

        subscriptionRepository.save(sub);
    }
}
