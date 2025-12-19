package krs.erp.model.finance;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_fee_fine_categories")
@Data
@EqualsAndHashCode(callSuper = true)
public class FineCategory extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name; // Late Fee, Attendance, Discipline, Library

    @Column(name = "description")
    private String description;
}
