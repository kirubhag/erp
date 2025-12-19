package krs.erp.repository.alumni;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.alumni.AlumniContribution;
import java.util.List;

@Repository
public interface AlumniContributionRepository extends JpaRepository<AlumniContribution, Long> {
    List<AlumniContribution> findByAlumniId(Long alumniId);
}
