package krs.erp.repository.finance;

import krs.erp.model.finance.BudgetLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetLineRepository extends JpaRepository<BudgetLine, Long> {
}
