package krs.erp.controller.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.inventory.Asset;
import krs.erp.model.inventory.Consumable;
import krs.erp.model.inventory.PurchaseOrder;
import krs.erp.model.inventory.Vendor;
import krs.erp.repository.inventory.AssetRepository;
import krs.erp.repository.inventory.ConsumableRepository;
import krs.erp.repository.inventory.PurchaseOrderRepository;
import krs.erp.repository.inventory.VendorRepository;
import krs.erp.service.inventory.InventoryService;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private ConsumableRepository consumableRepository;
    
    @Autowired
    private VendorRepository vendorRepository;
    
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    // === Assets CRUD ===
    @GetMapping("/assets")
    public ResponseEntity<Page<Asset>> getAllAssets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(assetRepository.findAll(pageable));
    }
    
    @GetMapping("/assets/{id}")
    public ResponseEntity<Asset> getAsset(@PathVariable Long id) {
        return assetRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/assets")
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        return ResponseEntity.ok(assetRepository.save(asset));
    }
    
    @PutMapping("/assets/{id}")
    public ResponseEntity<Asset> updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        if (!assetRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        asset.setId(id);
        return ResponseEntity.ok(assetRepository.save(asset));
    }
    
    @DeleteMapping("/assets/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        if (!assetRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        assetRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assets/{assetId}/assign/{staffId}")
    public ResponseEntity<Asset> assignAsset(@PathVariable Long assetId, @PathVariable Long staffId) {
        return ResponseEntity.ok(inventoryService.assignAsset(assetId, staffId));
    }

    // === Consumables CRUD ===
    @GetMapping("/consumables")
    public ResponseEntity<Page<Consumable>> getAllConsumables(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(consumableRepository.findAll(pageable));
    }
    
    @GetMapping("/consumables/{id}")
    public ResponseEntity<Consumable> getConsumable(@PathVariable Long id) {
        return consumableRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/consumables")
    public ResponseEntity<Consumable> createConsumable(@RequestBody Consumable consumable) {
        return ResponseEntity.ok(consumableRepository.save(consumable));
    }
    
    @PutMapping("/consumables/{id}")
    public ResponseEntity<Consumable> updateConsumable(@PathVariable Long id, @RequestBody Consumable consumable) {
        if (!consumableRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        consumable.setId(id);
        return ResponseEntity.ok(consumableRepository.save(consumable));
    }
    
    @DeleteMapping("/consumables/{id}")
    public ResponseEntity<Void> deleteConsumable(@PathVariable Long id) {
        if (!consumableRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        consumableRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/consumables/{consumableId}/update-stock")
    public ResponseEntity<Consumable> updateStock(@PathVariable Long consumableId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.updateStock(consumableId, quantity));
    }

    // === Vendors CRUD ===
    @GetMapping("/vendors")
    public ResponseEntity<Page<Vendor>> getAllVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(vendorRepository.findAll(pageable));
    }
    
    @GetMapping("/vendors/{id}")
    public ResponseEntity<Vendor> getVendor(@PathVariable Long id) {
        return vendorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/vendors")
    public ResponseEntity<Vendor> createVendor(@RequestBody Vendor vendor) {
        return ResponseEntity.ok(vendorRepository.save(vendor));
    }
    
    @PutMapping("/vendors/{id}")
    public ResponseEntity<Vendor> updateVendor(@PathVariable Long id, @RequestBody Vendor vendor) {
        if (!vendorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        vendor.setId(id);
        return ResponseEntity.ok(vendorRepository.save(vendor));
    }
    
    @DeleteMapping("/vendors/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        if (!vendorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        vendorRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Purchase Orders CRUD ===
    @GetMapping("/purchase-orders")
    public ResponseEntity<Page<PurchaseOrder>> getAllPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(purchaseOrderRepository.findAll(pageable));
    }
    
    @GetMapping("/purchase-orders/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrder(@PathVariable Long id) {
        return purchaseOrderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/purchase-orders")
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
        return ResponseEntity.ok(purchaseOrderRepository.save(purchaseOrder));
    }
    
    @PutMapping("/purchase-orders/{id}")
    public ResponseEntity<PurchaseOrder> updatePurchaseOrder(@PathVariable Long id, @RequestBody PurchaseOrder purchaseOrder) {
        if (!purchaseOrderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        purchaseOrder.setId(id);
        return ResponseEntity.ok(purchaseOrderRepository.save(purchaseOrder));
    }
    
    @DeleteMapping("/purchase-orders/{id}")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable Long id) {
        if (!purchaseOrderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        purchaseOrderRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
