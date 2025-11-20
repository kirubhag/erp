package krs.erp.repository.academic;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.academic.AcademicSettings;

/**
 * Repository for AcademicSettings entity
 */
@Repository
public interface AcademicSettingsRepository extends JpaRepository<AcademicSettings, Long> {
    
    Optional<AcademicSettings> findByOrganizationId(Long organizationId);
}
