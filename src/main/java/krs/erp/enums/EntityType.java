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
    PURCHASE_ORDER("PurchaseOrder"),
    // Finance types
    FEE_TYPE("FeeType"),
    FEE_STRUCTURE("FeeStructure"),
    FEE_DISCOUNT_RULE("FeeDiscountRule"),
    FINE_CATEGORY("FineCategory"),
    FINE_CONFIGURATION("FineConfiguration"),
    FEE_PAYMENT("FeePayment"),
    FINE_LEDGER("FineLedger"),
    DISCIPLINARY_INCIDENT("DisciplinaryIncident"),
    FINE_WAIVER_REQUEST("FineWaiverRequest"),
    INVOICE("Invoice"),
    TRANSACTION("Transaction"),
    INVOICE_ITEM("InvoiceItem"),
    BUDGET("Budget"),
    BANK_STATEMENT("BankStatement"),
    // Communication types
    MESSAGE("Message"),
    ANNOUNCEMENT("Announcement"),
    SUPPORT_TICKET("SupportTicket");

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
        
        // Handle Finance module routes
        if ("FEE-TYPES".equals(upperValue) || "FEE-TYPE".equals(upperValue)) {
            return EntityType.FEE_TYPE;
        }
        if ("FEE-STRUCTURES".equals(upperValue) || "FEE-STRUCTURE".equals(upperValue)) {
            return EntityType.FEE_STRUCTURE;
        }
        if ("DISCOUNTS".equals(upperValue) || "DISCOUNT".equals(upperValue)) {
            return EntityType.FEE_DISCOUNT_RULE;
        }
        if ("FINE-CATEGORIES".equals(upperValue) || "FINE-CATEGORY".equals(upperValue)) {
            return EntityType.FINE_CATEGORY;
        }
        if ("FINE-MANAGEMENT".equals(upperValue)) {
            return EntityType.FINE_CONFIGURATION;
        }
        if ("PAYMENTS".equals(upperValue) || "PAYMENT".equals(upperValue)) {
            return EntityType.FEE_PAYMENT;
        }
        if ("FINE-LEDGER".equals(upperValue)) {
            return EntityType.FINE_LEDGER;
        }
        if ("INCIDENTS".equals(upperValue) || "INCIDENT".equals(upperValue)) {
            return EntityType.DISCIPLINARY_INCIDENT;
        }
        if ("WAIVERS".equals(upperValue) || "WAIVER".equals(upperValue)) {
            return EntityType.FINE_WAIVER_REQUEST;
        }
        if ("INVOICES".equals(upperValue) || "INVOICE".equals(upperValue)) {
            return EntityType.INVOICE;
        }
        if ("TRANSACTIONS".equals(upperValue) || "TRANSACTION".equals(upperValue)) {
            return EntityType.TRANSACTION;
        }
        if ("INVOICE-ITEMS".equals(upperValue) || "INVOICE-ITEM".equals(upperValue)) {
            return EntityType.INVOICE_ITEM;
        }
        if ("BUDGETS".equals(upperValue) || "BUDGET".equals(upperValue)) {
            return EntityType.BUDGET;
        }
        if ("BANK-STATEMENTS".equals(upperValue) || "BANK-STATEMENT".equals(upperValue)) {
            return EntityType.BANK_STATEMENT;
        }
        if ("SCHOLARSHIP-CATEGORIES".equals(upperValue) || "SCHOLARSHIP-CATEGORY".equals(upperValue)) {
            return EntityType.SCHOLARSHIP_CATEGORY;
        }
        if ("SCHOLARSHIP-APPLICATIONS".equals(upperValue) || "SCHOLARSHIP-APPLICATION".equals(upperValue)) {
            return EntityType.SCHOLARSHIP_APPLICATION;
        }
        if ("ACCOUNTS".equals(upperValue) || "ACCOUNT".equals(upperValue)) {
            return EntityType.CHART_OF_ACCOUNT;
        }
        if ("JOURNAL-ENTRIES".equals(upperValue) || "JOURNAL-ENTRY".equals(upperValue)) {
            return EntityType.JOURNAL_ENTRY;
        }
        if ("ACCOUNTING-PERIODS".equals(upperValue) || "ACCOUNTING-PERIOD".equals(upperValue)) {
            return EntityType.ACCOUNTING_PERIOD;
        }
        
        // Handle Communication module routes
        if ("MESSAGES".equals(upperValue) || "MESSAGE".equals(upperValue)) {
            return EntityType.MESSAGE;
        }
        if ("ANNOUNCEMENTS".equals(upperValue) || "ANNOUNCEMENT".equals(upperValue)) {
            return EntityType.ANNOUNCEMENT;
        }
        if ("TICKETS".equals(upperValue) || "TICKET".equals(upperValue)) {
            return EntityType.SUPPORT_TICKET;
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