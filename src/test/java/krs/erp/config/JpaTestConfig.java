package krs.erp.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test configuration to ensure entity scanning works properly for repository tests
 */
@TestConfiguration
@EntityScan({"krs.erp.model", "krs.erp.entity"})
@EnableJpaRepositories("krs.erp.repository")
public class JpaTestConfig {
    // Configuration class to ensure all entities and repositories are scanned during tests
}
