package krs.erp.repository.finance;

import krs.erp.model.finance.BankStatementLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankStatementLineRepository extends JpaRepository<BankStatementLine, Long> {
}
