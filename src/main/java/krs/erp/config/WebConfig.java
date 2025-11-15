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
        // Serve all files from Angular dist directory
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/angular/dist/erp-frontend/browser/")
                .setCachePeriod(0);
    }

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // Redirect root path to index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}