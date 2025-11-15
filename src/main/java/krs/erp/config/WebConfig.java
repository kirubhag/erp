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
        
        // Angular resources - serve from dist
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/angular/dist/erp-frontend/browser/");
        
        registry.addResourceHandler("/assets/**", "/dist/**", "/angular/**")
                .addResourceLocations("classpath:/static/angular/dist/erp-frontend/browser/");
    }

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // No view controllers - let resource handlers and REST controllers handle it
    }
}