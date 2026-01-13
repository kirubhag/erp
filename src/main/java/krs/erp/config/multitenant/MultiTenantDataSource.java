package krs.erp.config.multitenant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.NonNull;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Dynamic DataSource that routes to the correct tenant database.
 * If the tenant DataSource is not cached, it queries the IAM Master DB to
 * create it.
 */
public class MultiTenantDataSource extends AbstractRoutingDataSource {

    private final Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();
    private final DataSource masterDataSource;

    public MultiTenantDataSource(DataSource masterDataSource) {
        this.masterDataSource = masterDataSource;
        // Set the master DB as the default target
        this.setDefaultTargetDataSource(masterDataSource);
        this.setTargetDataSources(targetDataSources);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }

    @Override
    @NonNull
    protected DataSource determineTargetDataSource() {
        String tenantId = (String) determineCurrentLookupKey();

        // If no tenant context, use default (master)
        if (tenantId == null) {
            return masterDataSource;
        }

        // Check if we already have a DataSource for this tenant
        if (targetDataSources.containsKey(tenantId)) {
            return (DataSource) targetDataSources.get(tenantId);
        }

        // If not, create one dynamically
        DataSource tenantDataSource = createTenantDataSource(tenantId);
        if (tenantDataSource != null) {
            targetDataSources.put(tenantId, tenantDataSource);
            return tenantDataSource;
        }

        // Fallback to master if tenant not found (or handle error)
        return masterDataSource;
    }

    private DataSource createTenantDataSource(String tenantId) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
            String sql = "SELECT db_host, db_name FROM erp_tenants WHERE tenant_id = ? AND status = 'Active'";

            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                String dbHost = rs.getString("db_host");
                String dbName = rs.getString("db_name");

                HikariDataSource ds = new HikariDataSource();
                ds.setJdbcUrl("jdbc:mysql://" + dbHost + ":3307/" + dbName
                        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                ds.setUsername("root"); // In prod, fetch from vault or encrypted column
                ds.setPassword(""); // In prod, fetch from vault or encrypted column
                ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
                ds.setMaximumPoolSize(5); // Conservative pool size per tenant
                return ds;
            }, tenantId);
        } catch (Exception e) {
            // Log error and return null
            System.err.println("Failed to load datasource for tenant: " + tenantId + " Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() {
        // Call parent to properly initialize the routing mechanism
        super.afterPropertiesSet();
    }
}
