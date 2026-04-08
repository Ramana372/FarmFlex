package com.example.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Serve images from uploads folder (takes priority)
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:uploads/images/");
        
        // Also serve images from public folder
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/public/images/");
    }
}
