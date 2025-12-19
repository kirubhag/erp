package krs.erp.model.inventory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_inventory_consumables")
@Data
@EqualsAndHashCode(callSuper = true)
public class Consumable extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "category")
    private String category; // e.g. Stationery, Chemicals, Cleaning

    @Column(name = "unit")
    private String unit; // e.g. Nos, Box, Litre

    @Column(name = "reorder_level")
    private Integer reorderLevel = 10;

    @Column(name = "current_stock")
    private Integer currentStock = 0;
}
