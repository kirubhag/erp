package krs.erp.model.calendar;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Entity
@Table(name = "erp_cal_calendar_days")
@Data
@EqualsAndHashCode(callSuper = true)
public class CalendarDay extends BaseEntity {

    @Column(name = "calendar_date", nullable = false, unique = true)
    private LocalDate date;

    @Column(name = "day_type")
    private String type; // HOLIDAY, EXAM, WORKING_DAY, EVENT

    @Column(name = "description")
    private String description;

    @Column(name = "is_holiday")
    private boolean isHoliday;

    @Column(name = "event_id")
    private Long eventId; // Optional link to an InstitutionEvent
}
