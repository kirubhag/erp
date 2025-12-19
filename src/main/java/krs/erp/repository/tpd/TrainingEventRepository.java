package krs.erp.repository.tpd;

import krs.erp.model.tpd.TrainingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingEventRepository extends JpaRepository<TrainingEvent, Long> {
}
