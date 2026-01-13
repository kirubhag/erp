package krs.erp.repository;

import krs.erp.entity.ErpSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ErpSubscriptionRepository extends JpaRepository<ErpSubscription, Long> {
    Optional<ErpSubscription> findByRazorpaySubscriptionId(String razorpaySubscriptionId);

    Optional<ErpSubscription> findByOrganizationId(Long organizationId);

    List<ErpSubscription> findByStatusAndCurrentPeriodEndBefore(String status, java.time.LocalDateTime date);

}
