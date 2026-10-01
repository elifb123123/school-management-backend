package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    // Spring MVC'nin WebMvcConfigurer.addCorsMappings'i yerine bir bean olarak tanımlıyoruz,
    // çünkü Spring Security'nin kendi filter zinciri (SecurityConfig) buna ihtiyaç duyuyor —
    // .cors(cors -> cors.configurationSource(...)) ile bu bean'i kullanıyor. WebMvcConfigurer
    // tabanlı config, Security devredeyken preflight (OPTIONS) isteklerini etkilemiyordu.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
