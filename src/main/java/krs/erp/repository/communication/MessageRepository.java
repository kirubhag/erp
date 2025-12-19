package krs.erp.repository.communication;

import krs.erp.model.communication.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRecipientIdOrderBySentAtDesc(Long recipientId);

    List<Message> findBySenderIdOrderBySentAtDesc(Long senderId);
}
