package krs.erp.model.tpd;

import krs.erp.model.BaseEntity;
import krs.erp.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_tpd_cpd_ledger")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "ledger_id"))
public class CpdLedger extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    private Integer academicYear;

    private Double creditsEarned = 0.0;

    private Double creditsRequired = 30.0; // Default requirement

    @Column(columnDefinition = "TEXT")
    private String notes;
}
