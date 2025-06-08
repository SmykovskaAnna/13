package com.marketplace.config;

import com.marketplace.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;

@Configuration
@EnableWebSecurity // Enables Spring Security in the application
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    // Inject the custom user details service
    public SecurityConfig(CustomUserDetailsService service) {
        this.userDetailsService = service;
    }

    // Define the security filter chain that configures HTTP security
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Allow unauthenticated access to public pages and resources
                        .requestMatchers("/", "/register", "/css/**", "/images/**").permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        // Specify custom login page
                        .loginPage("/login")
                        // Redirect here after successful login
                        .defaultSuccessUrl("/products", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        // Redirect here after logout
                        .logoutSuccessUrl("/").permitAll()
                );
        return http.build();
    }

    // Configure the authentication manager with user details service and password encoder
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService) // Use custom UserDetailsService
                .passwordEncoder(passwordEncoder())     // Use BCrypt password encoder
                .and()
                .build();
    }

    // Define the password encoder bean (BCrypt is strong and widely used)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}