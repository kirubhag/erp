package krs.erp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.Parent;
import krs.erp.model.ParentStudentRelation;
import krs.erp.model.Student;
import krs.erp.model.Student.GradeLevel;

/**
 * Test class for ParentStudentRelationRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ParentStudentRelationRepositoryTest {

    @Autowired
    private ParentStudentRelationRepository parentStudentRelationRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private StudentRepository studentRepository;

    private Parent parent1;
    private Student student1;
    private Student student2;
    private ParentStudentRelation relation1;
    private ParentStudentRelation relation2;

    @BeforeEach
    void setUp() {
        parentStudentRelationRepository.deleteAllInBatch();
        parentRepository.deleteAllInBatch();
        studentRepository.deleteAllInBatch();

        // Create test parent
        parent1 = new Parent();
        parent1.setFirstName("John");
        parent1.setLastName("Doe");
        parent1.setEmail("john.doe@example.com");
        parent1.setPhone("1234567890");
        parent1 = parentRepository.save(parent1);

        // Create test students
        student1 = new Student();
        student1.setStudentId("STU001");
        student1.setFirstName("Jane");
        student1.setLastName("Doe");
        student1.setEmail("jane.doe@example.com");
        student1.setDateOfBirth(java.time.LocalDate.now().minusYears(10));
        student1.setEnrollmentDate(java.time.LocalDate.now());
        student1.setGradeLevel(GradeLevel.GRADE_5);
        student1 = studentRepository.save(student1);

        student2 = new Student();
        student2.setStudentId("STU002");
        student2.setFirstName("Jack");
        student2.setLastName("Doe");
        student2.setEmail("jack.doe@example.com");
        student2.setDateOfBirth(java.time.LocalDate.now().minusYears(12));
        student2.setEnrollmentDate(java.time.LocalDate.now());
        student2.setGradeLevel(GradeLevel.GRADE_7);
        student2 = studentRepository.save(student2);

        // Create test relations
        relation1 = new ParentStudentRelation();
        relation1.setParent(parent1);
        relation1.setStudent(student1);
        relation1.setRelationshipType(ParentStudentRelation.RelationshipType.FATHER);
        relation1.setPrimaryContact(true);
        relation1.setEmergencyContact(true);
        relation1.setAuthorizedPickup(true);
        relation1.setCustodyRights(true);
        relation1.setReceiveCommunications(true);
        relation1.setIsActive(1);
        relation1 = parentStudentRelationRepository.save(relation1);

        relation2 = new ParentStudentRelation();
        relation2.setParent(parent1);
        relation2.setStudent(student2);
        relation2.setRelationshipType(ParentStudentRelation.RelationshipType.FATHER);
        relation2.setPrimaryContact(false);
        relation2.setEmergencyContact(false);
        relation2.setAuthorizedPickup(true);
        relation2.setCustodyRights(true);
        relation2.setReceiveCommunications(false);
        relation2.setIsActive(1);
        relation2 = parentStudentRelationRepository.save(relation2);
    }

    @Test
    void testSaveParentStudentRelation() {
        ParentStudentRelation newRelation = new ParentStudentRelation();
        newRelation.setParent(parent1);
        newRelation.setStudent(student1);
        newRelation.setRelationshipType(ParentStudentRelation.RelationshipType.MOTHER);
        
        ParentStudentRelation saved = parentStudentRelationRepository.save(newRelation);
        
        assertNotNull(saved.getId());
        assertEquals(ParentStudentRelation.RelationshipType.MOTHER, saved.getRelationshipType());
    }

    @Test
    void testFindByParentId() {
        List<ParentStudentRelation> relations = parentStudentRelationRepository.findByParentId(parent1.getId());
        
        assertThat(relations).hasSize(2);
    }

    @Test
    void testFindByStudentId() {
        List<ParentStudentRelation> relations = parentStudentRelationRepository.findByStudentId(student1.getId());
        
        assertThat(relations).hasSize(1);
        assertEquals(ParentStudentRelation.RelationshipType.FATHER, relations.get(0).getRelationshipType());
    }

    @Test
    void testFindByParentIdAndStudentId() {
        List<ParentStudentRelation> relations = parentStudentRelationRepository.findByParentIdAndStudentId(
            parent1.getId(), student1.getId());
        
        assertThat(relations).hasSize(1);
    }

    @Test
    void testFindByRelationshipType() {
        List<ParentStudentRelation> fatherRelations = parentStudentRelationRepository.findByRelationshipType(
            ParentStudentRelation.RelationshipType.FATHER);
        
        assertThat(fatherRelations).hasSize(2);
    }

    @Test
    void testFindPrimaryContactsByStudentId() {
        List<ParentStudentRelation> primaryContacts = parentStudentRelationRepository.findPrimaryContactsByStudentId(
            student1.getId());
        
        assertThat(primaryContacts).hasSize(1);
        assertTrue(primaryContacts.get(0).getPrimaryContact());
    }

    @Test
    void testFindEmergencyContactsByStudentId() {
        List<ParentStudentRelation> emergencyContacts = parentStudentRelationRepository.findEmergencyContactsByStudentId(
            student1.getId());
        
        assertThat(emergencyContacts).hasSize(1);
        assertTrue(emergencyContacts.get(0).getEmergencyContact());
    }

    @Test
    void testFindAuthorizedPickupsByStudentId() {
        List<ParentStudentRelation> authorizedPickups = parentStudentRelationRepository.findAuthorizedPickupsByStudentId(
            student1.getId());
        
        assertThat(authorizedPickups).hasSize(1);
        assertTrue(authorizedPickups.get(0).getAuthorizedPickup());
    }

    @Test
    void testFindCustodyRightsByStudentId() {
        List<ParentStudentRelation> custodyRights = parentStudentRelationRepository.findCustodyRightsByStudentId(
            student1.getId());
        
        assertThat(custodyRights).hasSize(1);
        assertTrue(custodyRights.get(0).getCustodyRights());
    }

    @Test
    void testFindCommunicationEnabledByParentId() {
        List<ParentStudentRelation> commEnabled = parentStudentRelationRepository.findCommunicationEnabledByParentId(
            parent1.getId());
        
        assertThat(commEnabled).hasSize(1);
        assertTrue(commEnabled.get(0).getReceiveCommunications());
    }

    @Test
    void testCountStudentsByParentId() {
        Long count = parentStudentRelationRepository.countStudentsByParentId(parent1.getId());
        assertEquals(2L, count);
    }

    @Test
    void testCountParentsByStudentId() {
        Long count = parentStudentRelationRepository.countParentsByStudentId(student1.getId());
        assertEquals(1L, count);
    }

    @Test
    void testFindByStudentIdAndIsActive() {
        List<ParentStudentRelation> activeRelations = parentStudentRelationRepository.findByStudentIdAndIsActive(
            student1.getId(), 1);
        
        assertThat(activeRelations).hasSize(1);
    }

    @Test
    void testFindAllActive() {
        List<ParentStudentRelation> activeRelations = parentStudentRelationRepository.findAllActive();
        
        assertThat(activeRelations).hasSize(2);
    }

    @Test
    void testUpdateParentStudentRelation() {
        relation1.setPrimaryContact(false);
        relation1.setEmergencyContact(false);
        
        ParentStudentRelation updated = parentStudentRelationRepository.save(relation1);
        
        assertFalse(updated.getPrimaryContact());
        assertFalse(updated.getEmergencyContact());
    }

    @Test
    void testDeleteParentStudentRelation() {
        parentStudentRelationRepository.delete(relation1);
        
        List<ParentStudentRelation> relations = parentStudentRelationRepository.findByParentId(parent1.getId());
        assertThat(relations).hasSize(1);
    }
}
