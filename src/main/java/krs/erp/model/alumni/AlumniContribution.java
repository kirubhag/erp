package krs.erp.model.alumni;

import javax.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Entity
@Table(name = "erp_alumni_contributions")
@Data
@EqualsAndHashCode(callSuper = true)
public class AlumniContribution extends BaseEntity {

    @Column(name = "alumni_id", nullable = false)
    private Long alumniId;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "contribution_date")
    private LocalDate contributionDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ContributionType type;

    @Column(name = "description", length = 1000)
    private String description;

    public enum ContributionType {
        DONATION, VOLUNTEER, MENTORSHIP
    }
}
