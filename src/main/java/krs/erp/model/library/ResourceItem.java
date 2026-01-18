package krs.erp.model.library;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "erp_library_items")
@EqualsAndHashCode(callSuper = true)
public class ResourceItem extends BaseEntity {

    public enum ItemStatus {
        AVAILABLE,
        LOANED,
        RESERVED,
        LOST
    }

    public enum AuditStatus {
        MATCHED,
        MISPLACED
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private LibraryResource resource;

    @Column(name = "accession_number", unique = true, nullable = false)
    private String accessionNumber;

    @Column(unique = true)
    private String barcode;

    private String location; // Rack/Shelf

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status = ItemStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "audit_status")
    private AuditStatus auditStatus;
}
