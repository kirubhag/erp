package krs.erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.dto.ExportRequest;
import krs.erp.model.ErpEntity;
import krs.erp.repository.ErpEntityRepository;
import krs.erp.service.ExportService;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @Autowired
    private ErpEntityRepository erpEntityRepository;

    @PostMapping
    public ResponseEntity<ByteArrayResource> exportData(@RequestBody ExportRequest request) {
        try {
            byte[] data = exportService.exportData(request);
            ByteArrayResource resource = new ByteArrayResource(data);

            String format = request.getFormat() != null ? request.getFormat().toLowerCase() : "csv";
            
            // Get entity name for filename
            String entityName = "export";
            if (request.getEntityId() != null) {
                ErpEntity entity = erpEntityRepository.findById(request.getEntityId()).orElse(null);
                if (entity != null) {
                    entityName = entity.getPluralName().toLowerCase().replaceAll("\\s+", "_");
                }
            }
            String filename = entityName + "_data." + format;
            
            String contentType = "text/csv";
            if ("xlsx".equals(format)) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(data.length)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
