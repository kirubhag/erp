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
@Table(name = "erp_hr_departments")
@AttributeOverride(name = "id", column = @Column(name = "department_id"))
@Data
@EqualsAndHashCode(callSuper = true)
public class Department extends BaseEntity {

    @NotBlank(message = "Department name is required")
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "department_code", unique = true)
    private String code;

    @Column(name = "head_of_department_name")
    private String headOfDepartmentName;
}
