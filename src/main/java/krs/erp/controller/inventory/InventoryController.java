package krs.erp.controller.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import krs.erp.model.inventory.Asset;
import krs.erp.model.inventory.Consumable;
import krs.erp.service.inventory.InventoryService;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/assets/{assetId}/assign/{staffId}")
    public ResponseEntity<Asset> assignAsset(@PathVariable Long assetId, @PathVariable Long staffId) {
        return ResponseEntity.ok(inventoryService.assignAsset(assetId, staffId));
    }

    @PostMapping("/consumables/{consumableId}/update-stock")
    public ResponseEntity<Consumable> updateStock(@PathVariable Long consumableId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.updateStock(consumableId, quantity));
    }
}
