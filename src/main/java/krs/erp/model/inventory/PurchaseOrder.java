package krs.erp.model.inventory;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_inventory_purchase_orders")
@Data
@EqualsAndHashCode(callSuper = true)
public class PurchaseOrder extends BaseEntity {

    @Column(name = "po_number", nullable = false, unique = true)
    private String poNumber;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private POStatus status = POStatus.DRAFT;

    public enum POStatus {
        DRAFT, ORDERED, RECEIVED, CANCELLED
    }
}
