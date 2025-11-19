package krs.erp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Angular dist resources - serve root-level Angular files (highest priority)
        // These must be served BEFORE the SpaController fallback
        registry.addResourceHandler("/main-*.js", "/polyfills-*.js", "/styles-*.css", 
                "/favicon.ico", "/*.map")
                .addResourceLocations("classpath:/static/angular/dist/erp-frontend/browser/")
                .setCachePeriod(3600); // Cache for 1 hour
        
        // Angular vendor libraries
        registry.addResourceHandler("/vendor/**")
                .addResourceLocations("classpath:/static/angular/dist/erp-frontend/browser/vendor/");
        
        // Angular assets
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/angular/dist/erp-frontend/browser/assets/");
        
        // Static resources
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
        
        registry.addResourceHandler("/templates/**")
                .addResourceLocations("classpath:/static/templates/");
                
        // Webjars resources
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // No view controllers - let resource handlers and REST controllers handle it
    }
}