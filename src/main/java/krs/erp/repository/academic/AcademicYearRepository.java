package krs.erp.repository.academic;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import krs.erp.model.academic.AcademicYear;

/**
 * Repository for AcademicYear entity
 */
@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    
    List<AcademicYear> findByOrganizationIdOrderByStartDateDesc(Long organizationId);
    
    Optional<AcademicYear> findByOrganizationIdAndIsActive(Long organizationId, Boolean isActive);
    
    @Modifying
    @Query("UPDATE AcademicYear a SET a.isActive = false WHERE a.organizationId = :organizationId")
    void deactivateAllForOrganization(Long organizationId);
}
