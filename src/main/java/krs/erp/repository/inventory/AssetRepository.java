package krs.erp.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.inventory.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
}
