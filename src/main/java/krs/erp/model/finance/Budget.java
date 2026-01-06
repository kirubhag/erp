package krs.erp.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "erp_budgets")
@EqualsAndHashCode(callSuper = true)
public class Budget extends BaseEntity {

    public enum BudgetStatus {
        DRAFT,
        APPROVED,
        CLOSED
    }

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_period_id", nullable = false)
    private AccountingPeriod accountingPeriod;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetStatus status = BudgetStatus.DRAFT;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetLine> lines = new ArrayList<>();
}
