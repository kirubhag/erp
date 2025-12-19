package krs.erp.repository.maintenance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.maintenance.Facility;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
}
