package krs.erp.repository.admission;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.admission.AdmissionCycle;

@Repository
public interface AdmissionCycleRepository extends JpaRepository<AdmissionCycle, Long> {

    List<AdmissionCycle> findByAcademicYearId(Long academicYearId);

    @Query("SELECT ac FROM AdmissionCycle ac WHERE :date BETWEEN ac.startDate AND ac.endDate")
    Optional<AdmissionCycle> findActiveCycleForDate(@Param("date") LocalDate date);
}
