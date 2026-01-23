package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.ErpLayout;

@Repository
public interface ErpLayoutRepository extends JpaRepository<ErpLayout, Long> {

    /**
     * Find all active layouts for an entity
     */
    @Query("SELECT l FROM ErpLayout l WHERE l.erpEntity.id = :erpEntityId AND l.isActive = 1 ORDER BY l.layoutName ASC")
    List<ErpLayout> findByErpEntityIdAndIsActiveTrue(@Param("erpEntityId") Long erpEntityId);

    /**
     * Find the default layout for an entity
     */
    @Query("SELECT l FROM ErpLayout l WHERE l.erpEntity.id = :erpEntityId AND l.isDefault = true AND l.isActive = 1")
    Optional<ErpLayout> findDefaultByErpEntityId(@Param("erpEntityId") Long erpEntityId);

    /**
     * Find layout by entity and name
     */
    @Query("SELECT l FROM ErpLayout l WHERE l.erpEntity.id = :erpEntityId AND l.layoutName = :layoutName AND l.isActive = 1")
    Optional<ErpLayout> findByErpEntityIdAndLayoutName(
            @Param("erpEntityId") Long erpEntityId,
            @Param("layoutName") String layoutName);

    /**
     * Find layouts by type
     */
    @Query("SELECT l FROM ErpLayout l WHERE l.erpEntity.id = :erpEntityId AND l.layoutType = :layoutType AND l.isActive = 1")
    List<ErpLayout> findByErpEntityIdAndLayoutType(
            @Param("erpEntityId") Long erpEntityId,
            @Param("layoutType") String layoutType);

    /**
     * Check if a layout exists
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM ErpLayout l WHERE l.erpEntity.id = :erpEntityId AND l.layoutName = :layoutName AND l.isActive = 1")
    boolean existsByErpEntityIdAndLayoutName(
            @Param("erpEntityId") Long erpEntityId,
            @Param("layoutName") String layoutName);
}
