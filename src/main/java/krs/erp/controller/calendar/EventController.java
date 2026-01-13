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

    @GetMapping("/events")
    public List<InstitutionEvent> getEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/holidays")
    public List<CalendarDay> getCalendarDays() {
        return eventService.getAllCalendarDays();
    }

    @PostMapping("/holidays")
    public CalendarDay createCalendarDay(@RequestBody CalendarDay day) {
        return eventService.createCalendarDay(day);
    }

}
