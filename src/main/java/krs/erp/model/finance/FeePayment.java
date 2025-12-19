package krs.erp.model.finance;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_fee_payments")
@Data
@EqualsAndHashCode(callSuper = true)
public class FeePayment extends BaseEntity {

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "fee_structure_id")
    private Long feeStructureId;

    @Column(name = "base_amount")
    private Double baseAmount;

    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;

    @Column(name = "fine_amount")
    private Double fineAmount = 0.0;

    @Column(name = "net_amount")
    private Double netAmount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode")
    private PaymentMode paymentMode;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "remarks")
    private String remarks;

    public enum PaymentMode {
        CASH, ONLINE, CHEQUE
    }
}
