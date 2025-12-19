package krs.erp.repository.finance;

import krs.erp.model.finance.JournalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalItemRepository extends JpaRepository<JournalItem, Long> {
}
