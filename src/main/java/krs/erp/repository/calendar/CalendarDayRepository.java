package krs.erp.repository.calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.calendar.CalendarDay;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CalendarDayRepository extends JpaRepository<CalendarDay, Long> {
    Optional<CalendarDay> findByDate(LocalDate date);
}
