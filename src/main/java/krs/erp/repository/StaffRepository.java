package krs.erp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    
    Optional<Staff> findByStaffId(String staffId);
    
    Optional<Staff> findByEmail(String email);
    
    List<Staff> findByEmploymentStatus(Staff.EmploymentStatus employmentStatus);
    
    List<Staff> findByStaffType(Staff.StaffType staffType);
    
    List<Staff> findByDepartment(String department);
    
    @Query("SELECT s FROM Staff s WHERE s.employmentStatus = 'ACTIVE'")
    List<Staff> findActiveStaff();
    
    @Query("SELECT s FROM Staff s WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Staff> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT s FROM Staff s WHERE s.hireDate BETWEEN :startDate AND :endDate")
    List<Staff> findByHireDateBetween(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s FROM Staff s WHERE s.staffType = :staffType AND s.employmentStatus = 'ACTIVE'")
    List<Staff> findActiveStaffByType(@Param("staffType") Staff.StaffType staffType);
    
    @Query("SELECT s FROM Staff s WHERE s.department = :department AND s.employmentStatus = 'ACTIVE'")
    List<Staff> findActiveStaffByDepartment(@Param("department") String department);
    
    @Query("SELECT COUNT(s) FROM Staff s WHERE s.employmentStatus = 'ACTIVE'")
    Long countActiveStaff();
    
    @Query("SELECT COUNT(s) FROM Staff s WHERE s.staffType = :staffType AND s.employmentStatus = 'ACTIVE'")
    Long countActiveStaffByType(@Param("staffType") Staff.StaffType staffType);
    
    @Query("SELECT DISTINCT s.department FROM Staff s WHERE s.department IS NOT NULL AND s.employmentStatus = 'ACTIVE'")
    List<String> findAllDepartments();
    
    boolean existsByStaffId(String staffId);
    
    boolean existsByEmail(String email);
}