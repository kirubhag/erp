package krs.erp.model.maintenance;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_maint_work_orders")
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkOrder extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "priority")
    private String priority; // LOW, MEDIUM, HIGH, URGENT

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WorkOrderStatus status = WorkOrderStatus.NEW;

    @Column(name = "request_date")
    private LocalDateTime requestDate = LocalDateTime.now();

    @Column(name = "assigned_technician_id")
    private Long assignedTechnicianId;

    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    public enum WorkOrderStatus {
        NEW, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
