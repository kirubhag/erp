package krs.erp.model.reporting;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_reporting_mis_reports")
@Data
@EqualsAndHashCode(callSuper = true)
public class MISReport extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type")
    private String type; // ADMISSION, HR, FINANCE, INVENTORY, MAINTENANCE

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "last_run_date")
    private LocalDateTime lastRunDate;

    @Column(name = "report_data", columnDefinition = "TEXT")
    private String reportData; // Store summary or JSON configuration
}
