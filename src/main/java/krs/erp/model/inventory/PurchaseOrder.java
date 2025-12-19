package krs.erp.model.inventory;

import java.time.LocalDate;
import javax.persistence.*;
import java.time.LocalDate;

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
