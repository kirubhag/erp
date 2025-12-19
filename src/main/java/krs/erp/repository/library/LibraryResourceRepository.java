package krs.erp.repository.library;

import krs.erp.model.library.LibraryResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryResourceRepository extends JpaRepository<LibraryResource, Long> {
    Optional<LibraryResource> findByIsbnIssn(String isbnIssn);
}
