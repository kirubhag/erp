package krs.erp.repository.tpd;

import krs.erp.model.tpd.Evidence;
import krs.erp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByStaff(User staff);

    List<Evidence> findByStatus(Evidence.ApprovalStatus status);
}
