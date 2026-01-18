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
@Table(name = "erp_library_resources")
@EqualsAndHashCode(callSuper = true)
public class LibraryResource extends BaseEntity {

    public enum ResourceFormat {
        BOOK,
        JOURNAL,
        EBOOK,
        MULTIMEDIA
    }

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceFormat format = ResourceFormat.BOOK;

    @Column(name = "isbn_issn")
    private String isbnIssn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Publisher publisher;

    private String edition;

    private Integer year;

    private String category;

    @Column(name = "digital_path")
    private String digitalPath;

    @Column(columnDefinition = "TEXT")
    private String description;
}
