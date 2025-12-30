package krs.erp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import java.util.Objects;

/**
 * Entity representing a Tab Group (Module) in the UI.
 * e.g., "Core Platform", "HR", "Finance"
 */
@Entity
@Table(name = "erp_tab_groups", indexes = {
        @Index(name = "idx_tab_group_code", columnList = "code"),
        @Index(name = "idx_tab_group_sequence", columnList = "sequence"),
        @Index(name = "idx_tab_group_is_active", columnList = "is_active")
})
public class ErpTabGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_tab_group_id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "sequence", nullable = false)
    private Integer sequence = 0;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ErpTabGroup that = (ErpTabGroup) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
