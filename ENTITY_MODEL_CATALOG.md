# ERP Entity Model Catalog

**Total Entity Models: 140** (excluding enums)
**Last Updated:** 30 December 2025

---

## Model Organization

The entity models are organized into **15 functional modules** plus **42 core/shared entities**.

### Module Distribution

| Module | Count | Purpose |
|--------|-------|---------|
| **Finance** | 23 | Financial accounting, fees, scholarships, fines |
| **LMS (Learning Management)** | 18 | Online learning, quizzes, assignments, virtual classes |
| **HR (Human Resources)** | 12 | Recruitment, payroll, leave, performance management |
| **Library** | 9 | Library resources, loans, purchases, policies |
| **TPD (Teacher Professional Dev)** | 8 | Professional development, training, competencies |
| **Communication** | 6 | Announcements, messages, notifications, support |
| **Admission** | 5 | Student admission process, applications, inquiries |
| **Academic** | 4 | Academic settings, grading, terms, years |
| **Inventory** | 4 | Assets, consumables, vendors, purchase orders |
| **Maintenance** | 3 | Facilities, bookings, work orders |
| **Calendar** | 2 | Institutional events, calendar days |
| **Alumni** | 2 | Alumni tracking, contributions |
| **Reporting** | 1 | MIS reports |
| **Documents** | 1 | Document management |
| **Core/Shared** | 42 | Student, staff, users, permissions, etc. |

---

## 1. Core/Shared Entities (42)

**Student Management:**
- Student
- StudentGuardianInfo
- StudentMedicalInfo
- Parent
- ParentStudentRelation

**Staff & User Management:**
- Staff
- User
- Role
- Permission
- LoginHistory

**Organization & Settings:**
- Organization
- OrganizationSettings
- ErpTenant
- UserSettings

**Academic Core:**
- Course
- Subject
- Grade
- Exam
- ErpClass
- Attendance
- Timetable
- Room

**System Metadata:**
- ErpEntity
- ErpField
- ErpSection
- ErpEntityRelation
- ErpEntityRelationMapping
- ErpEntityRoleRelation
- CustomView
- FieldType

**Communication & Email:**
- EmailTemplate
- EmailLog

**Common Services:**
- Address
- HealthRecord
- RecycleBin
- ImportHistory
- ErpAttachment

**Subscription & Billing:**
- PricingPlan
- UserSubscription
- SubscriptionHistory
- PaymentTransaction

---

## 2. Academic Module (4)

**Purpose:** Manage academic calendar, grading systems, and institutional settings

| Entity | Description |
|--------|-------------|
| AcademicSettings | Institution-wide academic policies and configurations |
| AcademicYear | Academic year definitions and active year tracking |
| GradingScale | Grading schemes and grade boundaries |
| Term | Academic terms/semesters within academic years |

---

## 3. Admission Module (5)

**Purpose:** Student admission and enrollment process management

| Entity | Description |
|--------|-------------|
| AdmissionApplication | Complete student admission applications |
| AdmissionCycle | Admission cycles and enrollment periods |
| AdmissionInquiry | Initial inquiry tracking before applications |
| AdmissionSeatAllocation | Seat allocation and class assignments |
| StudentRegistration | Final student registration and enrollment |

---

## 4. Alumni Module (2)

**Purpose:** Alumni relationship management and engagement

| Entity | Description |
|--------|-------------|
| Alumni | Alumni profile and career tracking |
| AlumniContribution | Alumni donations and contributions |

---

## 5. Calendar Module (2)

**Purpose:** Institutional calendar and event management

| Entity | Description |
|--------|-------------|
| CalendarDay | Individual calendar days with special markings |
| InstitutionEvent | Events, holidays, and important dates |

---

## 6. Communication Module (6)

**Purpose:** Internal communication and notification system

| Entity | Description |
|--------|-------------|
| Announcement | Institution-wide announcements and bulletins |
| Message | Direct messaging between users |
| NotificationLog | Notification delivery history and tracking |
| NotificationTemplate | Pre-defined notification templates |
| SupportTicket | Help desk and support ticket management |
| TicketComment | Comments and updates on support tickets |

---

## 7. Documents Module (1)

**Purpose:** Centralized document management

| Entity | Description |
|--------|-------------|
| ErpDocument | Document storage and metadata management |

---

## 8. Finance Module (23)

**Purpose:** Complete financial management including accounting, fees, scholarships, and fines

### Accounting & Banking (8)
- ChartOfAccount - Chart of accounts structure
- JournalEntry - General ledger journal entries
- JournalItem - Individual line items in journal entries
- AccountingPeriod - Fiscal periods for financial reporting
- Transaction - Financial transactions tracking
- BankStatement - Bank statement reconciliation
- BankStatementLine - Individual bank statement lines
- Invoice - Invoice generation and tracking
- InvoiceItem - Line items in invoices

### Budget Management (2)
- Budget - Budget planning and allocation
- BudgetLine - Individual budget line items

### Fee Management (3)
- FeeStructure - Fee structures for different programs
- FeeType - Types of fees (tuition, lab, sports, etc.)
- FeePayment - Fee payment records
- FeeDiscountRule - Fee discount rules and eligibility

### Scholarship Management (3)
- ScholarshipCategory - Scholarship types and categories
- ScholarshipApplication - Student scholarship applications
- ScholarshipDisbursement - Scholarship payment disbursements

### Fine & Discipline (5)
- FineCategory - Categories of fines (late, damage, etc.)
- FineConfiguration - Fine rules and calculations
- StudentFineLedger - Student fine tracking ledger
- FineWaiverRequest - Fine waiver requests and approvals
- DisciplinaryIncident - Disciplinary incidents and actions

---

## 9. HR (Human Resources) Module (12)

**Purpose:** Complete HR lifecycle from recruitment to performance management

### Recruitment (2)
- JobPosting - Job vacancy postings
- JobApplication - Candidate applications

### Leave Management (3)
- LeaveType - Leave types (sick, vacation, etc.)
- LeaveRequest - Leave applications
- LeaveBalance - Leave balance tracking per employee

### Payroll (3)
- PayrollRun - Payroll processing cycles
- Payslip - Individual employee payslips
- StaffSalary - Salary structure and components

### Performance Management (4)
- PerformanceCycle - Performance review cycles
- PerformanceCriteria - Performance evaluation criteria
- PerformanceReview - Employee performance reviews
- PerformanceReviewDetail - Detailed performance assessments

---

## 10. Inventory Module (4)

**Purpose:** Asset and inventory management

| Entity | Description |
|--------|-------------|
| Asset | Fixed assets and equipment tracking |
| Consumable | Consumable items and stock management |
| Vendor | Vendor/supplier information |
| PurchaseOrder | Purchase orders for inventory |

---

## 11. Library Module (9)

**Purpose:** Library resource management and circulation

### Resource Management (4)
- LibraryResource - Library resources (books, journals, etc.)
- ResourceItem - Individual physical copies of resources
- Author - Author information
- Publisher - Publisher information

### Circulation (2)
- LibraryLoan - Book loans to members
- LibraryHold - Hold requests for resources

### Procurement (2)
- LibraryPO - Library purchase orders
- LibraryPurchaseRequest - Resource purchase requests

### Administration (1)
- LibraryPolicy - Library policies and rules

---

## 12. LMS (Learning Management System) Module (18)

**Purpose:** Online learning platform with courses, assessments, and virtual classes

### Course Structure (3)
- LmsModule - Learning modules within courses
- LmsTopic - Topics within modules
- Lesson - Individual lessons/content units

### Content & Materials (1)
- LmsContent - Learning content (videos, PDFs, etc.)

### Assessments (5)
- LmsQuiz - Quizzes and tests
- LmsQuestionBank - Question repository
- LmsQuestion - Individual quiz questions
- LmsAnswer - Student answers
- LmsRubric - Grading rubrics

### Assignments & Submissions (2)
- LmsSubmission - Student assignment submissions
- LmsPeerReview - Peer review of submissions

### Virtual Classes (2)
- VirtualClassSession - Live/recorded class sessions
- VirtualAttendanceRecord - Virtual class attendance

### Gamification (2)
- LmsBadge - Achievement badges
- LmsPointLog - Points and rewards tracking

### Collaboration (2)
- LmsForum - Discussion forums
- LmsForumPost - Forum posts and replies

### Progress Tracking (1)
- LmsStudentProgress - Student progress tracking

---

## 13. Maintenance Module (3)

**Purpose:** Facility and maintenance management

| Entity | Description |
|--------|-------------|
| Facility | Buildings and facility information |
| FacilityBooking | Facility reservation and booking |
| WorkOrder | Maintenance work orders and tracking |

---

## 14. Reporting Module (1)

**Purpose:** Management Information System reports

| Entity | Description |
|--------|-------------|
| MISReport | Pre-configured MIS reports and dashboards |

---

## 15. TPD (Teacher Professional Development) Module (8)

**Purpose:** Teacher training and professional development tracking

### Competency & Skills (2)
- Competency - Teaching competencies framework
- SkillAssessment - Teacher skill assessments

### Training & Events (3)
- TrainingEvent - Professional development events
- TrainingAttendance - Attendance at training sessions
- TrainingEvaluation - Training feedback and evaluation

### Professional Growth (3)
- ProfessionalPortfolio - Teacher professional portfolios
- Evidence - Evidence of professional development
- CpdLedger - Continuing Professional Development (CPD) credits

---

## Model Grouping Strategy

### Current Organization ✅

**Advantages:**
1. **Domain-Driven Design**: Models are grouped by business domain
2. **Clear Separation**: Each module has distinct responsibility
3. **Scalability**: Easy to add new entities to existing modules
4. **Team Collaboration**: Different teams can work on different modules
5. **Microservices Ready**: Can split into microservices if needed

### Suggested Enhancements

**1. Create Model Group Enums/Categories:**
```java
public enum ModelGroup {
    CORE("Core System"),
    ACADEMIC("Academic Management"),
    STUDENT_LIFECYCLE("Student Lifecycle"),  // Admission + Alumni
    FINANCE("Financial Management"),
    HR("Human Resources"),
    LEARNING("Learning & Teaching"),  // LMS + TPD
    OPERATIONS("Operations"),  // Inventory + Maintenance
    LIBRARY("Library Management"),
    COMMUNICATION("Communication & Collaboration"),
    ADMINISTRATION("Administration");  // Calendar + Documents + Reporting
}
```

**2. Add @ModelGroup Annotation:**
```java
@Entity
@ModelGroup(ModelGroup.HR)
@Table(name = "erp_hr_leave_requests")
public class LeaveRequest extends BaseEntity {
    // ...
}
```

**3. Consolidated Grouping:**

- **Core System** (42): Student, Staff, User, Organization, Permissions
- **Academic Management** (4): Academic settings, grading, terms
- **Student Lifecycle** (7): Admission (5) + Alumni (2)
- **Financial Management** (23): All finance entities
- **Human Resources** (12): All HR entities
- **Learning & Teaching** (26): LMS (18) + TPD (8)
- **Operations** (7): Inventory (4) + Maintenance (3)
- **Library Management** (9): All library entities
- **Communication & Collaboration** (6): All communication entities
- **Administration** (4): Calendar (2) + Documents (1) + Reporting (1)

---

## Key Statistics

- **Total Entities:** 140
- **Largest Module:** Finance (23 entities)
- **Second Largest:** LMS (18 entities)
- **Average per Module:** ~9 entities
- **Core/Shared:** 42 entities (30% of total)
- **Specialized Modules:** 98 entities (70% of total)

---

## Usage Recommendations

### For Development:
1. Use package structure for model grouping
2. Follow naming convention: `{Module}{EntityName}.java`
3. Place shared/common entities in root model package
4. Module-specific entities in subpackages

### For Database Schema:
1. Prefix table names with module code: `erp_hr_`, `erp_finance_`, `erp_lms_`
2. Core tables without prefix: `students`, `staff`, `users`
3. Consider separate schemas per module for large deployments

### For API Design:
1. Group API endpoints by module: `/api/hr/*`, `/api/finance/*`
2. Use consistent entity naming across API and models
3. Implement module-level access control

---

## Future Considerations

**Potential New Modules:**
- **Transport Management**: Bus routes, tracking, maintenance
- **Cafeteria Management**: Menu, orders, inventory
- **Medical/Health Services**: Clinic, medical records, appointments
- **Sports Management**: Teams, fixtures, achievements
- **Hostel Management**: Rooms, allocation, billing
- **Examination System**: Exam schedules, seating, results (beyond basic Exam entity)

**Refactoring Opportunities:**
- Split Finance module into sub-modules (Accounting, Fees, Scholarships)
- Extract Assessment from LMS to separate module
- Consolidate Student-related entities (Student, Guardian, Medical) into Student module
