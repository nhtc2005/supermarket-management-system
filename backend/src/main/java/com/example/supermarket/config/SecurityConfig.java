package com.example.supermarket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/auth/login", "/auth/register", "/auth/refresh").permitAll()
                        .requestMatchers("/api/v1/swagger-ui/**", "/api/v1/v3/api-docs/**", "/swagger-ui/**", "/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Customer endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/customers/me").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/customers/me").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/customers/me").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/my-orders").hasRole("CUSTOMER")

                        // Customer management (Manager & Admin only)
                        .requestMatchers("/api/v1/customers/**").hasAnyRole("MANAGER", "SALES")

                        // Product endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").hasAnyRole("CUSTOMER", "SALES", "WAREHOUSE", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasAnyRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasAnyRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasAnyRole("MANAGER")

                        // Order endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasAnyRole("CUSTOMER", "SALES")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAnyRole("SALES", "WAREHOUSE", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/orders/**").hasAnyRole("SALES", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasAnyRole("MANAGER")

                        // Warehouse endpoints
                        .requestMatchers("/api/v1/warehouses/**").hasAnyRole("WAREHOUSE", "MANAGER")
                        .requestMatchers("/api/v1/batches/**").hasAnyRole("WAREHOUSE", "MANAGER")
                        .requestMatchers("/api/v1/warehouse/imports/**").hasAnyRole("WAREHOUSE", "MANAGER")
                        .requestMatchers("/api/v1/warehouse/exports/**").hasAnyRole("WAREHOUSE", "MANAGER")

                        // Employee management (Manager only)
                        .requestMatchers("/api/v1/employees/**").hasRole("MANAGER")
                        .requestMatchers("/api/v1/sales-employees/**").hasRole("MANAGER")
                        .requestMatchers("/api/v1/warehouse-employees/**").hasRole("MANAGER")

                        // Store endpoints
                        .requestMatchers("/api/v1/stores/**").hasAnyRole("SALES", "MANAGER")

                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Add your frontend URLs
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
