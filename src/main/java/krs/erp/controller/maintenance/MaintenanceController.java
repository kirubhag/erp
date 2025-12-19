package krs.erp.controller.maintenance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import krs.erp.model.maintenance.WorkOrder;
import krs.erp.model.maintenance.FacilityBooking;
import krs.erp.service.maintenance.MaintenanceService;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "*")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @PostMapping("/booking")
    public ResponseEntity<FacilityBooking> bookFacility(@RequestBody FacilityBooking booking) {
        return ResponseEntity.ok(maintenanceService.bookFacility(booking));
    }

    @PatchMapping("/work-orders/{id}/status")
    public ResponseEntity<WorkOrder> updateStatus(@PathVariable Long id,
            @RequestParam WorkOrder.WorkOrderStatus status) {
        return ResponseEntity.ok(maintenanceService.updateWorkOrderStatus(id, status));
    }
}
