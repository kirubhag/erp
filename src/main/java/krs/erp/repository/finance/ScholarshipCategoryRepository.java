package krs.erp.repository.finance;

import krs.erp.model.finance.ScholarshipCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScholarshipCategoryRepository extends JpaRepository<ScholarshipCategory, Long> {
}
