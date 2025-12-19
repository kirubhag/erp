package krs.erp.repository.admission;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.Student.GradeLevel;
import krs.erp.model.admission.AdmissionSeatAllocation;

@Repository
public interface AdmissionSeatAllocationRepository extends JpaRepository<AdmissionSeatAllocation, Long> {

    Optional<AdmissionSeatAllocation> findByAdmissionCycleIdAndGradeLevel(Long admissionCycleId, GradeLevel gradeLevel);

    boolean existsByAdmissionCycleIdAndGradeLevel(Long admissionCycleId, GradeLevel gradeLevel);
}
