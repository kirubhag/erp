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
import krs.erp.model.Organization;
import krs.erp.service.OrganizationService;

@RestController
@RequestMapping("/api/organizations")
@CrossOrigin(origins = "*")
public class OrganizationController {
    
    @Autowired
    private OrganizationService organizationService;
    
    /**
     * Get all organizations with pagination
     */
    @GetMapping
    public ResponseEntity<Page<Organization>> getAllOrganizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Organization> organizations;
            
            if (search != null && !search.trim().isEmpty()) {
                organizations = organizationService.searchOrganizations(search, pageable);
            } else {
                organizations = organizationService.getAllOrganizations(pageable);
            }
            
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get organization by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable Long id) {
        Optional<Organization> organization = organizationService.getOrganizationById(id);
        return organization.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get organization by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<Organization> getOrganizationByCode(@PathVariable String code) {
        Optional<Organization> organization = organizationService.getOrganizationByCode(code);
        return organization.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get organizations by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Organization>> getOrganizationsByType(@PathVariable String type) {
        try {
            List<Organization> organizations = organizationService.getOrganizationsByType(type);
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get all organization types
     */
    @GetMapping("/types")
    public ResponseEntity<List<String>> getAllOrganizationTypes() {
        try {
            List<String> types = organizationService.getAllOrganizationTypes();
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get organizations by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Organization>> getOrganizationsByCity(@PathVariable String city) {
        try {
            List<Organization> organizations = organizationService.getOrganizationsByCity(city);
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get organizations by country
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Organization>> getOrganizationsByCountry(@PathVariable String country) {
        try {
            List<Organization> organizations = organizationService.getOrganizationsByCountry(country);
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create new organization
     */
    @PostMapping
    public ResponseEntity<Organization> createOrganization(@Valid @RequestBody Organization organization) {
        try {
            Organization savedOrganization = organizationService.createOrganization(organization);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrganization);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update existing organization
     */
    @PutMapping("/{id}")
    public ResponseEntity<Organization> updateOrganization(
            @PathVariable Long id, 
            @Valid @RequestBody Organization organization) {
        try {
            Organization updatedOrganization = organizationService.updateOrganization(id, organization);
            return ResponseEntity.ok(updatedOrganization);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete organization
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        try {
            organizationService.deleteOrganization(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get organization count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getOrganizationCount() {
        try {
            long count = organizationService.countOrganizations();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}