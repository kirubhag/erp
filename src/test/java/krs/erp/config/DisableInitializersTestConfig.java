package krs.erp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.CommandLineRunner;

/**
 * Test configuration to disable CommandLineRunner beans during tests
 */
@TestConfiguration
public class DisableInitializersTestConfig {
    
    /**
     * Override all CommandLineRunner beans with a no-op implementation
     * This prevents data initializers from running during tests
     */
    @Bean
    @Primary
    public CommandLineRunner disableCommandLineRunners() {
        return args -> {
            // No-op: Do nothing during tests
        };
    }
}
