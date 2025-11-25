package krs.erp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.UserSubscription;
import krs.erp.model.enums.SubscriptionStatus;

/**
 * Repository for UserSubscription entity
 */
@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByUserIdAndOrganizationIdAndSubscriptionStatus(
            Long userId, Long organizationId, SubscriptionStatus status);

    List<UserSubscription> findByUserId(Long userId);

    List<UserSubscription> findByOrganizationId(Long organizationId);

    @Query("SELECT s FROM UserSubscription s JOIN FETCH s.plan WHERE s.userId = :userId AND s.organizationId = :organizationId " +
           "AND s.subscriptionStatus IN ('ACTIVE', 'TRIAL') ORDER BY s.createdAt DESC")
    Optional<UserSubscription> findActiveSubscription(@Param("userId") Long userId, 
                                                       @Param("organizationId") Long organizationId);

    @Query("SELECT s FROM UserSubscription s JOIN FETCH s.plan WHERE s.subscriptionStatus = 'TRIAL' " +
           "AND s.trialEndDate = :date")
    List<UserSubscription> findTrialsExpiringOnDate(@Param("date") LocalDate date);

    @Query("SELECT s FROM UserSubscription s JOIN FETCH s.plan WHERE s.subscriptionStatus = 'TRIAL' " +
           "AND s.trialEndDate < :date")
    List<UserSubscription> findExpiredTrials(@Param("date") LocalDate date);

    @Query("SELECT s FROM UserSubscription s JOIN FETCH s.plan WHERE s.nextBillingDate = :date " +
           "AND s.subscriptionStatus = 'ACTIVE' AND s.isAutoRenew = true")
    List<UserSubscription> findSubscriptionsForRenewal(@Param("date") LocalDate date);

    @Query("SELECT COUNT(s) FROM UserSubscription s WHERE s.organizationId = :orgId " +
           "AND s.subscriptionStatus IN ('ACTIVE', 'TRIAL')")
    long countActiveSubscriptionsByOrganization(@Param("orgId") Long organizationId);
}
