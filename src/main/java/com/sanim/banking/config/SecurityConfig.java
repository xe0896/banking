package com.sanim.banking.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties(JwtProperties.class) // Enables the JWT environment variables
public class SecurityConfig {
    // @Bean and @Component are very similar, @Bean is used whenever we are using a third party library
    // as we couldn't put @Component in the third party library like Jackson, the code below shows
    // that we are creating a PasswordEncoder for everyone to use via @Bean. If we was to make our own
    // class and want an auto instantiate then we would use @Component on the class not the method, methods
    // are not allowed to be annoted via @Component
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}