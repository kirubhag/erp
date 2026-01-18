package krs.erp.controller.hr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.hr.LeaveBalance;
import krs.erp.model.hr.LeaveRequest;
import krs.erp.model.hr.LeaveType;
import krs.erp.repository.hr.LeaveBalanceRepository;
import krs.erp.repository.hr.LeaveRequestRepository;
import krs.erp.repository.hr.LeaveTypeRepository;

/**
 * REST Controller for Leave Management module.
 * Provides CRUD endpoints for LeaveType, LeaveRequest, and LeaveBalance entities.
 */
@RestController
@RequestMapping("/api/hr")
@CrossOrigin(origins = "*")
public class LeaveManagementController {

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;
    
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    
    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    // === Leave Types CRUD ===
    @GetMapping("/leave-types")
    public ResponseEntity<Page<LeaveType>> getAllLeaveTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(leaveTypeRepository.findAll(pageable));
    }
    
    @GetMapping("/leave-types/{id}")
    public ResponseEntity<LeaveType> getLeaveType(@PathVariable Long id) {
        return leaveTypeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/leave-types")
    public ResponseEntity<LeaveType> createLeaveType(@RequestBody LeaveType leaveType) {
        return ResponseEntity.ok(leaveTypeRepository.save(leaveType));
    }
    
    @PutMapping("/leave-types/{id}")
    public ResponseEntity<LeaveType> updateLeaveType(@PathVariable Long id, @RequestBody LeaveType leaveType) {
        if (!leaveTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        leaveType.setId(id);
        return ResponseEntity.ok(leaveTypeRepository.save(leaveType));
    }
    
    @DeleteMapping("/leave-types/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable Long id) {
        if (!leaveTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        leaveTypeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Leave Requests CRUD ===
    @GetMapping("/leave-requests")
    public ResponseEntity<Page<LeaveRequest>> getAllLeaveRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(leaveRequestRepository.findAll(pageable));
    }
    
    @GetMapping("/leave-requests/{id}")
    public ResponseEntity<LeaveRequest> getLeaveRequest(@PathVariable Long id) {
        return leaveRequestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/leave-requests")
    public ResponseEntity<LeaveRequest> createLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        return ResponseEntity.ok(leaveRequestRepository.save(leaveRequest));
    }
    
    @PutMapping("/leave-requests/{id}")
    public ResponseEntity<LeaveRequest> updateLeaveRequest(@PathVariable Long id, @RequestBody LeaveRequest leaveRequest) {
        if (!leaveRequestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        leaveRequest.setId(id);
        return ResponseEntity.ok(leaveRequestRepository.save(leaveRequest));
    }
    
    @DeleteMapping("/leave-requests/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Long id) {
        if (!leaveRequestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        leaveRequestRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Leave Balances CRUD ===
    @GetMapping("/leave-balances")
    public ResponseEntity<Page<LeaveBalance>> getAllLeaveBalances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(leaveBalanceRepository.findAll(pageable));
    }
    
    @GetMapping("/leave-balances/{id}")
    public ResponseEntity<LeaveBalance> getLeaveBalance(@PathVariable Long id) {
        return leaveBalanceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/leave-balances")
    public ResponseEntity<LeaveBalance> createLeaveBalance(@RequestBody LeaveBalance leaveBalance) {
        return ResponseEntity.ok(leaveBalanceRepository.save(leaveBalance));
    }
    
    @PutMapping("/leave-balances/{id}")
    public ResponseEntity<LeaveBalance> updateLeaveBalance(@PathVariable Long id, @RequestBody LeaveBalance leaveBalance) {
        if (!leaveBalanceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        leaveBalance.setId(id);
        return ResponseEntity.ok(leaveBalanceRepository.save(leaveBalance));
    }
    
    @DeleteMapping("/leave-balances/{id}")
    public ResponseEntity<Void> deleteLeaveBalance(@PathVariable Long id) {
        if (!leaveBalanceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        leaveBalanceRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
