package krs.erp.repository.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.finance.FineCategory;

@Repository
public interface FineCategoryRepository extends JpaRepository<FineCategory, Long> {
}
