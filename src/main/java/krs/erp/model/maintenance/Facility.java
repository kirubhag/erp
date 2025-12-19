package krs.erp.model.maintenance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_maint_facilities")
@Data
@EqualsAndHashCode(callSuper = true)
public class Facility extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type")
    private String type; // Lab, Auditorium, Sports Ground, Meeting Room

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "is_bookable")
    private Boolean isBookable = true;

    @Column(name = "description")
    private String description;
}
