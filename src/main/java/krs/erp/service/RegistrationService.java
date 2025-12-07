package krs.erp.service;

import krs.erp.config.multitenant.TenantContext;
import krs.erp.dto.RegistrationRequest;
import krs.erp.model.Organization;
import krs.erp.model.User;
import krs.erp.model.User.UserType;
import krs.erp.repository.OrganizationRepository;
import krs.erp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrationService {

    @Autowired
    private TenantProvisioningService tenantProvisioningService;

    @Autowired
    private DataSource masterDataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // These repositories will work on the tenant DB once context is set
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    public void registerTenant(RegistrationRequest request) {
        // 1. Generate Tenant ID and DB Name
        // Generate a random Long tenant ID (using current time + random digits)
        Long tenantId = System.currentTimeMillis() + (long) (Math.random() * 100000);
        String dbName = "erpdb" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        // 2. Create Tenant in Master DB
        createTenantInMasterDb(request.getOrganizationName(), dbName, tenantId);

        // 3. Provision Tenant Database
        tenantProvisioningService.provisionTenantDatabase(dbName);

        // 4. Switch Context to New Tenant
        TenantContext.setCurrentTenant(tenantId.toString());

        // 5. Initialize Tenant Data (Organization & Admin User)
        Long organizationId = null;
        try {
            organizationId = initializeTenantData(request);
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
        String sql = "INSERT INTO IAM_MasterDB.iam_users (username, password_hash, email, first_name, last_name, phone, user_type, enabled, tenant_id, organization_id, created_time, is_active) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";

        jdbcTemplate.update(sql,
                request.getAdminEmail(), // username is email
                passwordEncoder.encode(request.getAdminPassword()),
                request.getAdminEmail(),
                request.getAdminFirstName(),
                request.getAdminLastName(),
                request.getAdminPhone(),
                UserType.ADMIN.name(),
                true,
                tenantId,
                organizationId,
                LocalDateTime.now());
    }

    @Transactional
    protected Long initializeTenantData(RegistrationRequest request) {
        System.out.println("Initializing tenant data for organization: " + request.getOrganizationName());

        // Create Organization
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

        userRepository.save(admin);

        return org.getId();
    }
}
