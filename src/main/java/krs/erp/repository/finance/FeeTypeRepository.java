package krs.erp.repository.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.finance.*;

@Repository
public interface FeeTypeRepository extends JpaRepository<FeeType, Long> {
}
