package krs.erp.repository.hr;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.hr.LeaveBalance;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    List<LeaveBalance> findByStaffIdAndAcademicYear(Long staffId, String academicYear);

    Optional<LeaveBalance> findByStaffIdAndLeaveTypeIdAndAcademicYear(Long staffId, Long leaveTypeId,
            String academicYear);
}
