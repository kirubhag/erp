package krs.erp.repository.library;

import krs.erp.model.library.LibraryHold;
import krs.erp.model.library.LibraryResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryHoldRepository extends JpaRepository<LibraryHold, Long> {
    List<LibraryHold> findByResourceAndStatusOrderByRequestDateAsc(LibraryResource resource,
            LibraryHold.HoldStatus status);
}
