package krs.erp.model.finance;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import krs.erp.model.BaseEntity;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "erp_fin_transactions")
public class Transaction extends BaseEntity {

    @Column(name = "transaction_id_ext", unique = true)
    private String transactionIdExt;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "amount_paid")
    private Double amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode")
    private PaymentMode paymentMode;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;

    public enum PaymentMode {
        CASH, ONLINE, CHEQUE, BANK_TRANSFER
    }

    public enum TransactionStatus {
        SUCCESS, PENDING, FAILED
    }
}
