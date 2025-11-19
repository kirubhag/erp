package krs.erp.config;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LogDirectoryInitializerTest {

    private static Path baseTempDir;
    private static Path nestedPath;

    @BeforeAll
    static void setup() throws Exception {
        baseTempDir = Files.createTempDirectory("erp-logs-test");
        nestedPath = baseTempDir.resolve("nested/logs");
        // set system property before context initialization so initializer uses it
        System.setProperty("app.logging.path", nestedPath.toString());
    }

    @AfterAll
    static void cleanup() throws Exception {
        // attempt to clean up temporary files
        try {
            if (Files.exists(baseTempDir)) {
                Files.walk(baseTempDir)
                        .sorted((a, b) -> b.compareTo(a)) // delete children first
                        .forEach(p -> p.toFile().delete());
            }
        } catch (Exception ignored) {
        }
    }

    @Test
    void initializerCreatesLogDirectory() {
        // The LogDirectoryInitializer should have created the nested path during context refresh
        assertTrue(Files.exists(nestedPath), "Log directory should exist: " + nestedPath);
    }
}
