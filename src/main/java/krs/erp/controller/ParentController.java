package krs.erp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

import jakarta.validation.Valid;
import krs.erp.enums.EntityType;
import krs.erp.model.Parent;
import krs.erp.repository.ParentRepository;
import krs.erp.service.RecycleBinService;

@RestController
@RequestMapping("/api/parents")
@CrossOrigin(origins = "*")
public class ParentController {
    
    @Autowired
    private ParentRepository parentRepository;
    
    @Autowired
    private RecycleBinService recycleBinService;
    
    // Get all parents with pagination
    @GetMapping
    public ResponseEntity<Page<Parent>> getAllParents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Parent> parents = parentRepository.findAll(pageable);
        return ResponseEntity.ok(parents);
    }
    
    // Get parent by ID
    @GetMapping("/{id}")
    public ResponseEntity<Parent> getParentById(@PathVariable Long id) {
        Optional<Parent> parent = parentRepository.findById(id);
        return parent.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new parent
    @PostMapping
    public ResponseEntity<Parent> createParent(@Valid @RequestBody Parent parent) {
        try {
            Parent savedParent = parentRepository.save(parent);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedParent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // Update parent
    @PutMapping("/{id}")
    public ResponseEntity<Parent> updateParent(@PathVariable Long id, @Valid @RequestBody Parent parentDetails) {
        return parentRepository.findById(id)
                .map(parent -> {
                    parent.setFirstName(parentDetails.getFirstName());
                    parent.setMiddleName(parentDetails.getMiddleName());
                    parent.setLastName(parentDetails.getLastName());
                    parent.setEmail(parentDetails.getEmail());
                    parent.setPhone(parentDetails.getPhone());
                    parent.setAlternatePhone(parentDetails.getAlternatePhone());
                    parent.setWorkPhone(parentDetails.getWorkPhone());
                    parent.setAddressLine1(parentDetails.getAddressLine1());
                    parent.setAddressLine2(parentDetails.getAddressLine2());
                    parent.setCity(parentDetails.getCity());
                    parent.setState(parentDetails.getState());
                    parent.setPostalCode(parentDetails.getPostalCode());
                    parent.setCountry(parentDetails.getCountry());
                    parent.setOccupation(parentDetails.getOccupation());
                    parent.setWorkplace(parentDetails.getWorkplace());
                    parent.setGender(parentDetails.getGender());
                    parent.setEmergencyContact(parentDetails.getEmergencyContact());
                    parent.setAuthorizedPickup(parentDetails.getAuthorizedPickup());
                    parent.setReceiveNotifications(parentDetails.getReceiveNotifications());
                    parent.setIsActive(parentDetails.getIsActive());
                    
                    return ResponseEntity.ok(parentRepository.save(parent));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Delete parent (soft delete with recycle bin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParent(@PathVariable Long id) {
        Optional<Parent> parentOpt = parentRepository.findById(id);
        if (parentOpt.isPresent()) {
            // Soft delete using recycle bin service
            recycleBinService.softDeleteEntity(id, EntityType.PARENT, "current-user", "User deleted parent");
            
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Get active parents (isActive = 1)
    @GetMapping("/active")
    public ResponseEntity<List<Parent>> getActiveParents() {
        List<Parent> parents = parentRepository.findByIsActive(1);
        return ResponseEntity.ok(parents);
    }
    
    // Get parent count
    @GetMapping("/count")
    public ResponseEntity<Long> getParentCount() {
        Long count = parentRepository.countByIsActiveTrue();
        return ResponseEntity.ok(count);
    }
    
    // Search parents by name
    @GetMapping("/search")
    public ResponseEntity<List<Parent>> searchParents(@RequestParam String name) {
        List<Parent> parents = parentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
        return ResponseEntity.ok(parents);
    }
}