package krs.erp.model.inventory;

import jakarta.persistence.*;

import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_inventory_vendors")
@Data
@EqualsAndHashCode(callSuper = true)
public class Vendor extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "tin_gstin")
    private String tinGstin;

    @Column(name = "address")
    private String address;
}
