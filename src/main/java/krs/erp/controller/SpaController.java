package krs.erp.controller;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for serving Single Page Application (Angular)
 * Serves index.html for root path and SPA routes
 */
@RestController
public class SpaController {
    
    /**
     * Serve index.html for root path
     */
    @GetMapping("/")
    public ResponseEntity<byte[]> index() throws IOException {
        return serveIndexHtml();
    }
    
    /**
     * Serve index.html for Angular SPA routes
     * This handles any route that doesn't match API or static files
     */
    @GetMapping("/{path:^(?!api|actuator|webjars|css|js|images|fonts|vendor|dist|angular|assets).*}")
    public ResponseEntity<byte[]> spaRoute() throws IOException {
        return serveIndexHtml();
    }
    
    private ResponseEntity<byte[]> serveIndexHtml() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/angular/dist/erp-frontend/browser/index.html");
        
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        byte[] content = resource.getInputStream().readAllBytes();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setPragma("no-cache");
        headers.setExpires(0);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }
}
