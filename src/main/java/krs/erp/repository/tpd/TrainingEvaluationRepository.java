package krs.erp.repository.tpd;

import krs.erp.model.tpd.TrainingEvaluation;
import krs.erp.model.tpd.TrainingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrainingEvaluationRepository extends JpaRepository<TrainingEvaluation, Long> {
    List<TrainingEvaluation> findByEvent(TrainingEvent event);
}
