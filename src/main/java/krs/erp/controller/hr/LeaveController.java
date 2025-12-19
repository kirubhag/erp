package krs.erp.controller.hr;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.hr.LeaveRequest;
import krs.erp.service.hr.LeaveService;

@RestController
@RequestMapping("/api/hr/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyForLeave(@RequestBody Map<String, Object> payload) {
        try {
            Long staffId = Long.parseLong(payload.get("staffId").toString());
            Long leaveTypeId = Long.parseLong(payload.get("leaveTypeId").toString());
            LocalDate start = LocalDate.parse(payload.get("startDate").toString());
            LocalDate end = LocalDate.parse(payload.get("endDate").toString());
            String reason = (String) payload.get("reason");

            LeaveRequest request = leaveService.applyForLeave(staffId, leaveTypeId, start, end, reason);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveLeave(@PathVariable Long id) {
        try {
            LeaveRequest request = leaveService.approveLeave(id);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeave(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String reason = payload.get("reason");
            LeaveRequest request = leaveService.rejectLeave(id, reason);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<LeaveRequest>> getStaffLeaves(@PathVariable Long staffId) {
        return ResponseEntity.ok(leaveService.getStaffLeaves(staffId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequest>> getPendingLeaves() {
        return ResponseEntity.ok(leaveService.getPendingRequests());
    }
}
