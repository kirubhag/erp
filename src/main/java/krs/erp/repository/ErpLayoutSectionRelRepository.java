package krs.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.ErpLayoutSectionRel;

@Repository
public interface ErpLayoutSectionRelRepository extends JpaRepository<ErpLayoutSectionRel, ErpLayoutSectionRel.ErpLayoutSectionRelId> {

    /**
     * Find all section relationships for a layout, ordered by section order
     */
    @Query("SELECT r FROM ErpLayoutSectionRel r WHERE r.id.erpLayoutId = :layoutId ORDER BY r.sectionOrder ASC")
    List<ErpLayoutSectionRel> findByLayoutId(@Param("layoutId") Long layoutId);

    /**
     * Find all layout relationships for a section
     */
    @Query("SELECT r FROM ErpLayoutSectionRel r WHERE r.id.erpSectionId = :sectionId")
    List<ErpLayoutSectionRel> findBySectionId(@Param("sectionId") Long sectionId);

    /**
     * Delete all section relationships for a layout
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ErpLayoutSectionRel r WHERE r.id.erpLayoutId = :layoutId")
    void deleteByLayoutId(@Param("layoutId") Long layoutId);

    /**
     * Delete all layout relationships for a section
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ErpLayoutSectionRel r WHERE r.id.erpSectionId = :sectionId")
    void deleteBySectionId(@Param("sectionId") Long sectionId);

    /**
     * Check if a relationship exists
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ErpLayoutSectionRel r WHERE r.id.erpLayoutId = :layoutId AND r.id.erpSectionId = :sectionId")
    boolean existsByLayoutIdAndSectionId(
            @Param("layoutId") Long layoutId,
            @Param("sectionId") Long sectionId);
}
