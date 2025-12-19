package krs.erp.repository.library;

import krs.erp.model.library.LibraryPurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryPurchaseRequestRepository extends JpaRepository<LibraryPurchaseRequest, Long> {
}
