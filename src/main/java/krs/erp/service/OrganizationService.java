package krs.erp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.Organization;
import krs.erp.repository.OrganizationRepository;

@Service
@Transactional
public class OrganizationService {
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    /**
     * Get all active organizations with pagination
     */
    @Transactional(readOnly = true)
    public Page<Organization> getAllOrganizations(Pageable pageable) {
        return organizationRepository.findByIsActiveTrueOrderByNameAsc(pageable);
    }
    
    /**
     * Get organization by ID
     */
    @Transactional(readOnly = true)
    public Optional<Organization> getOrganizationById(Long id) {
        return organizationRepository.findById(id)
                .filter(org -> org.getIsActive());
    }
    
    /**
     * Get organization by code
     */
    @Transactional(readOnly = true)
    public Optional<Organization> getOrganizationByCode(String code) {
        return organizationRepository.findByCodeAndIsActiveTrue(code);
    }
    
    /**
     * Get organization by name
     */
    @Transactional(readOnly = true)
    public Optional<Organization> getOrganizationByName(String name) {
        return organizationRepository.findByNameAndIsActiveTrue(name);
    }
    
    /**
     * Get organizations by type
     */
    @Transactional(readOnly = true)
    public List<Organization> getOrganizationsByType(String type) {
        return organizationRepository.findByTypeAndIsActiveTrueOrderByNameAsc(type);
    }
    
    /**
     * Search organizations by name or code
     */
    @Transactional(readOnly = true)
    public Page<Organization> searchOrganizations(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllOrganizations(pageable);
        }
        return organizationRepository.searchByNameOrCode(searchTerm.trim(), pageable);
    }
    
    /**
     * Create new organization
     */
    public Organization createOrganization(Organization organization) {
        validateOrganization(organization, null);
        return organizationRepository.save(organization);
    }
    
    /**
     * Update existing organization
     */
    public Organization updateOrganization(Long id, Organization organization) {
        Optional<Organization> existingOrgOpt = getOrganizationById(id);
        if (existingOrgOpt.isEmpty()) {
            throw new RuntimeException("Organization not found with id: " + id);
        }
        
        Organization existingOrg = existingOrgOpt.get();
        validateOrganization(organization, id);
        
        // Update fields
        existingOrg.setName(organization.getName());
        existingOrg.setType(organization.getType());
        existingOrg.setCode(organization.getCode());
        existingOrg.setDescription(organization.getDescription());
        existingOrg.setEmail(organization.getEmail());
        existingOrg.setPhone(organization.getPhone());
        existingOrg.setFax(organization.getFax());
        existingOrg.setWebsite(organization.getWebsite());
        existingOrg.setStreetAddress(organization.getStreetAddress());
        existingOrg.setCity(organization.getCity());
        existingOrg.setState(organization.getState());
        existingOrg.setPostalCode(organization.getPostalCode());
        existingOrg.setCountry(organization.getCountry());
        existingOrg.setRegistrationNumber(organization.getRegistrationNumber());
        existingOrg.setTaxId(organization.getTaxId());
        existingOrg.setEstablishedYear(organization.getEstablishedYear());
        existingOrg.setAccreditation(organization.getAccreditation());
        existingOrg.setAcademicYearFormat(organization.getAcademicYearFormat());
        existingOrg.setDefaultLanguage(organization.getDefaultLanguage());
        existingOrg.setDefaultCurrency(organization.getDefaultCurrency());
        existingOrg.setTimezone(organization.getTimezone());
        existingOrg.setLogoUrl(organization.getLogoUrl());
        
        return organizationRepository.save(existingOrg);
    }
    
    /**
     * Delete organization (soft delete)
     */
    public void deleteOrganization(Long id) {
        Optional<Organization> organizationOpt = getOrganizationById(id);
        if (organizationOpt.isEmpty()) {
            throw new RuntimeException("Organization not found with id: " + id);
        }
        
        Organization organization = organizationOpt.get();
        organization.setIsActive(false);
        organizationRepository.save(organization);
    }
    
    /**
     * Get all organization types
     */
    @Transactional(readOnly = true)
    public List<String> getAllOrganizationTypes() {
        return organizationRepository.findDistinctTypes();
    }
    
    /**
     * Get organizations by city
     */
    @Transactional(readOnly = true)
    public List<Organization> getOrganizationsByCity(String city) {
        return organizationRepository.findByCityAndIsActiveTrueOrderByNameAsc(city);
    }
    
    /**
     * Get organizations by country
     */
    @Transactional(readOnly = true)
    public List<Organization> getOrganizationsByCountry(String country) {
        return organizationRepository.findByCountryAndIsActiveTrueOrderByNameAsc(country);
    }
    
    /**
     * Count total organizations
     */
    @Transactional(readOnly = true)
    public long countOrganizations() {
        return organizationRepository.findByIsActiveTrueOrderByNameAsc(Pageable.unpaged()).getTotalElements();
    }
    
    /**
     * Validate organization data
     */
    private void validateOrganization(Organization organization, Long excludeId) {
        // Check if name already exists
        if (organization.getName() != null) {
            Optional<Organization> existingByName = organizationRepository.findByNameAndIsActiveTrue(organization.getName());
            if (existingByName.isPresent() && (excludeId == null || !existingByName.get().getId().equals(excludeId))) {
                throw new RuntimeException("Organization name already exists: " + organization.getName());
            }
        }
        
        // Check if code already exists (if provided)
        if (organization.getCode() != null && !organization.getCode().trim().isEmpty()) {
            Optional<Organization> existingByCode = organizationRepository.findByCodeAndIsActiveTrue(organization.getCode());
            if (existingByCode.isPresent() && (excludeId == null || !existingByCode.get().getId().equals(excludeId))) {
                throw new RuntimeException("Organization code already exists: " + organization.getCode());
            }
        }
    }
}