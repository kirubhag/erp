package krs.erp.model.calendar;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_cal_holidays")
@Data
@EqualsAndHashCode(callSuper = true)
public class Holiday extends BaseEntity {

    @Column(name = "holiday_date", nullable = false, unique = true)
    private LocalDate holidayDate;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "holiday_type")
    private String holidayType; // NATIONAL, STATE, RELIGIOUS, OPTIONAL, etc.

    @Column(name = "is_optional")
    private Boolean isOptional = false;

    @Column(name = "applicable_to")
    private String applicableTo; // ALL, STAFF, STUDENTS, etc.
}
