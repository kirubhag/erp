package krs.erp.logging;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {"spring.flyway.enabled=false","server.port=8082"})
public class AccessLoggingIntegrationTest {

    static Path tmpLogDir;

    @BeforeAll
    static void setup() throws Exception {
        tmpLogDir = Files.createTempDirectory("erp-test-logs");
        System.setProperty("APP_LOG_PATH", tmpLogDir.toAbsolutePath().toString());
    }

    @AfterAll
    static void teardown() throws Exception {
        System.clearProperty("APP_LOG_PATH");
        // Leave logs for inspection
    }

    @Test
    void appWritesAccessLog() throws Exception {
        // Wait a short while for server to start and then hit the health endpoint
        Thread.sleep(2000);
        var code = new java.net.URL("http://localhost:8082/__healthcheck").openConnection();
        ((java.net.HttpURLConnection)code).setInstanceFollowRedirects(false);
        int status = ((java.net.HttpURLConnection)code).getResponseCode();

        // Check the access log file exists and contains a JSON line
        Path accessLog = tmpLogDir.resolve("access-log.log");
        assertThat(Files.exists(accessLog)).isTrue();
        List<String> lines = Files.readAllLines(accessLog);
        assertThat(lines).anyMatch(l -> l.contains("\"path\":\"/__healthcheck\""));
    }
}
