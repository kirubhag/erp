package krs.erp.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExportRequest {
    private Long entityId;
    private String format; // "csv" or "xlsx"
    private List<String> selectedFields; // List of field names (camelCase) or column names?
    // We will assume field IDs or names. The UI sends field names or IDs.
    // Plan said "fieldIds". Let's support Field IDs for robustness, or Field Names.
    // Given the UI sends "Selected Fields", using IDs is safer if available, but
    // names are easier for nativesql mapping if we just map name->column.
    // Let's use List<Long> fieldIds to matches relationships.
    private List<Long> fieldIds;
    private List<Long> excludedFieldIds; // Field IDs to exclude from export

    private boolean excludeFields; // If true, maybe invert selection? Or just a flag from UI.
    private String searchQuery;
}
