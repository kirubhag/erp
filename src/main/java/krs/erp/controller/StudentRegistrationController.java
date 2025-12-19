package krs.erp.controller;

import krs.erp.model.admission.StudentRegistration;
import krs.erp.service.StudentRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admission/registrations")
@Slf4j
public class StudentRegistrationController {

    @Autowired
    private StudentRegistrationService registrationService;

    @GetMapping
    public List<StudentRegistration> getAllRegistrations() {
        log.debug("REST request to get all StudentRegistrations");
        return registrationService.getAllRegistrations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentRegistration> getRegistrationById(@PathVariable Long id) {
        log.debug("REST request to get StudentRegistration by id : {}", id);
        return registrationService.getRegistrationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public List<StudentRegistration> getRegistrationsByStudent(@PathVariable Long studentId) {
        log.debug("REST request to get StudentRegistrations by studentId : {}", studentId);
        return registrationService.getRegistrationsByStudent(studentId);
    }

    @PostMapping
    public ResponseEntity<StudentRegistration> createRegistration(@RequestBody StudentRegistration registration) {
        log.debug("REST request to save StudentRegistration : {}", registration);
        return ResponseEntity.ok(registrationService.createRegistration(registration));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentRegistration> updateRegistration(@PathVariable Long id,
            @RequestBody StudentRegistration registrationDetails) {
        log.debug("REST request to update StudentRegistration : {} with details : {}", id, registrationDetails);
        try {
            return ResponseEntity.ok(registrationService.updateRegistration(id, registrationDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable Long id) {
        registrationService.deleteRegistration(id);
        return ResponseEntity.noContent().build();
    }
}
