package krs.erp.repository.admission;

import krs.erp.model.admission.StudentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentRegistrationRepository extends JpaRepository<StudentRegistration, Long> {
    List<StudentRegistration> findByStudentId(Long studentId);

    List<StudentRegistration> findByAcademicYearId(Long academicYearId);

    List<StudentRegistration> findByErpClassId(Long classId);

    List<StudentRegistration> findByStatus(StudentRegistration.RegistrationStatus status);
}
