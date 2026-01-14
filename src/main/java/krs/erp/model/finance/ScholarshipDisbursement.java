package krs.erp.model.finance;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tracks the fulfillment of an approved scholarship.
 */
@Data
@Entity
@Table(name = "erp_fin_scholarship_disbursements")
@EqualsAndHashCode(callSuper = true)
public class ScholarshipDisbursement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private ScholarshipApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice; // Linked when applied to an invoice

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal disbursementAmount;

    @Column(nullable = false)
    private LocalDate disbursementDate;

    private String referenceNumber;

    private String status; // PENDING, APPLIED, CANCELLED
}
