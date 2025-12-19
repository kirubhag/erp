package krs.erp.model.finance;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_fee_disciplinary_incidents")
@Data
@EqualsAndHashCode(callSuper = true)
public class DisciplinaryIncident extends BaseEntity {

    @Column(name = "reported_by_id", nullable = false)
    private Long reportedById;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "evidence_url")
    private String evidenceUrl;

    @Column(name = "fine_amount")
    private Double fineAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "incident_date")
    private LocalDateTime incidentDate = LocalDateTime.now();

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }
}
