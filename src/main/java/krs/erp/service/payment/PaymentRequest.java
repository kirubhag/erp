package krs.erp.service.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Payment request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long userId;
    private Long organizationId;
    private Long subscriptionId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, PAYPAL, etc.
    private String cardNumber; // For mock purposes
    private String cardHolderName;
    private String cardExpiry;
    private String cardCvv;
    private String billingAddress;
}
