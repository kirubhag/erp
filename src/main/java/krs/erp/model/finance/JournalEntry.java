package krs.erp.model.finance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "erp_journal_entries")
@EqualsAndHashCode(callSuper = true)
public class JournalEntry extends BaseEntity {

    public enum EntryStatus {
        DRAFT,
        POSTED,
        CANCELLED
    }

    @Column(name = "entry_number", nullable = false, unique = true)
    private String entryNumber;

    private String reference;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryStatus status = EntryStatus.DRAFT;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JournalItem> items = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
