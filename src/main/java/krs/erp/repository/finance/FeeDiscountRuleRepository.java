package krs.erp.repository.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.finance.FeeDiscountRule;

@Repository
public interface FeeDiscountRuleRepository extends JpaRepository<FeeDiscountRule, Long> {
}
