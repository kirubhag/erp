package krs.erp.service.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import krs.erp.model.inventory.Asset;
import krs.erp.model.inventory.Consumable;
import krs.erp.repository.inventory.AssetRepository;
import krs.erp.repository.inventory.ConsumableRepository;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private ConsumableRepository consumableRepository;

    @Transactional
    public Asset assignAsset(Long assetId, Long staffId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        asset.setAssignedStaffId(staffId);
        asset.setStatus(Asset.AssetStatus.ASSIGNED);
        return assetRepository.save(asset);
    }

    @Transactional
    public Consumable updateStock(Long consumableId, Integer quantityChange) {
        Consumable consumable = consumableRepository.findById(consumableId)
                .orElseThrow(() -> new RuntimeException("Consumable not found"));
        consumable.setCurrentStock(consumable.getCurrentStock() + quantityChange);
        return consumableRepository.save(consumable);
    }
}
