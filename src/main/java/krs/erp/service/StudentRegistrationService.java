package krs.erp.service;

import krs.erp.model.admission.StudentRegistration;
import krs.erp.repository.admission.StudentRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StudentRegistrationService {

    @Autowired
    private StudentRegistrationRepository registrationRepository;

    public List<StudentRegistration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    public Optional<StudentRegistration> getRegistrationById(Long id) {
        return registrationRepository.findById(id);
    }

    public List<StudentRegistration> getRegistrationsByStudent(Long studentId) {
        return registrationRepository.findByStudentId(studentId);
    }

    public List<StudentRegistration> getRegistrationsByAcademicYear(Long academicYearId) {
        return registrationRepository.findByAcademicYearId(academicYearId);
    }

    @Transactional
    public StudentRegistration createRegistration(StudentRegistration registration) {
        log.info("Creating student registration for student: {}",
                registration.getStudent() != null ? registration.getStudent().getId() : "null");
        if (registration.getRegistrationDate() == null) {
            registration.setRegistrationDate(LocalDate.now());
        }
        return registrationRepository.save(registration);
    }

    @Transactional
    public StudentRegistration updateRegistration(Long id, StudentRegistration registrationDetails) {
        return registrationRepository.findById(id).map(registration -> {
            registration.setStudent(registrationDetails.getStudent());
            registration.setAcademicYear(registrationDetails.getAcademicYear());
            registration.setErpClass(registrationDetails.getErpClass());
            registration.setRegistrationDate(registrationDetails.getRegistrationDate());
            registration.setStatus(registrationDetails.getStatus());
            registration.setRemarks(registrationDetails.getRemarks());
            return registrationRepository.save(registration);
        }).orElseThrow(() -> new RuntimeException("Registration not found with id " + id));
    }

    @Transactional
    public void deleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }

    public List<StudentRegistration> getRegistrationsByStatus(StudentRegistration.RegistrationStatus status) {
        return registrationRepository.findByStatus(status);
    }
}
