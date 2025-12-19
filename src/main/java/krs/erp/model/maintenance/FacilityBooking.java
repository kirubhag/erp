package krs.erp.model.maintenance;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_maint_facility_bookings")
@Data
@EqualsAndHashCode(callSuper = true)
public class FacilityBooking extends BaseEntity {

    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    @Column(name = "booked_by_id", nullable = false)
    private Long bookedById;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "status")
    private String status = "BOOKED"; // BOOKED, CANCELLED
}
