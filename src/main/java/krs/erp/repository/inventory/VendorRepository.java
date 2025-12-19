package krs.erp.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.inventory.Vendor;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
}
