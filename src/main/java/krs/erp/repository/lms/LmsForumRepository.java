package krs.erp.repository.lms;

import krs.erp.model.lms.LmsForum;
import krs.erp.model.lms.LmsModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LmsForumRepository extends JpaRepository<LmsForum, Long> {
    List<LmsForum> findByCourse(LmsModule course);
}
