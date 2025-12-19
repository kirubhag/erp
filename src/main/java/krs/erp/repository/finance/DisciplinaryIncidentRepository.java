package krs.erp.repository.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.finance.DisciplinaryIncident;

import java.util.List;

@Repository
public interface DisciplinaryIncidentRepository extends JpaRepository<DisciplinaryIncident, Long> {
    List<DisciplinaryIncident> findByStudentId(Long studentId);
}
