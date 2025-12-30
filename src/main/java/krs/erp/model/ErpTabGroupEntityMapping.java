package krs.erp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;

/**
 * Entity mapping a Tab Group to an implementation Entity (Tab/Page).
 */
@Entity
@Table(name = "erp_tab_group_entity_rel", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "tab_group_id", "entity_id" })
}, indexes = {
        @Index(name = "idx_rel_tab_group", columnList = "tab_group_id"),
        @Index(name = "idx_rel_entity", columnList = "entity_id")
})
public class ErpTabGroupEntityMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_tab_group_entity_rel_id")
    private Long id;

    @Column(name = "tab_group_id", nullable = false)
    private Long tabGroupId;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "sequence", nullable = false)
    private Integer sequence = 0;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getTabGroupId() {
        return tabGroupId;
    }

    public void setTabGroupId(Long tabGroupId) {
        this.tabGroupId = tabGroupId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ErpTabGroupEntityMapping that = (ErpTabGroupEntityMapping) o;
        return Objects.equals(tabGroupId, that.tabGroupId) && Objects.equals(entityId, that.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tabGroupId, entityId);
    }
}
