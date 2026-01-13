package krs.erp.model.hr;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_hr_designations")
@AttributeOverride(name = "id", column = @Column(name = "designation_id"))
@Data
@EqualsAndHashCode(callSuper = true)
public class Designation extends BaseEntity {

    @NotBlank(message = "Designation title is required")
    @Column(name = "title", nullable = false, unique = true)
    private String title;

    private String description;

    @Column(name = "rank_level")
    private Integer rankLevel; // For hierarchy
}
