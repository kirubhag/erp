package krs.erp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

@Service
public class TenantProvisioningService {

    @Autowired
    private DataSource masterDataSource;

    public void provisionTenantDatabase(String dbName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);

        // 1. Create Database
        jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS " + dbName);

        // 2. Initialize Schema
        initializeTenantSchema(dbName);
    }

    private void initializeTenantSchema(String dbName) {
        // Create a temporary DataSource for the new tenant DB
        HikariDataSource tenantDataSource = new HikariDataSource();
        tenantDataSource.setJdbcUrl("jdbc:mysql://localhost:3307/" + dbName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        tenantDataSource.setUsername("root"); // In prod, use configured credentials
        tenantDataSource.setPassword(""); // In prod, use configured credentials
        tenantDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        try {
            Resource resource = new ClassPathResource("scripts/tenant_schema.sql");
            ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
            databasePopulator.execute(tenantDataSource);
        } finally {
            tenantDataSource.close();
        }
    }

    public void deleteTenantDatabase(String dbName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        jdbcTemplate.execute("DROP DATABASE IF EXISTS " + dbName);
    }
}
