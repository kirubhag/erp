package krs.erp.controller.calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import krs.erp.model.calendar.CalendarDay;
import krs.erp.model.calendar.InstitutionEvent;
import krs.erp.service.calendar.EventService;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/days")
    public List<CalendarDay> getAllDays() {
        return eventService.getAllCalendarDays();
    }

    @GetMapping("/events")
    public List<InstitutionEvent> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PostMapping("/days")
    public CalendarDay createDay(@RequestBody CalendarDay day) {
        return eventService.saveCalendarDay(day);
    }

    @PostMapping("/events")
    public InstitutionEvent createEvent(@RequestBody InstitutionEvent event) {
        return eventService.saveEvent(event);
    }
}
