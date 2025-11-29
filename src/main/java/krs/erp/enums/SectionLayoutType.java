package krs.erp.enums;

/**
 * Enum representing the different layout types for module sections
 * Determines how many columns of fields can be displayed in a section
 */
public enum SectionLayoutType {
    SINGLE_COLUMN("Single Column", 1),
    TWO_COLUMN("Two Columns", 2),
    THREE_COLUMN("Three Columns", 3),
    FOUR_COLUMN("Four Columns", 4);

    private final String displayName;
    private final int columnCount;

    SectionLayoutType(String displayName, int columnCount) {
        this.displayName = displayName;
        this.columnCount = columnCount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Get SectionLayoutType from column count
     */
    public static SectionLayoutType fromColumnCount(int columnCount) {
        for (SectionLayoutType type : values()) {
            if (type.columnCount == columnCount) {
                return type;
            }
        }
        return TWO_COLUMN; // Default
    }

    /**
     * Get SectionLayoutType from string value
     */
    public static SectionLayoutType fromValue(String value) {
        if (value == null) {
            return TWO_COLUMN;
        }

        try {
            return SectionLayoutType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TWO_COLUMN;
        }
    }
}
