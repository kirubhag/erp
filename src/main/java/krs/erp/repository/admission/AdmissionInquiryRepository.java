package krs.erp.repository.admission;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.admission.AdmissionInquiry;
import krs.erp.model.admission.AdmissionInquiry.InquiryStatus;
import krs.erp.model.Student.GradeLevel;

@Repository
public interface AdmissionInquiryRepository extends JpaRepository<AdmissionInquiry, Long> {

    List<AdmissionInquiry> findByStatus(InquiryStatus status);

    Page<AdmissionInquiry> findByStatus(InquiryStatus status, Pageable pageable);

    List<AdmissionInquiry> findByGradeInterested(GradeLevel grade);

    List<AdmissionInquiry> findByEmail(String email);

    List<AdmissionInquiry> findByPhone(String phone);
}
