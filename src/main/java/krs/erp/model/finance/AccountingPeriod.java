package krs.erp.model.finance;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_accounting_periods")
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountingPeriod extends BaseEntity {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_closed")
    private Boolean isClosed = false;
}
