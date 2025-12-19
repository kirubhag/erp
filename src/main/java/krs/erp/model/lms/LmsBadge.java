package krs.erp.model.lms;

import krs.erp.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_lms_badges")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "badge_id"))
public class LmsBadge extends BaseEntity {

    @NotBlank(message = "Badge name is required")
    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String iconUrl; // URL or CSS class for the badge icon

    private Integer pointsRequired = 0;

    @Enumerated(EnumType.STRING)
    private BadgeTier tier = BadgeTier.BRONZE;

    public enum BadgeTier {
        BRONZE, SILVER, GOLD, PLATINUM
    }
}
