package krs.erp.repository;

import krs.erp.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PaymentTransaction entity
 */
@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByTransactionId(String transactionId);

    List<PaymentTransaction> findBySubscriptionId(Long subscriptionId);

    List<PaymentTransaction> findByUserId(Long userId);

    List<PaymentTransaction> findByOrganizationId(Long organizationId);

    @Query("SELECT t FROM PaymentTransaction t WHERE t.userId = :userId " +
           "ORDER BY t.transactionDate DESC")
    List<PaymentTransaction> findUserTransactionsOrderedByDate(@Param("userId") Long userId);

    @Query("SELECT t FROM PaymentTransaction t WHERE t.transactionStatus = 'PENDING' " +
           "AND t.transactionDate < :cutoffTime")
    List<PaymentTransaction> findPendingTransactionsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT t FROM PaymentTransaction t WHERE t.subscriptionId = :subscriptionId " +
           "AND t.transactionStatus = 'SUCCESS' ORDER BY t.transactionDate DESC")
    List<PaymentTransaction> findSuccessfulTransactionsBySubscription(@Param("subscriptionId") Long subscriptionId);
}
