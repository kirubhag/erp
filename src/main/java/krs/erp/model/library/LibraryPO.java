package krs.erp.model.library;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import krs.erp.model.inventory.Vendor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "erp_library_pos")
@EqualsAndHashCode(callSuper = true)
public class LibraryPO extends BaseEntity {

    public enum POStatus {
        DRAFT,
        SENT,
        PARTIAL,
        COMPLETED
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private POStatus status = POStatus.DRAFT;
}
