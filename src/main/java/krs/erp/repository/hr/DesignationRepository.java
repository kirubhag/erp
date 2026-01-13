package krs.erp.repository.hr;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.hr.Designation;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {
    Optional<Designation> findByTitle(String title);
}
