package krs.erp.service.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import krs.erp.model.communication.*;
import krs.erp.repository.communication.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CommunicationService {

    @Autowired
    private NotificationLogRepository logRepository;

    @Autowired
    private NotificationTemplateRepository templateRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Autowired
    private TicketCommentRepository ticketCommentRepository;

    // --- Messaging logic ---

    public Message sendMessage(Message message) {
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);
        return messageRepository.save(message);
    }

    public List<Message> getReceivedMessages(Long userId) {
        return messageRepository.findByRecipientIdOrderBySentAtDesc(userId);
    }

    // --- Announcements logic ---

    public Announcement publishAnnouncement(Announcement announcement) {
        announcement.setPublishedAt(LocalDateTime.now());
        return announcementRepository.save(announcement);
    }

    public List<Announcement> getActiveAnnouncements(List<Announcement.AudienceType> targetAudiences) {
        return announcementRepository.findByTargetAudienceInAndExpiresAtAfterOrderByPublishedAtDesc(
                targetAudiences, LocalDateTime.now());
    }

    // --- Support Ticketing logic ---

    public SupportTicket createTicket(SupportTicket ticket) {
        ticket.setStatus(SupportTicket.TicketStatus.OPEN);
        return supportTicketRepository.save(ticket);
    }

    public SupportTicket assignTicket(Long ticketId, krs.erp.model.User staff) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setAssignedTo(staff);
        ticket.setStatus(SupportTicket.TicketStatus.IN_PROGRESS);
        return supportTicketRepository.save(ticket);
    }

    public TicketComment addComment(TicketComment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        return ticketCommentRepository.save(comment);
    }

    public SupportTicket resolveTicket(Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setStatus(SupportTicket.TicketStatus.RESOLVED);
        return supportTicketRepository.save(ticket);
    }

    // --- Existing Notifications logic ---

    public List<NotificationLog> getAllLogs() {
        return logRepository.findAll();
    }

    public List<NotificationTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    @SuppressWarnings("null")
    public NotificationTemplate saveTemplate(NotificationTemplate template) {
        return templateRepository.save(template);
    }

    public NotificationLog sendNotification(String templateName, String recipient, String recipientType,
            java.util.Map<String, String> data) {
        NotificationTemplate template = templateRepository.findByName(templateName)
                .orElseThrow(() -> new RuntimeException("Template not found: " + templateName));

        String content = template.getContent();
        if (data != null) {
            for (java.util.Map.Entry<String, String> entry : data.entrySet()) {
                content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
        }

        NotificationLog log = new NotificationLog();
        log.setRecipient(recipient);
        log.setRecipientType(recipientType);
        log.setChannel(template.getChannel());
        log.setSubject(template.getSubject());
        log.setContent(content);
        log.setStatus("SENT"); // Mocking successful delivery
        log.setSentAt(LocalDateTime.now());

        return logRepository.save(log);
    }
}
