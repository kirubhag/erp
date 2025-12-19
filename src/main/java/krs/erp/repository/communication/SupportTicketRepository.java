package krs.erp.repository.communication;

import krs.erp.model.communication.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByAuthorIdOrderByCreatedTimeDesc(Long userId);

    List<SupportTicket> findByAssignedToIdOrderByCreatedTimeDesc(Long userId);
}
