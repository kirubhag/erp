package krs.erp.controller.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import krs.erp.model.communication.NotificationLog;
import krs.erp.model.communication.NotificationTemplate;
import krs.erp.service.communication.CommunicationService;
import java.util.List;

@RestController
@RequestMapping("/api/communication")
public class CommunicationController {

    @Autowired
    private CommunicationService communicationService;

    @GetMapping("/logs")
    public List<NotificationLog> getAllLogs() {
        return communicationService.getAllLogs();
    }

    @GetMapping("/templates")
    public List<NotificationTemplate> getAllTemplates() {
        return communicationService.getAllTemplates();
    }

    @PostMapping("/templates")
    public NotificationTemplate createTemplate(@RequestBody NotificationTemplate template) {
        return communicationService.saveTemplate(template);
    }
}
