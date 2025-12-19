package krs.erp.repository.alumni;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.alumni.Alumni;

@Repository
public interface AlumniRepository extends JpaRepository<Alumni, Long> {
}
