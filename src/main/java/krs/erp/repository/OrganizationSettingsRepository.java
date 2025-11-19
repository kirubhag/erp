package krs.erp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.OrganizationSettings;

/**
 * Repository for OrganizationSettings entity
 * Provides CRUD operations and custom queries for organization-wide settings
 */
@Repository
public interface OrganizationSettingsRepository extends JpaRepository<OrganizationSettings, Long> {

    /**
     * Find settings by organization ID
     * @param organizationId the organization ID
     * @return Optional containing OrganizationSettings if found
     */
    Optional<OrganizationSettings> findByOrganizationId(Long organizationId);

    /**
     * Check if settings exist for an organization
     * @param organizationId the organization ID
     * @return true if settings exist, false otherwise
     */
    boolean existsByOrganizationId(Long organizationId);

    /**
     * Find active settings by organization ID
     * @param organizationId the organization ID
     * @return Optional containing active OrganizationSettings if found
     */
    Optional<OrganizationSettings> findByOrganizationIdAndIsActiveTrue(Long organizationId);
}
