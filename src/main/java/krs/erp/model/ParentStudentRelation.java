package krs.erp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "parent_student_relations")
public class ParentStudentRelation extends BaseEntity {
    
    @NotNull(message = "Parent is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;
    
    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @NotNull(message = "Relationship type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private RelationshipType relationshipType;
    
    @Column(name = "primary_contact", nullable = false)
    private Boolean primaryContact = false;
    
    @Column(name = "custody_rights", nullable = false)
    private Boolean custodyRights = true;
    
    @Column(name = "emergency_contact", nullable = false)
    private Boolean emergencyContact = false;
    
    @Column(name = "authorized_pickup", nullable = false)
    private Boolean authorizedPickup = true;
    
    @Column(name = "receive_communications", nullable = false)
    private Boolean receiveCommunications = true;
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    // Enums
    public enum RelationshipType {
        FATHER, MOTHER, GUARDIAN, STEPFATHER, STEPMOTHER, 
        GRANDFATHER, GRANDMOTHER, UNCLE, AUNT, 
        FOSTER_PARENT, ADOPTIVE_PARENT, OTHER
    }
    
    // Constructors
    public ParentStudentRelation() {}
    
    public ParentStudentRelation(Parent parent, Student student, RelationshipType relationshipType) {
        this.parent = parent;
        this.student = student;
        this.relationshipType = relationshipType;
    }
    
    // Getters and Setters
    public Parent getParent() {
        return parent;
    }
    
    public void setParent(Parent parent) {
        this.parent = parent;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public RelationshipType getRelationshipType() {
        return relationshipType;
    }
    
    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }
    
    public Boolean getPrimaryContact() {
        return primaryContact;
    }
    
    public void setPrimaryContact(Boolean primaryContact) {
        this.primaryContact = primaryContact;
    }
    
    public Boolean getCustodyRights() {
        return custodyRights;
    }
    
    public void setCustodyRights(Boolean custodyRights) {
        this.custodyRights = custodyRights;
    }
    
    public Boolean getEmergencyContact() {
        return emergencyContact;
    }
    
    public void setEmergencyContact(Boolean emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    
    public Boolean getAuthorizedPickup() {
        return authorizedPickup;
    }
    
    public void setAuthorizedPickup(Boolean authorizedPickup) {
        this.authorizedPickup = authorizedPickup;
    }
    
    public Boolean getReceiveCommunications() {
        return receiveCommunications;
    }
    
    public void setReceiveCommunications(Boolean receiveCommunications) {
        this.receiveCommunications = receiveCommunications;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Helper methods
    public String getRelationshipDescription() {
        return relationshipType.toString().replace("_", " ").toLowerCase();
    }
    
    @Override
    public String toString() {
        return "ParentStudentRelation{" +
                "parent=" + (parent != null ? parent.getFullName() : "null") +
                ", student=" + (student != null ? student.getFullName() : "null") +
                ", relationshipType=" + relationshipType +
                ", primaryContact=" + primaryContact +
                '}';
    }
}