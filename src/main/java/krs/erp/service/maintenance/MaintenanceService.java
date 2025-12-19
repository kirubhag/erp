package krs.erp.service.maintenance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import krs.erp.model.maintenance.WorkOrder;
import krs.erp.model.maintenance.Facility;
import krs.erp.model.maintenance.FacilityBooking;
import krs.erp.repository.maintenance.WorkOrderRepository;
import krs.erp.repository.maintenance.FacilityRepository;
import krs.erp.repository.maintenance.FacilityBookingRepository;
import java.util.List;

@Service
public class MaintenanceService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private FacilityBookingRepository facilityBookingRepository;

    @Transactional
    public FacilityBooking bookFacility(FacilityBooking booking) {
        // Conflict check
        List<FacilityBooking> overlapping = facilityBookingRepository.findOverlappingBookings(
                booking.getFacilityId(), booking.getStartTime(), booking.getEndTime());

        if (!overlapping.isEmpty()) {
            throw new RuntimeException("Facility is already booked for the selected time range.");
        }

        return facilityBookingRepository.save(booking);
    }

    @Transactional
    public WorkOrder updateWorkOrderStatus(Long workOrderId, WorkOrder.WorkOrderStatus status) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Work order not found"));
        workOrder.setStatus(status);
        if (status == WorkOrder.WorkOrderStatus.COMPLETED) {
            workOrder.setCompletionDate(java.time.LocalDateTime.now());
        }
        return workOrderRepository.save(workOrder);
    }
}
