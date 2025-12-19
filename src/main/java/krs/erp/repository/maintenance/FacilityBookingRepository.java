package krs.erp.repository.maintenance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import krs.erp.model.maintenance.FacilityBooking;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FacilityBookingRepository extends JpaRepository<FacilityBooking, Long> {

    @Query("SELECT b FROM FacilityBooking b WHERE b.facilityId = :facilityId " +
            "AND ((b.startTime < :endTime AND b.endTime > :startTime)) " +
            "AND b.status = 'BOOKED'")
    List<FacilityBooking> findOverlappingBookings(Long facilityId, LocalDateTime startTime, LocalDateTime endTime);
}
