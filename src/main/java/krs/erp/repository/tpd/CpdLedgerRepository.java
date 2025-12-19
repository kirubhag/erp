package krs.erp.repository.tpd;

import krs.erp.model.tpd.CpdLedger;
import krs.erp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CpdLedgerRepository extends JpaRepository<CpdLedger, Long> {
    Optional<CpdLedger> findByStaffAndAcademicYear(User staff, Integer academicYear);
}
