package krs.erp.repository;

import krs.erp.model.PricingPlan;
import krs.erp.model.enums.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PricingPlan entity
 */
@Repository
public interface PricingPlanRepository extends JpaRepository<PricingPlan, Long> {

    Optional<PricingPlan> findByPlanType(PlanType planType);

    Optional<PricingPlan> findByPlanName(String planName);

    List<PricingPlan> findByIsActiveTrue();

    @Query("SELECT p FROM PricingPlan p WHERE p.planType = :planType AND p.isActive = true")
    Optional<PricingPlan> findActivePlanByType(PlanType planType);

    @Query("SELECT p FROM PricingPlan p WHERE p.isActive = true ORDER BY p.priceMonthly ASC")
    List<PricingPlan> findAllActivePlansOrderedByPrice();
}
