package com.example.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Get absolute path to uploads/images directory
        String uploadsPath = Paths.get(System.getProperty("user.dir"), "uploads", "images").toAbsolutePath().toUri().toString();
        
        // Serve images from uploads folder with absolute path
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations(uploadsPath)
                .setCachePeriod(31536000)  // 1 year cache
                .resourceChain(true)
                .addResolver(new org.springframework.web.servlet.resource.PathResourceResolver());
        
        // Also serve static resources from classpath
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/public/images/")
                .setCachePeriod(31536000);
    }
}

