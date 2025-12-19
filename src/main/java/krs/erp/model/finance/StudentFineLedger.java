package krs.erp.model.finance;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_fee_fine_ledger")
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentFineLedger extends BaseEntity {

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "fine_config_id", nullable = false)
    private Long fineConfigId;

    @Column(name = "base_amount")
    private Double baseAmount;

    @Column(name = "accrued_amount")
    private Double accruedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FineStatus status = FineStatus.PENDING;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt = LocalDateTime.now();

    public enum FineStatus {
        PENDING, PAID, WAIVED
    }
}
