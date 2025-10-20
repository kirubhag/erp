package krs.erp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.repository.AttendanceRepository;
import krs.erp.repository.HealthRecordRepository;
import krs.erp.repository.ParentRepository;
import krs.erp.repository.ParentStudentRelationRepository;
import krs.erp.repository.PermissionRepository;
import krs.erp.repository.RoleRepository;
import krs.erp.repository.StaffRepository;
import krs.erp.repository.StudentRepository;
import krs.erp.repository.UserRepository;

@RestController
@RequestMapping("/api/data")
public class DataController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ParentRepository parentRepository;
    
    @Autowired
    private StaffRepository staffRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private HealthRecordRepository healthRecordRepository;
    
    @Autowired
    private ParentStudentRelationRepository parentStudentRelationRepository;
    
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDataSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("users", userRepository.count());
        summary.put("roles", roleRepository.count());
        summary.put("permissions", permissionRepository.count());
        summary.put("students", studentRepository.count());
        summary.put("parents", parentRepository.count());
        summary.put("staff", staffRepository.count());
        summary.put("attendance", attendanceRepository.count());
        summary.put("healthRecords", healthRecordRepository.count());
        summary.put("parentStudentRelations", parentStudentRelationRepository.count());
        
        // Calculate totals
        long totalRecords = (Long) summary.get("users") + 
                           (Long) summary.get("roles") + 
                           (Long) summary.get("permissions") + 
                           (Long) summary.get("students") + 
                           (Long) summary.get("parents") + 
                           (Long) summary.get("staff") + 
                           (Long) summary.get("attendance") + 
                           (Long) summary.get("healthRecords") + 
                           (Long) summary.get("parentStudentRelations");
        
        summary.put("totalRecords", totalRecords);
        summary.put("status", totalRecords > 0 ? "loaded" : "empty");
        summary.put("message", totalRecords > 0 ? 
            "Sample data has been successfully loaded into the database" : 
            "No data found - database appears to be empty");
        
        return ResponseEntity.ok(summary);
    }
}