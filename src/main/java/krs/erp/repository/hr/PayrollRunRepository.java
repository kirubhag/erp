package krs.erp.repository.hr;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.hr.PayrollRun;

@Repository
public interface PayrollRunRepository extends JpaRepository<PayrollRun, Long> {
    Optional<PayrollRun> findByMonthAndYear(Integer month, Integer year);

    boolean existsByMonthAndYear(Integer month, Integer year);
}
