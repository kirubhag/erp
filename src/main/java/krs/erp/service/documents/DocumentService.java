package krs.erp.service.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import krs.erp.model.documents.ErpDocument;
import krs.erp.repository.documents.DocumentRepository;
import java.util.List;

@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public ErpDocument saveDocument(ErpDocument document) {
        return documentRepository.save(document);
    }

    public List<ErpDocument> getAllDocuments() {
        return documentRepository.findAll();
    }

    public List<ErpDocument> getDocumentsByCategory(String category) {
        return documentRepository.findByCategory(category);
    }

    public List<ErpDocument> getDocumentsByOwner(Long ownerId) {
        return documentRepository.findByOwnerId(ownerId);
    }

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}
