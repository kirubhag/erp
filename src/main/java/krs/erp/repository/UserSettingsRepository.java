package krs.erp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import krs.erp.model.UserSettings;

/**
 * UserSettingsRepository - Data access layer for UserSettings entity
 * 
 * Provides query methods for:
 * - Finding settings by user and organization
 * - Updating specific settings fields
 * - Checking if settings exist
 */
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    
    /**
     * Find settings by userId and organizationId
     * @param userId User ID
     * @param organizationId Organization ID
     * @return Optional containing UserSettings if found
     */
    Optional<UserSettings> findByUserIdAndOrganizationId(Long userId, Long organizationId);
    
    /**
     * Update theme preference
     */
    @Modifying
    @Query("UPDATE UserSettings u SET u.theme = :theme, u.lastUpdated = CURRENT_TIMESTAMP " +
           "WHERE u.userId = :userId AND u.organizationId = :organizationId")
    void updateThemePreference(Long userId, Long organizationId, String theme);
    
    /**
     * Update list items per page
     */
    @Modifying
    @Query("UPDATE UserSettings u SET u.recordsPerPage = :itemsPerPage, u.lastUpdated = CURRENT_TIMESTAMP " +
           "WHERE u.userId = :userId AND u.organizationId = :organizationId")
    void updateListItemsPerPage(Long userId, Long organizationId, Integer itemsPerPage);
    
    /**
     * Update list sidebar visibility
     */
    @Modifying
    @Query("UPDATE UserSettings u SET u.listSidebarExpanded = :expanded, u.lastUpdated = CURRENT_TIMESTAMP " +
           "WHERE u.userId = :userId AND u.organizationId = :organizationId")
    void updateListSidebarState(Long userId, Long organizationId, Boolean expanded);
    
    /**
     * Update default list view (table or card)
     */
    @Modifying
    @Query("UPDATE UserSettings u SET u.defaultListView = :listView, u.lastUpdated = CURRENT_TIMESTAMP " +
           "WHERE u.userId = :userId AND u.organizationId = :organizationId")
    void updateDefaultListView(Long userId, Long organizationId, String listView);
}

