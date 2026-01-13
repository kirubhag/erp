package krs.erp.repository.calendar;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.calendar.Holiday;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    
    Optional<Holiday> findByHolidayDate(LocalDate holidayDate);
    
    List<Holiday> findByHolidayDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT h FROM Holiday h WHERE YEAR(h.holidayDate) = :year ORDER BY h.holidayDate")
    List<Holiday> findByYear(@Param("year") int year);
    
    List<Holiday> findByHolidayType(String holidayType);
    
    @Query("SELECT COUNT(h) > 0 FROM Holiday h WHERE h.holidayDate = :date")
    boolean existsByHolidayDate(@Param("date") LocalDate date);
}
