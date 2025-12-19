package krs.erp.repository.finance;

import krs.erp.model.finance.ScholarshipDisbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScholarshipDisbursementRepository extends JpaRepository<ScholarshipDisbursement, Long> {
    List<ScholarshipDisbursement> findByApplicationId(Long applicationId);

    List<ScholarshipDisbursement> findByInvoiceId(Long invoiceId);
}
