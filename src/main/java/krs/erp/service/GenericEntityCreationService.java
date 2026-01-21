package krs.erp.service;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.EntityType;
import krs.erp.model.Address;
import krs.erp.model.Attendance;
import krs.erp.model.Course;
import krs.erp.model.CustomView;
import krs.erp.model.EmailLog;
import krs.erp.model.EmailTemplate;
import krs.erp.model.ErpAttachment;
import krs.erp.model.ErpAutoNumber;
import krs.erp.model.ErpClass;
import krs.erp.model.ErpEntity;
import krs.erp.model.ErpEntityRelation;
import krs.erp.model.ErpField;
import krs.erp.model.ErpSection;
import krs.erp.model.ErpTabGroup;
import krs.erp.model.Exam;
import krs.erp.model.Grade;
import krs.erp.model.HealthRecord;
import krs.erp.model.ImportHistory;
import krs.erp.model.LoginHistory;
import krs.erp.model.Organization;
import krs.erp.model.OrganizationSettings;
import krs.erp.model.Parent;
import krs.erp.model.ParentStudentRelation;
import krs.erp.model.PaymentTransaction;
import krs.erp.model.Permission;
import krs.erp.model.PricingPlan;
import krs.erp.model.RecycleBin;
import krs.erp.model.Role;
import krs.erp.model.Room;
import krs.erp.model.Staff;
import krs.erp.model.Student;
import krs.erp.model.StudentGuardianInfo;
import krs.erp.model.StudentMedicalInfo;
import krs.erp.model.Subject;
import krs.erp.model.SubscriptionHistory;
import krs.erp.model.Timetable;
import krs.erp.model.User;
import krs.erp.model.UserSettings;
import krs.erp.model.UserSubscription;
import krs.erp.model.academic.AcademicSettings;
import krs.erp.model.academic.AcademicYear;
import krs.erp.model.academic.GradingScale;
import krs.erp.model.academic.Term;
import krs.erp.model.admission.AdmissionApplication;
import krs.erp.model.admission.AdmissionCycle;
import krs.erp.model.admission.AdmissionInquiry;
import krs.erp.model.admission.AdmissionSeatAllocation;
import krs.erp.model.admission.StudentRegistration;
import krs.erp.model.alumni.Alumni;
import krs.erp.model.alumni.AlumniContribution;
import krs.erp.model.calendar.CalendarDay;
import krs.erp.model.calendar.Holiday;
import krs.erp.model.calendar.InstitutionEvent;
import krs.erp.model.communication.Announcement;
import krs.erp.model.communication.Message;
import krs.erp.model.communication.NotificationLog;
import krs.erp.model.communication.NotificationTemplate;
import krs.erp.model.communication.SupportTicket;
import krs.erp.model.communication.TicketComment;
import krs.erp.model.documents.ErpDocument;
import krs.erp.model.finance.AccountingPeriod;
import krs.erp.model.finance.BankStatement;
import krs.erp.model.finance.BankStatementLine;
import krs.erp.model.finance.Budget;
import krs.erp.model.finance.BudgetLine;
import krs.erp.model.finance.ChartOfAccount;
import krs.erp.model.finance.DisciplinaryIncident;
import krs.erp.model.finance.FeeDiscountRule;
import krs.erp.model.finance.FeePayment;
import krs.erp.model.finance.FeeStructure;
import krs.erp.model.finance.FeeType;
import krs.erp.model.finance.FineCategory;
import krs.erp.model.finance.FineConfiguration;
import krs.erp.model.finance.FineWaiverRequest;
import krs.erp.model.finance.Invoice;
import krs.erp.model.finance.InvoiceItem;
import krs.erp.model.finance.JournalEntry;
import krs.erp.model.finance.JournalItem;
import krs.erp.model.finance.ScholarshipApplication;
import krs.erp.model.finance.ScholarshipCategory;
import krs.erp.model.finance.ScholarshipDisbursement;
import krs.erp.model.finance.StudentFineLedger;
import krs.erp.model.finance.Transaction;
import krs.erp.model.hr.Department;
import krs.erp.model.hr.Designation;
import krs.erp.model.hr.JobApplication;
import krs.erp.model.hr.JobPosting;
import krs.erp.model.hr.LeaveBalance;
import krs.erp.model.hr.LeaveRequest;
import krs.erp.model.hr.LeaveType;
import krs.erp.model.hr.PayrollRun;
import krs.erp.model.hr.Payslip;
import krs.erp.model.hr.PerformanceCriteria;
import krs.erp.model.hr.PerformanceCycle;
import krs.erp.model.hr.PerformanceReview;
import krs.erp.model.hr.PerformanceReviewDetail;
import krs.erp.model.hr.StaffSalary;
import krs.erp.model.inventory.Asset;
import krs.erp.model.inventory.Consumable;
import krs.erp.model.inventory.PurchaseOrder;
import krs.erp.model.inventory.Vendor;
import krs.erp.model.library.Author;
import krs.erp.model.library.LibraryHold;
import krs.erp.model.library.LibraryLoan;
import krs.erp.model.library.LibraryPO;
import krs.erp.model.library.LibraryPolicy;
import krs.erp.model.library.LibraryPurchaseRequest;
import krs.erp.model.library.LibraryResource;
import krs.erp.model.library.Publisher;
import krs.erp.model.library.ResourceItem;
import krs.erp.model.lms.Lesson;
import krs.erp.model.lms.LmsAnswer;
import krs.erp.model.lms.LmsBadge;
import krs.erp.model.lms.LmsContent;
import krs.erp.model.lms.LmsForum;
import krs.erp.model.lms.LmsForumPost;
import krs.erp.model.lms.LmsModule;
import krs.erp.model.lms.LmsPeerReview;
import krs.erp.model.lms.LmsPointLog;
import krs.erp.model.lms.LmsQuestion;
import krs.erp.model.lms.LmsQuestionBank;
import krs.erp.model.lms.LmsQuiz;
import krs.erp.model.lms.LmsRubric;
import krs.erp.model.lms.LmsStudentProgress;
import krs.erp.model.lms.LmsSubmission;
import krs.erp.model.lms.LmsTopic;
import krs.erp.model.lms.VirtualAttendanceRecord;
import krs.erp.model.lms.VirtualClassSession;
import krs.erp.model.maintenance.Facility;
import krs.erp.model.maintenance.FacilityBooking;
import krs.erp.model.maintenance.WorkOrder;
import krs.erp.model.reporting.MISReport;
import krs.erp.model.tpd.Competency;
import krs.erp.model.tpd.CpdLedger;
import krs.erp.model.tpd.Evidence;
import krs.erp.model.tpd.ProfessionalPortfolio;
import krs.erp.model.tpd.SkillAssessment;
import krs.erp.model.tpd.TrainingAttendance;
import krs.erp.model.tpd.TrainingEvaluation;
import krs.erp.model.tpd.TrainingEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic service for creating ANY entity dynamically based on entity type.
 * Uses reflection to map incoming data to entity fields automatically.
 * 
 * TO ADD A NEW ENTITY:
 * 1. Add mapping in ENTITY_CLASS_MAP: EntityType -> Entity.class
 * 2. Ensure repository exists with standard naming convention (e.g., StudentRepository)
 * 3. That's it! No other code changes needed.
 */
@Service
@Slf4j
@Transactional
public class GenericEntityCreationService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AutoNumberService autoNumberService;

    /**
     * Mapping from EntityType to Entity Class.
     * Add new entity types here - no other code changes needed!
     */
    private static final Map<EntityType, Class<?>> ENTITY_CLASS_MAP = new HashMap<>();
    
    /**
     * Mapping from EntityType to Repository bean name.
     */
    private static final Map<EntityType, String> REPOSITORY_NAME_MAP = new HashMap<>();
    
    static {
        // Core entities
        ENTITY_CLASS_MAP.put(EntityType.STUDENT, Student.class);
        ENTITY_CLASS_MAP.put(EntityType.STAFF, Staff.class);
        ENTITY_CLASS_MAP.put(EntityType.PARENT, Parent.class);
        ENTITY_CLASS_MAP.put(EntityType.USER, User.class);
        ENTITY_CLASS_MAP.put(EntityType.SUBJECT, Subject.class);
        ENTITY_CLASS_MAP.put(EntityType.ROOM, Room.class);
        ENTITY_CLASS_MAP.put(EntityType.ATTENDANCE, Attendance.class);
        ENTITY_CLASS_MAP.put(EntityType.TIMETABLE, Timetable.class);
        ENTITY_CLASS_MAP.put(EntityType.ADDRESS, Address.class);
        
        // Academic entities
        ENTITY_CLASS_MAP.put(EntityType.SCHOLARSHIP_CATEGORY, ScholarshipCategory.class);
        ENTITY_CLASS_MAP.put(EntityType.SCHOLARSHIP_APPLICATION, ScholarshipApplication.class);
        ENTITY_CLASS_MAP.put(EntityType.SCHOLARSHIP_DISBURSEMENT, ScholarshipDisbursement.class);
        
        // Admission entities
        ENTITY_CLASS_MAP.put(EntityType.ADMISSION_INQUIRY, AdmissionInquiry.class);
        ENTITY_CLASS_MAP.put(EntityType.ADMISSION_APPLICATION, AdmissionApplication.class);
        
        // HR entities
        ENTITY_CLASS_MAP.put(EntityType.PERFORMANCE_CYCLE, PerformanceCycle.class);
        ENTITY_CLASS_MAP.put(EntityType.PERFORMANCE_CRITERIA, PerformanceCriteria.class);
        ENTITY_CLASS_MAP.put(EntityType.PERFORMANCE_REVIEW, PerformanceReview.class);
        ENTITY_CLASS_MAP.put(EntityType.LEAVE_TYPE, LeaveType.class);
        ENTITY_CLASS_MAP.put(EntityType.LEAVE_REQUEST, LeaveRequest.class);
        ENTITY_CLASS_MAP.put(EntityType.LEAVE_BALANCE, LeaveBalance.class);
        ENTITY_CLASS_MAP.put(EntityType.PAYROLL_RUN, PayrollRun.class);
        ENTITY_CLASS_MAP.put(EntityType.JOB_POSTING, JobPosting.class);
        ENTITY_CLASS_MAP.put(EntityType.JOB_APPLICATION, JobApplication.class);
        
        // Inventory entities
        ENTITY_CLASS_MAP.put(EntityType.ASSET, Asset.class);
        ENTITY_CLASS_MAP.put(EntityType.CONSUMABLE, Consumable.class);
        ENTITY_CLASS_MAP.put(EntityType.VENDOR, Vendor.class);
        
        // Finance entities
        ENTITY_CLASS_MAP.put(EntityType.FEE_TYPE, FeeType.class);
        ENTITY_CLASS_MAP.put(EntityType.FEE_STRUCTURE, FeeStructure.class);
        ENTITY_CLASS_MAP.put(EntityType.FEE_DISCOUNT_RULE, FeeDiscountRule.class);
        ENTITY_CLASS_MAP.put(EntityType.FINE_CATEGORY, FineCategory.class);
        ENTITY_CLASS_MAP.put(EntityType.FINE_CONFIGURATION, FineConfiguration.class);
        ENTITY_CLASS_MAP.put(EntityType.FEE_PAYMENT, FeePayment.class);
        ENTITY_CLASS_MAP.put(EntityType.FINE_LEDGER, StudentFineLedger.class);
        ENTITY_CLASS_MAP.put(EntityType.DISCIPLINARY_INCIDENT, DisciplinaryIncident.class);
        ENTITY_CLASS_MAP.put(EntityType.FINE_WAIVER_REQUEST, FineWaiverRequest.class);
        ENTITY_CLASS_MAP.put(EntityType.INVOICE, Invoice.class);
        ENTITY_CLASS_MAP.put(EntityType.INVOICE_ITEM, InvoiceItem.class);
        ENTITY_CLASS_MAP.put(EntityType.BUDGET, Budget.class);
        ENTITY_CLASS_MAP.put(EntityType.BANK_STATEMENT, BankStatement.class);
        ENTITY_CLASS_MAP.put(EntityType.CHART_OF_ACCOUNT, ChartOfAccount.class);
        ENTITY_CLASS_MAP.put(EntityType.JOURNAL_ENTRY, JournalEntry.class);
        ENTITY_CLASS_MAP.put(EntityType.ACCOUNTING_PERIOD, AccountingPeriod.class);
        
        // Communication entities
        ENTITY_CLASS_MAP.put(EntityType.MESSAGE, Message.class);
        ENTITY_CLASS_MAP.put(EntityType.ANNOUNCEMENT, Announcement.class);
        ENTITY_CLASS_MAP.put(EntityType.SUPPORT_TICKET, SupportTicket.class);
        
        // Library entities
        ENTITY_CLASS_MAP.put(EntityType.AUTHOR, Author.class);
        ENTITY_CLASS_MAP.put(EntityType.PUBLISHER, Publisher.class);
        ENTITY_CLASS_MAP.put(EntityType.LIBRARY_RESOURCE, LibraryResource.class);
        ENTITY_CLASS_MAP.put(EntityType.RESOURCE_ITEM, ResourceItem.class);
        ENTITY_CLASS_MAP.put(EntityType.LIBRARY_LOAN, LibraryLoan.class);
        ENTITY_CLASS_MAP.put(EntityType.LIBRARY_HOLD, LibraryHold.class);
        ENTITY_CLASS_MAP.put(EntityType.LIBRARY_POLICY, LibraryPolicy.class);
        
        // LMS entities
        ENTITY_CLASS_MAP.put(EntityType.LMS_MODULE, LmsModule.class);
        ENTITY_CLASS_MAP.put(EntityType.LESSON, Lesson.class);
        ENTITY_CLASS_MAP.put(EntityType.LMS_TOPIC, LmsTopic.class);
        ENTITY_CLASS_MAP.put(EntityType.LMS_CONTENT, LmsContent.class);
        ENTITY_CLASS_MAP.put(EntityType.LMS_QUIZ, LmsQuiz.class);
        ENTITY_CLASS_MAP.put(EntityType.LMS_SUBMISSION, LmsSubmission.class);
        ENTITY_CLASS_MAP.put(EntityType.VIRTUAL_SESSION, VirtualClassSession.class);
        
        // TPD entities
        ENTITY_CLASS_MAP.put(EntityType.COMPETENCY, Competency.class);
        ENTITY_CLASS_MAP.put(EntityType.SKILL_ASSESSMENT, SkillAssessment.class);
        ENTITY_CLASS_MAP.put(EntityType.TRAINING_EVENT, TrainingEvent.class);
        ENTITY_CLASS_MAP.put(EntityType.TRAINING_ATTENDANCE, TrainingAttendance.class);
        ENTITY_CLASS_MAP.put(EntityType.CPD_LEDGER, CpdLedger.class);
        ENTITY_CLASS_MAP.put(EntityType.PROFESSIONAL_PORTFOLIO, ProfessionalPortfolio.class);
        ENTITY_CLASS_MAP.put(EntityType.EVIDENCE, Evidence.class);
        ENTITY_CLASS_MAP.put(EntityType.EVALUATION, TrainingEvaluation.class);
        
        // Alumni entities
        ENTITY_CLASS_MAP.put(EntityType.ALUMNI, Alumni.class);
        ENTITY_CLASS_MAP.put(EntityType.ALUMNI_CONTRIBUTION, AlumniContribution.class);
        
        // Finance additional entities
        ENTITY_CLASS_MAP.put(EntityType.BANK_STATEMENT_LINE, BankStatementLine.class);
        ENTITY_CLASS_MAP.put(EntityType.BUDGET_LINE, BudgetLine.class);
        
        // HR additional entities
        ENTITY_CLASS_MAP.put(EntityType.STAFF_SALARY, StaffSalary.class);
        ENTITY_CLASS_MAP.put(EntityType.PERFORMANCE_REVIEW_DETAIL, PerformanceReviewDetail.class);
        
        // Calendar entities
        ENTITY_CLASS_MAP.put(EntityType.CALENDAR_DAY, CalendarDay.class);
        ENTITY_CLASS_MAP.put(EntityType.INSTITUTION_EVENT, InstitutionEvent.class);
        
        // Document entities
        ENTITY_CLASS_MAP.put(EntityType.ERP_DOCUMENT, ErpDocument.class);
        
        // LMS additional entities
        ENTITY_CLASS_MAP.put(EntityType.LMS_ANSWER, LmsAnswer.class);
        ENTITY_CLASS_MAP.put(EntityType.LMS_QUESTION, LmsQuestion.class);
        ENTITY_CLASS_MAP.put(EntityType.LMS_STUDENT_PROGRESS, LmsStudentProgress.class);
        
        // Communication additional entities
        ENTITY_CLASS_MAP.put(EntityType.NOTIFICATION_LOG, NotificationLog.class);
        ENTITY_CLASS_MAP.put(EntityType.NOTIFICATION_TEMPLATE, NotificationTemplate.class);
        ENTITY_CLASS_MAP.put(EntityType.TICKET_COMMENT, TicketComment.class);
        
        // Academic additional entities
        ENTITY_CLASS_MAP.put(EntityType.ACADEMIC_SETTINGS, AcademicSettings.class);
        ENTITY_CLASS_MAP.put(EntityType.ACADEMIC_YEAR, AcademicYear.class);
        ENTITY_CLASS_MAP.put(EntityType.GRADING_SCALE, GradingScale.class);
        ENTITY_CLASS_MAP.put(EntityType.TERM, Term.class);
        ENTITY_CLASS_MAP.put(EntityType.ERP_CLASS, ErpClass.class);
        ENTITY_CLASS_MAP.put(EntityType.COURSE, Course.class);
        ENTITY_CLASS_MAP.put(EntityType.EXAM, Exam.class);
        ENTITY_CLASS_MAP.put(EntityType.GRADE, Grade.class);
        
        // Admission additional entities
        ENTITY_CLASS_MAP.put(EntityType.ADMISSION_CYCLE, AdmissionCycle.class);
        ENTITY_CLASS_MAP.put(EntityType.ADMISSION_SEAT_ALLOCATION, AdmissionSeatAllocation.class);
        ENTITY_CLASS_MAP.put(EntityType.STUDENT_REGISTRATION, StudentRegistration.class);
        
        // Organization entities
        ENTITY_CLASS_MAP.put(EntityType.ORGANIZATION, Organization.class);
        ENTITY_CLASS_MAP.put(EntityType.ORGANIZATION_SETTINGS, OrganizationSettings.class);
        ENTITY_CLASS_MAP.put(EntityType.DEPARTMENT, Department.class);
        ENTITY_CLASS_MAP.put(EntityType.DESIGNATION, Designation.class);
        
        // HR Payslip
        ENTITY_CLASS_MAP.put(EntityType.PAYSLIP, Payslip.class);
        
        // Finance additional entities
        ENTITY_CLASS_MAP.put(EntityType.JOURNAL_ITEM, JournalItem.class);
        ENTITY_CLASS_MAP.put(EntityType.PAYMENT_TRANSACTION, PaymentTransaction.class);
        ENTITY_CLASS_MAP.put(EntityType.TRANSACTION, Transaction.class);
        
        // Facility/Maintenance entities
        ENTITY_CLASS_MAP.put(EntityType.FACILITY, Facility.class);
        ENTITY_CLASS_MAP.put(EntityType.FACILITY_BOOKING, FacilityBooking.class);
        ENTITY_CLASS_MAP.put(EntityType.WORK_ORDER, WorkOrder.class);
        ENTITY_CLASS_MAP.put(EntityType.PURCHASE_ORDER, PurchaseOrder.class);
        
        // Calendar additional entities
        ENTITY_CLASS_MAP.put(EntityType.HOLIDAY, Holiday.class);
        
        // Health entities
        ENTITY_CLASS_MAP.put(EntityType.HEALTH_RECORD, HealthRecord.class);
        ENTITY_CLASS_MAP.put(EntityType.STUDENT_GUARDIAN, StudentGuardianInfo.class);
        ENTITY_CLASS_MAP.put(EntityType.STUDENT_MEDICAL, StudentMedicalInfo.class);
        
        // System/Audit entities
        ENTITY_CLASS_MAP.put(EntityType.LOGIN_HISTORY, LoginHistory.class);
        ENTITY_CLASS_MAP.put(EntityType.IMPORT_HISTORY, ImportHistory.class);
        ENTITY_CLASS_MAP.put(EntityType.MIS_REPORT, MISReport.class);
        ENTITY_CLASS_MAP.put(EntityType.RECYCLE_BIN, RecycleBin.class);
        
        // Subscription entities
        ENTITY_CLASS_MAP.put(EntityType.PRICING_PLAN, PricingPlan.class);
        ENTITY_CLASS_MAP.put(EntityType.USER_SUBSCRIPTION, UserSubscription.class);
        ENTITY_CLASS_MAP.put(EntityType.SUBSCRIPTION_HISTORY, SubscriptionHistory.class);
        
        // ERP metadata entities
        ENTITY_CLASS_MAP.put(EntityType.ERP_ATTACHMENT, ErpAttachment.class);
        ENTITY_CLASS_MAP.put(EntityType.ERP_AUTO_NUMBER, ErpAutoNumber.class);
        ENTITY_CLASS_MAP.put(EntityType.ERP_ENTITY, ErpEntity.class);
        ENTITY_CLASS_MAP.put(EntityType.ERP_ENTITY_RELATION, ErpEntityRelation.class);
        ENTITY_CLASS_MAP.put(EntityType.ERP_SECTION, ErpSection.class);
        ENTITY_CLASS_MAP.put(EntityType.ERP_TAB_GROUP, ErpTabGroup.class);
        ENTITY_CLASS_MAP.put(EntityType.ERP_FIELD, ErpField.class);
        ENTITY_CLASS_MAP.put(EntityType.CUSTOM_VIEW, CustomView.class);
        
        // User entities
        ENTITY_CLASS_MAP.put(EntityType.USER_SETTINGS, UserSettings.class);
        ENTITY_CLASS_MAP.put(EntityType.EMAIL_TEMPLATE, EmailTemplate.class);
        ENTITY_CLASS_MAP.put(EntityType.EMAIL_LOG, EmailLog.class);
        ENTITY_CLASS_MAP.put(EntityType.PERMISSION, Permission.class);
        ENTITY_CLASS_MAP.put(EntityType.ROLE, Role.class);
        ENTITY_CLASS_MAP.put(EntityType.PARENT_STUDENT_RELATION, ParentStudentRelation.class);
        
        // Library additional entities
        ENTITY_CLASS_MAP.put(EntityType.LIBRARY_PURCHASE_REQUEST, LibraryPurchaseRequest.class);
        ENTITY_CLASS_MAP.put(EntityType.LIBRARY_PO, LibraryPO.class);
        
        // LMS additional entities
        ENTITY_CLASS_MAP.put(EntityType.LMS_QUESTION_BANK, LmsQuestionBank.class);
        ENTITY_CLASS_MAP.put(EntityType.LMS_RUBRIC, LmsRubric.class);
        ENTITY_CLASS_MAP.put(EntityType.VIRTUAL_ATTENDANCE, VirtualAttendanceRecord.class);
        ENTITY_CLASS_MAP.put(EntityType.VIRTUAL_ATTENDANCE_RECORD, VirtualAttendanceRecord.class);
        ENTITY_CLASS_MAP.put(EntityType.LMS_BADGE, LmsBadge.class);
        ENTITY_CLASS_MAP.put(EntityType.LMS_POINT_LOG, LmsPointLog.class);
        ENTITY_CLASS_MAP.put(EntityType.LMS_FORUM, LmsForum.class);
        ENTITY_CLASS_MAP.put(EntityType.LMS_FORUM_POST, LmsForumPost.class);
        ENTITY_CLASS_MAP.put(EntityType.LMS_PEER_REVIEW, LmsPeerReview.class);
        
        // Repository name mappings (bean names follow Spring convention: classNameRepository)
        for (EntityType type : ENTITY_CLASS_MAP.keySet()) {
            Class<?> entityClass = ENTITY_CLASS_MAP.get(type);
            String repoName = Character.toLowerCase(entityClass.getSimpleName().charAt(0)) 
                            + entityClass.getSimpleName().substring(1) + "Repository";
            REPOSITORY_NAME_MAP.put(type, repoName);
        }
    }

    /**
     * Create an entity based on the entity type and provided data.
     * This method works for ANY entity type registered in ENTITY_CLASS_MAP.
     * 
     * @param entityType The type of entity to create
     * @param data       The entity data as a map
     * @return The created entity with generated ID
     */
    public Object createEntity(EntityType entityType, Map<String, Object> data) {
        log.info("Creating entity of type: {} with data keys: {}", entityType, data.keySet());

        // Check if entity type is supported
        Class<?> entityClass = ENTITY_CLASS_MAP.get(entityType);
        if (entityClass == null) {
            throw new UnsupportedOperationException("Entity creation not supported for type: " + entityType + 
                ". Add mapping in ENTITY_CLASS_MAP.");
        }

        // Generate auto-numbers for the entity
        Map<String, String> generatedAutoNumbers = autoNumberService.generateAllAutoNumbersForEntity(entityType);
        data.putAll(generatedAutoNumbers);
        log.info("Generated auto-numbers: {}", generatedAutoNumbers);

        try {
            // Create entity instance
            Object entity = entityClass.getDeclaredConstructor().newInstance();
            
            // Map data to entity fields using reflection
            mapDataToEntity(entity, data, entityClass);
            
            // Save entity using appropriate repository
            Object savedEntity = saveEntity(entityType, entity);
            
            log.info("Successfully created {} entity", entityType);
            return savedEntity;
            
        } catch (Exception e) {
            log.error("Failed to create entity of type {}: {}", entityType, e.getMessage(), e);
            throw new RuntimeException("Failed to create entity: " + e.getMessage(), e);
        }
    }

    /**
     * Map data from Map to entity using reflection.
     * Automatically handles type conversions for common types.
     */
    private void mapDataToEntity(Object entity, Map<String, Object> data, Class<?> entityClass) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            
            if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                continue; // Skip null or empty values
            }
            
            try {
                // Find setter method
                String setterName = "set" + capitalize(fieldName);
                Method setter = findSetterMethod(entityClass, setterName);
                
                if (setter != null) {
                    Class<?> paramType = setter.getParameterTypes()[0];
                    Object convertedValue = convertValue(value, paramType);
                    
                    if (convertedValue != null) {
                        setter.invoke(entity, convertedValue);
                        log.debug("Set field {} = {}", fieldName, convertedValue);
                    }
                } else {
                    log.debug("No setter found for field: {}", fieldName);
                }
            } catch (Exception e) {
                log.warn("Could not set field {}: {}", fieldName, e.getMessage());
            }
        }
    }

    /**
     * Find setter method in class hierarchy
     */
    private Method findSetterMethod(Class<?> clazz, String methodName) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            for (Method method : currentClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
                    method.setAccessible(true);
                    return method;
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return null;
    }

    /**
     * Convert value to target type
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        
        String strValue = value.toString();
        
        try {
            // String
            if (targetType == String.class) {
                return strValue;
            }
            
            // Integer/int
            if (targetType == Integer.class || targetType == int.class) {
                return Integer.parseInt(strValue);
            }
            
            // Long/long
            if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(strValue);
            }
            
            // Double/double
            if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(strValue);
            }
            
            // Float/float
            if (targetType == Float.class || targetType == float.class) {
                return Float.parseFloat(strValue);
            }
            
            // Boolean/boolean
            if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.parseBoolean(strValue);
            }
            
            // LocalDate
            if (targetType == LocalDate.class) {
                return parseDate(strValue);
            }
            
            // LocalDateTime
            if (targetType == LocalDateTime.class) {
                return parseDateTime(strValue);
            }
            
            // Enum
            if (targetType.isEnum()) {
                return parseEnum(strValue, targetType);
            }
            
            // If already correct type, return as is
            if (targetType.isInstance(value)) {
                return value;
            }
            
        } catch (Exception e) {
            log.warn("Could not convert value '{}' to type {}: {}", strValue, targetType.getSimpleName(), e.getMessage());
        }
        
        return null;
    }

    /**
     * Parse date string to LocalDate
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        
        // Try ISO format first
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            // Try alternative formats
            String[] formats = {"MM/dd/yyyy", "dd/MM/yyyy", "yyyy/MM/dd", "dd-MM-yyyy"};
            for (String format : formats) {
                try {
                    return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
                } catch (DateTimeParseException ex) {
                    // Continue trying
                }
            }
        }
        log.warn("Could not parse date: {}", dateStr);
        return null;
    }

    /**
     * Parse datetime string to LocalDateTime
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (DateTimeParseException e) {
            // Try parsing as date and convert to datetime
            LocalDate date = parseDate(dateTimeStr);
            if (date != null) {
                return date.atStartOfDay();
            }
        }
        log.warn("Could not parse datetime: {}", dateTimeStr);
        return null;
    }

    /**
     * Parse string to enum value
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object parseEnum(String value, Class<?> enumType) {
        if (value == null || value.isEmpty()) return null;
        
        // Normalize: uppercase, replace spaces/hyphens with underscores
        String normalized = value.toUpperCase()
                                 .replace(" ", "_")
                                 .replace("-", "_");
        
        try {
            return Enum.valueOf((Class<Enum>) enumType, normalized);
        } catch (IllegalArgumentException e) {
            // Try to find a close match
            for (Object constant : enumType.getEnumConstants()) {
                if (constant.toString().equalsIgnoreCase(value) || 
                    constant.toString().equalsIgnoreCase(normalized)) {
                    return constant;
                }
            }
            log.warn("Unknown enum value '{}' for type {}", value, enumType.getSimpleName());
            return null;
        }
    }

    /**
     * Save entity using the appropriate repository
     */
    @SuppressWarnings("unchecked")
    private Object saveEntity(EntityType entityType, Object entity) {
        String repoName = REPOSITORY_NAME_MAP.get(entityType);
        
        try {
            JpaRepository<Object, Long> repository = 
                (JpaRepository<Object, Long>) applicationContext.getBean(repoName);
            return repository.save(entity);
        } catch (Exception e) {
            log.error("Failed to find or use repository '{}': {}", repoName, e.getMessage());
            throw new RuntimeException("Repository not found for entity type: " + entityType + 
                ". Ensure " + repoName + " exists.", e);
        }
    }

    /**
     * Capitalize first letter of string
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Check if entity type is supported for creation
     */
    public boolean isEntityTypeSupported(EntityType entityType) {
        return ENTITY_CLASS_MAP.containsKey(entityType);
    }

    /**
     * Get list of all supported entity types
     */
    public java.util.Set<EntityType> getSupportedEntityTypes() {
        return ENTITY_CLASS_MAP.keySet();
    }
}
