# Import System - Backend Implementation Guide

## Overview

This guide helps backend developers implement the REST API endpoints required by the Angular Import Wizard.

## Required Endpoints

### 1. File Upload & Preview

**Endpoint**: `POST /api/import/upload-preview`

**Purpose**: Validate file and return preview data

**Request**:
- Content-Type: `multipart/form-data`
- File upload with metadata

**Implementation Example (Java)**:

```java
@PostMapping("/upload-preview")
public ResponseEntity<ImportPreviewDTO> uploadPreview(
    @RequestParam("file") MultipartFile file,
    @RequestParam("entityType") String entityType) {
    
    // Validate file
    if (file.isEmpty() || !isSupportedFormat(file.getOriginalFilename())) {
        throw new BadRequestException("Invalid file format");
    }
    
    if (file.getSize() > MAX_FILE_SIZE) {
        throw new BadRequestException("File size exceeds 50 MB");
    }
    
    // Parse file and get preview
    ImportPreviewDTO preview = importService.parseFilePreview(file, entityType);
    
    return ResponseEntity.ok(preview);
}

private boolean isSupportedFormat(String filename) {
    return filename.matches(".*\\.(xlsx|xls|csv|vcf)$");
}
```

**Response**: `200 OK`
```json
{
  "totalRows": 500,
  "headerRow": ["fullName", "email", "phone", "grade"],
  "sampleRows": [
    {"fullName": "John Doe", "email": "john@example.com", ...},
    {"fullName": "Jane Smith", "email": "jane@example.com", ...}
  ],
  "detectedFormat": "csv"
}
```

---

### 2. Create Import Session

**Endpoint**: `POST /api/import/sessions`

**Purpose**: Create new import session with file and settings

**Request**:
```json
{
  "file": <multipart file>,
  "entityType": "students",
  "settings": {
    "importType": "personal",
    "enableManualApproval": false,
    "duplicateAction": "overwrite",
    "skipEmptyFields": false,
    "findDuplicatesBy": "email"
  }
}
```

**Implementation Example (Java)**:

```java
@PostMapping("/sessions")
public ResponseEntity<ImportSessionDTO> createSession(
    @RequestParam("file") MultipartFile file,
    @RequestParam("entityType") String entityType,
    @RequestParam("settings") String settingsJson,
    @AuthenticationPrincipal UserDetails user) {
    
    ImportSettings settings = objectMapper.readValue(settingsJson, ImportSettings.class);
    
    // Create session
    ImportSession session = importService.createSession(
        file, 
        entityType, 
        settings, 
        getCurrentUserId()
    );
    
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ImportSessionDTO.fromEntity(session));
}
```

**Response**: `201 Created`
```json
{
  "id": "session_abc123",
  "fileName": "students.csv",
  "fileFormat": "csv",
  "totalRecords": 500,
  "uploadedAt": "2024-11-19T10:30:00Z",
  "importType": "personal",
  "fieldMappings": [...],
  "unmappedColumns": ["column1", "column2"],
  "status": "in-progress"
}
```

---

### 3. Get Field Mapping Template

**Endpoint**: `GET /api/import/mapping-templates/{entityType}`

**Purpose**: Return available fields for entity type

**Implementation Example (Java)**:

```java
@GetMapping("/mapping-templates/{entityType}")
public ResponseEntity<FieldMappingTemplateDTO> getMappingTemplate(
    @PathVariable String entityType) {
    
    FieldMappingTemplate template = templateService.getTemplate(entityType);
    
    if (template == null) {
        throw new EntityNotFoundException("Template not found: " + entityType);
    }
    
    return ResponseEntity.ok(FieldMappingTemplateDTO.fromEntity(template));
}
```

**Response**: `200 OK`
```json
{
  "entityType": "students",
  "fields": [
    {
      "key": "fullName",
      "label": "Full Name",
      "required": true,
      "dataType": "string",
      "suggestions": ["name", "student_name", "full_name"]
    },
    {
      "key": "email",
      "label": "Email",
      "required": true,
      "dataType": "email",
      "suggestions": ["email", "email_address", "student_email"]
    },
    {
      "key": "phone",
      "label": "Phone",
      "required": false,
      "dataType": "phone",
      "suggestions": ["phone", "phone_number", "contact"]
    },
    {
      "key": "grade",
      "label": "Grade",
      "required": false,
      "dataType": "string",
      "suggestions": ["grade", "class", "level"]
    }
  ]
}
```

---

### 4. Save Field Mappings

**Endpoint**: `PUT /api/import/sessions/{sessionId}/mappings`

**Purpose**: Save user-defined field mappings

**Request**:
```json
{
  "mappings": [
    {
      "sourceColumn": "fullName",
      "sourceIndex": 0,
      "targetField": "fullName",
      "targetFieldLabel": "Full Name",
      "isRequired": true,
      "dataType": "string"
    }
  ]
}
```

**Implementation Example (Java)**:

```java
@PutMapping("/sessions/{sessionId}/mappings")
public ResponseEntity<ImportSessionDTO> saveMappings(
    @PathVariable String sessionId,
    @RequestBody MappingsSaveRequest request,
    @AuthenticationPrincipal UserDetails user) {
    
    ImportSession session = importService.saveMappings(
        sessionId,
        request.getMappings(),
        getCurrentUserId()
    );
    
    return ResponseEntity.ok(ImportSessionDTO.fromEntity(session));
}
```

**Response**: `200 OK`
```json
{
  "id": "session_abc123",
  "fieldMappings": [...],
  "unmappedColumns": [...],
  "status": "in-progress"
}
```

---

### 5. Validate Field Mappings

**Endpoint**: `POST /api/import/sessions/{sessionId}/validate-mappings`

**Purpose**: Validate that all required fields are mapped

**Implementation Example (Java)**:

```java
@PostMapping("/sessions/{sessionId}/validate-mappings")
public ResponseEntity<ValidationResultDTO> validateMappings(
    @PathVariable String sessionId,
    @AuthenticationPrincipal UserDetails user) {
    
    List<String> errors = importService.validateMappings(
        sessionId,
        getCurrentUserId()
    );
    
    return ResponseEntity.ok(
        new ValidationResultDTO(errors.isEmpty(), errors)
    );
}
```

**Response**: `200 OK`
```json
{
  "valid": true,
  "errors": []
}
```

Or with errors:
```json
{
  "valid": false,
  "errors": [
    "Required field 'email' is not mapped",
    "Required field 'studentId' is not mapped"
  ]
}
```

---

### 6. Auto-Detect Field Mappings

**Endpoint**: `POST /api/import/sessions/{sessionId}/auto-detect-mappings`

**Purpose**: Auto-detect field mappings based on headers

**Implementation Example (Java)**:

```java
@PostMapping("/sessions/{sessionId}/auto-detect-mappings")
public ResponseEntity<List<FieldMappingDTO>> autoDetectMappings(
    @PathVariable String sessionId,
    @AuthenticationPrincipal UserDetails user) {
    
    List<FieldMapping> mappings = importService.autoDetectMappings(
        sessionId,
        getCurrentUserId()
    );
    
    return ResponseEntity.ok(
        mappings.stream()
            .map(FieldMappingDTO::fromEntity)
            .toList()
    );
}
```

**Response**: `200 OK`
```json
[
  {
    "sourceColumn": "fullName",
    "targetField": "fullName",
    "targetFieldLabel": "Full Name",
    "isRequired": true,
    "dataType": "string"
  }
]
```

---

### 7. Start Import

**Endpoint**: `POST /api/import/sessions/{sessionId}/import`

**Purpose**: Start the actual data import process

**Implementation Example (Java)**:

```java
@PostMapping("/sessions/{sessionId}/import")
public ResponseEntity<Void> startImport(
    @PathVariable String sessionId,
    @AuthenticationPrincipal UserDetails user) {
    
    // Start import asynchronously
    importService.startImportAsync(
        sessionId,
        getCurrentUserId()
    );
    
    return ResponseEntity.accepted().build();
}
```

**Response**: `202 Accepted`

The actual import happens asynchronously. Client polls for progress via GET endpoint.

---

### 8. Get Import Summary

**Endpoint**: `GET /api/import/sessions/{sessionId}/summary`

**Purpose**: Get import results and statistics

**Implementation Example (Java)**:

```java
@GetMapping("/sessions/{sessionId}/summary")
public ResponseEntity<ImportSessionDTO> getImportSummary(
    @PathVariable String sessionId,
    @AuthenticationPrincipal UserDetails user) {
    
    ImportSession session = importService.getSession(
        sessionId,
        getCurrentUserId()
    );
    
    return ResponseEntity.ok(ImportSessionDTO.fromEntity(session));
}
```

**Response**: `200 OK`
```json
{
  "id": "session_abc123",
  "fileName": "students.csv",
  "status": "completed",
  "statistics": {
    "totalRecords": 500,
    "addedRecords": 450,
    "updatedRecords": 40,
    "skippedRecords": 10,
    "failedRecords": 0,
    "successRate": 100
  },
  "importedRecords": [
    {
      "rowNumber": 1,
      "recordId": "STU001",
      "status": "added",
      "data": {...},
      "errors": []
    }
  ]
}
```

---

### 9. Undo Import

**Endpoint**: `POST /api/import/sessions/{sessionId}/undo`

**Purpose**: Reverse the import operation

**Implementation Example (Java)**:

```java
@PostMapping("/sessions/{sessionId}/undo")
public ResponseEntity<Void> undoImport(
    @PathVariable String sessionId,
    @AuthenticationPrincipal UserDetails user) {
    
    importService.undoImport(sessionId, getCurrentUserId());
    
    return ResponseEntity.ok().build();
}
```

**Response**: `200 OK`

---

### 10. Get Import History

**Endpoint**: `GET /api/import/history/{entityType}?limit=10`

**Purpose**: Get past import sessions

**Implementation Example (Java)**:

```java
@GetMapping("/history/{entityType}")
public ResponseEntity<Page<ImportSessionDTO>> getImportHistory(
    @PathVariable String entityType,
    @RequestParam(defaultValue = "10") int limit,
    @AuthenticationPrincipal UserDetails user) {
    
    Page<ImportSession> history = importService.getImportHistory(
        entityType,
        getCurrentUserId(),
        limit
    );
    
    return ResponseEntity.ok(
        history.map(ImportSessionDTO::fromEntity)
    );
}
```

**Response**: `200 OK`
```json
{
  "content": [...],
  "totalElements": 45,
  "totalPages": 5,
  "size": 10,
  "number": 0
}
```

---

### 11. Cancel Session

**Endpoint**: `POST /api/import/sessions/{sessionId}/cancel`

**Purpose**: Cancel an in-progress import

**Implementation Example (Java)**:

```java
@PostMapping("/sessions/{sessionId}/cancel")
public ResponseEntity<Void> cancelSession(
    @PathVariable String sessionId,
    @AuthenticationPrincipal UserDetails user) {
    
    importService.cancelSession(sessionId, getCurrentUserId());
    
    return ResponseEntity.ok().build();
}
```

**Response**: `200 OK`

---

## Data Models

### ImportSession Entity

```java
@Entity
@Table(name = "import_sessions")
public class ImportSession {
    @Id
    private String id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "organization_id")
    private Long organizationId;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_format")
    private String fileFormat;
    
    @Column(name = "total_records")
    private Integer totalRecords;
    
    @Column(name = "import_type")
    @Enumerated(EnumType.STRING)
    private ImportType importType; // PERSONAL, ORGANIZATION
    
    @Column(name = "duplicate_action")
    @Enumerated(EnumType.STRING)
    private DuplicateAction duplicateAction; // SKIP, OVERWRITE, CLONE
    
    @Column(name = "find_duplicates_by")
    private String findDuplicatesBy;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ImportStatus status;
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    @Column(name = "imported_at")
    private LocalDateTime importedAt;
    
    @OneToMany(mappedBy = "importSession", cascade = CascadeType.ALL)
    private List<FieldMapping> fieldMappings;
    
    @OneToMany(mappedBy = "importSession", cascade = CascadeType.ALL)
    private List<ImportResult> importedRecords;
}
```

### ImportResult Entity

```java
@Entity
@Table(name = "import_results")
public class ImportResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "import_session_id")
    private ImportSession importSession;
    
    @Column(name = "row_number")
    private Integer rowNumber;
    
    @Column(name = "record_id")
    private String recordId;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ImportResultStatus status; // ADDED, UPDATED, SKIPPED, FAILED
    
    @Column(name = "data", columnDefinition = "JSON")
    private String data;
    
    @Column(name = "errors", columnDefinition = "JSON")
    private String errors;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

---

## Implementation Checklist

- [ ] Create ImportSession entity and repository
- [ ] Create ImportResult entity and repository
- [ ] Create FieldMapping entity and repository
- [ ] Implement file parsing (XLSX, XLS, CSV, VCF)
- [ ] Implement field mapping auto-detection
- [ ] Implement data validation
- [ ] Implement duplicate detection and handling
- [ ] Implement batch import processing
- [ ] Implement import progress tracking
- [ ] Implement import undo functionality
- [ ] Create all REST endpoints
- [ ] Add authentication/authorization checks
- [ ] Add audit logging
- [ ] Add error handling
- [ ] Add unit tests
- [ ] Add integration tests
- [ ] Document API endpoints
- [ ] Configure CORS if needed
- [ ] Set up file upload directory
- [ ] Configure maximum file size
- [ ] Configure batch size for processing

---

## Security Considerations

1. **Authentication**: All endpoints require authentication
2. **Authorization**: Users can only import to their organization
3. **File Validation**: Validate file type and size
4. **Data Validation**: Validate all imported data
5. **SQL Injection**: Use parameterized queries
6. **Rate Limiting**: Implement rate limiting on import endpoints
7. **Audit Logging**: Log all import operations
8. **Temporary Files**: Clean up temporary files after import
9. **CORS**: Configure CORS for API access

---

## Performance Tips

1. **Batch Processing**: Process records in batches (1000 at a time)
2. **Database Transactions**: Use bulk insert for better performance
3. **Indexing**: Add appropriate database indexes
4. **Caching**: Cache field mapping templates
5. **Async Processing**: Process large imports asynchronously
6. **File Size Limits**: Enforce reasonable file size limits
7. **Connection Pooling**: Configure database connection pooling

---

## Error Handling

Implement consistent error responses:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid file format",
  "timestamp": "2024-11-19T10:30:00Z"
}
```

Common error codes:
- `400`: Invalid file format, missing fields
- `401`: Unauthorized
- `403`: Forbidden (user cannot import to org)
- `404`: Session not found
- `409`: Duplicate session
- `413`: File too large
- `500`: Server error

---

## Testing

Example endpoint tests:

```java
@SpringBootTest
class ImportControllerTest {
    
    @Test
    void testUploadPreview_ValidFile() {
        // Test valid file upload
    }
    
    @Test
    void testUploadPreview_InvalidFormat() {
        // Test invalid file format
    }
    
    @Test
    void testCreateSession_Success() {
        // Test session creation
    }
    
    @Test
    void testAutoDetectMappings() {
        // Test auto-detection
    }
    
    @Test
    void testStartImport_DuplicateHandling() {
        // Test all duplicate strategies
    }
}
```

---

## Deployment Considerations

1. Set environment variables for file upload path
2. Configure logging levels
3. Set up database migrations
4. Configure CORS headers
5. Set up monitoring and alerting
6. Configure backup strategy for import records
7. Set up disaster recovery plan
