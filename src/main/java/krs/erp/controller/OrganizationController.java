package krs.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import krs.erp.config.CustomUserDetails;
import krs.erp.model.Organization;
import krs.erp.service.OrganizationService;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private krs.erp.service.SubscriptionService subscriptionService;

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

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

    /**
     * Register new organization with sample data population option
     * POST /api/organizations/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerOrganization(
            @Valid @RequestBody krs.erp.dto.OrganizationRegistrationRequest request) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long currentUserId = null;

            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                currentUserId = userDetails.getUserId();
            }

            if (currentUserId == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User must be authenticated to create an organization");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // Create organization from registration request
            Organization organization = new Organization();
            organization.setName(request.getName());
            organization.setType(request.getType());
            organization.setCode(request.getCode());
            organization.setDescription(request.getDescription());
            organization.setEmail(request.getEmail());
            organization.setPhone(request.getPhone());
            organization.setFax(request.getFax());
            organization.setWebsite(request.getWebsite());
            organization.setStreetAddress(request.getStreetAddress());
            organization.setCity(request.getCity());
            organization.setState(request.getState());
            organization.setPostalCode(request.getPostalCode());
            organization.setCountry(request.getCountry());
            organization.setRegistrationNumber(request.getRegistrationNumber());
            organization.setTaxId(request.getTaxId());
            organization.setEstablishedYear(request.getEstablishedYear());
            organization.setAccreditation(request.getAccreditation());

            // Save organization
            Organization savedOrganization = organizationService.createOrganization(organization);

            // Update user's organization_id in IAM_MasterDB
            try {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
                String updateSql = "UPDATE iam_users SET organization_id = ? WHERE user_id = ?";
                int updated = jdbcTemplate.update(updateSql, savedOrganization.getId(), currentUserId);

                if (updated == 0) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "Failed to link organization to user. User not found.");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
                }
            } catch (Exception e) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Organization created but failed to link to user: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(error);
            }

            // TODO: If loadSampleData is true, trigger sample data population
            // This will be handled by a separate async task or by the frontend
            // importHistoryService.populateSampleData(request.getLoadSampleData());

            // Create Trial Subscription
            try {
                subscriptionService.createTrialSubscription(savedOrganization);
            } catch (Exception e) {
                // Log but don't fail registration
                System.err.println("Failed to create trial subscription: " + e.getMessage());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrganization);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>();
            String message = e.getMessage();
            if (message != null && message.contains("code")) {
                error.put("message", "Organization code already exists. Please use a different code.");
            } else if (message != null && message.contains("name")) {
                error.put("message", "Organization name already exists. Please use a different name.");
            } else {
                error.put("message", "Duplicate organization data. Please check your inputs.");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to create organization: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}