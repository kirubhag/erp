package krs.erp.model.finance;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import krs.erp.model.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "erp_fin_invoice_items")
public class InvoiceItem extends BaseEntity {

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type")
    private ItemType itemType;

    public enum ItemType {
        FEE, FINE, OTHER
    }
}
