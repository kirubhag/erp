package krs.erp.repository.documents;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.documents.ErpDocument;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<ErpDocument, Long> {
    List<ErpDocument> findByCategory(String category);

    List<ErpDocument> findByOwnerId(Long ownerId);
}
