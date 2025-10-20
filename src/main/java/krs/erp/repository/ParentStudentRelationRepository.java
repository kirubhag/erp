package krs.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.ParentStudentRelation;

@Repository
public interface ParentStudentRelationRepository extends JpaRepository<ParentStudentRelation, Long> {
    
    List<ParentStudentRelation> findByParentId(Long parentId);
    
    List<ParentStudentRelation> findByStudentId(Long studentId);
    
    List<ParentStudentRelation> findByParentIdAndStudentId(Long parentId, Long studentId);
    
    List<ParentStudentRelation> findByRelationshipType(ParentStudentRelation.RelationshipType relationshipType);
    
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.student.id = :studentId AND psr.primaryContact = true")
    List<ParentStudentRelation> findPrimaryContactsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.student.id = :studentId AND psr.emergencyContact = true")
    List<ParentStudentRelation> findEmergencyContactsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.student.id = :studentId AND psr.authorizedPickup = true")
    List<ParentStudentRelation> findAuthorizedPickupsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.student.id = :studentId AND psr.custodyRights = true")
    List<ParentStudentRelation> findCustodyRightsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.parent.id = :parentId AND psr.receiveCommunications = true")
    List<ParentStudentRelation> findCommunicationEnabledByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT COUNT(psr) FROM ParentStudentRelation psr WHERE psr.parent.id = :parentId")
    Long countStudentsByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT COUNT(psr) FROM ParentStudentRelation psr WHERE psr.student.id = :studentId")
    Long countParentsByStudentId(@Param("studentId") Long studentId);
}