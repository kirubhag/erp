package krs.erp.repository.hr;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.hr.StaffSalary;

@Repository
public interface StaffSalaryRepository extends JpaRepository<StaffSalary, Long> {
    Optional<StaffSalary> findByStaffId(Long staffId);
}
