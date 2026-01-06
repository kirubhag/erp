package krs.erp.model.finance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
        FEE, FINE, DISCOUNT, OTHER
    }
}
