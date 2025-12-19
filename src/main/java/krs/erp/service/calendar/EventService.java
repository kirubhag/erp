package krs.erp.service.calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import krs.erp.model.calendar.CalendarDay;
import krs.erp.model.calendar.InstitutionEvent;
import krs.erp.repository.calendar.CalendarDayRepository;
import krs.erp.repository.calendar.InstitutionEventRepository;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EventService {

    @Autowired
    private CalendarDayRepository calendarDayRepository;

    @Autowired
    private InstitutionEventRepository eventRepository;

    public List<CalendarDay> getAllCalendarDays() {
        return calendarDayRepository.findAll();
    }

    public List<InstitutionEvent> getAllEvents() {
        return eventRepository.findAll();
    }

    public CalendarDay saveCalendarDay(CalendarDay day) {
        return calendarDayRepository.save(day);
    }

    public InstitutionEvent saveEvent(InstitutionEvent event) {
        InstitutionEvent saved = eventRepository.save(event);
        // Automatically sync to calendar if it's an event
        syncEventToCalendar(saved);
        return saved;
    }

    private void syncEventToCalendar(InstitutionEvent event) {
        LocalDate date = event.getStartDate().toLocalDate();
        CalendarDay day = calendarDayRepository.findByDate(date).orElse(new CalendarDay());
        day.setDate(date);
        day.setType("EVENT");
        day.setDescription(event.getTitle());
        day.setHoliday(false);
        day.setEventId(event.getId());
        calendarDayRepository.save(day);
    }
}
