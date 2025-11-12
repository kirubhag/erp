package krs.erp.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Ensures the log directory exists at application startup.
 * The directory is configured via the environment variable `APP_LOG_PATH`.
 * If not set, the default is `/var/log/erp`.
 */
@Component
public class LogDirectoryInitializer implements ApplicationListener<ContextRefreshedEvent> {

    // Resolution: environment variable `APP_LOG_PATH` -> fallback `/var/log/erp`
    @Value("${APP_LOG_PATH:/var/log/erp}")
    private String logPath;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        try {
            Path path = Paths.get(logPath).toAbsolutePath().normalize();
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            // If directory creation fails, print to stderr; logging may not be initialized yet.
            System.err.println("Unable to create log directory '" + logPath + "': " + e.getMessage());
        }
    }
}
