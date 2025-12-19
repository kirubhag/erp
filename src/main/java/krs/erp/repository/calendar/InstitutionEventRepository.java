package krs.erp.repository.calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.calendar.InstitutionEvent;

@Repository
public interface InstitutionEventRepository extends JpaRepository<InstitutionEvent, Long> {
}
