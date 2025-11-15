package krs.erp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.User;
import krs.erp.model.UserSettings;

/**
 * UserSettingsRepository - Data access layer for UserSettings entity
 * 
 * Provides query methods for:
 * - Finding settings by user
 * - Updating specific settings fields
 * - Checking if settings exist
 */
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    
    /**
     * Find settings by user ID
     * @param userId User ID
     * @return Optional containing UserSettings if found
     */
    Optional<UserSettings> findByUserId(Long userId);
    
    /**
     * Find settings by user entity
     * @param user User entity
     * @return Optional containing UserSettings if found
     */
    Optional<UserSettings> findByUser(User user);
    
    /**
     * Check if settings exist for a user
     * @param userId User ID
     * @return true if settings exist, false otherwise
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Delete settings by user ID
     * @param userId User ID
     */
    @Modifying
    @Query("DELETE FROM UserSettings us WHERE us.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    /**
     * Update theme preference for a user
     * @param userId User ID
     * @param themePreference Theme preference (LIGHT/DARK)
     */
    @Modifying
    @Query("UPDATE UserSettings us SET us.themePreference = :themePreference, us.lastUpdated = CURRENT_TIMESTAMP WHERE us.user.id = :userId")
    void updateThemePreference(@Param("userId") Long userId, @Param("themePreference") String themePreference);
    
    /**
     * Update primary color for a user
     * @param userId User ID
     * @param primaryColor Primary color hex code
     */
    @Modifying
    @Query("UPDATE UserSettings us SET us.primaryColor = :primaryColor, us.lastUpdated = CURRENT_TIMESTAMP WHERE us.user.id = :userId")
    void updatePrimaryColor(@Param("userId") Long userId, @Param("primaryColor") String primaryColor);
    
    /**
     * Update list sidebar state for a user
     * @param userId User ID
     * @param expanded true if expanded, false if collapsed
     */
    @Modifying
    @Query("UPDATE UserSettings us SET us.listSidebarExpanded = :expanded, us.lastUpdated = CURRENT_TIMESTAMP WHERE us.user.id = :userId")
    void updateListSidebarState(@Param("userId") Long userId, @Param("expanded") Boolean expanded);
    
    /**
     * Update list items per page for a user
     * @param userId User ID
     * @param itemsPerPage Items per page count
     */
    @Modifying
    @Query("UPDATE UserSettings us SET us.listItemsPerPage = :itemsPerPage, us.lastUpdated = CURRENT_TIMESTAMP WHERE us.user.id = :userId")
    void updateListItemsPerPage(@Param("userId") Long userId, @Param("itemsPerPage") Integer itemsPerPage);
}
