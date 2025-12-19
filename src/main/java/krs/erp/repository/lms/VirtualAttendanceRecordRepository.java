package krs.erp.repository.lms;

import krs.erp.model.lms.VirtualAttendanceRecord;
import krs.erp.model.lms.VirtualClassSession;
import krs.erp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VirtualAttendanceRecordRepository extends JpaRepository<VirtualAttendanceRecord, Long> {
    List<VirtualAttendanceRecord> findBySession(VirtualClassSession session);

    List<VirtualAttendanceRecord> findBySessionAndUser(VirtualClassSession session, User user);
}
