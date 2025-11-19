package krs.erp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import krs.erp.dto.FieldMappingDTO;
import krs.erp.dto.ImportPreviewDTO;
import krs.erp.dto.ImportResultDTO;
import krs.erp.dto.ImportSessionDTO;
import krs.erp.dto.ImportStatisticsDTO;
import krs.erp.dto.ValidationResultDTO;
import krs.erp.entity.DuplicateAction;
import krs.erp.entity.FieldMapping;
import krs.erp.entity.FieldMappingTemplate;
import krs.erp.entity.ImportResult;
import krs.erp.entity.ImportSession;
import krs.erp.entity.ImportStatus;
import krs.erp.entity.ImportType;
import krs.erp.repository.FieldMappingRepository;
import krs.erp.repository.FieldMappingTemplateRepository;
import krs.erp.repository.ImportResultRepository;
import krs.erp.repository.ImportSessionRepository;

/**
 * Service for handling import operations
 */
@Service
@Transactional
public class ImportService {
    
    private final ImportSessionRepository importSessionRepository;
    private final ImportResultRepository importResultRepository;
    private final FieldMappingRepository fieldMappingRepository;
    private final FieldMappingTemplateRepository fieldMappingTemplateRepository;
    
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB
    private static final String[] SUPPORTED_FORMATS = {"csv", "xlsx", "xls", "vcf"};
    
    public ImportService(ImportSessionRepository importSessionRepository,
                        ImportResultRepository importResultRepository,
                        FieldMappingRepository fieldMappingRepository,
                        FieldMappingTemplateRepository fieldMappingTemplateRepository) {
        this.importSessionRepository = importSessionRepository;
        this.importResultRepository = importResultRepository;
        this.fieldMappingRepository = fieldMappingRepository;
        this.fieldMappingTemplateRepository = fieldMappingTemplateRepository;
    }
    
    /**
     * Upload and preview file
     */
    public ImportPreviewDTO uploadFilePreview(MultipartFile file, String entityType) throws IOException {
        validateFile(file);
        
        String format = detectFileFormat(file.getOriginalFilename());
        ImportPreviewDTO preview = new ImportPreviewDTO();
        preview.setDetectedFormat(format);
        preview.setFileSize(file.getSize());
        
        // Parse file based on format
        parseFilePreview(file, format, preview);
        
        return preview;
    }
    
    /**
     * Create import session
     */
    public ImportSessionDTO createSession(String sessionId, Long userId, Long organizationId, String entityType,
                                         String fileName, String fileFormat, Integer totalRecords, 
                                         String[] headerRow, ImportType importType, DuplicateAction duplicateAction,
                                         String findDuplicatesBy, Boolean enableManualApproval, Boolean skipEmptyFields) {
        
        ImportSession session = new ImportSession();
        session.setId(sessionId);
        session.setUserId(userId);
        session.setOrganizationId(organizationId);
        session.setEntityType(entityType);
        session.setFileName(fileName);
        session.setFileFormat(fileFormat);
        session.setTotalRecords(totalRecords);
        session.setHeaderRow(headerRow);
        session.setImportType(importType);
        session.setDuplicateAction(duplicateAction);
        session.setFindDuplicatesBy(findDuplicatesBy);
        session.setEnableManualApproval(enableManualApproval);
        session.setSkipEmptyFields(skipEmptyFields);
        session.setStatus(ImportStatus.PENDING);
        session.setUploadedAt(LocalDateTime.now());
        
        // Create initial field mappings from header
        List<FieldMapping> mappings = new ArrayList<>();
        if (headerRow != null) {
            for (int i = 0; i < headerRow.length; i++) {
                FieldMapping mapping = new FieldMapping();
                mapping.setSourceColumn(headerRow[i]);
                mapping.setSourceIndex(i);
                mapping.setImportSession(session);
                mappings.add(mapping);
            }
        }
        session.setFieldMappings(mappings);
        
        ImportSession saved = importSessionRepository.save(session);
        return convertToDTO(saved);
    }
    
    /**
     * Get field mapping template for entity type
     */
    public List<FieldMappingDTO> getFieldMappingTemplate(String entityType) {
        List<FieldMappingTemplate> templates = 
            fieldMappingTemplateRepository.findByEntityTypeOrderByDisplayOrder(entityType);
        
        return templates.stream()
                .map(this::convertTemplateToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Save field mappings
     */
    public ImportSessionDTO saveFieldMappings(String sessionId, List<FieldMappingDTO> mappings) {
        ImportSession session = importSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        // Clear existing mappings
        fieldMappingRepository.deleteByImportSessionId(sessionId);
        
        // Save new mappings
        List<FieldMapping> fieldMappings = new ArrayList<>();
        for (FieldMappingDTO mappingDTO : mappings) {
            FieldMapping mapping = new FieldMapping();
            mapping.setSourceColumn(mappingDTO.getSourceColumn());
            mapping.setSourceIndex(mappingDTO.getSourceIndex());
            mapping.setTargetField(mappingDTO.getTargetField());
            mapping.setTargetFieldLabel(mappingDTO.getTargetFieldLabel());
            mapping.setIsRequired(mappingDTO.getIsRequired());
            mapping.setDataType(mappingDTO.getDataType());
            mapping.setImportSession(session);
            fieldMappings.add(mapping);
        }
        
        session.setFieldMappings(fieldMappings);
        session.setUnmappedColumns(session.getUnmappedColumnsList().toArray(new String[0]));
        ImportSession saved = importSessionRepository.save(session);
        
        return convertToDTO(saved);
    }
    
    /**
     * Validate field mappings
     */
    public ValidationResultDTO validateMappings(String sessionId) {
        ImportSession session = importSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        List<String> errors = new ArrayList<>();
        
        if (!session.areAllRequiredFieldsMapped()) {
            List<String> unmappedRequired = session.getFieldMappings().stream()
                    .filter(FieldMapping::getIsRequired)
                    .filter(fm -> fm.getTargetField() == null || fm.getTargetField().isEmpty())
                    .map(FieldMapping::getSourceColumn)
                    .toList();
            
            for (String column : unmappedRequired) {
                errors.add("Required field '" + column + "' is not mapped");
            }
        }
        
        ValidationResultDTO result = new ValidationResultDTO();
        result.setValid(errors.isEmpty());
        result.setErrors(errors);
        
        return result;
    }
    
    /**
     * Auto-detect field mappings
     */
    public List<FieldMappingDTO> autoDetectMappings(String sessionId) {
        ImportSession session = importSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        List<FieldMappingTemplate> templates = 
            fieldMappingTemplateRepository.findByEntityTypeOrderByDisplayOrder(session.getEntityType());
        
        List<FieldMappingDTO> detectedMappings = new ArrayList<>();
        
        // For each source column (CSV header), try to find matching target field
        if (session.getFieldMappings() != null) {
            for (FieldMapping sourceMapping : session.getFieldMappings()) {
                String sourceColumn = sourceMapping.getSourceColumn().toLowerCase().trim();
                FieldMappingDTO detected = new FieldMappingDTO();
                detected.setSourceColumn(sourceMapping.getSourceColumn());
                detected.setSourceIndex(sourceMapping.getSourceIndex());
                
                // Try exact match first
                for (FieldMappingTemplate template : templates) {
                    String fieldName = template.getFieldName().toLowerCase();
                    if (fieldName.equals(sourceColumn) || fieldName.replace("_", "").equals(sourceColumn.replace("_", ""))) {
                        detected.setTargetField(template.getFieldName());
                        detected.setTargetFieldLabel(template.getFieldLabel());
                        detected.setIsRequired(template.getIsRequired());
                        detected.setDataType(template.getDataType());
                        break;
                    }
                }
                
                // Try fuzzy match on suggestions if no exact match
                if (detected.getTargetField() == null) {
                    for (FieldMappingTemplate template : templates) {
                        List<String> suggestions = template.getSuggestionsList();
                        for (String suggestion : suggestions) {
                            if (suggestion.toLowerCase().equals(sourceColumn)) {
                                detected.setTargetField(template.getFieldName());
                                detected.setTargetFieldLabel(template.getFieldLabel());
                                detected.setIsRequired(template.getIsRequired());
                                detected.setDataType(template.getDataType());
                                break;
                            }
                        }
                        if (detected.getTargetField() != null) break;
                    }
                }
                
                detectedMappings.add(detected);
            }
        }
        
        return detectedMappings;
    }
    
    /**
     * Start import process
     */
    public void startImport(String sessionId) {
        ImportSession session = importSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        session.setStatus(ImportStatus.IN_PROGRESS);
        session.setImportedAt(LocalDateTime.now());
        importSessionRepository.save(session);
        
        // TODO: Start async import processing
        // This would typically be done in a separate thread/task
    }
    
    /**
     * Get import summary
     */
    public ImportSessionDTO getImportSummary(String sessionId) {
        ImportSession session = importSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        return convertToDTO(session);
    }
    
    /**
     * Get import history
     */
    public Page<ImportSessionDTO> getImportHistory(Long userId, String entityType, Pageable pageable) {
        Page<ImportSession> sessions = importSessionRepository
                .findByUserIdAndEntityTypeOrderByUploadedAtDesc(userId, entityType, pageable);
        
        return sessions.map(this::convertToDTO);
    }
    
    /**
     * Undo import
     */
    public void undoImport(String sessionId) {
        ImportSession session = importSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        // TODO: Implement rollback logic
        session.setStatus(ImportStatus.CANCELLED);
        importSessionRepository.save(session);
    }
    
    /**
     * Cancel session
     */
    public void cancelSession(String sessionId) {
        ImportSession session = importSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        session.setStatus(ImportStatus.CANCELLED);
        importSessionRepository.save(session);
    }
    
    /**
     * Validate file
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds 50 MB limit");
        }
        
        String format = detectFileFormat(file.getOriginalFilename());
        if (!Arrays.asList(SUPPORTED_FORMATS).contains(format)) {
            throw new RuntimeException("Unsupported file format: " + format);
        }
    }
    
    /**
     * Detect file format from filename
     */
    public String detectFileFormat(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "unknown";
        }
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return extension;
    }
    
    /**
     * Parse file preview
     */
    private void parseFilePreview(MultipartFile file, String format, ImportPreviewDTO preview) throws IOException {
        if ("csv".equals(format)) {
            parseCSVPreview(file, preview);
        } else if ("xlsx".equals(format) || "xls".equals(format)) {
            // TODO: Implement Excel parsing
            preview.setTotalRows(0);
            preview.setHeaderRow(new String[0]);
            preview.setSampleRows(new ArrayList<>());
        } else if ("vcf".equals(format)) {
            // TODO: Implement VCF parsing
            preview.setTotalRows(0);
            preview.setHeaderRow(new String[0]);
            preview.setSampleRows(new ArrayList<>());
        }
    }
    
    /**
     * Parse CSV file preview
     */
    private void parseCSVPreview(MultipartFile file, ImportPreviewDTO preview) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = reader.readLine();
            if (headerLine != null) {
                String[] headers = headerLine.split(",");
                preview.setHeaderRow(headers);
            }
            
            List<String[]> sampleRows = new ArrayList<>();
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null && lineCount < 5) {
                String[] row = line.split(",");
                sampleRows.add(row);
                lineCount++;
            }
            preview.setSampleRows(sampleRows);
            
            // Count total lines
            try (BufferedReader countReader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                long count = countReader.lines().count();
                preview.setTotalRows((int) count);
            }
        }
    }
    
    /**
     * Convert ImportSession to DTO
     */
    private ImportSessionDTO convertToDTO(ImportSession session) {
        ImportSessionDTO dto = new ImportSessionDTO();
        dto.setId(session.getId());
        dto.setFileName(session.getFileName());
        dto.setFileFormat(session.getFileFormat());
        dto.setTotalRecords(session.getTotalRecords());
        dto.setFileSize(session.getFileSize());
        dto.setImportType(session.getImportType() != null ? session.getImportType().toString() : null);
        dto.setStatus(session.getStatus() != null ? session.getStatus().toString() : null);
        dto.setUploadedAt(session.getUploadedAt());
        dto.setImportedAt(session.getImportedAt());
        
        // Convert field mappings
        if (session.getFieldMappings() != null) {
            dto.setFieldMappings(session.getFieldMappings().stream()
                    .map(this::convertFieldMappingToDTO)
                    .collect(Collectors.toList()));
        }
        
        // Get unmapped columns
        dto.setUnmappedColumns(session.getUnmappedColumnsList());
        
        // Convert statistics
        ImportStatisticsDTO statistics = new ImportStatisticsDTO();
        statistics.setTotalRecords(session.getTotalRecords());
        statistics.setAddedRecords(session.getAddedRecords());
        statistics.setUpdatedRecords(session.getUpdatedRecords());
        statistics.setSkippedRecords(session.getSkippedRecords());
        statistics.setFailedRecords(session.getFailedRecords());
        statistics.setSuccessRate(session.getSuccessRate());
        dto.setStatistics(statistics);
        
        // Convert imported records (first 10)
        if (session.getImportedRecords() != null && !session.getImportedRecords().isEmpty()) {
            dto.setImportedRecords(session.getImportedRecords().stream()
                    .limit(10)
                    .map(this::convertImportResultToDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    /**
     * Convert FieldMapping to DTO
     */
    private FieldMappingDTO convertFieldMappingToDTO(FieldMapping mapping) {
        FieldMappingDTO dto = new FieldMappingDTO();
        dto.setId(mapping.getId());
        dto.setSourceColumn(mapping.getSourceColumn());
        dto.setSourceIndex(mapping.getSourceIndex());
        dto.setTargetField(mapping.getTargetField());
        dto.setTargetFieldLabel(mapping.getTargetFieldLabel());
        dto.setIsRequired(mapping.getIsRequired());
        dto.setDataType(mapping.getDataType());
        return dto;
    }
    
    /**
     * Convert FieldMappingTemplate to DTO
     */
    private FieldMappingDTO convertTemplateToDTO(FieldMappingTemplate template) {
        FieldMappingDTO dto = new FieldMappingDTO();
        dto.setTargetField(template.getFieldName());
        dto.setTargetFieldLabel(template.getFieldLabel());
        dto.setIsRequired(template.getIsRequired());
        dto.setDataType(template.getDataType());
        return dto;
    }
    
    /**
     * Convert ImportResult to DTO
     */
    private ImportResultDTO convertImportResultToDTO(ImportResult result) {
        ImportResultDTO dto = new ImportResultDTO();
        dto.setId(result.getId());
        dto.setRowNumber(result.getRowNumber());
        dto.setRecordId(result.getRecordId());
        dto.setStatus(result.getStatus() != null ? result.getStatus().toString() : null);
        dto.setData(result.getData());
        dto.setErrors(result.getErrorList());
        dto.setCreatedAt(result.getCreatedAt());
        return dto;
    }
}
