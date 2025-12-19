package krs.erp.model.library;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "erp_library_publishers")
@EqualsAndHashCode(callSuper = true)
public class Publisher extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(name = "contact_info")
    private String contactInfo;
}
