package krs.erp.model.lms;

import krs.erp.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_lms_rubrics")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "rubric_id"))
public class LmsRubric extends BaseEntity {

    @NotBlank(message = "Rubric name is required")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String criteria; // JSON or Structured text for criteria

    private double maxPoints;
}
