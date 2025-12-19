package krs.erp.service.hr;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.hr.LeaveBalance;
import krs.erp.model.hr.LeaveRequest;
import krs.erp.model.hr.LeaveType;
import krs.erp.repository.hr.LeaveBalanceRepository;
import krs.erp.repository.hr.LeaveRequestRepository;
import krs.erp.repository.hr.LeaveTypeRepository;

@Service
@Transactional
public class LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    public LeaveRequest applyForLeave(Long staffId, Long leaveTypeId, LocalDate start, LocalDate end, String reason) {
        // 1. Validate overlapping leaves
        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlappingApprovedLeaves(staffId, start, end);
        if (!overlapping.isEmpty()) {
            throw new RuntimeException("Leave already approved for this period.");
        }

        // 2. Calculate days
        long daysRequested = ChronoUnit.DAYS.between(start, end) + 1;
        if (daysRequested <= 0) {
            throw new RuntimeException("Invalid date range.");
        }

        // 3. Check Balance
        String academicYear = "2025-2026"; // TODO: Fetch current AY dynamically
        LeaveBalance balance = leaveBalanceRepository
                .findByStaffIdAndLeaveTypeIdAndAcademicYear(staffId, leaveTypeId, academicYear)
                .orElseThrow(() -> new RuntimeException("No leave balance found for this leave type."));

        if (balance.getRemainingDays() < daysRequested) {
            throw new RuntimeException("Insufficient leave balance. Remaining: " + balance.getRemainingDays());
        }

        // 4. Create Request
        LeaveRequest request = new LeaveRequest();
        request.setStaffId(staffId);
        request.setLeaveTypeId(leaveTypeId);
        request.setStartDate(start);
        request.setEndDate(end);
        request.setReason(reason);
        request.setStatus(LeaveRequest.LeaveStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        return leaveRequestRepository.save(request);
    }

    public LeaveRequest approveLeave(Long requestId) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave Request not found."));

        if (request.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new RuntimeException("Only PENDING requests can be approved.");
        }

        // Deduct Balance
        String academicYear = "2025-2026"; // TODO: Fetch current AY
        LeaveBalance balance = leaveBalanceRepository
                .findByStaffIdAndLeaveTypeIdAndAcademicYear(request.getStaffId(), request.getLeaveTypeId(),
                        academicYear)
                .orElseThrow(() -> new RuntimeException("Balance not found."));

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        balance.setConsumedDays(balance.getConsumedDays() + days);
        balance.setRemainingDays(balance.getRemainingDays() - days);
        leaveBalanceRepository.save(balance);

        request.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        request.setUpdatedAt(LocalDateTime.now());
        return leaveRequestRepository.save(request);
    }

    public LeaveRequest rejectLeave(Long requestId, String reason) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave Request not found."));

        if (request.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new RuntimeException("Only PENDING requests can be rejected.");
        }

        request.setStatus(LeaveRequest.LeaveStatus.REJECTED);
        request.setRejectionReason(reason);
        request.setUpdatedAt(LocalDateTime.now());
        return leaveRequestRepository.save(request);
    }

    public List<LeaveRequest> getStaffLeaves(Long staffId) {
        return leaveRequestRepository.findByStaffId(staffId);
    }

    public List<LeaveRequest> getPendingRequests() {
        return leaveRequestRepository.findByStatus(LeaveRequest.LeaveStatus.PENDING);
    }
}
