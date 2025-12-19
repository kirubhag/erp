package krs.erp.repository.library;

import krs.erp.model.library.LibraryPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryPORepository extends JpaRepository<LibraryPO, Long> {
}
