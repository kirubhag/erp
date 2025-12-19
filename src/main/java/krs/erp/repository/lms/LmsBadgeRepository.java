package krs.erp.repository.lms;

import krs.erp.model.lms.LmsBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LmsBadgeRepository extends JpaRepository<LmsBadge, Long> {
}
