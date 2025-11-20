package krs.erp.repository.academic;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.academic.GradingScale;

/**
 * Repository for GradingScale entity
 */
@Repository
public interface GradingScaleRepository extends JpaRepository<GradingScale, Long> {
    
    List<GradingScale> findByOrganizationIdOrderByMinPercentageDesc(Long organizationId);
}
