package krs.erp.model.finance;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_fee_fine_waivers")
@Data
@EqualsAndHashCode(callSuper = true)
public class FineWaiverRequest extends BaseEntity {

    @Column(name = "fine_ledger_id", nullable = false)
    private Long fineLedgerId;

    @Column(name = "requested_by_id", nullable = false)
    private Long requestedById;

    @Column(name = "reason", length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WaiverStatus status = WaiverStatus.PENDING;

    @Column(name = "approved_by_id")
    private Long approvedById;

    @Column(name = "adjustment_amount")
    private Double adjustmentAmount;

    @Column(name = "request_date")
    private LocalDateTime requestDate = LocalDateTime.now();

    public enum WaiverStatus {
        PENDING, APPROVED, REJECTED
    }
}
