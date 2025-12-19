package krs.erp.repository.lms;

import krs.erp.model.lms.LmsPointLog;
import krs.erp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LmsPointLogRepository extends JpaRepository<LmsPointLog, Long> {
    List<LmsPointLog> findByStudent(User student);
}
