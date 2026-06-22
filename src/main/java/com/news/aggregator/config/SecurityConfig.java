package com.news.aggregator.config;

import com.news.aggregator.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final RateLimitingFilter rateLimitingFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF disable kar rahe hain kyunki Stateless REST APIs (JWT) ko iski zaroorat nahi hoti
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Routing Rulebook (Yahan decide ho raha hai kaun public hai kaun nahi)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Login/Register routes are completely open
                        .anyRequest().authenticated()               // Baki saare endpoints secure hain
                )

                // 3. Making Session State Stateless (No cookies on server side)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Injecting our Custom Filter into Spring Security Chain
                // Custom filter ko core system authentication filter se pehle chala rahe hain
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitingFilter, JwtAuthFilter.class); // <-- JWT validation ke just baad check karo rate-limit

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // First Principle: Password hashing via BCrypt (Adaptive hashing algorithm with salt)
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}