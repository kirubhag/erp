package krs.erp.service;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.config.multitenant.TenantContext;
import krs.erp.dto.RegistrationRequest;
import krs.erp.enums.EntityType;
import krs.erp.model.Organization;
import krs.erp.model.User;
import krs.erp.model.User.UserType;
import krs.erp.repository.OrganizationRepository;
import krs.erp.repository.UserRepository;

@Service
public class RegistrationService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private TenantProvisioningService tenantProvisioningService;

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // These repositories will work on the tenant DB once context is set
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    public void registerTenant(RegistrationRequest request) {
        // 0. Check if email already exists in Master DB
        if (emailExistsInMasterDb(request.getAdminEmail())) {
            throw new IllegalArgumentException("Email address '" + request.getAdminEmail()
                    + "' is already registered. Please use a different email or login to your existing account.");
        }

        // 1. Generate Tenant ID and DB Name
        // Generate a random Long tenant ID (using current time + random digits)
        Long tenantId = System.currentTimeMillis() + (long) (Math.random() * 100000);
        String dbName = "erpdb" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        // 2. Create Tenant in Master DB
        createTenantInMasterDb(request.getOrganizationName(), dbName, tenantId);

        // 3. Provision Tenant Database with initial schema (without metadata copy)
        tenantProvisioningService.provisionTenantDatabase(dbName, tenantId, request.getOrganizationName());

        // 4. Switch Context to New Tenant
        TenantContext.setCurrentTenant(tenantId.toString());

        // 5. Initialize Tenant Data (Organization & Admin User) and copy metadata with user ID
        Long organizationId = null;
        try {
            organizationId = initializeTenantData(request, dbName);
        } finally {
            TenantContext.clear();
        }

        // 6. Create User in Master DB (Needs organization ID from tenant
        // initialization)
        // Note: Organization ID in Master DB might not match Tenant DB if we don't sync
        // them,
        // but for now we'll use the ID generated in the tenant DB.
        // Ideally, we should probably create the organization in Master DB too or have
        // a global Org ID.
        // For this implementation, we will use the ID returned from tenant DB.
        if (organizationId != null) {
            createUserInMasterDb(request, tenantId, organizationId);
        }
    }

    private void createTenantInMasterDb(String name, String dbName, Long tenantId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        String sql = "INSERT INTO erp_tenants (tenant_name, db_host, db_name, status, tenant_id) VALUES (?, ?, ?, 'Active', ?)";
        // Assuming db_host is localhost for now
        jdbcTemplate.update(sql, name, "localhost", dbName, tenantId);
    }

    private void createUserInMasterDb(RegistrationRequest request, Long tenantId, Long organizationId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        String sql = "INSERT INTO erp_iam_users (username, password_hash, email, first_name, last_name, phone, user_type, enabled, tenant_id, organization_id, created_time, is_active, account_non_expired, credentials_non_expired, account_non_locked, confirmation_token, is_primary_user) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, 1, 1, 1, ?, ?)";

        String confirmationToken = UUID.randomUUID().toString();

        jdbcTemplate.update(sql,
                request.getAdminEmail(), // username is email
                passwordEncoder.encode(request.getAdminPassword()),
                request.getAdminEmail(),
                request.getAdminFirstName(),
                request.getAdminLastName(),
                request.getAdminPhone(),
                UserType.ADMIN.name(),
                false, // Enabled is false until confirmed
                tenantId,
                organizationId,
                LocalDateTime.now(),
                confirmationToken,
                true); // is_primary_user = true for account creator

        // Send Confirmation Email
        sendConfirmationEmail(request.getAdminEmail(), confirmationToken);
    }

    private void sendConfirmationEmail(String toEmail, String token) {
        String subject = "Confirm your ERP Account";
        // Assuming frontend is running on standard port 4200, or use a configured
        // property
        String confirmationLink = "http://localhost:4200/confirm-account?token=" + token;

        String content = "Welcome to ERP System!\n\n"
                + "Please confirm your account by clicking the link below:\n"
                + confirmationLink + "\n\n"
                + "If you did not request this, please ignore this email.";

        emailService.sendDirectEmail(EntityType.GENERAL, 0L, toEmail, null, subject, content, "SYSTEM");
    }

    @Transactional
    protected Long initializeTenantData(RegistrationRequest request, String dbName) {
        System.out.println("Initializing tenant data for organization: " + request.getOrganizationName());

        // Create Organization first
        Organization org = new Organization();
        org.setName(request.getOrganizationName());
        org.setType("School"); // Default type
        org.setEmail(request.getAdminEmail());
        org.setPhone(request.getAdminPhone());
        org.setCreatedTime(LocalDateTime.now());
        org.setIsActive(1);
        organizationRepository.save(org);

        System.out.println("Organization saved with ID: " + org.getId() + ", Name: " + org.getName());

        // Create Admin User
        User admin = new User();
        admin.setUsername(request.getAdminEmail()); // Username is email
        admin.setEmail(request.getAdminEmail());
        admin.setPasswordHash(passwordEncoder.encode(request.getAdminPassword()));
        admin.setFirstName(request.getAdminFirstName());
        admin.setLastName(request.getAdminLastName());
        admin.setPhone(request.getAdminPhone());
        admin.setUserType(UserType.ADMIN);
        admin.setEnabled(true);
        admin.setCreatedTime(LocalDateTime.now());
        admin.setOrganizationId(org.getId());
        admin.setIsPrimaryUser(true); // Mark as primary user (account creator)
        admin.setEnabled(true); // Tenant DB copy can stay enabled, Master DB is gatekeeper.
        admin.setCreatedTime(LocalDateTime.now());

        userRepository.save(admin);

        System.out.println("Admin user saved with ID: " + admin.getId());

        // Update created_by to admin's own ID (self-referencing creator)
        admin.setCreatedBy(admin.getId());
        userRepository.save(admin);

        // Update organization created_by to admin's ID
        org.setCreatedBy(admin.getId());
        organizationRepository.save(org);

        // Now copy system metadata from master DB to tenant DB with the admin user as creator
        try {
            tenantProvisioningService.copySystemDataWithUserId(dbName, admin.getId());
            System.out.println("✓ System metadata copied with created_by = " + admin.getId());
        } catch (Exception e) {
            System.err.println("⚠ Failed to copy system metadata: " + e.getMessage());
            // Don't fail registration if metadata copy fails - basic functionality still works
        }

        // Create Trial Subscription for 15 days with Enterprise Edition
        try {
            subscriptionService.createTrialSubscription(org);
            System.out.println("✓ Trial subscription created for organization: " + org.getName());
        } catch (Exception e) {
            System.err.println("⚠ Failed to create trial subscription: " + e.getMessage());
            // Don't fail registration if subscription creation fails
        }

        return org.getId();
    }

    private boolean emailExistsInMasterDb(String email) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        String sql = "SELECT COUNT(*) FROM erp_iam_users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}
