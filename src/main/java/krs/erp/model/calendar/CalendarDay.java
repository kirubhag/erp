package krs.erp.model.calendar;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "erp_utility_calendar_days")
@AttributeOverride(name = "id", column = @Column(name = "calendar_day_id"))
public class CalendarDay extends BaseEntity {

    private java.time.LocalDate date;
    private String description;

    // e.g. "HOLIDAY", "EVENT", "WORKING_DAY"
    private String type;

    private boolean isHoliday;

    public enum DayType {
        WORKING_DAY,
        HOLIDAY,
        EVENT,
        WEEKEND
    }
}
