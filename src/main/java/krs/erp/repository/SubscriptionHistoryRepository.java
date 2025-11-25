package krs.erp.repository;

import krs.erp.model.SubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for SubscriptionHistory entity
 */
@Repository
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long> {

    List<SubscriptionHistory> findBySubscriptionId(Long subscriptionId);

    List<SubscriptionHistory> findByUserId(Long userId);

    @Query("SELECT h FROM SubscriptionHistory h WHERE h.userId = :userId " +
           "ORDER BY h.effectiveDate DESC, h.createdAt DESC")
    List<SubscriptionHistory> findUserHistoryOrderedByDate(@Param("userId") Long userId);

    @Query("SELECT h FROM SubscriptionHistory h WHERE h.subscriptionId = :subscriptionId " +
           "ORDER BY h.effectiveDate DESC, h.createdAt DESC")
    List<SubscriptionHistory> findSubscriptionHistoryOrderedByDate(@Param("subscriptionId") Long subscriptionId);
}
