package krs.erp.repository.library;

import krs.erp.model.library.ResourceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceItemRepository extends JpaRepository<ResourceItem, Long> {
    Optional<ResourceItem> findByAccessionNumber(String accessionNumber);

    Optional<ResourceItem> findByBarcode(String barcode);
}
