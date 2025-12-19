package krs.erp.repository.hr;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import krs.erp.model.hr.LeaveRequest;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByStaffId(Long staffId);

    List<LeaveRequest> findByStatus(LeaveRequest.LeaveStatus status);

    @Query("SELECT l FROM LeaveRequest l WHERE l.staffId = :staffId AND l.status = 'APPROVED' AND " +
            "((l.startDate BETWEEN :start AND :end) OR (l.endDate BETWEEN :start AND :end))")
    List<LeaveRequest> findOverlappingApprovedLeaves(Long staffId, LocalDate start, LocalDate end);
}
