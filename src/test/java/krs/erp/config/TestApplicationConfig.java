package krs.erp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test configuration that excludes CommandLineRunner beans
 * This prevents data initializers from running during repository tests
 */
@SpringBootApplication
@EntityScan({"krs.erp.model", "krs.erp.entity"})
@EnableJpaRepositories("krs.erp.repository")
@ComponentScan(basePackages = "krs.erp", 
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CommandLineRunner.class)
    })
public class TestApplicationConfig {
    // Minimal configuration for repository tests
}
