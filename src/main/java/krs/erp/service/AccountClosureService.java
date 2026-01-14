package krs.erp.service;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountClosureService {

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Autowired
    private TenantProvisioningService tenantProvisioningService;

    /**
     * Closes the account for the given tenant.
     * 1. Checks/Adds closure_reason column.
     * 2. Deletes users from Master DB.
     * 3. Updates tenant status and reason.
     * 4. Deletes Tenant DB.
     */
    @Transactional
    public void closeAccount(String tenantIdStr, String reason) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);

        // Parse tenantId to Long
        Long tenantId = Long.parseLong(tenantIdStr);

        // 0. Ensure closure_reason column exists
        ensureClosureReasonColumn(jdbcTemplate);

        // 1. Get Tenant DB Name
        String getDbNameSql = "SELECT db_name FROM erp_tenants WHERE tenant_id = ?";
        List<String> dbNames = jdbcTemplate.query(getDbNameSql, (rs, rowNum) -> rs.getString("db_name"), tenantId);

        if (dbNames.isEmpty()) {
            throw new RuntimeException("Tenant not found: " + tenantId);
        }
        String dbName = dbNames.get(0);

        // 2. Delete users from IAM_MasterDB
        String deleteUsersSql = "DELETE FROM erp_iam_users WHERE tenant_id = ?";
        jdbcTemplate.update(deleteUsersSql, tenantId);

        // 3. Update erp_tenants status and reason
        String updateTenantSql = "UPDATE erp_tenants SET status = 'Inactive', closure_reason = ? WHERE tenant_id = ?";
        jdbcTemplate.update(updateTenantSql, reason, tenantId);

        // 4. Delete Tenant Database
        tenantProvisioningService.deleteTenantDatabase(dbName);
    }

    private void ensureClosureReasonColumn(JdbcTemplate jdbcTemplate) {
        try {
            // Check if column exists
            jdbcTemplate.queryForList("SELECT closure_reason FROM erp_tenants LIMIT 1");
        } catch (Exception e) {
            // Column likely doesn't exist, try to add it
            try {
                jdbcTemplate.execute("ALTER TABLE erp_tenants ADD COLUMN closure_reason VARCHAR(255)");
            } catch (Exception ex) {
                // Log or ignore if it failed (e.g., race condition)
                System.err.println("Failed to add closure_reason column: " + ex.getMessage());
            }
        }
    }
}
