package krs.erp.repository.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.finance.FineWaiverRequest;

import java.util.List;

@Repository
public interface FineWaiverRequestRepository extends JpaRepository<FineWaiverRequest, Long> {
    List<FineWaiverRequest> findByFineLedgerId(Long fineLedgerId);
}
