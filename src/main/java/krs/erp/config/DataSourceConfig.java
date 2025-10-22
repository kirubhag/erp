package krs.erp.config;

import org.springframework.context.annotation.Configuration;

/**
 * DataSource configuration for MySQL 9.x.x database
 * Configuration is now loaded from application.properties
 * Spring Boot auto-configures HikariCP based on properties
 */
@Configuration
public class DataSourceConfig {
    
    // DataSource configuration is now handled by Spring Boot auto-configuration
    // All database settings are defined in application.properties
    // This class remains for any future custom database configurations
}