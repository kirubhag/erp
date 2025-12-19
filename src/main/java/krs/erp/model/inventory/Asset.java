package krs.erp.model.inventory;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_inventory_assets")
@Data
@EqualsAndHashCode(callSuper = true)
public class Asset extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "asset_tag", unique = true)
    private String assetTag;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "type")
    private String type; // e.g. Electronics, Furniture

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssetStatus status = AssetStatus.AVAILABLE;

    @Column(name = "location")
    private String location;

    @Column(name = "assigned_staff_id")
    private Long assignedStaffId;

    public enum AssetStatus {
        AVAILABLE, ASSIGNED, UNDER_REPAIR, DISPOSED
    }
}
