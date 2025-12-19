package krs.erp.repository.admission;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.admission.AdmissionApplication;
import krs.erp.model.admission.AdmissionApplication.ApplicationStatus;

@Repository
public interface AdmissionApplicationRepository extends JpaRepository<AdmissionApplication, Long> {

    Optional<AdmissionApplication> findByApplicationNumber(String applicationNumber);

    List<AdmissionApplication> findByStatus(ApplicationStatus status);

    Page<AdmissionApplication> findByStatus(ApplicationStatus status, Pageable pageable);

    List<AdmissionApplication> findByAcademicYearId(Long academicYearId);

    boolean existsByApplicationNumber(String applicationNumber);
}
