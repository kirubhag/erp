package krs.erp.repository;

import krs.erp.entity.ErpPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ErpPlanRepository extends JpaRepository<ErpPlan, Long> {
    Optional<ErpPlan> findByType(String type);

    Optional<ErpPlan> findByRazorpayPlanId(String razorpayPlanId);
}
