package krs.erp.repository.communication;

import krs.erp.model.communication.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByCreatedByIdOrderByCreatedAtDesc(Long userId);

    List<SupportTicket> findByAssignedToIdOrderByCreatedAtDesc(Long userId);
}
