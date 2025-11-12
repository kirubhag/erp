package krs.erp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lightweight dedicated healthcheck endpoint for validating filters and logging.
 */
@RestController
public class HealthCheckController {

    @GetMapping("/__healthcheck")
    public ResponseEntity<String> check() {
        return ResponseEntity.ok("ok");
    }
}
