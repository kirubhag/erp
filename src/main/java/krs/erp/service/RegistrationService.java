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
        String tenantId = UUID.randomUUID().toString();
        String dbName = "erp_tenant_" + request.getOrganizationName().replaceAll("\\s+", "_").toLowerCase() + "_"
                + System.currentTimeMillis();

        // 2. Create Tenant in Master DB
        createTenantInMasterDb(request.getOrganizationName(), dbName, tenantId);

        // 3. Provision Tenant Database
        tenantProvisioningService.provisionTenantDatabase(dbName);

        // 4. Switch Context to New Tenant
        TenantContext.setCurrentTenant(tenantId);

        // 5. Initialize Tenant Data (Organization & Admin User)
        try {
            initializeTenantData(request);
        } finally {
            TenantContext.clear();
        }
    }

    private void createTenantInMasterDb(String name, String dbName, String tenantId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        String sql = "INSERT INTO erp_tenants (tenant_name, db_host, db_name, status, tenant_id) VALUES (?, ?, ?, 'Active', ?)";
        // Assuming db_host is localhost for now
        jdbcTemplate.update(sql, name, "localhost", dbName, tenantId);
    }

    @Transactional
    protected void initializeTenantData(RegistrationRequest request) {
        // Create Organization
        Organization org = new Organization();
        org.setName(request.getOrganizationName());
        org.setType("School"); // Default type
        org.setEmail(request.getAdminEmail());
        org.setPhone(request.getAdminPhone());
        org.setCreatedTime(LocalDateTime.now());
        org.setIsActive(1);
        organizationRepository.save(org);

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
    }
}
