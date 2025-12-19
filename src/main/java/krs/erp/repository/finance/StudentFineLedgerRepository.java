package krs.erp.repository.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.finance.StudentFineLedger;

import java.util.List;

@Repository
public interface StudentFineLedgerRepository extends JpaRepository<StudentFineLedger, Long> {
    List<StudentFineLedger> findByStudentId(Long studentId);
}
