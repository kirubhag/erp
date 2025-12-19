package krs.erp.model.finance;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Entity
@Table(name = "erp_fee_structures")
@Data
@EqualsAndHashCode(callSuper = true)
public class FeeStructure extends BaseEntity {

    @Column(name = "fee_type_id", nullable = false)
    private Long feeTypeId;

    @Column(name = "academic_year_id", nullable = false)
    private Long academicYearId;

    @Column(name = "grade_id")
    private Long gradeId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "due_date")
    private LocalDate dueDate;
}
