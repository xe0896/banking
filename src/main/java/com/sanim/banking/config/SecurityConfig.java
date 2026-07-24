package com.sanim.banking.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Configuration
@EnableConfigurationProperties(JwtProperties.class) // Enables the JWT environment variables
public class SecurityConfig {
    // @Bean and @Component are very similar, @Bean is used whenever we are using a third party library
    // as we couldn't put @Component in the third party library like Jackson, the code below shows
    // that we are creating a PasswordEncoder for everyone to use via @Bean. If we was to make our own
    // class and want an auto instantiate then we would use @Component on the class not the method, methods
    // are not allowed to be annoted via @Component

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}