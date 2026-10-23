package com.flowdesk.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // allow all endpoints
                .allowedOrigins("*") // allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // allow all common HTTP methods
                .allowedHeaders("*"); // allow all headers
    }
}
