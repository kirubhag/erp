package krs.erp.repository.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.inventory.Consumable;

@Repository
public interface ConsumableRepository extends JpaRepository<Consumable, Long> {
}
