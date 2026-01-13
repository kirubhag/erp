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
import java.util.Optional;

@Service
@Transactional
public class EventService {

    @Autowired
    private InstitutionEventRepository eventRepository;

    @Autowired
    private CalendarDayRepository calendarDayRepository;

    public List<InstitutionEvent> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<CalendarDay> getAllCalendarDays() {
        return calendarDayRepository.findAll();
    }

    public InstitutionEvent createEvent(InstitutionEvent event) {
        return eventRepository.save(event);
    }

    public CalendarDay createCalendarDay(CalendarDay calendarDay) {
        // Check if date already exists
        if (calendarDayRepository.findByDate(calendarDay.getDate()).isPresent()) {
            throw new IllegalArgumentException("Calendar day already exists for date: " + calendarDay.getDate());
        }
        return calendarDayRepository.save(calendarDay);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public void deleteCalendarDay(Long id) {
        calendarDayRepository.deleteById(id);
    }

    public Optional<CalendarDay> getCalendarDayByDate(LocalDate date) {
        return calendarDayRepository.findByDate(date);
    }
}
