package krs.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.ErpSectionFieldRel;
import krs.erp.model.ErpSectionFieldRel.ErpSectionFieldRelId;

@Repository
public interface ErpSectionFieldRelRepository extends JpaRepository<ErpSectionFieldRel, ErpSectionFieldRelId> {

    /**
     * Find all field relationships for a section
     */
    @Query("SELECT sfr FROM ErpSectionFieldRel sfr WHERE sfr.erpSection.id = :sectionId ORDER BY sfr.fieldOrder ASC")
    List<ErpSectionFieldRel> findBySectionId(@Param("sectionId") Long sectionId);

    /**
     * Find all section relationships for a field
     */
    @Query("SELECT sfr FROM ErpSectionFieldRel sfr WHERE sfr.erpField.id = :fieldId")
    List<ErpSectionFieldRel> findByFieldId(@Param("fieldId") Long fieldId);

    /**
     * Delete all field relationships for a section
     */
    @Modifying
    @Query("DELETE FROM ErpSectionFieldRel sfr WHERE sfr.erpSection.id = :sectionId")
    void deleteBySectionId(@Param("sectionId") Long sectionId);

    /**
     * Delete all section relationships for a field
     */
    @Modifying
    @Query("DELETE FROM ErpSectionFieldRel sfr WHERE sfr.erpField.id = :fieldId")
    void deleteByFieldId(@Param("fieldId") Long fieldId);

    /**
     * Check if a relationship exists
     */
    @Query("SELECT CASE WHEN COUNT(sfr) > 0 THEN true ELSE false END FROM ErpSectionFieldRel sfr WHERE sfr.erpSection.id = :sectionId AND sfr.erpField.id = :fieldId")
    boolean existsBySectionIdAndFieldId(@Param("sectionId") Long sectionId, @Param("fieldId") Long fieldId);
}
