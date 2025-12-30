package krs.erp.enums;

/**
 * Enum representing the different entity types that can have custom views
 */
public enum EntityType {
    STUDENT("Student"),
    PARENT("Parent"),
    ATTENDANCE("Attendance"),
    HEALTH("Health"),
    SUBJECT("Subject"),
    TIMETABLE("Timetable"),
    USER("User"),
    STAFF("Staff"),
    ORGANIZATION("Organization"),
    EMAIL_TEMPLATE("EmailTemplate"),
    EMAIL_LOG("EmailLog"),
    PERMISSION("Permission"),
    ROLE("Role"),
    CUSTOM_VIEW("CustomView"),
    ERP_FIELD("ErpField"),
    PARENT_STUDENT_RELATION("ParentStudentRelation"),
    RECYCLE_BIN("RecycleBin"),
    // From EmailTemplate - additional types
    GENERAL("General"),
    NOTIFICATION("Notification"),
    // Legacy types for compatibility
    TEACHER("Teacher"),
    COURSE("Course"),
    GRADE("Grade"),
    ASSIGNMENT("Assignment"),
    EXAM("Exam"),
    // Additional entity types
    ADDRESS("Address"),
    STUDENT_GUARDIAN("StudentGuardian"),
    STUDENT_MEDICAL("StudentMedical"),
    ROOM("Room"),
    DASHBOARD("Dashboard"),
    SCHOLARSHIP_CATEGORY("ScholarshipCategory"),
    SCHOLARSHIP_APPLICATION("ScholarshipApplication"),
    SCHOLARSHIP_DISBURSEMENT("ScholarshipDisbursement"),
    CHART_OF_ACCOUNT("ChartOfAccount"),
    JOURNAL_ENTRY("JournalEntry"),
    ACCOUNTING_PERIOD("AccountingPeriod"),
    STUDENT_REGISTRATION("StudentRegistration"),
    PERFORMANCE_CYCLE("PerformanceCycle"),
    PERFORMANCE_CRITERIA("PerformanceCriteria"),
    PERFORMANCE_REVIEW("PerformanceReview"),
    LEAVE_TYPE("LeaveType"),
    LEAVE_REQUEST("LeaveRequest"),
    LEAVE_BALANCE("LeaveBalance"),
    PAYROLL_RUN("PayrollRun"),
    JOB_POSTING("JobPosting"),
    JOB_APPLICATION("JobApplication"),
    ADMISSION_INQUIRY("AdmissionInquiry"),
    ADMISSION_APPLICATION("AdmissionApplication"),
    // Inventory types
    ASSET("Asset"),
    CONSUMABLE("Consumable"),
    VENDOR("Vendor"),
    PURCHASE_ORDER("PurchaseOrder");

    private final String displayName;

    EntityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return this.name();
    }

    /**
     * Get EntityType from string value
     * Handles both singular and plural forms (e.g., "student" or "students")
     * Also handles hyphenated forms (e.g., "job-postings", "job-applications")
     */
    public static EntityType fromValue(String value) {
        if (value == null) {
            return null;
        }

        String upperValue = value.toUpperCase();
        
        // Handle special hyphenated cases
        if ("JOB-POSTINGS".equals(upperValue) || "JOB-POSTING".equals(upperValue)) {
            return EntityType.JOB_POSTING;
        }
        if ("JOB-APPLICATIONS".equals(upperValue) || "JOB-APPLICATION".equals(upperValue)) {
            return EntityType.JOB_APPLICATION;
        }
        if ("PURCHASE-ORDERS".equals(upperValue) || "PURCHASE-ORDER".equals(upperValue)) {
            return EntityType.PURCHASE_ORDER;
        }
        if ("LEAVE-BALANCES".equals(upperValue) || "LEAVE-BALANCE".equals(upperValue)) {
            return EntityType.LEAVE_BALANCE;
        }
        if ("LEAVE-TYPES".equals(upperValue) || "LEAVE-TYPE".equals(upperValue)) {
            return EntityType.LEAVE_TYPE;
        }
        if ("LEAVE-REQUESTS".equals(upperValue) || "LEAVE-REQUEST".equals(upperValue)) {
            return EntityType.LEAVE_REQUEST;
        }
        if ("PAYROLL-RUNS".equals(upperValue) || "PAYROLL-RUN".equals(upperValue)) {
            return EntityType.PAYROLL_RUN;
        }
        if ("PERFORMANCE-CYCLES".equals(upperValue) || "PERFORMANCE-CYCLE".equals(upperValue)) {
            return EntityType.PERFORMANCE_CYCLE;
        }
        if ("PERFORMANCE-CRITERIA".equals(upperValue)) {
            return EntityType.PERFORMANCE_CRITERIA;
        }
        if ("PERFORMANCE-REVIEWS".equals(upperValue) || "PERFORMANCE-REVIEW".equals(upperValue)) {
            return EntityType.PERFORMANCE_REVIEW;
        }
        if ("INQUIRIES".equals(upperValue) || "INQUIRY".equals(upperValue)) {
            return EntityType.ADMISSION_INQUIRY;
        }
        if ("APPLICATIONS".equals(upperValue) || "APPLICATION".equals(upperValue)) {
            return EntityType.ADMISSION_APPLICATION;
        }
        if ("LEAVES".equals(upperValue)) {
            return EntityType.LEAVE_REQUEST;
        }
        if ("SALARIES".equals(upperValue)) {
            return EntityType.PAYROLL_RUN;
        }

        // Replace hyphens with underscores for standard enum matching
        upperValue = upperValue.replace("-", "_");

        try {
            // Try exact match first
            return EntityType.valueOf(upperValue);
        } catch (IllegalArgumentException e) {
            // Try removing trailing 'S' for plural forms
            if (upperValue.endsWith("S") && upperValue.length() > 1) {
                try {
                    return EntityType.valueOf(upperValue.substring(0, upperValue.length() - 1));
                } catch (IllegalArgumentException e2) {
                    // Ignore and return null
                }
            }
            return null;
        }
    }

    /**
     * Check if the given string is a valid entity type
     */
    public static boolean isValid(String value) {
        return fromValue(value) != null;
    }
}