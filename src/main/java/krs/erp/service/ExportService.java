package krs.erp.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import krs.erp.dto.ExportRequest;
import krs.erp.enums.EntityType;
import krs.erp.model.ErpEntity;
import krs.erp.model.ErpField;
import krs.erp.repository.ErpEntityRepository;
import krs.erp.repository.ErpFieldRepository;

@Service
public class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    @Autowired
    private ErpEntityRepository erpEntityRepository;

    @Autowired
    private ErpFieldRepository erpFieldRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    public byte[] exportData(ExportRequest request) throws Exception {
        // 1. Fetch Entity Metadata
        ErpEntity entity = erpEntityRepository.findById(request.getEntityId())
                .orElseThrow(() -> new RuntimeException("Entity not found: " + request.getEntityId()));

        String tableName = entity.getTableName();
        logger.info("Exporting data from table: {}", tableName);

        // 2. Get actual column names from database
        Set<String> actualColumns = getTableColumns(tableName);
        if (actualColumns.isEmpty()) {
            throw new RuntimeException("Could not retrieve columns for table: " + tableName);
        }
        logger.debug("Found {} columns in table {}", actualColumns.size(), tableName);

        // 3. Fetch Fields
        List<ErpField> fields;
        if (request.getFieldIds() != null && !request.getFieldIds().isEmpty()) {
            fields = erpFieldRepository.findAllById(request.getFieldIds());
        } else {
            // Fetch all fields for this entity using entityType
            EntityType entityType = EntityType.fromValue(entity.getSingularName());
            if (entityType != null) {
                fields = erpFieldRepository.findByEntityTypeAndIsActiveTrue(entityType);
            } else {
                // Fallback to erp_entity_id relationship
                fields = erpFieldRepository.findByErpEntityId(entity.getId());
            }
        }
        
        // Check if we have fields to export
        if (fields == null || fields.isEmpty()) {
            throw new RuntimeException("No fields found for entity: " + entity.getSingularName());
        }

        // 4. Build Dynamic SQL - only include fields that exist in the database
        StringBuilder sql = new StringBuilder("SELECT ");
        List<String> headers = new ArrayList<>();
        List<String> validColumns = new ArrayList<>();

        for (ErpField field : fields) {
            String colName = mapFieldToColumn(field);
            // Check if column exists in the table
            if (actualColumns.contains(colName.toLowerCase())) {
                validColumns.add(colName);
                headers.add(field.getFieldLabel());
                sql.append(colName).append(", ");
            } else {
                logger.debug("Skipping field '{}' - column '{}' not found in table '{}'", 
                    field.getFieldName(), colName, tableName);
            }
        }

        if (validColumns.isEmpty()) {
            throw new RuntimeException("No exportable columns found for entity: " + entity.getSingularName());
        }

        // Remove last comma
        if (sql.length() > 7) {
            sql.setLength(sql.length() - 2);
        }

        sql.append(" FROM ").append(tableName);

        logger.debug("Export SQL: {}", sql.toString());

        Query query = entityManager.createNativeQuery(sql.toString());
        List<Object[]> results = query.getResultList();

        // Handle single column result (JPA returns Object, not Object[])
        List<Object[]> finalResults = new ArrayList<>();
        if (!results.isEmpty() && !(results.get(0) instanceof Object[])) {
            for (Object obj : results) {
                finalResults.add(new Object[] { obj });
            }
        } else {
            finalResults = results;
        }

        logger.info("Exported {} rows with {} columns from {}", 
            finalResults.size(), validColumns.size(), tableName);

        // 5. Generate File
        if ("csv".equalsIgnoreCase(request.getFormat())) {
            return generateCsv(headers, finalResults);
        } else {
            return generateXlsx(headers, finalResults);
        }
    }

    /**
     * Get all column names from a database table
     */
    private Set<String> getTableColumns(String tableName) {
        Set<String> columns = new HashSet<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            // Try with the exact table name first
            try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME").toLowerCase());
                }
            }
            // If no columns found, try with lowercase table name
            if (columns.isEmpty()) {
                try (ResultSet rs = metaData.getColumns(null, null, tableName.toLowerCase(), null)) {
                    while (rs.next()) {
                        columns.add(rs.getString("COLUMN_NAME").toLowerCase());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error getting columns for table {}: {}", tableName, e.getMessage());
        }
        return columns;
    }

    // Helper to map ErpField to DB column
    private String mapFieldToColumn(ErpField field) {
        // ideally field.getColumnName() but if not exists, converting fieldName to
        // snake_case
        // For now, let's assume camelToSnake.
        return camelToSnake(field.getFieldName());
    }

    private String camelToSnake(String str) {
        String result = str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
        return result;
    }

    private byte[] generateCsv(List<String> headers, List<Object[]> data) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(out),
                CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])))) {
            for (Object[] row : data) {
                // Convert nulls to empty string
                List<String> printRow = new ArrayList<>();
                for (Object cell : row) {
                    printRow.add(cell == null ? "" : cell.toString());
                }
                printer.printRecord(printRow);
            }
        }
        return out.toByteArray();
    }

    private byte[] generateXlsx(List<String> headers, List<Object[]> data) throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Export");

            // Header Row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }

            // Data Rows
            int rowNum = 1;
            for (Object[] rowData : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < rowData.length; i++) {
                    Cell cell = row.createCell(i);
                    Object value = rowData[i];
                    if (value != null) {
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
