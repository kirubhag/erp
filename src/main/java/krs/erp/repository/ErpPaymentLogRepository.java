package krs.erp.repository;

import krs.erp.entity.ErpPaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErpPaymentLogRepository extends JpaRepository<ErpPaymentLog, Long> {
}
