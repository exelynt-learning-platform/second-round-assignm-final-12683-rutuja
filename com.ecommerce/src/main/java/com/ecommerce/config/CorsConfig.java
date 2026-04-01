package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS configuration - restricted for production
        // In production, replace allowedOrigins with specific frontend domain(s)
        // Example: .allowedOrigins("https://yourdomain.com", "https://www.yourdomain.com")
        
        registry.addMapping("/**")
                // Development: allows localhost. Production: restrict to specific domains
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:3001",
                        "http://localhost:8080"
                        // Add production frontend URL here in application-prod.properties
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight response for 1 hour
    }
}
