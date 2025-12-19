package krs.erp.controller.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import krs.erp.model.documents.ErpDocument;
import krs.erp.service.documents.DocumentService;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping
    public List<ErpDocument> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @GetMapping("/category/{category}")
    public List<ErpDocument> getByCategory(@PathVariable String category) {
        return documentService.getDocumentsByCategory(category);
    }

    @GetMapping("/owner/{ownerId}")
    public List<ErpDocument> getByOwner(@PathVariable Long ownerId) {
        return documentService.getDocumentsByOwner(ownerId);
    }

    @PostMapping
    public ErpDocument createDocument(@RequestBody ErpDocument document) {
        return documentService.saveDocument(document);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }
}
