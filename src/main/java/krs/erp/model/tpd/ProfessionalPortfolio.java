package krs.erp.model.tpd;

import krs.erp.model.BaseEntity;
import krs.erp.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_tpd_portfolios")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "portfolio_id"))
public class ProfessionalPortfolio extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    private String currentRole;

    private String targetTrack; // e.g., Senior Teacher, HOD, Principal

    private Double totalCPDCredits;

    private Integer yearsOfService;

    private boolean promotionReady = false;

    private LocalDateTime lastUpdated = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String professionalSummary;
}
