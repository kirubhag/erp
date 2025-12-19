package krs.erp.model.documents;

import javax.persistence.*;
import java.time.LocalDateTime;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_docs_documents")
@Data
@EqualsAndHashCode(callSuper = true)
public class ErpDocument extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "category")
    private String category; // STUDENT, STAFF, POLICY, CONTRACT, CIRCULAR

    @Column(name = "file_type")
    private String fileType; // PDF, DOC, IMG

    @Column(name = "file_url")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DocumentStatus status = DocumentStatus.ACTIVE;

    @Column(name = "owner_id")
    private Long ownerId; // Related Student/Staff ID if applicable

    @Column(name = "upload_date")
    private LocalDateTime uploadDate = LocalDateTime.now();

    public enum DocumentStatus {
        ACTIVE, ARCHIVED, DELETED
    }
}
