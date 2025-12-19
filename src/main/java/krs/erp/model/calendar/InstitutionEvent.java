package krs.erp.model.calendar;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_cal_events")
@Data
@EqualsAndHashCode(callSuper = true)
public class InstitutionEvent extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "location")
    private String location;

    @Column(name = "category")
    private String category; // SPORTS, CULTURAL, ACADEMIC, MEETING

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventStatus status = EventStatus.SCHEDULED;

    public enum EventStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
