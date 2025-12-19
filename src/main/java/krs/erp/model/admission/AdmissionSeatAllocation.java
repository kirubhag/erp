package krs.erp.model.admission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

import krs.erp.model.BaseEntity;
import krs.erp.model.Student.GradeLevel;

@Entity
@Table(name = "erp_admission_seat_allocations", uniqueConstraints = @UniqueConstraint(columnNames = {
        "admission_cycle_id", "grade_level" }))
public class AdmissionSeatAllocation extends BaseEntity {

    @NotNull
    @Column(name = "admission_cycle_id", nullable = false)
    private Long admissionCycleId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "grade_level", nullable = false)
    private GradeLevel gradeLevel;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats = 0;

    @Column(name = "occupied_seats", nullable = false)
    private Integer occupiedSeats = 0;

    @Column(name = "waitlisted_seats", nullable = false)
    private Integer waitlistedSeats = 0;

    // Constructors
    public AdmissionSeatAllocation() {
    }

    // Getters and Setters
    public Long getAdmissionCycleId() {
        return admissionCycleId;
    }

    public void setAdmissionCycleId(Long admissionCycleId) {
        this.admissionCycleId = admissionCycleId;
    }

    public GradeLevel getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(GradeLevel gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getOccupiedSeats() {
        return occupiedSeats;
    }

    public void setOccupiedSeats(Integer occupiedSeats) {
        this.occupiedSeats = occupiedSeats;
    }

    public Integer getWaitlistedSeats() {
        return waitlistedSeats;
    }

    public void setWaitlistedSeats(Integer waitlistedSeats) {
        this.waitlistedSeats = waitlistedSeats;
    }
}
