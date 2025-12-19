package krs.erp.repository.reporting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.reporting.MISReport;

@Repository
public interface MISReportRepository extends JpaRepository<MISReport, Long> {
}
