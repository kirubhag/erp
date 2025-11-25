package krs.erp.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Mock Payment Gateway Service
 * Simulates payment processing without actual payment integration
 */
@Service
@Slf4j
public class MockPaymentGatewayService {

    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Process payment - simulates real payment gateway
     * Returns success for amounts < $10,000 (for testing)
     */
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing mock payment for user: {}, amount: {} {}",
                request.getUserId(), request.getAmount(), request.getCurrency());

        try {
            // Simulate network delay
            Thread.sleep(1000 + random.nextInt(1000));

            // Generate mock transaction ID
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // Simulate payment scenarios
            PaymentResponse response = new PaymentResponse();
            response.setTransactionId(transactionId);

            // Mock logic: Fail if amount > 100,000 or card number ends with '0000'
            boolean shouldFail = request.getAmount().compareTo(new BigDecimal("100000")) > 0 ||
                    (request.getCardNumber() != null && request.getCardNumber().endsWith("0000"));

            if (shouldFail) {
                response.setSuccess(false);
                response.setStatus("FAILED");
                response.setMessage("Payment failed: Insufficient funds or invalid card");
                response.setErrorCode("PAYMENT_DECLINED");
            } else {
                response.setSuccess(true);
                response.setStatus("SUCCESS");
                response.setMessage("Payment processed successfully");
            }

            // Create mock gateway response JSON
            Map<String, Object> gatewayData = new HashMap<>();
            gatewayData.put("transaction_id", transactionId);
            gatewayData.put("amount", request.getAmount());
            gatewayData.put("currency", request.getCurrency());
            gatewayData.put("payment_method", request.getPaymentMethod());
            gatewayData.put("status", response.getStatus());
            gatewayData.put("timestamp", LocalDateTime.now().toString());
            gatewayData.put("gateway", "MOCK_GATEWAY_V1");

            response.setGatewayResponse(objectMapper.writeValueAsString(gatewayData));

            log.info("Mock payment processed: {} - {}", transactionId, response.getStatus());
            return response;

        } catch (Exception e) {
            log.error("Error processing mock payment", e);
            PaymentResponse errorResponse = new PaymentResponse();
            errorResponse.setSuccess(false);
            errorResponse.setStatus("FAILED");
            errorResponse.setMessage("System error during payment processing");
            errorResponse.setErrorCode("SYSTEM_ERROR");
            return errorResponse;
        }
    }

    /**
     * Refund payment - mock implementation
     */
    public PaymentResponse refundPayment(String transactionId, BigDecimal amount) {
        log.info("Processing mock refund for transaction: {}, amount: {}", transactionId, amount);

        try {
            Thread.sleep(500);

            PaymentResponse response = new PaymentResponse();
            response.setTransactionId("REFUND-" + transactionId);
            response.setSuccess(true);
            response.setStatus("REFUNDED");
            response.setMessage("Refund processed successfully");

            return response;

        } catch (Exception e) {
            log.error("Error processing mock refund", e);
            PaymentResponse errorResponse = new PaymentResponse();
            errorResponse.setSuccess(false);
            errorResponse.setStatus("FAILED");
            errorResponse.setMessage("Refund failed");
            return errorResponse;
        }
    }

    /**
     * Verify payment status - mock implementation
     */
    public PaymentResponse verifyPayment(String transactionId) {
        log.info("Verifying mock payment: {}", transactionId);

        PaymentResponse response = new PaymentResponse();
        response.setTransactionId(transactionId);
        response.setSuccess(true);
        response.setStatus("SUCCESS");
        response.setMessage("Payment verified");

        return response;
    }
}
