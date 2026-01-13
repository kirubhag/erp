package krs.erp.repository.calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import krs.erp.model.calendar.CalendarDay;
import java.util.Optional;

public interface CalendarDayRepository extends JpaRepository<CalendarDay, Long> {
    Optional<CalendarDay> findByDate(java.time.LocalDate date);
}
