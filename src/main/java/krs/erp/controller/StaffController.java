package krs.erp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.enums.EntityType;
import krs.erp.model.Staff;
import krs.erp.repository.StaffRepository;
import krs.erp.service.AutoNumberService;

/**
 * REST Controller for Staff CRUD operations
 * Provides endpoints for managing staff members
 */
@RestController
@RequestMapping("/api/staff")
public class StaffController {
    
    @Autowired
    private StaffRepository staffRepository;
    
    @Autowired
    private AutoNumberService autoNumberService;
    
    /**
     * Get all staff members with pagination
     */
    @GetMapping
    public ResponseEntity<Page<Staff>> getAllStaff(Pageable pageable) {
        Page<Staff> staff = staffRepository.findAll(pageable);
        return ResponseEntity.ok(staff);
    }
    
    /**
     * Get staff member by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaffById(@PathVariable Long id) {
        Optional<Staff> staff = staffRepository.findById(id);
        return staff.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create new staff member
     */
    @PostMapping
    public ResponseEntity<?> createStaff(@RequestBody Staff staff) {
        try {
            // Auto-generate staffId if not provided
            if (staff.getStaffId() == null || staff.getStaffId().isEmpty()) {
                try {
                    String generatedId = autoNumberService.generateNextNumber(EntityType.STAFF, "staffId");
                    if (generatedId != null && !generatedId.isEmpty()) {
                        staff.setStaffId(generatedId);
                    } else {
                        // Fallback to timestamp-based ID
                        staff.setStaffId("STF-" + System.currentTimeMillis());
                    }
                } catch (Exception e) {
                    // Fallback to timestamp-based ID
                    staff.setStaffId("STF-" + System.currentTimeMillis());
                }
            }
            
            // Set default values for required fields if not provided
            if (staff.getEmploymentStatus() == null) {
                staff.setEmploymentStatus(Staff.EmploymentStatus.ACTIVE);
            }
            if (staff.getStaffType() == null) {
                staff.setStaffType(Staff.StaffType.OTHER);
            }
            // Set default hire date if not provided
            if (staff.getHireDate() == null) {
                staff.setHireDate(java.time.LocalDate.now());
            }
            Staff savedStaff = staffRepository.save(staff);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStaff);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating staff: " + e.getMessage());
        }
    }
    
    /**
     * Update staff member
     */
    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(@PathVariable Long id, @RequestBody Staff staffDetails) {
        Optional<Staff> staff = staffRepository.findById(id);
        if (staff.isPresent()) {
            Staff existingStaff = staff.get();
            if (staffDetails.getFirstName() != null) {
                existingStaff.setFirstName(staffDetails.getFirstName());
            }
            if (staffDetails.getLastName() != null) {
                existingStaff.setLastName(staffDetails.getLastName());
            }
            if (staffDetails.getEmail() != null) {
                existingStaff.setEmail(staffDetails.getEmail());
            }
            if (staffDetails.getPhone() != null) {
                existingStaff.setPhone(staffDetails.getPhone());
            }
            Staff updatedStaff = staffRepository.save(existingStaff);
            return ResponseEntity.ok(updatedStaff);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Delete staff member
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        if (staffRepository.existsById(id)) {
            staffRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
