package krs.erp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import krs.erp.model.Student;
import krs.erp.repository.FieldMappingRepository;
import krs.erp.repository.FieldMappingTemplateRepository;
import krs.erp.repository.ImportSessionRepository;
import krs.erp.repository.StudentRepository;

/**
 * Service for handling import operations
 */
@Service
@Transactional
public class ImportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);
    
    private final ImportSessionRepository importSessionRepository;
    private final FieldMappingRepository fieldMappingRepository;
    private final FieldMappingTemplateRepository fieldMappingTemplateRepository;
    private final StudentRepository studentRepository;
    
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB
    private static final String[] SUPPORTED_FORMATS = {"csv", "xlsx", "xls", "vcf"};
    
    public ImportService(ImportSessionRepository importSessionRepository,
                        FieldMappingRepository fieldMappingRepository,
                        FieldMappingTemplateRepository fieldMappingTemplateRepository,
                        StudentRepository studentRepository) {
        this.importSessionRepository = importSessionRepository;
        this.fieldMappingRepository = fieldMappingRepository;
        this.fieldMappingTemplateRepository = fieldMappingTemplateRepository;
        this.studentRepository = studentRepository;
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
                                         String findDuplicatesBy, Boolean enableManualApproval, Boolean skipEmptyFields,
                                         String filePath) {
        
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
        session.setFilePath(filePath);
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
     * Get field mappings for a session
     */
    public List<FieldMappingDTO> getFieldMappings(String sessionId) {
        ImportSession session = importSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        return session.getFieldMappings().stream()
                .map(mapping -> {
                    FieldMappingDTO dto = new FieldMappingDTO();
                    dto.setSourceColumn(mapping.getSourceColumn());
                    dto.setSourceIndex(mapping.getSourceIndex());
                    dto.setTargetField(mapping.getTargetField());
                    dto.setTargetFieldLabel(mapping.getTargetFieldLabel());
                    dto.setIsRequired(mapping.getIsRequired());
                    dto.setDataType(mapping.getDataType());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Save field mappings
     */
    public ImportSessionDTO saveFieldMappings(String sessionId, List<FieldMappingDTO> mappings) {
        ImportSession session = importSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        // Clear existing mappings from the managed collection (don't replace the list with orphanRemoval=true)
        session.getFieldMappings().clear();
        
        // Add new mappings (only save mapped fields)
        for (FieldMappingDTO mappingDTO : mappings) {
            // Only save mappings that have a source column selected
            if (mappingDTO.getSourceColumn() != null && !mappingDTO.getSourceColumn().isEmpty()) {
                FieldMapping mapping = new FieldMapping();
                mapping.setSourceColumn(mappingDTO.getSourceColumn());
                mapping.setSourceIndex(mappingDTO.getSourceIndex());
                mapping.setTargetField(mappingDTO.getTargetField());
                mapping.setTargetFieldLabel(mappingDTO.getTargetFieldLabel());
                mapping.setIsRequired(mappingDTO.getIsRequired() != null ? mappingDTO.getIsRequired() : false);
                mapping.setDataType(mappingDTO.getDataType());
                mapping.setImportSession(session);
                session.getFieldMappings().add(mapping);
            }
        }
        
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
     * Returns a list of entity field templates with matched CSV columns
     */
    public List<FieldMappingDTO> autoDetectMappings(String sessionId) {
        ImportSession session = importSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        List<FieldMappingTemplate> templates = 
            fieldMappingTemplateRepository.findByEntityTypeOrderByDisplayOrder(session.getEntityType());
        
        // Build a list of available CSV columns from session
        List<String> csvColumns = new ArrayList<>();
        if (session.getFieldMappings() != null) {
            for (FieldMapping fm : session.getFieldMappings()) {
                if (fm.getSourceColumn() != null) {
                    csvColumns.add(fm.getSourceColumn());
                }
            }
        }
        
        List<FieldMappingDTO> detectedMappings = new ArrayList<>();
        
        // For each entity field template, try to find a matching CSV column
        for (FieldMappingTemplate template : templates) {
            FieldMappingDTO detected = new FieldMappingDTO();
            detected.setTargetField(template.getFieldName());
            detected.setTargetFieldLabel(template.getFieldLabel());
            detected.setIsRequired(template.getIsRequired());
            detected.setDataType(template.getDataType());
            
            String fieldName = template.getFieldName().toLowerCase();
            String fieldLabel = template.getFieldLabel().toLowerCase();
            
            // Try to match CSV column to this entity field
            String matchedColumn = null;
            int matchedIndex = -1;
            
            for (int i = 0; i < csvColumns.size(); i++) {
                String csvCol = csvColumns.get(i);
                String csvColLower = csvCol.toLowerCase().trim();
                
                // Try exact match on field name
                if (fieldName.equals(csvColLower) || 
                    fieldName.replace("_", "").equals(csvColLower.replace("_", "").replace(" ", ""))) {
                    matchedColumn = csvCol;
                    matchedIndex = i;
                    break;
                }
                
                // Try match on field label
                if (fieldLabel.equals(csvColLower) ||
                    fieldLabel.replace(" ", "").equals(csvColLower.replace(" ", ""))) {
                    matchedColumn = csvCol;
                    matchedIndex = i;
                    break;
                }
            }
            
            // Try fuzzy match on suggestions if no exact match
            if (matchedColumn == null) {
                List<String> suggestions = template.getSuggestionsList();
                for (int i = 0; i < csvColumns.size(); i++) {
                    String csvCol = csvColumns.get(i);
                    String csvColLower = csvCol.toLowerCase().trim();
                    
                    for (String suggestion : suggestions) {
                        if (suggestion.toLowerCase().equals(csvColLower)) {
                            matchedColumn = csvCol;
                            matchedIndex = i;
                            break;
                        }
                    }
                    if (matchedColumn != null) break;
                }
            }
            
            detected.setSourceColumn(matchedColumn);
            detected.setSourceIndex(matchedIndex >= 0 ? matchedIndex : null);
            
            detectedMappings.add(detected);
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
        
        // Process import synchronously for now
        // TODO: Move to async processing for large files
        processImport(session);
    }
    
    /**
     * Process the actual import
     */
    private void processImport(ImportSession session) {
        try {
            String entityType = session.getEntityType();
            
            if ("students".equalsIgnoreCase(entityType)) {
                processStudentImport(session);
            } else {
                // Unsupported entity type - skip all
                int totalRecords = session.getTotalRecords() != null ? session.getTotalRecords() : 0;
                session.setSkippedRecords(totalRecords);
                session.setAddedRecords(0);
                session.setUpdatedRecords(0);
                session.setFailedRecords(0);
                session.setSuccessRate(0.0);
                session.setStatus(ImportStatus.COMPLETED);
                session.setUpdatedAt(LocalDateTime.now());
                importSessionRepository.save(session);
            }
            
        } catch (Exception e) {
            logger.error("Import failed for session {}: {}", session.getId(), e.getMessage(), e);
            session.setStatus(ImportStatus.FAILED);
            session.setUpdatedAt(LocalDateTime.now());
            importSessionRepository.save(session);
        }
    }
    
    /**
     * Process student import from CSV file
     */
    private void processStudentImport(ImportSession session) {
        String filePath = session.getFilePath();
        if (filePath == null || filePath.isEmpty()) {
            logger.error("No file path set for session {}", session.getId());
            session.setStatus(ImportStatus.FAILED);
            session.setUpdatedAt(LocalDateTime.now());
            importSessionRepository.save(session);
            return;
        }
        
        // Build field mapping: targetField -> sourceIndex
        Map<String, Integer> fieldMappingMap = new HashMap<>();
        for (FieldMapping mapping : session.getFieldMappings()) {
            if (mapping.getTargetField() != null && !mapping.getTargetField().isEmpty()) {
                fieldMappingMap.put(mapping.getTargetField(), mapping.getSourceIndex());
            }
        }
        
        logger.info("Processing student import with mappings: {}", fieldMappingMap);
        
        int addedCount = 0;
        int skippedCount = 0;
        int failedCount = 0;
        int updatedCount = 0;
        int rowNumber = 0;
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(Paths.get(filePath))))) {
            
            // Skip header row
            String headerLine = reader.readLine();
            if (headerLine == null) {
                logger.warn("Empty file for session {}", session.getId());
                session.setStatus(ImportStatus.COMPLETED);
                session.setAddedRecords(0);
                session.setUpdatedAt(LocalDateTime.now());
                importSessionRepository.save(session);
                return;
            }
            
            String line;
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                try {
                    String[] values = parseCSVLine(line);
                    Student student = createStudentFromRow(values, fieldMappingMap);
                    
                    if (student.getFirstName() == null || student.getFirstName().isEmpty() ||
                        student.getLastName() == null || student.getLastName().isEmpty()) {
                        logger.warn("Skipping row {}: missing required fields (firstName or lastName)", rowNumber);
                        skippedCount++;
                        continue;
                    }
                    
                    // Check for duplicate by email if email is present
                    boolean isDuplicate = false;
                    if (student.getEmail() != null && !student.getEmail().isEmpty()) {
                        isDuplicate = studentRepository.findByEmail(student.getEmail()).isPresent();
                    }
                    
                    // Check for duplicate by admission number if present
                    if (!isDuplicate && student.getAdmissionNumber() != null && !student.getAdmissionNumber().isEmpty()) {
                        isDuplicate = studentRepository.findByAdmissionNumber(student.getAdmissionNumber()).isPresent();
                    }
                    
                    if (isDuplicate) {
                        DuplicateAction action = session.getDuplicateAction();
                        if (action == DuplicateAction.SKIP) {
                            logger.debug("Skipping duplicate row {}", rowNumber);
                            skippedCount++;
                            continue;
                        } else if (action == DuplicateAction.OVERWRITE) {
                            // TODO: Implement update logic
                            updatedCount++;
                            continue;
                        }
                        // For CLONE, continue to add
                    }
                    
                    studentRepository.save(student);
                    addedCount++;
                    
                    if (addedCount % 100 == 0) {
                        logger.info("Processed {} records so far...", addedCount);
                    }
                    
                } catch (Exception e) {
                    logger.error("Failed to import row {}: {}", rowNumber, e.getMessage());
                    failedCount++;
                }
            }
            
        } catch (IOException e) {
            logger.error("Failed to read import file: {}", e.getMessage(), e);
            session.setStatus(ImportStatus.FAILED);
            session.setUpdatedAt(LocalDateTime.now());
            importSessionRepository.save(session);
            return;
        }
        
        // Update session with results
        int totalProcessed = addedCount + updatedCount + skippedCount + failedCount;
        double successRate = totalProcessed > 0 ? 
            ((double)(addedCount + updatedCount) / totalProcessed) * 100 : 0.0;
        
        session.setAddedRecords(addedCount);
        session.setUpdatedRecords(updatedCount);
        session.setSkippedRecords(skippedCount);
        session.setFailedRecords(failedCount);
        session.setSuccessRate(successRate);
        session.setStatus(ImportStatus.COMPLETED);
        session.setUpdatedAt(LocalDateTime.now());
        
        importSessionRepository.save(session);
        
        logger.info("Student import completed: added={}, updated={}, skipped={}, failed={}", 
            addedCount, updatedCount, skippedCount, failedCount);
    }
    
    /**
     * Parse a CSV line, handling quoted values
     */
    private String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        values.add(current.toString().trim());
        
        return values.toArray(new String[0]);
    }
    
    /**
     * Create a Student entity from CSV row values using field mappings
     */
    private Student createStudentFromRow(String[] values, Map<String, Integer> mappings) {
        Student student = new Student();
        
        // Set default gradeLevel since it's required
        student.setGradeLevel(Student.GradeLevel.GRADE_1);
        
        for (Map.Entry<String, Integer> entry : mappings.entrySet()) {
            String field = entry.getKey();
            int index = entry.getValue();
            
            if (index < 0 || index >= values.length) {
                continue;
            }
            
            String value = values[index];
            if (value == null || value.isEmpty()) {
                continue;
            }
            
            try {
                switch (field) {
                    case "firstName":
                        student.setFirstName(value);
                        break;
                    case "lastName":
                        student.setLastName(value);
                        break;
                    case "middleName":
                        student.setMiddleName(value);
                        break;
                    case "email":
                        student.setEmail(value);
                        break;
                    case "phone":
                        student.setPhone(value);
                        break;
                    case "dateOfBirth":
                        student.setDateOfBirth(parseDate(value));
                        break;
                    case "gender":
                        student.setGender(parseGender(value));
                        break;
                    case "gradeLevel":
                        student.setGradeLevel(Student.GradeLevel.fromValue(value));
                        break;
                    case "nationality":
                        student.setNationality(value);
                        break;
                    case "bloodGroup":
                        student.setBloodGroup(value);
                        break;
                    case "admissionNumber":
                        student.setAdmissionNumber(value);
                        break;
                    case "section":
                        student.setSection(value);
                        break;
                    case "emergencyContactName":
                        student.setEmergencyContactName(value);
                        break;
                    case "emergencyContactPhone":
                        student.setEmergencyContactPhone(value);
                        break;
                    case "emergencyContactRelation":
                        student.setEmergencyContactRelation(value);
                        break;
                    default:
                        logger.debug("Unknown field mapping: {}", field);
                }
            } catch (Exception e) {
                logger.warn("Failed to set field {} with value '{}': {}", field, value, e.getMessage());
            }
        }
        
        return student;
    }
    
    /**
     * Parse date from various formats
     */
    private LocalDate parseDate(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        // Try common date formats
        String[] formats = {
            "yyyy-MM-dd",
            "MM/dd/yyyy",
            "dd/MM/yyyy",
            "MM-dd-yyyy",
            "dd-MM-yyyy",
            "yyyy/MM/dd"
        };
        
        for (String format : formats) {
            try {
                return LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
            } catch (Exception ignored) {
            }
        }
        
        logger.warn("Could not parse date: {}", value);
        return null;
    }
    
    /**
     * Parse gender from string
     */
    private Student.Gender parseGender(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        String upper = value.toUpperCase().trim();
        if (upper.startsWith("M")) {
            return Student.Gender.MALE;
        } else if (upper.startsWith("F")) {
            return Student.Gender.FEMALE;
        } else if (upper.contains("OTHER")) {
            return Student.Gender.OTHER;
        } else {
            return Student.Gender.PREFER_NOT_TO_SAY;
        }
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
        dto.setEntityType(session.getEntityType());
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
