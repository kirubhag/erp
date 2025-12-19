package krs.erp.repository.tpd;

import krs.erp.model.tpd.TrainingAttendance;
import krs.erp.model.tpd.TrainingEvent;
import krs.erp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrainingAttendanceRepository extends JpaRepository<TrainingAttendance, Long> {
    List<TrainingAttendance> findByEvent(TrainingEvent event);

    List<TrainingAttendance> findByStaff(User staff);
}
