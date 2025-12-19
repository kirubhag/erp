package krs.erp.model.finance;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import krs.erp.model.BaseEntity;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "erp_fin_invoices")
public class Invoice extends BaseEntity {

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "total_amount")
    private Double totalAmount = 0.0;

    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;

    @Column(name = "tax_amount")
    private Double taxAmount = 0.0;

    @Column(name = "net_amount")
    private Double netAmount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status;

    public enum InvoiceStatus {
        DRAFT, ISSUED, PAID, PARTIALLY_PAID, OVERDUE, CANCELLED
    }
}
