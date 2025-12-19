package krs.erp.model.library;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import krs.erp.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "erp_library_purchase_requests")
@EqualsAndHashCode(callSuper = true)
public class LibraryPurchaseRequest extends BaseEntity {

    public enum RequestStatus {
        PENDING,
        APPROVED,
        ORDERED,
        REJECTED
    }

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;
}
