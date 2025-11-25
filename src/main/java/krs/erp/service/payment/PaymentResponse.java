package krs.erp.service.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private boolean success;
    private String transactionId;
    private String status; // SUCCESS, FAILED, PENDING
    private String message;
    private String errorCode;
    private String gatewayResponse; // JSON string
}
