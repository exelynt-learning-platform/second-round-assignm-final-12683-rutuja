package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Apply CORS config to all application endpoints (mapping paths are evaluated relative to the
        // servlet context). Using "/**" ensures we cover endpoints under the application's context-path
        // (e.g. with server.servlet.context-path=/api).
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // Allow all origins (for development)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight response for 1 hour
    }
}
