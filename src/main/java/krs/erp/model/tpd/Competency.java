package krs.erp.model.tpd;

import krs.erp.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_tpd_competencies")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "competency_id"))
public class Competency extends BaseEntity {

    @NotBlank(message = "Competency name is required")
    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String targetRole; // e.g., TEACHER, HOD, ADMIN

    private Integer requiredLevel; // 1-5 scale
}
