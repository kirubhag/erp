package krs.erp.repository;

import krs.erp.entity.ErpSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ErpSubscriptionRepository extends JpaRepository<ErpSubscription, Long> {
    Optional<ErpSubscription> findByUserIdAndStatus(Long userId, String status);

    Optional<ErpSubscription> findByRazorpaySubscriptionId(String razorpaySubscriptionId);

    List<ErpSubscription> findByUserId(Long userId);
}
