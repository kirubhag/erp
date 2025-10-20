package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.Parent;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    
    Optional<Parent> findByEmail(String email);
    
    @Query("SELECT p FROM Parent p WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Parent> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT p FROM Parent p JOIN p.studentRelations sr WHERE sr.student.id = :studentId")
    List<Parent> findParentsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT p FROM Parent p JOIN p.studentRelations sr " +
           "WHERE sr.student.id = :studentId AND sr.relationshipType = :relationshipType")
    List<Parent> findParentsByStudentIdAndRelationshipType(@Param("studentId") Long studentId,
                                                          @Param("relationshipType") krs.erp.model.ParentStudentRelation.RelationshipType relationshipType);
    
    @Query("SELECT p FROM Parent p JOIN p.studentRelations sr " +
           "WHERE sr.student.id = :studentId AND sr.primaryContact = true")
    Optional<Parent> findPrimaryContactByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT p FROM Parent p JOIN p.studentRelations sr " +
           "WHERE sr.student.id = :studentId AND sr.emergencyContact = true")
    List<Parent> findEmergencyContactsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT p FROM Parent p WHERE p.receiveNotifications = true")
    List<Parent> findParentsReceivingNotifications();
    
    @Query("SELECT p FROM Parent p WHERE p.emergencyContact = true")
    List<Parent> findEmergencyContacts();
    
    @Query("SELECT p FROM Parent p WHERE p.authorizedPickup = true")
    List<Parent> findAuthorizedPickupParents();
    
    @Query("SELECT COUNT(sr) FROM ParentStudentRelation sr WHERE sr.parent.id = :parentId")
    Long countStudentsByParentId(@Param("parentId") Long parentId);
    
    boolean existsByEmail(String email);
}